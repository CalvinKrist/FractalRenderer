package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
	
	protected String inetAdress;
	
	public SocketWrapper(Socket s) throws IOException {
		objOut = new ObjectOutputStream(s.getOutputStream());
		objIn = new ObjectInputStream(s.getInputStream());
		inetAdress = s.getInetAddress().getHostAddress();

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
	 */
	public void dispose() {
		try {
			objOut.close();
			objIn.close();
		} catch(IOException e) {
			
		}
	}
	
	public void sendMessage(Serializable m) {
		try {
			objOut.writeObject(m);
			objOut.flush();
			if(m instanceof DataTag && ((DataTag)m).getId().equals("log"))
				Log.log.newLine("Log sent to " + inetAdress);
			else
				Log.log.newLine(m.getClass().getSimpleName() + " sent to " + inetAdress + ": " + m.toString());
		} catch (IOException e) {
			connectListener.response(e);
			try {
				this.join();
			} catch (InterruptedException e1) {
				Log.log.newLine("Unable to join SocketWrapper thread.");
				Log.log.addError(e1);
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
				if(m instanceof DataTag && ((DataTag)m).getId().equals("log"))
					Log.log.newLine("Log recieved from " + inetAdress);
				else
					Log.log.newLine(m.getClass().getSimpleName() + " recieved from " + inetAdress + ": " + m.toString());
			} catch (IOException | ClassNotFoundException e) {
				connectListener.response(e);
				try {
					this.join();
				} catch (InterruptedException e1) {
					Log.log.newLine("Unable to join SocketWrapper thread.");
					Log.log.addError(e1);
				}
			} catch(ClassCastException e) {
				Log.log.addError(e);
			}
		}
	}
	
	public String getInetAdress() {
		return inetAdress;
	}
	
}
