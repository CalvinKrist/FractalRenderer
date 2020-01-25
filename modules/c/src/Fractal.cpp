#include "Fractal.h"
#include <iostream>
	
Fractal::Fractal() {
	this->x = 0;
	this->y = 0;
	this->viewportWidth = 4;
	this->width = 600;
	this->height = 400;
	
	image = new unsigned char[width * height * 3];
	layerImage = new unsigned char[width * height * 4];
}
Fractal::~Fractal() {
	delete[] image;
	delete[] layerImage;
}

void Fractal::updateImageMemory() {
	delete[] image;
	delete[] layerImage;
	
	image = new unsigned char[width * height * 3];
	layerImage = new unsigned char[width * height * 4];
}

unsigned char** Fractal::render() {
	int length = width * height * 4;
	int imageIndex;
	
	for(int i = 0; i < layers.size(); i++)
		if(layers[i]->isVisible()) {
			layers[i]->render(&layerImage, x, y, width, height, viewportWidth);
			
			imageIndex = 0;
			for(int layerIndex = 0; layerIndex < length; layerIndex += 4) {
				double opacity = layerImage[layerIndex + 3] / 255.0;
				double inv_opacity = 1 - opacity;
				image[imageIndex] = image[imageIndex] * inv_opacity + layerImage[layerIndex] * opacity;
				image[imageIndex + 1] = image[imageIndex + 1] * inv_opacity + layerImage[layerIndex + 1] * opacity;
				image[imageIndex + 2] = image[imageIndex + 2] * inv_opacity + layerImage[layerIndex + 2] * opacity;
				imageIndex += 3;
			}
		}
	
	return &image;
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
	std::string desc = "fractal: \n";
	
	for(int i = 0; i < layers.size(); i++) 
		desc += "\t" + layers[i]->toString() + "\n";
		
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
int Fractal::getWidth() {
	return this->width;
}
void Fractal::setWidth(int newWidth) {
	this->width = newWidth;
	updateImageMemory();
}
int Fractal::getHeight() {
	return this->height;
}
void Fractal::setHeight(int newHeight) {
	this->height = newHeight;
	updateImageMemory();
}