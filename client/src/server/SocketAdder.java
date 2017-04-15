package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import util.Constants;
import util.SocketWrapper;

public class SocketAdder extends Thread {
	
	private List<SocketWrapper> children;
	private ServerSocket socket;
	
	public SocketAdder(ArrayList<SocketWrapper> children) {
		this.children = children;
		try {
			socket = new ServerSocket(Constants.PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				children.add(new SocketWrapper(socket.accept()));
				this.notify();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
