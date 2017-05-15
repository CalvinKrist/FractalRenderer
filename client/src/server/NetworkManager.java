package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import util.Log;
import util.Utils;

/**
 *manages whether there should be an active client, an active server, or both
 */
public class NetworkManager {
	
	private Server server;
	private Client client;
	
	public static final NetworkManager network = new NetworkManager();
	
	private NetworkManager() {}
	
	public void init() {
		Log.log.setLogLevel(Log.LEVEL_LOG);
		Log.log.setPrintLevel(Log.LEVEL_LOG);
		Log.log.setPrintStream(System.out);
		client = new Client();
	}
	
	/**
	 * switches from a client to a server
	 */
	public void clientToServer() {
		if(client != null) {
			client.killClient();
			client = null;
		}

		Log.log.blankLine();
		Log.log.newLine("Killing client. Creating server.");
		Log.log.blankLine();
		
		server = new Server();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			Log.log.addError(e);
		}
		client = new Client();

	}
	
	/**
	 * Will close the client if the local server gets a new connection 
	 * from another computer and the local client is running
	 * @param ipAdress the ipAdress of the new connection
	 */
	public void newClientConnection(String ipAdress) {
		Log.log.blankLine();
		Log.log.newLine("NEW CLIENT CONNECTION");
		Log.log.blankLine();
		try {
			if(!(ipAdress.equals(InetAddress.getLocalHost().getHostAddress()) || ipAdress.equals(Utils.getExternalIP())) && client != null || client != null) {
				Log.log.newLine("Closing local client due to new connection.");
				client.killClient();
				client = null;
			}
		} catch (IOException e) {
			Log.log.addError(e);
		}
	}
	
	public void initalizeClient() {
		if(client != null)
			Log.log.addError(new IllegalStateException("Client is not null."));
		Log.log.newLine("All clients left the server. Initializing local client.");
		client = new Client();
	}
	
	/**
	 * Clients call this to ensure they connect to any active instances of a server running 
	 * on this computer. Else, the server is closed.
	 * @param ipAdress of the server the client connected to
	 */
	public void clientConnection(String ipAdress) {
		try {
			if(server!= null && !InetAddress.getLocalHost().getHostAddress().equals(ipAdress)) {

				server.killServer();
				server = null;
				Log.log.blankLine();
				Log.log.newLine("Killing local server. Client connected to another server.");
				Log.log.blankLine();
			} 
		} catch(IOException e) {
			Log.log.addError(e);
		}

	}
	
	public static void main(String args[]) {
		NetworkManager.network.init();
	}

}
