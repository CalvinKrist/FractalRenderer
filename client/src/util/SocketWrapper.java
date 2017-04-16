package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import server.MessageListener;
import server.NoConnectionListener;

/**
 * A class to simplify communication 
 * 
 * @author Calvin
 *
 */
public class SocketWrapper extends Thread {
	
	/**
	 * the stream used to send data to the other socket
	 */
	protected OutputStream output;
	
	/**
	 * the stream used to receive input from the other socket
	 */
	protected InputStream input;
	
	/**
	 * 
	 */
	protected ObjectOutputStream objOut;
	
	/**
	 * 
	 */
	protected ObjectInputStream objIn;
	
	/**
	 * 
	 */
	protected NoConnectionListener connectListener;
	
	protected MessageListener messageListener;
	
	public SocketWrapper(Socket s) throws IOException {
		output = s.getOutputStream();
		objOut = new ObjectOutputStream(output);
		input = s.getInputStream();
		objIn = new ObjectInputStream(input);
		
		connectListener = new NoConnectionListener() {
			public void response(Exception e) {
				System.out.println("Disconnected.");
				e.printStackTrace();
			}
		};
		
		messageListener = new MessageListener() {
			public void messageRecieved(Object m) {
				
			}
		};
		
		this.start();
	}

	/**
	 * clears up the data stored by the client
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		output.close();
		input.close();
	}
	
	public void sendMessage(Object m) {
		try {
			objOut.writeObject(m);
			objOut.flush();
		} catch (IOException e) {
			connectListener.response(e);
			try {
				this.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void addNoConnectionListener(NoConnectionListener l) {
		connectListener = l;
	}
	
	public void addMessageListener(MessageListener m) {
		messageListener = m;
	}
	
	public void run() {
		while(true) {
			try {
				Object m = objIn.readObject();
				messageListener.messageRecieved(m);
				
			} catch (IOException | ClassNotFoundException e) {
				connectListener.response(e);
				try {
					this.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
}
