package blackjack;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Deck {
	private int deal;
	private Card[] card;
	private Semaphore deck;
	public Deck(){
		deck = new Semaphore(1);
		card = Card.newDeck();
		shuffle();
		resetDeck();
	}
	public synchronized Card nextCard() throws InterruptedException{
		deck.acquire();
		Card nCard = null;
		if (deal < 52){
			nCard = card[deal];
			deal++;
		}
		deck.release();
		return nCard;
	}
		
	public synchronized void resetDeck(){
		deal = 0;
	}
	public synchronized void shuffle(){
			Random rand = new Random(System.currentTimeMillis());
			for(int x=0; x<(card.length*card.length); x++){
			int i, j;
			do {
				i = rand.nextInt(card.length);
				j = rand.nextInt(card.length);
			} while (i == j);

			Card temp = card[i];
			card[i] = card[j]; 
			card[j] = temp;
		}
		System.out.println("Everyday i'm shufflin, du du du du du du du ");
	}
	
}

