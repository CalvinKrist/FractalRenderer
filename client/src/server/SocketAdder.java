package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.Constants;
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
				System.out.println("New Connection.");
				synchronized(children) {
					System.out.println("Synchronized with children.");
					SocketWrapper w = new SocketWrapper(s);
					w.addNoConnectionListener(new NoConnectionListener() {
						public void response(Exception e) {
							try {
								w.dispose();
								w.join();
							} catch (IOException | InterruptedException e1) {
								e1.printStackTrace();
							}
							children.remove(w);
							server.moveFromUncompletedToUnassigned(w);
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
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
