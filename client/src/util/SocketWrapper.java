package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A class to simplify communication 
 * 
 * @author Calvin
 *
 */
public class SocketWrapper {
	
	/**
	 * the stream used to send data to the other socket
	 */
	private OutputStream output;
	
	/**
	 * the stream used to receive input from the other socket
	 */
	private InputStream input;
	
	public SocketWrapper(Socket s) throws IOException{
		output = s.getOutputStream();
		input = s.getInputStream();
	}

	/**
	 * clears up the data stored by the client
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		output.close();
		input.close();
	}
	
}
