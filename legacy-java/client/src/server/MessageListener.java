package server;

/**
 * Defines an interface for defining behavior when a socket recieves a message. SocketWrappers, when they receive a 
 * messagem will execute the code contained the messageRecieved(Object m) method.
 * @author 1355710
 *
 */
public interface MessageListener {

	public void messageRecieved(Object m);

}
