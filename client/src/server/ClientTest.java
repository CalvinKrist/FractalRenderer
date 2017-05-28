package server;

import util.Log;

public class ClientTest {

	public static void main(String[] args) {

		Log log = new Log();
		log.setLogLevel(Log.LEVEL_ERROR);
		log.setPrintLevel(Log.LEVEL_ERROR);
		Client c = new Client(log);
		
	}

}
