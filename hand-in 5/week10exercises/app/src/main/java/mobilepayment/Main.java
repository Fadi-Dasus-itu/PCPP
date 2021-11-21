package mobilepayment;

import akka.actor.typed.ActorSystem;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // actor system
        // To be implemented
        ActorSystem<SystemGuardian.Start> guardian =
                ActorSystem.create(SystemGuardian.create(), "my_system_guardian");
        guardian.tell(new SystemGuardian.Start("this is my message"));
// init message
        // To be implemented

        // wait until user presses enter
        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            e.printStackTrace();
        } finally {
            // terminate actor system execution
            // To be implemented
            guardian.terminate();
        }

    }

}
