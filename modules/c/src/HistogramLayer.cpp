#include "HistogramLayer.h"
#include <iostream>
#include <algorithm>
#include <cmath>

struct IterPair {
	double sn;
	int index;
};

bool compareIterPair(IterPair p1, IterPair p2) {
	return p1.sn < p2.sn;
}

void HistogramLayer::render(unsigned char** image, double rX, double rY, int width, int height, double viewportWidth) {
	double viewportHeight = height * viewportWidth / width;
	
	std::vector<IterPair> itersList;
	
	int maxIterations = 200;
	int bailout = 256;
	
	for(int r = 0; r < height; r++)
		for(int c = 0; c < width; c ++) {
			int index = (r * width + c) * 4;
			
			// Convert pixel coordinates to real world coordinates
			double x = rX + ((double)c / width - 0.5) * viewportWidth;
			double y = rY + ((double)r / height - 0.5) * viewportHeight;
									
			double z = 0;
			double zi = 0;
			int iterations = 0;

			//run the actual iterative Mandelbrot algorithm
			double newz;
			for (iterations = 0; iterations < maxIterations && (z * z) + (zi * zi) < bailout * bailout; iterations++) {
				newz = (z * z) - (zi * zi) + x;
				zi = 2 * z * zi + y;
				z = newz;
			}
			
			Color col;
			if(iterations == maxIterations) {
				col = palette->getInteriorColor();
				(*image)[index] = col.r;
				(*image)[index + 1] = col.g;
				(*image)[index + 2] = col.b;
				(*image)[index + 3] = 255;
			}
			else {
				// Calculate a normalized fractional value of 1 - ln(log2(abs(z)))
				double zSquared = zi * zi + z * z;				
				double sn = iterations - std::log2(std::log2(zSquared)) + 4.0;
				
				IterPair pair = {sn, index};
				itersList.push_back(pair);
			}
		}

	std::sort(itersList.begin(), itersList.end(), compareIterPair);
	for(int i = 0; i < itersList.size(); i++) {
		double frac = (double)i / itersList.size();
		Color col = palette->colorAt(frac);
		(*image)[itersList[i].index] = col.r;
		(*image)[itersList[i].index + 1] = col.g;
		(*image)[itersList[i].index + 2] = col.b;
		(*image)[itersList[i].index + 3] = (int)(palette->opacityAt(frac) * 255);
	}
}

std::string HistogramLayer::toString() {
	return "histogramLayer";
}