package mathsserver;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.ChildFailed;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.*;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Server extends AbstractBehavior<Server.ServerCommand> {

    /* --- Messages ------------------------------------- */

    public interface ServerCommand {
    }

    public record ComputeTasks(List<Task> tasks, ActorRef<Client.ClientCommand> client) implements ServerCommand {
    }

    public record WorkDone(ActorRef<Worker.WorkerCommand> worker) implements ServerCommand {
    }

    /* --- State ---------------------------------------- */

    Queue<ClientTaskDTO> pendingTasks = new LinkedList<>();
    Map<ActorRef<Worker.WorkerCommand>, String> workers = new HashMap<>();
    int minWorkers;
    int maxWorkers;


    /* --- Constructor ---------------------------------- */

    private Server(ActorContext<ServerCommand> context, int minWorkers, int maxWorkers) {
        super(context);
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        spawnMinWorkers(minWorkers);
        getContext().getLog().info("{}: Server and workers started",
                getContext().getSelf().path().name());
    }


    /* --- Actor initial state -------------------------- */

    public static Behavior<ServerCommand> create(int minWorkers, int maxWorkers) {
        return Behaviors.setup(context -> new Server(context, minWorkers, maxWorkers));
    }


    /* --- Message handling ----------------------------- */

    @Override
    public Receive<ServerCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ComputeTasks.class, this::onComputeTasks)
                .onMessage(WorkDone.class, this::onWorkDone)
                .onSignal(ChildFailed.class, this::onChildFailed)
                .onSignal(Terminated.class, this::onTerminated)
                .build();
    }


    /* --- Handlers ------------------------------------- */


    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {
        while (msg.tasks.size() > 0) {
            var idleWorkers = getIdleWorkersIfAny();

            if (idleWorkers.size() > 0 && msg.tasks.size() > 0) {
                assignTaskToIdleWorker(msg, idleWorkers);
                continue;
            }
            if ((idleWorkers.size() == 0) && (workers.size() < this.maxWorkers)) {
                assignTaskToNewWorker(msg);
                continue;
            }
            msg.tasks.forEach(x -> pendingTasks.add(new ClientTaskDTO(msg.client, x)));
            break;
        }
        return this;
    }

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
        if (pendingTasks.size() == 0) {
            workers.put(msg.worker, "IDLE");
        } else {
            var task = pendingTasks.poll();
            if (task != null) {
                getContext().watch(msg.worker);
                msg.worker.tell(new Worker.ComputeTask(task.task(), task.client()));
            }
        }
        return this;
    }

    public Behavior<ServerCommand> onTerminated(Terminated msg) {
        if (workers.remove(msg.getRef()) != null) {
            getContext().getLog().info("{}: {} terminated normally.",
                    getContext().getSelf().path().name(),
                    msg.getRef().path().name());
        }
        return this;
    }


    public Behavior<ServerCommand> onChildFailed(ChildFailed msg) {

        ActorRef<Void> crashedChild = msg.getRef();
        workers.remove(crashedChild);
        if (workers.size() < this.minWorkers && workers.size() > this.maxWorkers) {
            spawnNewWorkerIfAllowed();
        } else getContext().getLog().info("{}: No job from worker {} found.",
                getContext().getSelf().path().name(),
                msg.getRef().path().name());
        return this;
    }

    /*------------------- helper methods-------------------- */

    private List<ActorRef<Worker.WorkerCommand>> getIdleWorkersIfAny() {
        return workers
                .entrySet()
                .stream()
                .filter(x -> x.getValue().equals("IDLE"))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private void spawnMinWorkers(int minWorkers) {
        IntStream.range(0, minWorkers)
                .forEach((workerId) -> workers.put(getContext().spawn(Worker.create(getContext().getSelf()),
                        RandomStringUtils.randomNumeric(10)), "IDLE"));
    }

    private Optional<ActorRef<Worker.WorkerCommand>> spawnNewWorkerIfAllowed() {
        final ActorRef<Worker.WorkerCommand> worker =
                getContext().spawn(Worker.create(getContext().getSelf()),
                        RandomStringUtils.randomNumeric(10));
        workers.put(worker, "IDLE");
        return Optional.of(worker);
    }

    private void assignTaskToIdleWorker(ComputeTasks msg, List<ActorRef<Worker.WorkerCommand>> idleWorkers) {
        var idleWorker = idleWorkers.remove(0);
        getContext().watch(idleWorker);
        idleWorker.tell(new Worker.ComputeTask(msg.tasks.remove(0), msg.client));
        workers.put(idleWorker, "BUSY");
    }

    private void assignTaskToNewWorker(ComputeTasks msg) {
        var newWorker = spawnNewWorkerIfAllowed();
        if (newWorker.isPresent()) {
            getContext().watch(newWorker.get());
            newWorker.get().tell(new Worker.ComputeTask(msg.tasks.remove(0), msg.client));
        }
    }

}
