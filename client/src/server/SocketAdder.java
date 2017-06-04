package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.Constants;
import util.Log;

public class SocketAdder extends Thread {

	private ArrayList<SocketWrapper> children;
	private Server server;
	/**
	 * This varialbe is used to stop the thread. The main logic loop within the run() method will continue running
	 * until this is set to false.
	 */
	public volatile boolean running = true;

	public SocketAdder(ArrayList<SocketWrapper> children, Server server) {
		this.children = children;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			DatagramSocket sock = new DatagramSocket(Constants.BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
			sock.setBroadcast(true);
			while (running) {
				byte[] buffer = new byte[15000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				sock.receive(packet);

				// TODO: only create socket if the data matches a certain string

				Socket s = new Socket(packet.getAddress().getHostAddress(), Constants.PORT);

				server.getLog().blankLine();
				server.getLog().newLine("New connection from " + s.getInetAddress().getHostAddress());
				server.getLog().blankLine();
				synchronized (children) {
					SocketWrapper w = new SocketWrapper(s, server.getLog());
					w.addNoConnectionListener(new NoConnectionListener() {
						public void response(Exception e) {
							children.remove(w);
							server.moveFromUncompletedToUnassigned(w);
							w.close();
							server.getLog().blankLine();
							server.getLog().newLine("Client at " + w.getInetAdress() + " disconnected.");
							server.getLog().blankLine();
						}
					});
					w.addMessageListener(new MessageListener() {
						public void messageRecieved(Object o) {
							synchronized (server) {
								server.handleMessage(o, w);
							}
						}
					});
					w.sendMessage(server.getFractal());
					children.add(w);
					server.assignJob(w);
					server.assignJob(w);
					server.getLog().newLine("User from " + w.getInetAdress() + " added to list.");
					if (server.getDisplay() != null)
						server.getDisplay().updateNetworkView(children, server.getUncompletedJobs());
				}

			}
		} catch (IOException e) {
			server.getLog().addError(e);
		}
	}

}
