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
				server.getLog().blankLine();
				server.getLog().newLine("New connection from " + s.getInetAddress().getHostAddress());
				server.getLog().blankLine();
				synchronized(children) {
					SocketWrapper w = new SocketWrapper(s, server.getLog());
					w.addNoConnectionListener(new NoConnectionListener() {
						public void response(Exception e) {
							children.remove(w);
							server.moveFromUncompletedToUnassigned(w);
							w.close();
							if(server.getAdmins().contains(w)) {
								server.getLog().blankLine();
								server.getLog().newLine("Admin at " + w.getInetAdress() + " disconnected.");
								server.getLog().blankLine();
							}
							else {
								server.getLog().blankLine();
								server.getLog().newLine("Client at " + w.getInetAdress() + " disconnected.");
								server.getLog().blankLine();
							}
							//if(children.size() == 0)
								//NetworkManager.network.initalizeClient();
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
					server.getLog().newLine("User from " + w.getInetAdress() + " added to list.");
					//NetworkManager.network.newClientConnection();
				}
			} catch (IOException e) {
				server.getLog().addError(e);
			}
		}
	}

}
