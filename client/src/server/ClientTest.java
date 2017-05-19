package server;

import util.Log;

public class ClientTest {

	public static void main(String[] args) {
		Log log = new Log();
		log.setLogLevel(Log.LEVEL_LOG);
		log.setPrintLevel(Log.LEVEL_LOG);
		log.setPrintStream(System.out);
		Client c = new Client(log);
	}

}
