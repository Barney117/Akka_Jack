package blackjack;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class UI {
	ActorRef house,currentPlayer;
	
	/**
	 * constructor for the user interface.
	 * takes info from both the house/dealer
	 * and the current player.
	 * @param currPlayer
	 * @param house
	 */
	public UI( ActorRef house ,ActorRef currPlayer){
		this.currentPlayer = currPlayer;
		this.house = house;
		
	}
		
	/**
	 * asks the player for a response and waits to recieve. 
	 * if it doesn't get one it times out.
	 * "Future" is a scala implementaton for performing operations in 
	 * parallel. Its a placeholder object for a value that may not yet exist
	 * @return
	 */
	public String getPlayerResponse()  {
		Timeout wait = new Timeout(Duration.create(Blackjack.time, "seconds"));
		Future<Object> future = Patterns.ask(currentPlayer, "player response", wait);
		String	msg = null;
		try {
		msg = (String) Await.result(future, wait.duration());
		} catch (Exception e) {e.printStackTrace();}
		System.out.println(msg);
		return msg;
		}
	
	/**
	 * allows the players to send message to the userinterface
	 * @param print
	 */
	public void print(String print){
		currentPlayer.tell(print, house);
		}
	}

