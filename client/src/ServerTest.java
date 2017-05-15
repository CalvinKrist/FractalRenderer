import server.Server;
import util.Log;

public class ServerTest {

	public static void main(String[] args) {
		Log.log.setLogLevel(Log.LEVEL_LOG);
		Log.log.setPrintLevel(Log.LEVEL_LOG);
		Log.log.setPrintStream(System.out);
		Server s = new Server();
	}
	
}
