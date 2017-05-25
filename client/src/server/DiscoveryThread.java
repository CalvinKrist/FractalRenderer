package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import util.Log;

public class DiscoveryThread implements Runnable {
	
	private Log log;
		
	public DiscoveryThread(Log log) {
		this.log = log;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			while(true) {
				log.newLine("Ready to recieve broadcast packets.");
				
				byte[] buffer = new byte[15000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				log.newLine("Packet recieved from " + packet.getAddress().getHostAddress());
				String message = new String(packet.getData());
				log.newLine("Packet recieved. Data: " + message);
				log.blankLine();
				
				//if(message.equals("JOIN_REQUEST")) {
					System.out.println("creating socket");
					Socket s = new Socket(packet.getAddress().getHostAddress(), 6666);
					System.out.println("socket created");
					System.out.println("sending data");
					s.getOutputStream().write("hey".getBytes());
					System.out.println("data send");
				//}
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Log l = new Log();
		l.setLogLevel(Log.LEVEL_LOG);
		l.setPrintLevel(Log.LEVEL_LOG);
		l.setPrintStream(System.out);
		Thread t = new Thread(new DiscoveryThread(l));
		t.start();
	}

}
