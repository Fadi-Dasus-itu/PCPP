package mobilepayment;

import java.util.ArrayList;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Bank extends AbstractBehavior<Bank.Transaction> {


    /* --- Messages ------------------------------------- */
    public record Transaction(int deposit, ActorRef account1, ActorRef account2) {
    }

    /* --- State ---------------------------------------- */
    private  ArrayList accounts = new ArrayList<ActorRef<Account.Deposit>>();

    /* --- Constructor ---------------------------------- */
    private Bank(ActorContext<Transaction> context) {
        super(context);
    }


    /* --- Actor initial behavior ----------------------- */
    public static Behavior<Transaction> create() {
        return Behaviors.setup(Bank::new);
    }

    /* --- Message handling ----------------------------- */
    @Override
    public Receive<Transaction> createReceive() {
        return newReceiveBuilder()
                .onMessage(Transaction.class, this::onTransaction)
                .build();
    }

    /* --- Handlers ------------------------------------- */
    // To be Implemented
    private Behavior<Transaction> onTransaction(Transaction transaction) {
        var account1 = transaction.account1();
        var account2 = transaction.account2();
        var d = transaction.deposit;

        account1.tell(new Account.Deposit(transaction.deposit));
        account2.tell(new Account.Deposit(-transaction.deposit));
        return this;
    }
}
