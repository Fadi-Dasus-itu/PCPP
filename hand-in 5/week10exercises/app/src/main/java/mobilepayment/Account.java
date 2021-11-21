package mobilepayment;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Account extends AbstractBehavior<Account.Deposit> {


    /* --- Messages ------------------------------------- */
    // To be Implemented
    public record Deposit(int balance) {
    }

    /* --- State ---------------------------------------- */
    // we do not know, maybe the bank
    public  int balance = 1000;


    /* --- Constructor ---------------------------------- */
    // To be Implemented

    private Account(ActorContext<Deposit> context) {
        super(context);
        this.balance = balance;
    }

    /* --- Actor initial behavior ----------------------- */
    public static Behavior<Account.Deposit> create() {
        return Behaviors.setup(Account::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<Deposit> createReceive() {
        return newReceiveBuilder()
                .onMessage(Account.Deposit.class, this::onDeposit)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    private Behavior<Deposit> onDeposit(Deposit deposit) {
        var name = getContext().getSelf().path().name();

//        System.out.println("balance for the account " + name + " is " + this.balance + "we need to add " + deposit.balance);
            this.balance += deposit.balance;

        System.out.println("balance for the account " + name + " is " + this.balance);
        return this;
    }
}
