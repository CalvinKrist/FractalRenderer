#include "Palette.h"
#include <iostream>
#include <algorithm>

Color::Color() {
	r = 255;
	g = 255;
	b = 255;
}
Color::Color(int red, int green, int blue) {
	r = red;
	g = green;
	b = blue;
}

Palette::Palette() {
	// Default black to white gradient with full opacity
	addColor(Color(0, 0, 0), 0);
	addColor(Color(255, 255, 255), 1);
	addOpacity(1, 0);
	addOpacity(1, 1);
}
Palette::~Palette() {
}

Color Palette::colorAt(double x) {
	std::vector<ColorPoint> colorList = this->getColors();
	
	// Handle empty list
	if(colorList.size() == 0)
		return this->interiorColor;
	
	// Handle invalid x values
	if(x <= 0 || colorList.size() == 1)
		return colorList[0].color;
	if(x >= 1)
		return colorList[colorList.size() - 1].color;
	
	// Check for valid x before / after first / last color
	if(x <= colorList[0].location)
		return colorList[0].color;
	if(x >= colorList[colorList.size() - 1].location)
		return colorList[colorList.size() - 1].color;
	
	// Find the two color values on either size of x
	ColorPoint * c1;
	ColorPoint * c2;
	
	for(int i = colorList.size() - 2; i >= 0 ; i--)
		if(colorList[i].location <= x) {
			c1 = &(colorList[i]);
			c2 = &(colorList[i + 1]);
			break;
		}
		
	// Find the fractional x distance between the two
	double frac = (x - c1->location) / (c2->location - c1->location);
	int r = c1->color.r - (c1->color.r - c2->color.r) * frac;
	int g = c1->color.g - (c1->color.g - c2->color.g) * frac;
	int b = c1->color.b - (c1->color.b - c2->color.b) * frac;
	
	return Color(r, g, b);
}
Opacity Palette::opacityAt(double x) {
	std::vector<OpacityPoint> opacityList = this->getOpacities();
	
	// Handle empty list
	if(opacityList.size() == 0)
		return 1;
	
	// Handle invalid x values
	if(x <= 0 || opacityList.size() == 1)
		return opacityList[0].opacity;
	if(x >= 1)
		return opacityList[opacityList.size() - 1].opacity;
	
	// Find the two color values on either size of x
	OpacityPoint * o1;
	OpacityPoint * o2;
	for(int i = 0; i < opacityList.size() - 1; i++)
		if(opacityList[i].location <= x) {
			o1 = &(opacityList[i]);
			o2 = &(opacityList[i + 1]);
		}
		
	// Find the fractional x distance between the two
	double frac = (x - o1->location) / (o2->location - o1->location);
	Opacity opacity = o1->opacity - (o1->opacity - o2->opacity) * frac;
	
	return opacity;
}

bool Palette::addColor(Color color, double x) {
	// Make sure it's valid location
	if(x < 0 || x > 1)
		return false;
	for(int i = 0; i < colorList.size(); i++) 
		if(colorList[i].location == x) 
			return false;
		
	// Make sure it's a valid color
	if(color.r < 0 || color.r > 255 || color.g < 0 || color.g > 255 || color.b < 0 || color.b > 255)
		return false;
	
	ColorPoint point;
	point.color = color;
	point.location = x;
	this->colorList.push_back(point);
	
	return true;
}
bool Palette::removeColor(double x) {
	for(int i = 0; i < colorList.size(); i++) 
		if(colorList[i].location == x) {
			this->colorList.erase(this->colorList.begin() + i);
			return true;
		}
		
	return false;
}
bool Palette::addOpacity(Opacity opacity, double x) {
	// Make sure it's valid location
	if(x < 0 || x > 1)
		return false;
	for(int i = 0; i < opacityList.size(); i++) 
		if(opacityList[i].location == x) 
			return false;
		
	// Make sure it's a valid opacity
	if(opacity < 0 || opacity > 1)
		return false;
		
	OpacityPoint point;
	point.opacity = opacity;
	point.location = x;
	this->opacityList.push_back(point);
		
	return true;
}
bool Palette::removeOpacity(double x) {
	for(int i = 0; i < opacityList.size(); i++) 
		if(opacityList[i].location == x) {
			this->opacityList.erase(this->opacityList.begin() + i);
			return true;
		}
		
	return false;
}

/************************
** Getters and Setters **
*************************/

Color Palette::getInteriorColor() {
	return this->interiorColor;
}
void Palette::setInteriorColor(Color newColor) {
	this->interiorColor = newColor;
}

std::string Palette::toString() {
	return "palette string";
}

bool colorPointCompare(ColorPoint c1, ColorPoint c2) {
	return c1.location < c2.location;
}

bool opacityPointCompare(OpacityPoint o1, OpacityPoint o2) {
	return o1.location < o2.location;
}

std::vector<ColorPoint> Palette::getColors() {
	std::sort(this->colorList.begin(), this->colorList.end(), colorPointCompare);
	return this->colorList;
}

std::vector<OpacityPoint> Palette::getOpacities() {
	std::sort(this->opacityList.begin(), this->opacityList.end(), opacityPointCompare);
	return this->opacityList;
}