#pragma once

#include <vector>
#include <string>

// Helper data structures
using Opacity = double;

class Color {
	public:
		Color();
		Color(unsigned char red, unsigned char green, unsigned char blue);
		
		unsigned char r,g,b;
};

struct ColorPoint {
	Color color;
	double location;
};
struct OpacityPoint {
	Opacity opacity;
	double location;
};

class Palette {
	
	public:
		Palette();
		~Palette();
		
		// Return sorted lists by location of the points
		std::vector<ColorPoint> getColors();
		std::vector<OpacityPoint> getOpacities();
		
		Color colorAt(double x);
		Opacity opacityAt(double x);
		
		bool addColor(Color color, double x);
		bool removeColor(double x);
		bool addOpacity(Opacity opacity, double x);
		bool removeOpacity(double x);
		
		// Getters and setters
		Color getInteriorColor();
		void setInteriorColor(Color newColor);
		
		std::string toString();
	
	private:
		std::vector<ColorPoint> colorList;
		std::vector<OpacityPoint> opacityList;
		Color interiorColor;
};
