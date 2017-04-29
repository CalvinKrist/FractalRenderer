package fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import util.Utils;

public class HistogramRenderer extends Renderer {
	
	public HistogramRenderer() {
		bailout = 1000;
		maxIterations = 400;
	}

	public void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos) {
		calculateIterationsAndBailout(rWidth, rHeight);
		ArrayList<double[]> info = new ArrayList<double[]>(width * height);

		for (int i = 0; i < width; i++)
			for (int k = 0; k < height; k++) {
				double x = i / (width / rWidth) + xPos - rWidth / 2;
				double y = k / (height / rWidth) - +yPos - rWidth / 2;

				double z = 0;
				double zi = 0;
				int iterations = 0;

				double newz;

				for (iterations = 0; iterations < maxIterations
						&& (z * z) + (zi * zi) < bailout; iterations++) {
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
				if(layer == 0) 
					pixels[(int) (info.get(i)[1])][(int) (info.get(i)[2])] = palette[0].getRGB();
				else {
					Color c1 = new Color(pixels[(int)info.get(i)[1]][(int)info.get(i)[2]]);
					Color c2 = palette[0];
					double weight = c2.getAlpha() / 255.0;
					Color finalColor = new Color((int)layerAverage(c1.getRed(), c2.getRed(), weight), (int)layerAverage(c1.getGreen(), c2.getGreen(), weight), (int)layerAverage(c1.getBlue(), c2.getBlue(), weight)); 
					pixels[(int)info.get(i)[1]][(int)info.get(i)[2]] = finalColor.getRGB(); 
				}
				info.remove(i--);
			}
		
		for(int i = 0; i < info.size(); i++) {

			double hue = (double)i / info.size();
			int index = (int) (hue * (palette.length - 1)) + 1;
			if(layer == 0) 
				pixels[(int) (info.get(i)[1])][(int) (info.get(i)[2])] = palette[index].getRGB();
			else {
				Color c1 = new Color(pixels[(int)info.get(i)[1]][(int)info.get(i)[2]]);
				Color c2 = palette[index];
				double weight = c2.getAlpha() / 255.0;
				Color finalColor = new Color((int)layerAverage(c1.getRed(), c2.getRed(), weight), (int)layerAverage(c1.getGreen(), c2.getGreen(), weight), (int)layerAverage(c1.getBlue(), c2.getBlue(), weight)); 
				pixels[(int)info.get(i)[1]][(int)info.get(i)[2]] = finalColor.getRGB(); 
			}
		}
	}

	protected void calculateIterationsAndBailout(double rWidth, double rHeight) {

	}
	
	public void setZoom(double zoom) {
		zoom /= 2;
		super.setZoom(zoom);
	}
	
	private double layerAverage(double d1, double d2, double weight) {
		return (d1 * layer + d2 * weight) / (layer + weight);
	}

}
