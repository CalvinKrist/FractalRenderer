package fractal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class FractalTester {

	public static void main(String[] args) {
		
		try {
			try {
				Layer.initializeFractalRegistry();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		RenderManager manager = new RenderManager();
		manager.render("TEST", "test");

	}

}
