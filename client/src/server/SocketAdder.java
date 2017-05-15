package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.Constants;
import util.Log;
import util.SocketWrapper;

public class SocketAdder extends Thread {
	
	private ArrayList<SocketWrapper> children;
	private ServerSocket socket;
	private Server server;
	
	public SocketAdder(ArrayList<SocketWrapper> children, Server server) {
		this.children = children;
		try {
			socket = new ServerSocket(Constants.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.server = server;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket s = socket.accept();
				Log.log.blankLine();
				Log.log.newLine("New connection from " + s.getInetAddress().getHostAddress());
				Log.log.blankLine();
				synchronized(children) {
					SocketWrapper w = new SocketWrapper(s);
					w.addNoConnectionListener(new NoConnectionListener() {
						public void response(Exception e) {
							System.out.println("\n\n" + children.size());
							children.remove(w);
							System.out.println("\n\n" + children.size());
							server.moveFromUncompletedToUnassigned(w);
							try {
								w.dispose();
								if(server.getAdmins().contains(w)) {
									Log.log.blankLine();
									Log.log.newLine("Admin at " + w.getInetAdress() + " disconnected.");
									Log.log.blankLine();
								}
								else {
									Log.log.blankLine();
									Log.log.newLine("Client at " + w.getInetAdress() + " disconnected.");
									Log.log.blankLine();
								}
								
								w.join();
								if(children.size() == 0)
									NetworkManager.network.initalizeClient();
							} catch (InterruptedException e1) {
								Log.log.addError(e1);
							}
							if(server.getAdmins().contains(w))
								server.getAdmins().remove(w);
						}
					});
					w.addMessageListener(new MessageListener() {
						public void messageRecieved(Object o) {
							synchronized(server) {
								server.handleMessage(o, w);
							}
						}
					});
					w.sendMessage(server.getParameters());
					children.add(w);
					server.assignJob(w);
					server.assignJob(w);
					Log.log.newLine("User from " + w.getInetAdress() + " added to list.");
					NetworkManager.network.newClientConnection(s.getInetAddress().getHostAddress());
				}
			} catch (IOException e) {
				Log.log.addError(e);
			}
		}
	}

}
