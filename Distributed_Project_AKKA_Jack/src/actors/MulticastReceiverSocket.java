package actors;


import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class  MulticastReceiverSocket  {
	private static final int port = 2101;
	private MulticastSocket socket;
	private InetAddress inetAddress;
	private static final String multicastChannel = "224.1.1.1";
	public MulticastReceiverSocket ( ) throws IOException{
		this.inetAddress = InetAddress.getByName(multicastChannel);
		this.socket = new MulticastSocket(port);
		this.socket.joinGroup(inetAddress);
	}

	public String run(){
		while (true){
			DatagramPacket dataGramPacket = new DatagramPacket(new byte[1024], 1024);
			try {
				socket.receive(dataGramPacket);
				String hostAddress = dataGramPacket.getAddress().getHostAddress();
				return hostAddress;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
