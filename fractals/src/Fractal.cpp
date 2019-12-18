#include "Fractal.h"
#include <iostream>
	
Fractal::Fractal() {
	this->x = -10;
}
Fractal::~Fractal() {
}

/***************************
***  Layer Manipulation  ***
****************************/
Layer* Fractal::getLayer(int index) {
	return this->layers.at(index);
}

Layer* Fractal::removeLayer(int index) {
	Layer* layer = this->layers.at(index);
	this->layers.erase(this->layers.begin() + index);
	return layer;
}

void Fractal::insertLayer(int index, Layer* layer) {
	if(index == layers.size()) 
		layers.push_back(layer);
	else
		layers.insert(layers.begin() + index, layer);
}

int Fractal::layerCount() {
	return this->layers.size();
}

std::string Fractal::toString() {
	std::string desc = "fractal: ";
	
	for(int i = 0; i < layers.size(); i++) 
		desc += layers[i]->toString() + " : ";
		
	return desc;
}

/****************************
***  Getters and Setters  ***
*****************************/

double Fractal::getX() {
	return this->x;
}

void Fractal::setX(double newX) {
	this->x = newX;
}

double Fractal::getY() {
	return this->y;
}
void Fractal::setY(double newY) {
	this->y = newY;
}
double Fractal::getViewportWidth() {
	return this->viewportWidth;
}
void Fractal::setViewportWidth(double width) {
	this->viewportWidth = width;
}