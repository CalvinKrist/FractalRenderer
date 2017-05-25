package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import util.Utils;

public class BroadcastClient {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		DatagramSocket socket = new DatagramSocket();
		socket.setBroadcast(true);
		byte[] message = "JOIN_REQUEST".getBytes();
		
		DatagramPacket packet = new DatagramPacket(message, message.length, Utils.getBroadcastAddress(), 8888);
		socket.send(packet);
		
		System.out.println("creating socket");
		ServerSocket s = new ServerSocket(6666);
		Socket server = s.accept();
		System.out.println("socket created");
		System.out.println("getting data");
		while(true) {
			System.out.println(server.getInputStream().read());
		}
		
	}

}
