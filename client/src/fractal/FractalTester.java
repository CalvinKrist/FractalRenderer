package fractal;

import java.awt.Dimension;
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
		manager.setScreenResolution(new Dimension(1000, 200));
		manager.setZoom(manager.getZoom());
		manager.render("TEST", "test");

	}

}
