package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.Message> {

    /* --- Messages ------------------------------------- */
    // To be Implemented

    public static record Message(ActorRef<Bank.Transaction> bankRef, Bank.Transaction transaction) {
    }

    /* --- State ---------------------------------------- */
    // To be Implemented
// empty

    /* --- Constructor ---------------------------------- */
    // To be Implemented
    private MobileApp(ActorContext<MobileApp.Message> context) {
        super(context);
    }

    /* --- Actor initial behavior ----------------------- */
    // To be Implemented
    public static Behavior<MobileApp.Message> create() {
        return Behaviors.setup(MobileApp::new);
    }

    /* --- Message handling ----------------------------- */
    // To be Implemented

    @Override
    public Receive createReceive() {
        return newReceiveBuilder()
                .onMessage(MobileApp.Message.class, this::onMessage)
                .build();
    }


    /* --- Handlers ------------------------------------- */
    // To be Implemented

    private Behavior<Message> onMessage(Message message) {
        var bankRef = message.bankRef;
        bankRef.tell(message.transaction);
        return this;
    }
}
