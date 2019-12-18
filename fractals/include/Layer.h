#pragma once

#include "Color.h"
#include "Palette.h"
#include <string>

class Layer {
	public:
		Layer();
		~Layer();
	
		// Getters and setters
		float getOpacity();
		void setOpacity(float opacity);
		Palette getPalette();
		void setPalettte(Palette palette);
		bool getVisiblity();
		void setVisible(bool isVisible);
		
		virtual Color** render(double x, double y, int width, int height, double zoom) = 0;
		virtual std::string toString() = 0;
		
	private:
		float opacity;
		Palette palette;
		bool isVisible;
};

// python also needs to include name