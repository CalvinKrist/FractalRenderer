package fractal;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;

import util.Parameters;

public class MyLayer extends Layer {

	@Override
	protected void render(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos,
			double yPos) {
		
		for(int i = 0; i < width; i++)
			for(int k = 0; k < height; k++) {
				double x = (i / (double)width) * rWidth * 2 - rWidth + xPos;
				double y = (k / (double)height) * rHeight * 2 - rHeight - yPos;
				
				double z = 0; //The real component of z
			    double zi = 0; //The imaginary component of z
			    int iterations = 0; //The number of times the function as iterated
			    double newz; //A placeholder that we will use in the iterative function to update the z and zi values.
			    
			    for (iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < bailout * bailout; iterations++) {
					newz = (z * z) - (zi * zi) + x;
					zi = 2 * z * zi + y;
					z = newz;
				}
			    pixels[i][k] = iterations == maxIterations ? Color.black : Color.white;
			}
		
	}

	@Override
	protected void calculateIterations(double rWidth, double rHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void calculateBailout(double rWidth, double rHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Parameters getParameters() {
		Parameters props = new Parameters(new HashMap<String, Serializable>());
		props.put("maxIterations", 100);
		return props;
	}

	@Override
	public void setParameters(Parameters newProperties) {
		this.maxIterations = Integer.valueOf(newProperties.getParameter("maxIterations", String.class));
	}

}
