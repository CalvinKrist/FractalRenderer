#include "Palette.h"

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
	//std::cout << "Palette created" << std::endl;
}
Palette::~Palette() {
	//std::cout << "Palette deleted" << std::endl;
}

Color Palette::getInteriorColor() {
	return this->interiorColor;
}
void Palette::setInteriorColor(Color newColor) {
	this->interiorColor = newColor;
}

std::string Palette::toString() {
	return "palette string";
}

Color Palette::colorAt(double x) {
	return Color();
}
Opacity Palette::opacityAt(double x) {
	return 1;
}

bool Palette::addColor(Color color, double x) {
	return true;
}
bool Palette::removeColor(double x) {
	return true;
}
bool Palette::addOpacity(Opacity opacity, double x) {
	return true;
}
bool Palette::removeOpacity(double x) {
	return true;
}