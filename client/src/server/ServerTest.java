package server;

import util.Log;

public class ServerTest {

	public static void main(String[] args) {
		Log log = new Log();
		log.setLogLevel(Log.LEVEL_LOG);
		log.setPrintLevel(Log.LEVEL_LOG);
		log.setPrintStream(System.out); 
		Server s = new Server(log);
	}

}
