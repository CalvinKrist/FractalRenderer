#pragma once

#include "Layer.h"
#include <vector>
#include <string>

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
		unsigned char** render();
		
		// Layer manipulation functions
		Layer* getLayer(int index);
		Layer* removeLayer(int index);
		void insertLayer(int index, Layer* layer);
		int layerCount();
		
		// Getters and setters
		double getX();
		void setX(double x);
		double getY();
		void setY(double y);
		double getViewportWidth();
		void setViewportWidth(double width);
		int getWidth();
		void setWidth(int width);
		int getHeight();
		void setHeight(int height);
		
		std::string toString();
		
	private:
		// Viewport parameters
		double x;
		double y;
		double viewportWidth; // zoom level
		int width, height;
		
		std::vector<Layer*> layers;
		
		unsigned char* image;
		unsigned char* layerImage;
		void updateImageMemory();
};