package actors;

import akka.actor.*;
import blackjack.Deck;
import blackjack.Card;

public class CardActor extends UntypedActor {
    private Deck deck;

    public CardActor(Deck deck){
        this.deck = deck;
    }
    @Override
    public void onReceive(Object message) throws InterruptedException {
        if (message.equals("Deal Next")){
             Card card = deck.nextCard();
            getSender().tell(card, getSelf());
        }
        if (message.equals("shuffle")){
            deck.shuffle();
        }
         if (message.equals("new deck")){
            deck.resetDeck();
        }
       
    }


}
