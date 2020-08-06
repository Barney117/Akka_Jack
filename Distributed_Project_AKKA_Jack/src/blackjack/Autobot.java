package blackjack;

import akka.actor.ActorRef;
import java.io.*;

@SuppressWarnings("static-access")
public class Autobot extends Player {
	int botScore;
	static int i= 0;
	UI UI;
	private int currentBet;
	public Autobot(ActorRef inputDeck, UI console) throws InterruptedException {
		super(inputDeck);
		UI = console;
	}
	public void botCards(){
		hand.resetCardsHeld();
		UI.print("the bots hand:" + this.hand );
		botScore = Integer.parseInt(this.hand.gameScore());
		UI.print("gives a bot score of " + botScore);
	}
	public void updateBank(){
		this.payPlayer(currentBet*2);
	}
	public void resetBank() {

		this.payPlayer(currentBet);
	}
	@Override
	public boolean isHuman() {
		return false;
	}

	@Override
	public void onReceive(Object object) throws Exception {

		if(object.toString().contains("hit the bot")){
			int hitScore = hitTheBot();
			getSender().tell(hitScore, ActorRef.noSender());
		}
		if(object.toString().contains("reset")){
			reset();
		}
		if(object.toString().contains("show player wallets")){
		getSender().tell(wallet, getSelf());
		}
		if(object.toString().contains("update bank")){
			updateBank();
		}
		if(object.toString().contains("reset bank")){
			resetBank();
		}
		if(object.toString().contains("get bet")){
			int botBet = getBet();
			getSender().tell(botBet, getSelf());
		}
		if(object.toString().contains("are you player one?")){
			getSender().tell(false,getSelf());
		}
		if (object.toString().contains("give me cards")) {
			dealNewHand();
			}
		if(object instanceof Blackjack){
			currentGame = (Blackjack) object;
		}
		if(object.toString().contains("player wallet")){
			getSender().tell(wallet, getSelf());
		}
		
		if(object.toString().contains("show cards")){
			botCards();
		}
	}
	private int hitTheBot() throws  IOException, InterruptedException {
		boolean hitout = false;
		this.i = i + 1;
		do {
			if (botScore == 21) {
				UI.print("Autobot " + i + " scored " + botScore);
				Thread.sleep(2000);
				hitout = true;
				break;
			}
			if (botScore <= 16) {
				UI.print("Autobot " + i + " hits");
				Thread.sleep(2000);
				this.hand.checkCard(botScore);
				UI.print("the bots new cards "+this.hand.toString());
				botScore = Integer.parseInt(this.hand.gameScore());
				UI.print("Autobot " + i + " now scores " + botScore);
				Thread.sleep(2000);
				if (botScore > 21) {
					UI.print("Autobot " + i + " busted ");
					Thread.sleep(2000);
					hitout = true;
					botScore =0;
					break;
				}
			}
			else if(botScore >=13) {
				UI.print("Autobot " + i + " stays");
				Thread.sleep(2000);
				hitout = true;
				break;
			}
		}while(hitout == false);
		return botScore;
	}
	
	public int reset() {
		this.i = 0;
		return 0;
	}
		public int getBet() {
		this.currentBet = ((int) (Math.random()*(40 - 10))) + 10;
		int bet = this.currentBet;
		this.subtractMoney(bet);
		return bet;
	}
}

