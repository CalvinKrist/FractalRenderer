package fractal;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import util.Parameters;

/**
 * This is one of the two included layers that all installs of this program has. It sorts all the points outside the 
 * fractal based on their approximate distance from the fractal. It then assigns each point a color proportional to
 * its location in the list, such that the point at the 34% percentile would get the color at the 34% percentile of the
 * palette.
 * @author Calvin
 *
 */
public class HistogramLayer extends Layer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6444733438837306009L;

	/**
	 * Creates default bailout and maxIterations values for the layer to use. It also assigns this layer a description
	 * so that users can understand what it does in the layer editor.
	 * Bailout = 1000, maxIterations = 400;
	 */
	public HistogramLayer() {
		super();
		bailout = 1000;
		maxIterations = 400;
		description = "Uses a histogram to distribute all the aproximated distances across the palete.";
	}
	
	/**
	 *This method will return bailout and maxIterations as being editable parameters. 
	 */
	public Parameters getParameters() {
		Parameters props = new Parameters(new HashMap<String, Serializable>());
		if(autoBailout = true)
			props.put("bailout", "auto");
		else
			props.put("bailout", bailout);
		if(autoMaxIterations == true)
			props.put("maxIterations", "auto");
		else
			props.put("maxIterations", maxIterations);
		return props;
	}
	
	public void setParameters(Parameters params) {
		String bailout = params.getParameter("bailout", String.class);
		try {
			this.bailout = Integer.valueOf(bailout);
			
			autoBailout = false;
		} catch(Exception e) {
			autoBailout = true;
		}
		String maxIterations = params.getParameter("maxIterations", String.class);
		try {
			this.maxIterations = Integer.valueOf(maxIterations);
			autoMaxIterations = false;
		} catch(Exception e) {
			autoMaxIterations = true;
		}
	}

	/**
	 * This method executes the actual rendering code of the layer assuming the given parameters.
	 * It will sort all the points outside the fractal based on their approximate distance from the fractal. 
	 * It then assigns each point a color proportional to its location in the list, such that the point at the 
	 * 34% percentile would get the color at the 34% percentile of the palette.
	 * @param pixels the array of pixel data the layer draws itself to
	 * @param width the width of the image created in pixels
	 * @param height the height of the image created in pixels
	 * @param rWidth the width of the viewport being drawn in real coordinates
	 * @param rHeight the height of the viewport being drawn in real coordinates
	 * @param xPos the x position the viewport is centered on in real coordinates
	 * @param yPos the y position the viewport is centered on in real coordinates
	 */
	@Override
	protected void render(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {
		ArrayList<double[]> info = new ArrayList<double[]>(width * height);
		for (int i = 0; i < width; i++)
			for (int k = 0; k < height; k++) {
				double x = (i / (double)width) * rWidth * 2 - rWidth + xPos;
				double y = (k / (double)height) * rHeight * 2 - rHeight - yPos;
				double z = 0;
				double zi = 0;
				int iterations = 0;

				double newz;
				for (iterations = 0; iterations < maxIterations
						&& (z * z) + (zi * zi) < bailout * bailout; iterations++) {
					newz = (z * z) - (zi * zi) + x;
					zi = 2 * z * zi + y;
					z = newz;
				}
				double zSquared = zi * zi + z * z;

				double logZ = Math.log(zSquared) / 2;
				logZ = Math.abs(logZ);

				double n = iterations - Math.log(logZ) / Math.log(2);

				double[] d = new double[4];
				d[0] = n;
				d[1] = (double) i;
				d[2] = (double) k;
				d[3] = iterations;
				info.add(d);
			}
		info.sort(new Comparator<double[]>() {
			public int compare(double[] o1, double[] o2) {
				return new Double(o1[0]).compareTo(new Double(o2[0]));
			}
		});
		for (int i = 0; i < info.size(); i++) 
			if ((int) (info.get(i)[3]) == maxIterations) {
				pixels[(int) (info.get(i)[1])][(int) (info.get(i)[2])] = palette.getBackground();
				info.remove(i--);
			}
		for(int i = 0; i < info.size(); i++) {
			double hue = (double)i / info.size();
			pixels[(int) (info.get(i)[1])][(int) (info.get(i)[2])] = palette.colorAt(hue);
		}
	}
	
	protected void calculateIterations(double rWidth, double rHeight) {
		maxIterations = 10000;
	}

	@Override
	protected void calculateBailout(double rWidth, double rHeight) {
		bailout = 10000;
	}

}
