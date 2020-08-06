package actors;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Client {
	static int port = 2100;
    @SuppressWarnings({ "deprecation" })
	public static void main(String[] args) {
      
        String playerName = "Rem";
        System.out.print("###########################################\n"+
						 "# 	 Welcome to the Akka Jack lobby!  #\n"+
						 "# Please wait while we find an acive game #\n"+
        				 "###########################################\n");
        
        MulticastReceiverSocket socket = null;
        String ipAddress = "";
        try {
            socket = new MulticastReceiverSocket();
            ipAddress = socket.run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Game found at " + ipAddress);
               
        ActorSystem actorSYS = ActorSystem.create("PlayerActor", createConfig());
        final ActorRef currentPlayer = actorSYS.actorOf(Props.create(PlayerActor.class, playerName), playerName);
        final String gamePath = "akka.tcp://GameEngine@"+ipAddress+":8080/user/Game";
        ActorRef houseActor = actorSYS.actorFor(gamePath);
        houseActor.tell("Lets do this", currentPlayer);
        while (true){
            if(currentPlayer.isTerminated()){
                System.exit(0);
            }
        }

    }

    private static Config createConfig() {
        String address = "";
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // creates a random port number very time a client connects.
        Random rand = new Random();
        int extension = rand.nextInt(1000 - 1) + 1;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider");
        map.put("akka.remote.transport", "akka.remote.netty.NettyRemoteTransport");
        map.put("akka.remote.netty.tcp.hostname", address);
        map.put("akka.remote.netty.tcp.port", (port + extension));
        return ConfigFactory.parseMap(map);
    }
}
