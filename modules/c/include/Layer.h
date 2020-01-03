#pragma once

#include "Palette.h"
#include <string>

class Layer {
	public:
		Layer();
		~Layer();
		
		/**
		@param image pointer to a buffer to store the rendered image
		@param x real world x value where the camera is centered
		@param y real world y value where the camera is centered
		@param width pixel width of the buffer
		@param height pixel height of the buffer
		@param viewportWidth width of the camera in real world coordinates
		*/
		virtual void render(unsigned char** image, double x, double y, int width, int height, double viewportWidth) = 0;
		virtual std::string toString() = 0;
	
		// Getters and setters
		float getOpacity();
		void setOpacity(float opacity);
<<<<<<< HEAD:fractals/include/Layer.h
		Palette getPalette();
		void setPalette(Palette palette);
=======
		Palette* getPalette();
		void setPalette(Palette* palette);
>>>>>>> 0a6296437186bed5ea7f0f1be53cecb5f75c74f2:modules/c/include/Layer.h
		bool isVisible();
		void setVisible(bool isVisible);
		std::string getName();
		void setName(std::string newName);
		
	protected:
		float opacity;
		Palette * palette;
		bool visible;
		
		std::string name;
};

// python also needs to include name