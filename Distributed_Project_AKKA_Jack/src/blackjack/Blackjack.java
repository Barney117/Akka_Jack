package blackjack;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import java.io.IOException;
import java.util.ArrayList;

public class Blackjack {
	public static final int time = 60000;
	private ArrayList<ActorRef> players;
	int blackJackScoreHouse;
	UI UI;
	int bank;
	ActorRef deck,currentPlayer, house;
	static boolean hitout = false;
	ArrayList<Integer> gameScore = new ArrayList<Integer>();
	public Blackjack(ArrayList<ActorRef> players, ActorRef deck, UI UI, ActorRef currentPlayer, ActorRef house) throws IOException{
		this.players = new ArrayList<ActorRef>();
		this.house = house;
		this.players.addAll(players);
		for (int i=0; i< this.players.size(); i++){
			this.players.get(i).tell(this,house);
		}
		this.deck = deck;
		this.UI = new UI(house,currentPlayer);
		this.currentPlayer = (ActorRef) players.get(0);
	}

	boolean continous() throws InterruptedException, IOException {
		checkBank();
		dealCards();
		initailDeal();
		dealerDeal();
		checkWinner();
		System.out.println("number of players "+players.size());
		players.clear();
		Timeout wait = new Timeout(Duration.create(time, "seconds"));
		Future<Object> future = Patterns.ask(currentPlayer, "play again?", wait);
		String response = "";
		try {
			response = (String) Await.result(future, wait.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(response.equalsIgnoreCase("l")){
			return false;
		}
		return true;
	}
	private void checkBank() {
		for (int i=0; i< players.size(); i++){

			Timeout timeout = new Timeout(Duration.create(time, "seconds"));
			Future<Object> future = Patterns.ask(players.get(i), "show player wallets", timeout);
			int pot = 0;
			try {
				pot = (int) Await.result(future, timeout.duration());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(pot<=0) {
				UI.print(players.get(i).path().name() + " has left the game");
				players.remove(i);
			}
			UI.print(players.get(i).path().name() + " has " + pot + " Euros");
		}
	}

	private void dealCards() throws InterruptedException {
		deck.tell("shuffle", house);
		deck.tell("new deck", house);
			for (int i=0; i<players.size(); i++){
			ActorRef actor = players.get(i);
			System.out.println("current ref "+actor);
			actor.tell("give me cards", house);
		}
	}

	private void initailDeal() throws  InterruptedException {
		currentPlayer.tell("reset", house);
		UI.print("## Place your bets!##\n");
		for (int i=0; i< players.size(); i++){
			int dbet =0;
			int botBet = 0;

			Timeout timeout = new Timeout(Duration.create(time, "seconds"));
			Future<Object> future = Patterns.ask(players.get(i), "are you player one?", timeout);
			boolean playerOne = false;
			try {
				playerOne = (Boolean) Await.result(future, timeout.duration());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (playerOne){

				timeout = new Timeout(Duration.create(time, "seconds"));
				future = Patterns.ask(currentPlayer, "get first choice", timeout);

				try {
					botBet = (Integer) Await.result(future, timeout.duration());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				UI.print("##############################################\n"+
						"# It's " + players.get(i).path().name()+"'s turn. #\n"+
						"##############################################");
				Thread.sleep(1000);
				timeout = new Timeout(Duration.create(time, "seconds"));
				future = Patterns.ask(players.get(i), "get bet", timeout);

				try {
					botBet = (Integer) Await.result(future, timeout.duration());
				} catch (Exception e) {
					e.printStackTrace();
				}
				UI.print("The bot has bet €" + botBet);
			}
			switch(botBet) {
			case 0:
				UI.print("You choose to skip this round and let the bots play!");
				break;
			default:
				if (playerOne){
					currentPlayer.tell("play initial cards", house);
				}
				else {
					players.get(i).tell("show cards", house);

				}
				if (playerOne){
					timeout = new Timeout(Duration.create(time, "seconds"));
					future = Patterns.ask(currentPlayer, "hit me", timeout);

					try {
						dbet = (Integer) Await.result(future, timeout.duration());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				else {
					timeout = new Timeout(Duration.create(time, "seconds"));
					future = Patterns.ask(players.get(i), "hit the bot", timeout);

					try {
						dbet = (Integer) Await.result(future, timeout.duration());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			this.gameScore.add(dbet);
			UI.print(players.get(i).path().name()+" scored " + dbet);
			Thread.sleep(1000);
			
		}
	}
	private void dealerDeal() throws InterruptedException {
		UI.print("##############################################\n"+
				"# GET READY EVERYONE, HERE COMES THE DEALER! #\n"+
				"##############################################");
		Thread.sleep(1000);
		this.blackJackScoreHouse = getRandomInteger(24,16 );
		UI.print("## Dealer Scored "+ blackJackScoreHouse + " ##");
		Thread.sleep(1000);
		if(blackJackScoreHouse == 21) {
			UI.print("#######################\n"+
					"# BOOOOOOM, BLACKJACK #\n"+
					"#######################");
			Thread.sleep(2000);
		}
		if(blackJackScoreHouse > 21) {
			this.blackJackScoreHouse = 0;
			UI.print("## Dealer Busted ##");
			Thread.sleep(2000);
		}
	}
	public static int getRandomInteger(int maximum, int minimum){ return ((int) (Math.random()*(maximum - minimum))) + minimum; }


	private void checkWinner() throws InterruptedException {
		for (int i=0; i< players.size(); i++){
			Thread.sleep(500);
			int black = gameScore.get(i);
			if (blackJackScoreHouse > black) {
				UI.print("house wins, unlucky " + players.get(i).path().name());
			}
			if (blackJackScoreHouse == black) {
				UI.print("its a draw, " + players.get(i).path().name() + " You get yout money back.");
				players.get(i).tell("reset bank", house);
			}
			if (blackJackScoreHouse < black) {

				players.get(i).tell("update bank", house);
				UI.print("congrats " + players.get(i).path().name()+ " you win");
			}
		}
		displayWallet();
	}

	private void displayWallet() {
		for (int i=0; i< players.size(); i++){
			Timeout timeout = new Timeout(Duration.create(time, "seconds"));
			Future<Object> future = Patterns.ask(players.get(i), "show player wallets", timeout);
			int money = 0;
			try {
				money = (int) Await.result(future, timeout.duration());
			} catch (Exception e) {
				e.printStackTrace();
			}
			UI.print(players.get(i).path().name() + " has " + money + " Euros");
		}
	}

}

