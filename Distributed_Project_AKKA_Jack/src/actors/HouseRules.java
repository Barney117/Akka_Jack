package actors;


import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class HouseRules extends UntypedActor {
	
	private static ActorRef player;
	static volatile boolean isPlayer = false;
	 static String actorName = "";

	public HouseRules(String myName){
		actorName = myName;
	}

	

	@Override
	public void preStart() {
		try {
			super.preStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(actorName + ", Shall we play a game?");
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

	@Override
	public void onReceive(Object msg) {
		player = getSender();
		System.out.println(player.path().name()+" kills: " + msg);
		isPlayer = true;
	}

	
}

