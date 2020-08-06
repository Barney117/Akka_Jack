package actors;



import java.util.Scanner;

import akka.actor.UntypedActor;
@SuppressWarnings({ "resource" })
public class PlayerActor extends UntypedActor {
       static String actor = "";
    public PlayerActor(String name) {
        actor = name;
    }

   
	@Override
    public void onReceive(Object message) throws Exception {
        if (message.toString().contains("player response")) {
            String response = "myResponse";
            Scanner scanner1 = new Scanner(System.in);
            response = scanner1.nextLine();
             getSender().tell(response, null);
        } else {
            System.out.println("" + message);
          
        }
      
    }

    @Override
    public void preStart() {
        try {
            super.preStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("lets get ready to rumble");
     
    }

    @Override
    public void postStop() {
        try {
            super.postStop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Game over man! GAME OVER!!!");
    }

      

}

