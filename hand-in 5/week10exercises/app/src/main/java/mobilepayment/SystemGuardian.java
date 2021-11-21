package mobilepayment;

import java.util.Random;
import java.util.stream.IntStream;

import akka.actor.ActorPath;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class SystemGuardian extends AbstractBehavior<SystemGuardian.Start> {

    /* --- Messages ------------------------------------- */
    // To be Implemented
    public static record Start(String msg) {
    }

    /* --- State ---------------------------------------- */
    // To be Implemented
// empty

    /* --- Constructor ---------------------------------- */
    // To be Implemented
    private SystemGuardian(ActorContext<Start> context) {
        super(context);
    }

    /* --- Actor initial behavior ----------------------- */
    // To be Implemented
    public static Behavior<Start> create() {
        return Behaviors.setup(SystemGuardian::new);
    }

    /* --- Message handling ----------------------------- */
    // To be Implemented
    @Override
    public Receive<Start> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    // To be Implemented

    private Behavior<Start> onStart(Start message) {
        // ---------- mobile apps actors ------------
        final ActorRef<MobileApp.Message> mobileApp =
                getContext().spawn(MobileApp.create(), "first-app");

        final ActorRef<MobileApp.Message> mobileApp2 =
                getContext().spawn(MobileApp.create(), "second-app");

        // ---------- Accounts actors ------------
        final ActorRef<Account.Deposit> account1 =
                getContext().spawn(Account.create(), "first-account");

        final ActorRef<Account.Deposit> account2 =
                getContext().spawn(Account.create(), "second-account");

        // ---------- Banks actors ------------
        final ActorRef<Bank.Transaction> bank1 =
                getContext().spawn(Bank.create(), "first-bank");

        final ActorRef<Bank.Transaction> bank2 =
                getContext().spawn(Bank.create(), "second-bank");

        Random rd = new Random(); // creating Random object
        System.out.println();
        for (int i = 0; i <100 ;i ++){
        // ---------- first transaction------------

        mobileApp.tell(new MobileApp
                .Message(bank1,new Bank.Transaction(rd.nextInt(),account1,account2)));

        // ---------- second transaction------------
        mobileApp2.tell(new MobileApp
                .Message(bank1,new Bank.Transaction(rd.nextInt(),account2,account1)));

        }

        return this;
    }

}
