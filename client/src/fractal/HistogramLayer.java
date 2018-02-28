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
		ArrayList[] lists = generateHistogram(pixels, width, height, rWidth, rHeight, xPos, yPos);
		ArrayList<double[]> info = lists[0];
		ArrayList<int[]> max = lists[1];
		
		for (int[] pos : max) 
			pixels[pos[0]][pos[1]] = palette.getBackground();
		
		double inverse_size = 1.0 / info.size();
		for(int i = 0; i < info.size(); i++) {
			double hue = i * inverse_size;
			pixels[(int) (info.get(i)[1])][(int) (info.get(i)[2])] = palette.colorAt(hue);
		}
	}
	
	/***
	 * This method runs the actual iterative Mandelbrot algorithm. Afterwards, it separates elements that reached max iterations
	 * from those that didn't. It then sorts the non-max colors and returns the two lists in an array, where the non-max list is first
	 * and the max list is second. 
	 * @param pixels the array of pixel data the layer draws itself to
	 * @param width the width of the image created in pixels
	 * @param height the height of the image created in pixels
	 * @param rWidth the width of the viewport being drawn in real coordinates
	 * @param rHeight the height of the viewport being drawn in real coordinates
	 * @param xPos the x position the viewport is centered on in real coordinates
	 * @param yPos the y position the viewport is centered on in real coordinates
	 * @return an array containing the list of max iteration data points and a list of other data points
	 */
	private ArrayList[] generateHistogram(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {
		ArrayList<double[]> info = new ArrayList<double[]>(width * height / 2);
		ArrayList<int[]> max = new ArrayList<int[]>(width * height / 4); //seperate the maximums from others for optimization purposes
		double inv_width = 1.0 / width;
		double inv_height = 1.0 / height;
		for (int i = 0; i < width; i++)
			for (int k = 0; k < height; k++) {
				//Convert pixel coordinates to real world coordinates
				double x = (i * inv_width) * rWidth * 2 - rWidth + xPos;
				double y = (k  * inv_height) * rHeight * 2 - rHeight - yPos;
				double z = 0;
				double zi = 0;
				int iterations = 0;

				//run the actual iterative Mandelbrot algorithm
				double newz;
				for (iterations = 0; iterations < maxIterations
						&& (z * z) + (zi * zi) < bailout * bailout; iterations++) {
					newz = (z * z) - (zi * zi) + x;
					zi = 2 * z * zi + y;
					z = newz;
				}
				
				if(iterations == maxIterations) {
					
					int[] loc = new int[2];
					loc[0] = i;
					loc[1] = k;
					max.add(loc);
					
				} else {
					double zSquared = zi * zi + z * z;
	
					double logZ = Math.log(zSquared) * 0.5;
					logZ = Math.abs(logZ);
	
					double n = iterations - Math.log(logZ) / Math.log(2);
	
					double[] d = new double[3];
					d[0] = n;
					d[1] = (double) i;
					d[2] = (double) k;
					info.add(d);
				}
			}
		info.sort(new Comparator<double[]>() {
			public int compare(double[] o1, double[] o2) {
				int i = o1[0] > o2[0] ? 1 : o1[0] == o2[0] ? 0 : -1;
				return i;
			}
		});
		ArrayList[] lists = {info, max};
		return lists;
	}
	
	protected void calculateIterations(double rWidth, double rHeight) {
		maxIterations = 10000;
	}

	@Override
	protected void calculateBailout(double rWidth, double rHeight) {
		bailout = 10000;
	}

}
