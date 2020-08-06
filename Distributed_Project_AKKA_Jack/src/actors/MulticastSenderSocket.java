package actors;

import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastSenderSocket {
	private static final int port = 2101;
	private MulticastSocket socket;
	private InetAddress inetAddress;
	private static final String multicastChannel = "224.1.1.1";
		public MulticastSenderSocket ( ) throws IOException{
		this.inetAddress = InetAddress.getByName(multicastChannel);
		this.socket = new MulticastSocket(port);
		this.socket.joinGroup(inetAddress);
	}
	public void send(String msg ) throws IOException{
		msg = "captain's log 96848.98";
		DatagramPacket dataGramPacket = new DatagramPacket(msg.getBytes(), msg.length(), inetAddress, port);
		socket.send(dataGramPacket);
	}
	public void run() throws IOException {
		while (true) {
			try {
				this.send(InetAddress.getLocalHost().getHostAddress());
				Thread.sleep(2000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

