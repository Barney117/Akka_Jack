package blackjack;


import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import java.util.ArrayList;


public class Main implements Runnable{
	boolean goAgain = true;
	public String currPlayer = "";
	public ActorRef humanPlayer, house, Autobot,Autobot2, player ,deck;
	ArrayList<ActorRef> playerList = new ArrayList<ActorRef>(3);
	UI UI;
	ActorSystem masterSystem;
	public static final int COMPLIMENTARY = 100;
	public Main(ActorRef house, ActorRef player, ActorRef deck, ActorSystem master) throws InterruptedException {
		masterSystem = master;
		this.player = player;
		this.house = house;
		currPlayer = player.path().name();
		this.deck = deck;
		UI = new UI(house, player);
		humanPlayer = master.actorOf(Props.create(Human.class, deck, house, player, UI), "Rem");
		Autobot = master.actorOf(Props.create(Autobot.class, deck, UI), "AutoBots_Player_1");
		Autobot2 = master.actorOf(Props.create(Autobot.class, deck, UI), "AutoBots_Player_2");
		playerList.add(humanPlayer);
		playerList.add(Autobot);
		playerList.add(Autobot2);
		
	}

	@Override
	public void run() {
		System.out.println("ya'll ready....");
		UI.print(playerList.size()-1 + " AutoBots have also joined the game");
		try {
			while(goAgain && !(Thread.currentThread().isInterrupted())){
				Blackjack akkaJack = new Blackjack(playerList,deck,UI, player, house);
				goAgain = akkaJack.continous();
				if(goAgain == false){
					UI.print("you've ended the game thank you for playing");
					player.tell(PoisonPill.getInstance(),house);
					house.tell(PoisonPill.getInstance(), ActorRef.noSender());
					masterSystem.shutdown();
					break;
				}
				Timeout timeout = new Timeout(Duration.create(Blackjack.time, "seconds"));
				Future<Object> future = Patterns.ask(playerList.get(0), "player wallet", timeout);
				int result = 0;
				try {
					result = (Integer) Await.result(future, timeout.duration());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if((result<=0)){
					UI.print("you are out of money, come back again soon");
					player.tell(PoisonPill.getInstance(),house);
					house.tell(PoisonPill.getInstance(), ActorRef.noSender());
					masterSystem.shutdown();
					break;
				}
			}
			System.out.println("lets go again");
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

}
