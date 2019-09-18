package server;

import util.Log;

/**
 * This class describes a node in the network. This includes servers and clients.
 * @author Calvin
 *
 */
public abstract class NetworkNode {
	
	protected Log log;
	
	/**
	 * This method is called to close the network node completely
	 */
	public abstract void kill();
	
	/**
	 * Returns the log the network node uses
	 * @return the log the network node uses
	 */
	public Log getLog() {
		return log;
	}
	
}
