package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import util.DataTag;
import util.Log;

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
	
	private Log log;
	
	private Socket socket;
	
	public volatile boolean running = true;
	
	public SocketWrapper(Socket s, Log log) throws IOException {
		this.log = log;
		objOut = new ObjectOutputStream(s.getOutputStream());
		objIn = new ObjectInputStream(s.getInputStream());
		inetAdress = s.getInetAddress().getHostAddress();
		this.socket = s;
		
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
	
	public void sendMessage(Serializable m) {
		try {
			objOut.writeObject(m);
			objOut.flush();
			if(m instanceof DataTag && ((DataTag)m).getId().equals("log"))
				log.newLine("Log sent to " + inetAdress);
			else
				log.newLine(m.getClass().getSimpleName() + " sent to " + inetAdress + ": " + m.toString());
		} catch (IOException e) {
			System.out.println("no response");
			log.addError(e);
			connectListener.response(e);
		}
	}
	
	public void addNoConnectionListener(NoConnectionListener l) {
		connectListener = l;
	}
	
	public void addMessageListener(MessageListener m) {
		messageListener = m;
	}
	
	public void run() {
		while(running) {
			try {
				Object m = objIn.readObject();
				System.out.println(m);
				messageListener.messageRecieved(m);
				if(m instanceof DataTag && ((DataTag)m).getId().equals("log"))
					log.newLine("Log recieved from " + inetAdress);
				else
					log.newLine(m.getClass().getSimpleName() + " recieved from " + inetAdress + ": " + m.toString());
			} catch (IOException e) {
				log.addError(e);
				System.out.println();
				connectListener.response(e);
			} catch(ClassCastException e) {
				log.addError(e);
			} catch(ClassNotFoundException e) {
				log.addError(e);
			}
		}
		try {
			objIn.close();
			objOut.close();
			socket.close();
		} catch (IOException e1) {
			log.addError(e1);
		}
		
		log.newLine("SocketWrapper sucesfully closed.");
	}
	
	public String getInetAdress() {
		return inetAdress;
	}
	
	public InetAddress getInet() {
		return socket.getInetAddress();
	}
	
	public void close() {
		running = false;
	}
	
}
