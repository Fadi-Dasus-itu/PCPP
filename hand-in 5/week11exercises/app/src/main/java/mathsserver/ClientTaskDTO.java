package mathsserver;

import akka.actor.typed.ActorRef;

public record ClientTaskDTO(ActorRef<Client.ClientCommand> client,
                            Task task) {
}
