package blackjack;


import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;

@SuppressWarnings("static-access")
public class Hand {
	public static int handSize = 2;
	public Card[] handOfCards;
	private ActorRef deck;
	
	/**
	 * creates the initial hand
	 * @param deck
	 * @throws InterruptedException
	 */
	public Hand(ActorRef deck) throws InterruptedException {
		this.deck = deck;
		handOfCards = new Card[10];
		for (int i=0; i<handSize; i++){
			handOfCards[i] = getNextCard();
		}
	}
	 
	/**
	 * actor call to pull nest card from deck.
	 * @return card
	 */
	public Card getNextCard(){
		Timeout wait = new Timeout(Duration.create(Blackjack.time, "seconds"));
		Future<Object> future = Patterns.ask(deck, "Deal Next", wait);
		Card card = null;
		try {
			card = (Card) Await.result(future, wait.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return card;
	}
/**
 * resets the size of the hand array between games
 */
	public void resetCardsHeld() {
		this.handSize=2;
	}

	/**
	 * called when a player chooses to hit
	 * increases the hand size and pulls a new card.
	 * checks the new card against the current game score.
	 * if the score over 10 and an ace is pulled the value is cahnged to one before being added to the hand
	 * @param score
	 * @throws InterruptedException
	 */
	public synchronized void checkCard(int score) throws InterruptedException{
		this.handSize = handSize + 1;
		Card card = getNextCard();
		int i = card.getGameValue();
		if (i == 11 && score + i > 21) {
			card.setGameValue();
			System.out.println("Ace pulled, card value changed " + card.getGameValue());
		}
		handOfCards[handSize-1] = card;


	}
	
	/**
	 * returns the current hand as string for user readibility
	 */
	public String toString(){
		String cardString = "";
		for (int i=0; i<handSize; i++){
			cardString += handOfCards[i];
			cardString += " ";
		}
		return cardString;
	}
	/**
	 * returns the current game score
	 */
	public String gameScore(){
		int gameScore = 0;
		for (int i=0; i<handSize; i++){
			gameScore += handOfCards[i].getGameValue();
		}
		return Integer.toString(gameScore);
	}


}

