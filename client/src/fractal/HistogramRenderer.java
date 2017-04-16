package fractal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import util.Constants;
import util.Point;
import util.Utils;

public class HistogramRenderer {	
	
	//TODO: turn palate from Color[] to int[]
	public static int[][] renderHistogramColoring(int width, int height, int rWidth, int rHeight, double xPos, double yPos, int maxIterations, Color[] palate) {

		double[][] info = new double[width * height][4];
		int o = 0;
		
		int nonEscaped = 0;
		
		for(int i = 0; i < width; i++)
			for(int k = 0; k < height; k++) {
				double x = i / (width / rWidth) + xPos - rWidth / 2;
				double y = k / (height / rWidth) - + yPos - rWidth / 2;
				
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
		
		int total = width * height - nonEscaped;
		
		int[][] result = new int[width][height];
		for(int i = 0; i < info.length; i++) {
			float hue = (i) / (float)total;
			if((int)(info[i][3]) == maxIterations) 
				result[(int)(info[i][1])][(int)(info[i][2])] = Color.black.getRGB();
			else {
				int index = (int)(hue * palate.length - 1);
				index = Math.abs(index);
				result[(int)(info[i][1])][(int)(info[i][2])] = palate[index].getRGB();
			}
		}
		return result;
	}

}
