package server;

import java.io.IOException;
import java.net.Socket;

import util.Constants;
import util.SocketWrapper;

public class Client extends Thread {

	/**
	 * the job the client is assigned
	 */
	private Job job;
	
	/**
	 * a wrapper for communicating with the server
	 */
	private SocketWrapper server;
	
	public Client() {
		//TODO: get server ip
		String serverIP = "";
		try {
			server = new SocketWrapper(new Socket(serverIP, Constants.PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	

}
