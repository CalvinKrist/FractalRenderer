#include "Layer.h"
#include <iostream>

Layer::Layer() {
	this->opacity = 1;
	this->isVisible = true;
	this->name = "My Layer";
}

Layer::~Layer() {
}

float Layer::getOpacity() {
	return this->opacity;
}
void Layer::setOpacity(float newopacity) {
	this->opacity = newopacity;
}
Palette Layer::getPalette() {
	return Palette();
}
void Layer::setPalettte(Palette palette) {
}
bool Layer::getVisiblity() {
	return this->isVisible;
}
void Layer::setVisible(bool visible) {
	this->isVisible = visible;
}
std::string Layer::getName() {
	return this->name;
}
void Layer::setName(std::string newName) {
	this->name = newName;
}

// python also needs to include name