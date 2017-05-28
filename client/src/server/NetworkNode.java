package server;

import util.Log;

public abstract class NetworkNode {
	
	protected Log log;
	
	public abstract void kill();
	
	public Log getLog() {
		return log;
	}
	
}
