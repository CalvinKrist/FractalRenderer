#pragma once

#include "Color.h"
#include "Palette.h"
#include <string>

class Layer {
	public:
		Layer();
		~Layer();
		
		virtual Color** render(double x, double y, int width, int height, double zoom) = 0;
		virtual std::string toString() = 0;
	
		// Getters and setters
		float getOpacity();
		void setOpacity(float opacity);
		Palette getPalette();
		void setPalettte(Palette palette);
		bool isVisible();
		void setVisible(bool isVisible);
		std::string getName();
		void setName(std::string newName);
		
	private:
		float opacity;
		Palette palette;
		bool visible;
		
		std::string name;
};

// python also needs to include name