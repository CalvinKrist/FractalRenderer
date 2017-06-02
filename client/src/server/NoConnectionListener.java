package server;

/**
 * This interface defines behavior for when a SocketWrapper tries to connect to the socket at the other end and fails 
 * to do so. In such a situatin, it will execute the code contained in the response(Exception e) method
 */
public interface NoConnectionListener {
	
	public void response(Exception e);

}
