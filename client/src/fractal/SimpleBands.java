package fractal;

import java.awt.Color;
import java.util.ArrayList;

import util.MinMax;
import util.Parameters;

public class SimpleBands extends Layer {

	@Override
	protected void render(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos,
			double yPos) {
		double inv_width = 1.0 / width;
		double inv_height = 1.0 / height;
		MinMax track = new MinMax();
		ArrayList<int[]> list = new ArrayList<int[]>();
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
				
				if(iterations != maxIterations)
					track.update(iterations);
				int[] temp = {i, k, iterations};
				list.add(temp);
			}
		for(int[] temp : list) {
			if(temp[2] == maxIterations)
				pixels[temp[0]][temp[1]] = palette.getBackground();
			else
				pixels[temp[0]][temp[1]] = palette.colorAt((temp[2] - track.min) / track.max);
		}
		
	}

	@Override
	protected void calculateIterations(double rWidth, double rHeight) {

	}

	@Override
	protected void calculateBailout(double rWidth, double rHeight) {

	}

	@Override
	public Parameters getParameters() {

		return new Parameters();
	}

	@Override
	public void setParameters(Parameters newProperties) {

	}
	
	

}
