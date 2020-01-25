#include "SimpleBandsLayer.h"
#include <iostream>

void SimpleBandsLayer::render(unsigned char** image, double rX, double rY, int width, int height, double viewportWidth) {
	double viewportHeight = height * viewportWidth / width;
	
	for(int r = 0; r < height; r++)
		for(int c = 0; c < width; c ++) {
			int index = (r * width + c) * 4;
			
			// Convert pixel coordinates to real world coordinates
			double x = rX + ((double)c / width - 0.5) * viewportWidth;
			double y = rY + ((double)r / height - 0.5) * viewportHeight;
									
			double z = 0;
			double zi = 0;
			int iterations = 0;
			int maxIterations = 100;
			int bailout = 2;

			//run the actual iterative Mandelbrot algorithm
			double newz;
			for (iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < bailout * bailout; iterations++) {
				newz = (z * z) - (zi * zi) + x;
				zi = 2 * z * zi + y;
				z = newz;
			}
			
			Color col;
			int opacity = 0;
			if(iterations == maxIterations) {
				col = palette->getInteriorColor();
				opacity = 255;
			}
			else {
				double frac = (double)iterations / maxIterations;
				opacity = (int)(palette->opacityAt(frac) * 255);
				col = palette->colorAt(frac);
			}
			
			(*image)[index] = col.r;
			(*image)[index + 1] = col.g;
			(*image)[index + 2] = col.b;
			(*image)[index + 3] = opacity;
		}		
}

std::string SimpleBandsLayer::toString() {
	return "SimpleBandsLayer";
}