package fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Queue;

import util.MinMax;
import util.Utils;
import util.Vector2;

public class TriangleAverageRenderer extends Renderer {

	private long bailoutSquared;
	private double magnitude, r1, r2, g1, g2, b1, b2, tweenval;
	private double realIterations;
	private int colval1, colval2;
	
	private Vector2 z, c;
	
	private double sum, sum2, ac, il, lp, az2, lowbound, f, index;
	
	private LinkedList<double[]> data;
	
	private int FPS = 30;
	private LinkedList<MinMax> bounds;
	
	//video
	int su = 0;
	
	public TriangleAverageRenderer() {
		maxIterations = 1000;
		bailout = (int)(Math.pow(10, 4));
		bailoutSquared = bailout * bailout;
		c = new Vector2();
		z = new Vector2();
		bounds = new LinkedList<MinMax>();
	}
	
	private void findColor(int[][] pixels, int id, int k) {
		int i;
		ac = c.length();
		il = 1.0 / Math.log(2);
		lp = Math.log(Math.log(bailout) / 2);
		
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
		if(i == maxIterations) {
			if(layer != 0) {
				Color c1 = new Color(pixels[id][k]);
				Color c2 = palette[0];
				double weight = c2.getAlpha() / 255.0;
				Color finalColor = new Color((int)layerAverage(c1.getRed(), c2.getRed(), weight), (int)layerAverage(c1.getGreen(), c2.getGreen(), weight), (int)layerAverage(c1.getBlue(), c2.getBlue(), weight)); 
				pixels[id][k] = finalColor.getRGB(); 
			} else
				pixels[id][k] = palette[0].getRGB();
		}
		else {
			//tia
			sum = sum / i;
			sum2 = sum2 / (i - 1.0);
			f = il * lp - il * Math.log(Math.log(z.length()));
			index = sum2 + (sum - sum2) * (f + 1.0);
			realIterations = palette.length * index;
			colval1 = (int)(realIterations % palette.length);
			colval2 = (int)((colval1 + 1) % palette.length);
			tweenval = realIterations % 1;
			if(colval1 < 0) colval1 += palette.length;
			if(colval2 < 0) colval2 += palette.length;
			double[] data = {colval1, colval2, tweenval, id, k};
			this.data.add(data);
			colVal.update(colval1);
			colVal.update(colval2);
		}
	}
	
	protected void calculateIterationsAndBailout(double rWidth, double rHeight) {
		
	}

	public void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {
		yPos = -yPos;
		calculateIterationsAndBailout(rWidth, rHeight);
		data = new LinkedList<double[]>();
		bounds.add(new MinMax());
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
				c.x = (i / (double)width) * rWidth * 2 - rWidth + xPos;
				c.y = (k / (double)height) * rHeight * 2 - rHeight + yPos;
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
			if(prop1 < 0)
				prop1 = 0;
			if(prop2 >= 1)
				prop2 = .9999999999;
			if(prop2 < 0)
				prop2 = 0;
			if(layer != 0) {
				Color c1 = new Color(pixels[(int)d[3]][(int)d[4]]);
				Color c2 = Utils.interpolateColors(palette[(int)(prop1 * (palette.length - 1)) + 1], palette[(int)(prop2 * (palette.length - 1)) + 1], d[2]);
				double weight = c2.getAlpha() / 255.0;
				Color finalColor = new Color((int)layerAverage(c1.getRed(), c2.getRed(), weight), (int)layerAverage(c1.getGreen(), c2.getGreen(), weight), (int)layerAverage(c1.getBlue(), c2.getBlue(), weight)); 
				pixels[(int)d[3]][(int)d[4]] = finalColor.getRGB(); 
			} else 
				pixels[(int)d[3]][(int)d[4]] = Utils.interpolateColors(palette[(int)(prop1 * (palette.length - 1)) + 1], palette[(int)(prop2 * (palette.length - 1)) + 1], d[2]).getRGB();
		}
	}
	
	private double layerAverage(double d1, double d2, double weight) {
		return (d1 * layer + d2 * weight) / (layer + weight);
	}
	
}












