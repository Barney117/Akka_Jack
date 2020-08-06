package actors;


import akka.actor.ActorRef;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import blackjack.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings({"unused" })
public class Server extends UntypedActor {
    static int port = 8080;
    static ActorSystem master = ActorSystem.create("GameEngine", createConfig());

    public static void main(String[] args) {
        final ActorRef game = master.actorOf(Props.create(Server.class), "Game");

        try {
            MulticastSenderSocket sender = new MulticastSenderSocket();
            sender.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
            if(o.toString().contains("Lets do this")){
                ActorSystem actorSYS = ActorSystem.create("HouseRules", createConfig());
                final ActorRef house = actorSYS.actorOf(Props.create(HouseRules.class, "Sheldon_Cooper"), "Sheldon_Cooper");
                ActorRef player = getSender();
                ActorRef deck = actorSYS.actorOf(Props.create(CardActor.class, new Deck()));
                UI UI = new UI(house,player);
                Main test = new Main(house, player, deck, actorSYS);
                test.run();
            }
        }
    private static Config createConfig() {
        String address = "";
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider");
        map.put("akka.remote.transport", "akka.remote.netty.NettyRemoteTransport");
        map.put("akka.remote.netty.tcp.hostname", address);
        map.put("akka.remote.netty.tcp.port", String.valueOf(port++));
        return ConfigFactory.parseMap(map);
    }
    }
