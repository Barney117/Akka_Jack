package blackjack;


import akka.actor.ActorRef;
import java.io.IOException;
import java.util.Random;
@SuppressWarnings({"unused"})
public class Human extends Player implements Runnable {


	public int currentBet =0;
	int iScore;
	ActorRef player,house, deck;

	UI UI;

	public Human(ActorRef deckref, ActorRef house, ActorRef player, UI console) throws InterruptedException {
		super(deckref);
		this.playerName = player.path().name();
		deck = deckref;
		this.player = player;
		this.house = house;
		UI = console;
	}

	private void checkIfBlackJack(){
		boolean blackJack = false;
		if(iScore == 21) {
			blackJack = true;
			UI.print("HURRAY!!...BLACKJACK, YOU WON");
		}
	}
	public void showHumanHand() throws  IOException, InterruptedException {
		hand.resetCardsHeld();
		UI.print("Your first cards are "+ this.hand.toString());
		iScore = Integer.parseInt(this.hand.gameScore());
		UI.print("That gives a total of "+ iScore);
		checkIfBlackJack();
	}
	public int hitMe() throws  IOException, InterruptedException {
		boolean hitout = false;
		do {
			if (iScore == 21) {
				UI.print("you got 21 ");
				hitout = true;
				break;
			}
			else {
				UI.print("Hit or Stay? [Enter H or S]");
				String Answer = UI.getPlayerResponse();
				Answer = Answer.toLowerCase();
				switch (Answer) {
				case "s":
					hitout = true;
					iScore = Integer.parseInt(this.hand.gameScore());
					break;
				case "h":	
					this.hand.checkCard(iScore);
					UI.print("here your new cards "+this.hand.toString());
					iScore = Integer.parseInt(this.hand.gameScore());
					UI.print("That gives a new total of "+ iScore);
					if (iScore > 21) {
						UI.print("you've busted ");
						hitout = true;
						iScore =0;
						break;
					}
				}
			}
		}while(hitout == false);
		return iScore;
	}
	public void updateBank(){

		this.payPlayer(currentBet*2);
	}
	public void resetBank() {

		this.payPlayer(currentBet);
	}

	
	public int openingBet() throws   InterruptedException{
		String betMsg = "B";
		String stayMsg = "S";
		if(this.wallet < 1){
			currentBet = 0;
		}
		else{
			UI.print("Do you want to BET or SKIP the game [B or S]??\n");
		}
		String Answer = UI.getPlayerResponse();
		if (Answer.equalsIgnoreCase(betMsg)){
			UI.print("Enter your bet for this game:\n");
			String response = UI.getPlayerResponse();
			this.currentBet = checkBet(response);
			if(response.equals(null)){
				return -1;
			}

		}else if(Answer.equalsIgnoreCase(stayMsg)){
			currentBet =0;
		}else{
			UI.print("Sorry! That is an incorrect response. \n Please choose again");
			this.openingBet();

		}
		this.subtractMoney(currentBet);
		return currentBet;
	}
	@Override
	public void run() {
	}
	public int checkBet(String Bet) {
		boolean isValid = false;
		int currBet = 0;
		while(isValid == false){
			try{
				int bet = Integer.parseInt(Bet);
				if(bet <= this.wallet && bet >= 0 ){
					currBet = bet;	
					isValid =true;
					break;
				}
				else{
					UI.print("Sorry! That is an incorrect input.\n Please choose again\n");
					try {
						this.openingBet();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}catch(NumberFormatException ex){

			}
		}
		return currBet;
	}

	@Override
	public void onReceive(Object object) throws Exception {
		if(object.toString().contains("hit me")){
			int hitScore = hitMe();;
			getSender().tell(hitScore, ActorRef.noSender());
		}
		
		if(object.toString().contains("show player wallets")){
			getSender().tell(wallet, ActorRef.noSender());
		}
		if(object.toString().contains("get first choice")){
			int playerChoice = openingBet();
			getSender().tell(playerChoice, ActorRef.noSender());
		}

		if(object.toString().contains("update bank")){
			updateBank();
		}
		if(object.toString().contains("give me cards")){
			dealNewHand();
		}
		if(object.toString().contains("reset bank")){
			resetBank();
		}
		if(object.toString().contains("play again?")){
			String nextGame = this.nextGame();
			getSender().tell(nextGame, getSelf());
		}
		if(object.toString().contains("are you player one?")){
			getSender().tell(true,getSelf());
		}
		if(object instanceof Blackjack){
			currentGame = (Blackjack) object;
		}
		if(object.toString().contains("player wallet")){
			getSender().tell(wallet, getSelf());
		}
		if(object.toString().contains("play initial cards")){
			showHumanHand();
		}
		

	}

	public String nextGame() throws   InterruptedException {

		UI.print("Would you like to play again?");
		UI.print("please select LEAVE or STAY [ L or anything else] ");
		String msg = UI.getPlayerResponse();
		return msg;
	}

	@Override
	public boolean isHuman() {
		return true;
	}
}

