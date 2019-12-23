#include "Layer.h"
#include <iostream>

Layer::Layer() {
	this->opacity = 1;
	this->visible = true;
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
	return this->palette;
}
void Layer::setPalette(Palette newPalette) {
	this->palette = newPalette;
}
bool Layer::isVisible() {
	return this->visible;
}
void Layer::setVisible(bool isVisible) {
	this->visible = isVisible;
}
std::string Layer::getName() {
	return this->name;
}
void Layer::setName(std::string newName) {
	this->name = newName;
}

// python also needs to include name