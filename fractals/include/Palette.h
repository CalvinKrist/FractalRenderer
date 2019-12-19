#pragma once

#include <vector>
#include <string>

// Helper data structures
using Opacity = double;

class Color {
	public:
		Color();
		Color(int red, int green, int blue);
		
		int r,g,b;
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
