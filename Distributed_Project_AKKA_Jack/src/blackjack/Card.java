package blackjack;


public class Card {

	static public final String 
	HEARTS = "Hearts",
	DIAMONDS = "Diamonds",
	CLUBS = "Clubs",
	SPADES = "Spades";
	
	static public final String[] Card_Num = {
			"Ace of ",
			"Two of ",
			"Three of ", 
			"Four of ",
			"Five of ",
			"Six of ",
			"Seven of ",
			"Eight of ",
			"Nine of ",
			"Ten of ",
			"Jack of ",
			"Queen of ",
			"King of "};
	
	static public final int Deck_Size = 52;
	static public final int[] Card_Values = {11, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
	static public final String[] Suit = {HEARTS, DIAMONDS, CLUBS, SPADES};

	private String type;
	private String suit;
	private int  value;
	public Card (String type, String suit,  int value){
		this.type = type;
		this.suit = suit;
		this.value = value;
	}

	public static Card[] newDeck(){
		Card[] deck = new Card[52];
		for (int i=0; i<Deck_Size; i++){
			deck[i] = new Card(Card_Num[i%13], Suit[(i/13)%4], Card_Values[i%13]);
		}
		return deck;
	}
	public String getSuit(){
		return suit;
	}
	public int getGameValue(){
		return value;
	}
	//deals with low ace problem
	public void setGameValue(){
		this.value = (this.value -10);
	}

	public String toString (){
		return" " + type + suit + " ("+ value+")";
	}

}