package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

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
                // To be extended
                .build();
    }


    /* --- Handlers ------------------------------------- */

    public Behavior<ServerCommand> onComputeTasks(ComputeTasks msg) {

        LOG("number of tasks :" + msg.tasks.size());
//TODO improve this


        while (msg.tasks.size() > 0) {
            var idleWorkers = getIdleWorkersIfAny();
            if (idleWorkers.size() > 0 && msg.tasks.size() > 0) {
                var idleWorker = idleWorkers.remove(0);
                idleWorker.tell(new Worker.ComputeTask(msg.tasks.remove(0), msg.client));
                LOG(" the worker is currently doing the task, worker name : " + idleWorker.path().name());
                workers.put(idleWorker, "BUSY");
                LOG("number of tasks left : " + msg.tasks.size());
                LOG("------------");
                continue;
            }
            if (idleWorkers.size() == 0) {
                LOG("no idle workers are available .... ");
                var newWorker = spawnNewWorkerIfAllowed();
                if (newWorker.isPresent())
                    newWorker.get().tell(new Worker.ComputeTask(msg.tasks.remove(0), msg.client));
                continue;
            }

            msg.tasks.forEach(x -> pendingTasks.add(new ClientTaskDTO(msg.client, x)));

        }
        return this;
    }


    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
        // To be implemented
        return this;
    }

    /*------------------- helper methods-------------------- */

    private List<ActorRef<Worker.WorkerCommand>> getIdleWorkersIfAny() {
        return workers
                .entrySet()
                .stream()
                .filter(x -> x.getValue().equals("IDLE"))
                .map(x -> x.getKey()).collect(Collectors.toList());
    }

    private void spawnMinWorkers(int minWorkers) {
        IntStream.range(0, minWorkers)
                .forEach((workerId) -> {
                    workers.put(getContext().spawn(Worker.create(getContext().getSelf()),
                            "worker_" + workerId), "IDLE");
                });
        LOG("spawn initial workers, number of workers: " + workers.size());
        LOG("------------");

    }

    private Optional<ActorRef<Worker.WorkerCommand>> spawnNewWorkerIfAllowed() {
        LOG("checking if allowed to spawn new worker");
        if (workers.size() < this.maxWorkers) {
            LOG("spawning a new worker");
            final ActorRef<Worker.WorkerCommand> worker =
                    getContext().spawn(Worker.create(getContext().getSelf()),
                            "worker_" + (workers.size() + 1));
            workers.put(worker, "IDLE");
            LOG("Number of workers: " + workers.size());
            LOG("------------");
            return Optional.of(worker);
        }

        return Optional.empty();
    }

    private void LOG(String msg) {
        System.out.println(msg);
    }
}
