package blackjack;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public abstract class Player extends UntypedActor {
	public abstract boolean isHuman();
	private ActorRef deck;
	protected Blackjack currentGame;
	protected Hand hand;
	public int wallet;
	protected String playerName;
	public Player(ActorRef deckOfCards) throws InterruptedException {
		deck = deckOfCards;
		hand = new Hand(deck);
		wallet = Main.COMPLIMENTARY;
	}


	public void dealNewHand() throws InterruptedException{
		hand = new Hand(deck);
	}

	
	public void payPlayer(int winnings) {
		wallet += winnings;
	}

	public void subtractMoney (int bet) {
		wallet = wallet - bet;
	}

}

