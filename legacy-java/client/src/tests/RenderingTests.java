package tests;

import fractal.Layer;
import fractal.RenderManager;

public class RenderingTests {
	
	public static void histogramTest(int count) {
		
		Layer.initializeDefaultFractalRegistry();
		RenderManager m = new RenderManager();
		for(int i = 0; i < count; i++) {
			System.out.println("Running render " + i);
			m.render();
		}
		
	}
	
	public static void main(String args[]) {
		histogramTest(1000);
	}

}
