package server;

import com.dropbox.core.DbxException;

import dropbox.DatabaseCommunicator;
import util.Constants;
import util.Log;

public abstract class NetworkNode {
	
	protected DatabaseCommunicator database;
	protected Log log;
	
	public NetworkNode(Log log) {
		this.log = log;
		try {
			database = new DatabaseCommunicator(Constants.DATABASE_KEY);
		} catch (DbxException e) {
			log.addError(e);
		}
	}
	
	public abstract void kill();
	
	public Log getLog() {
		return log;
	}
	
}
