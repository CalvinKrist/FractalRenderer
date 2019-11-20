#include "Fractal.h"

	
Fractal::Fractal() {
	this->x = -10;
}
Fractal::~Fractal() {
}
	
	
double Fractal::getX() {
	return this->x;
}

void Fractal::setX(double newX) {
	this->x = newX;
}