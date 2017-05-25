package fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import util.Parameters;
import util.Point;
import util.Utils;

public class FractalTester {
	
	public static void main(String[] args) {
		
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("radius", 4.0);
		map.put("location", new Point(0, 0));
		map.put("resolution", new Dimension(800, 800));
		map.put("name", "TestFractal");
		Layer layer1 = new HistogramRenderer(new Palette(Utils.getColorPalate(), Color.black), 1);
		layer1.setName("Background");
		map.put("layer1", layer1);
		
		Layer layer2 = new TriangleAverageRenderer(new Palette(Utils.getColorPalate(), Color.black), 2);
		layer2.setName("Highlights");
		map.put("layer2", layer2);
		
		RenderManager fractal = new RenderManager(new Parameters(map));
		fractal.saveFractal();
		
		
	}

}
