package mathsserver;

// Hint: The imports below may give you hints for solving the exercise.
//       But feel free to change them.

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Server extends AbstractBehavior<Server.ServerCommand> {
    /* --- Messages ------------------------------------- */
    public interface ServerCommand {
    }

    public record ComputeTasks(List<Task> tasks,
                               ActorRef<Client.ClientCommand> client) implements ServerCommand {
    }

    public static final class WorkDone implements ServerCommand {
        ActorRef<Worker.WorkerCommand> worker;

        public WorkDone(ActorRef<Worker.WorkerCommand> worker) {
            this.worker = worker;
        }
    }

    /* --- State ---------------------------------------- */
    Queue<ClientTaskDTO> pendingTasks = new LinkedList<>();
    Map<ActorRef<Worker.WorkerCommand>, String> workers = new HashMap<>();
    int minWorkers;
    int maxWorkers;


    /* --- Constructor ---------------------------------- */
    private Server(ActorContext<ServerCommand> context,
                   int minWorkers, int maxWorkers) {
        super(context);
        // To be implemented
        this.minWorkers = minWorkers;
        this.minWorkers = maxWorkers;
        IntStream
                .range(0, minWorkers)
                .forEach((workerId) -> {
                    final ActorRef<Worker.WorkerCommand> worker =
                            getContext().spawn(Worker.create(getContext().getSelf()),
                                    "worker_" + workerId);
                    workers.put(worker, "IDLE");
                });
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
        var localTasks = msg.tasks;
        var taskSize = msg.tasks.size();
        var workersReference = workers
                .entrySet()
                .stream()
                .filter(x -> x.getValue().equals("IDLE"))
                .map(x -> x.getKey()).collect(Collectors.toList());
//TODO improve this
        while (localTasks.size() > 0) {
            if (workersReference.size() > 0 && localTasks.size() > 0) {
                workersReference.get(0).tell(new Worker.ComputeTask(localTasks.remove(0), msg.client));
            }
            if (workersReference.size() == 0 && workers.size() < this.maxWorkers) {
                final ActorRef<Worker.WorkerCommand> worker =
                        getContext().spawn(Worker.create(getContext().getSelf()),
                                "worker_" + workers.size() + 1);
                workers.put(worker, "IDLE");
                worker.tell(new Worker.ComputeTask(localTasks.remove(0), msg.client));
            }
            if (!(workersReference.size() > 0 && localTasks.size() > 0) && !(workersReference.size() == 0 && workers.size() < this.maxWorkers)) {
                localTasks.forEach(x ->
                        pendingTasks.add(new ClientTaskDTO(msg.client, x))
                );
            }
        }
        return this;
    }

    public Behavior<ServerCommand> onWorkDone(WorkDone msg) {
        // To be implemented
        return this;
    }
}
