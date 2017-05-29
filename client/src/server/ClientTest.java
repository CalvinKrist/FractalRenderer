package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import fractal.Layer;
import fractal.RenderManager;

public class ClientTest {

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		
		/*Layer.initializeFractalRegistry();
		Log log = new Log();
		log.setPrintStream(System.out);
		log.setLogLevel(Log.LEVEL_ERROR);
		log.setPrintLevel(Log.LEVEL_ERROR);
		Client c = new Client();
		c.init(log); */
		Layer.initializeFractalRegistry();
		RenderManager m = new RenderManager();
		int[][] pixels = m.render();
		for(int i = 0; i < pixels.length; i++)
			for(int k = 0; k < pixels[i].length; k++)
				System.out.println(pixels[i][k]);
		m.render("textures", "temp");
	}

}
