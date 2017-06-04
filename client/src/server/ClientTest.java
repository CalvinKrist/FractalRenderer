package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import fractal.Layer;
import fractal.RenderManager;
import util.Log;

public class ClientTest {

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		Layer.initializeFractalRegistry();
		Log log = new Log();
		log.setPrintStream(System.out);
		log.setLogLevel(Log.LEVEL_LOG);
		log.setPrintLevel(Log.LEVEL_ERROR);
		Client c = new Client();
		c.init(log); 
	}

}
