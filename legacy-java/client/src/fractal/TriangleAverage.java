package fractal;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import util.MinMax;
import util.Parameters;
import util.Utils;
import util.Vector2;

/**
 * A layer which uses a rendering method called 'Triangle Average Inequality'. Please refer to
 * "Hrknen, Jussi: On Smooth Fractal Coloring Techniques" for more information
 * @author Calvin
 *
 */
public class TriangleAverage extends Layer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7937980458959852619L;
	private long bailoutSquared;
	private double magnitude, tweenval;
	private double realIterations;
	private int colval1, colval2;
	
	private Vector2 z, c;
	
	private double sum, sum2, ac, il, lp, az2, lowbound, f, index;
	
	private LinkedList<double[]> data;
	
	private int FPS = 30;
	private LinkedList<MinMax> bounds;
	
	//video
	int su = 0;
	
	/**
	 * Initializes the layer with some basic values. AutoBailout and AutoMaxIterations are false, is creates a 
	 * description, maxIterations is 1000, bailout is set to 10^4
	 */
	public TriangleAverage() {
		super();
		autoBailout = false;
		autoMaxIterations = false;
		description = "Uses the Triangle Average Inequality rendering method.";
		maxIterations = 1000;
		bailout = (int)(Math.pow(10, 4));
		bailoutSquared = bailout * bailout;
		c = new Vector2();
		z = new Vector2();
		bounds = new LinkedList<MinMax>();
	}
	
	/**
	 * Returns bailout and maxIterations as customizable parameters
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
			bailoutSquared = this.bailout * this.bailout;
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
	
	private void findColor(Color[][] pixels, int id, int k) {
		int i;
		ac = c.length();
		il = 1.0 / Math.log(2);
		lp = Math.log(Math.log(bailout) * 0.5);
		
		for(i = 0; i < maxIterations; i++) {
			double x = (z.x * z.x - z.y * z.y) + c.x;
			double y = (2 * z.y * z.x) + c.y;
			
			magnitude = (x * x + y * y);
			if(magnitude > bailoutSquared) break;
			
			z.x = x;
			z.y = y;
			
			//tia
			sum2 = sum;
			if(i != 0 && i != maxIterations - 1) {
				az2 = z.subtract(c).length();
				lowbound = Math.abs(az2 - ac);
				sum += (z.length() - lowbound) / (az2 + ac - lowbound);
			}
		}
		MinMax colVal = bounds.getLast();
		if(i == maxIterations) 
			pixels[id][k] = palette.getBackground();
		else {
			//tia
			sum = sum / i;
			sum2 = sum2 / (i - 1.0);
			f = il * lp - il * Math.log(Math.log(z.length()));
			index = sum2 + (sum - sum2) * (f + 1.0);
			realIterations = palette.size * index;
			colval1 = (int)(realIterations % palette.size);
			colval2 = (int)((colval1 + 1) % palette.size);
			tweenval = realIterations % 1;
			if(colval1 < 0) colval1 += palette.size;
			if(colval2 < 0) colval2 += palette.size;
			double[] data = {colval1, colval2, tweenval, id, k};
			this.data.add(data);
			colVal.update(colval1);
			colVal.update(colval2);
		}
	}

	@Override
	protected void render(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {
		yPos = -yPos;
		data = new LinkedList<double[]>();
		bounds.add(new MinMax());
		double inv_width = 1.0 / width;
		double inv_height = 1.0 / height;
		for(int i = 0; i < width; i++) {
			for(int k = 0; k < height; k++) {
				sum = 0;
				sum2 = 0;
				il = 0;
				lp = 0;
				az2 = 0;
				lowbound = 0;
				f = 0;
				index = 0;
				c.x = i * inv_width * rWidth * 2 - rWidth + xPos;
				c.y = k * inv_height * rHeight * 2 - rHeight + yPos;
				z.x = 0;
				z.y = 0;
				findColor(pixels, i, k);
			}
		}
		double min = 0;
		double max = 0;
		for(MinMax m : bounds) {
			min += m.min;
			max += m.max;
		}
		min /= bounds.size();
		max /= bounds.size();
		if(bounds.size() > FPS * 0.2)
			bounds.remove();
		for(double[] d: data) {
			double prop1 = (d[0] - min) / (max - min);
			double prop2 = (d[1] - min) / (max - min);
			if(prop1 >= 1)
				prop1 = .9999999999;
			else if(prop1 < 0)
				prop1 = 0;
			if(prop2 >= 1)
				prop2 = .9999999999;
			else if(prop2 < 0)
				prop2 = 0;
			pixels[(int)d[3]][(int)d[4]] = Utils.interpolateColors(palette.colorAt(prop1), palette.colorAt(prop2), d[2]);
		}
	}

	@Override
	protected void calculateIterations(double rWidth, double rHeight) {
		maxIterations = 10000;
	}

	@Override
	protected void calculateBailout(double rWidth, double rHeight) {
		bailout = 10000;
	}
	
}












