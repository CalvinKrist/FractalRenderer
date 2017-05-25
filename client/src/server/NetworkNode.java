package server;

import util.Log;

public abstract class NetworkNode {
	
	protected Log log;
	
	public NetworkNode(Log log) {
		this.log = log;
	}
	
	public abstract void kill();
	
	public Log getLog() {
		return log;
	}
	
}
