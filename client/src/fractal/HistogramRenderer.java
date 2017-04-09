package fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import util.Constants;
import util.Point;
import util.Utils;

public class HistogramRenderer {
	
	private Point location; //point in real world coordinates the fractal is centered on
	private float rWidth; //real world coordinate width of displayed fractal
	private int dimension; //width and height of the window in pixels
	private int maxIterations;
	
	private Color[] palate;
	
	public HistogramRenderer() {
		
		rWidth = 4;
		location = new Point(0, 0);
		this.dimension = Constants.WIDTH;
		maxIterations = 1000;

		palate = Utils.getColorPalate();
	}
	
	public void newLocation(Point clicked) { 
		double x = clicked.getX() * rWidth / dimension; //scaled x
		double y = clicked.getY() * rWidth / dimension; //scaled y
		
		x -= rWidth / 2 - location.getX();
		y -= rWidth / 2 + location.getY();
		y = -y;
		
		location = new Point(x, y);
		
		rWidth /= 2;
		System.out.println(maxIterations);
		
		System.out.println(x + ", " + y);
	}
	
	public void render(Graphics2D g) {
		//TODO: define maxIterations as a function of rWidth so we never loose definition
			//define it up here to the iterationCount still works. also to save computations
		double zoom = 1.0 / rWidth;
		System.out.println("zoom: " + zoom);
		//maxIterations = (int)(Math.pow(Math.log(zoom), 4));
		//if(maxIterations < 400)
			//maxIterations = 400;
		renderHistogramColoring(g);
	}
	
	private void renderHistogramColoring(Graphics2D g) {
		System.out.println(maxIterations);
		g.setColor(Color.black);
		g.fillRect(0, 0, Constants.WIDTH, Constants.WIDTH);
		
		double[][] info = new double[dimension * dimension][4];
		int o = 0;
		
		int nonEscaped = 0;
		
		for(int i = 0; i < dimension; i++)
			for(int k = 0; k < dimension; k++) {
				double x = i / (dimension / rWidth) + location.getX() - rWidth / 2;
				double y = k / (dimension / rWidth) - + location.getY() - rWidth / 2;
				
				double z = 0;
				double zi = 0;
				int iterations = 0;
				
				double newz;
			
				for(iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < Constants.BAILOUT; iterations++) {
					newz = (z * z) - (zi * zi) + x;
					zi = 2 * z * zi + y;
					z = newz;
				}
				
				if(iterations == maxIterations) 
					nonEscaped++;

				double zSquared = zi * zi + z * z;
				
				double logZ = Math.log(zSquared) / 2;
				logZ = Math.abs(logZ);
				
				double n = iterations - Math.log(logZ) / Math.log(2); 
	
				double[] d = new double[4];
				d[0] = n;
				d[1] = (double)i;
				d[2] = (double)k;
				d[3] = iterations;
				info[o++] = d;
			}
		
		Utils.mergeSort(info);
		
		int total = dimension * dimension - nonEscaped;
		
		for(int i = 0; i < info.length; i++) {
			float hue = (i) / (float)total;
			if((int)(info[i][3]) == maxIterations) 
				g.setColor(Color.BLACK);
			else {
				int index = (int)(hue * palate.length - 1);
				index = Math.abs(index);
				g.setColor(palate[index]);
			}

			g.fillRect((int)(info[i][1]), (int)(info[i][2]), 1, 1);
		}
	}

}
