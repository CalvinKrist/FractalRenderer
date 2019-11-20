#pragma once

#include "Layer.h"
#include <vector>

/*
A fractal consists of a series of different layers as 
well as a viewport through which they are viewed.
*/
class Fractal {
	public:
		Fractal();
		~Fractal();
		
		/*
		Takes width and height in pixels of rendered image
		*/
		Color** render(int width, int height);
		
		// Layer manipulation functions
		Layer * getLayer(int index);
		void addLayer(Layer layer);
		Layer * removeLayer(int index);
		void insertLayer(int index, Layer layer);
		Layer ** getLayers();
		
		// Getters and setters
		double getX();
		void setX(double x);
		double getY();
		void setY(double y);
		double getViewportWidth();
		void setViewportWidth(double width);
		
	private:
		// Viewport parameters
		double x;
		double y;
		double viewportWidth;
		
		std::vector<Layer> layers;
};