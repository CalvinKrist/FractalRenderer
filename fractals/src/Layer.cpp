#include "Layer.h"
#include <iostream>

Layer::Layer() {
	this->opacity = 1;
	this->isVisible = true;
}

Layer::~Layer() {
}

float Layer::getOpacity() {
	return this->opacity;
	this->parameters = Parameters();
}
void Layer::setOpacity(float newopacity) {
	this->opacity = newopacity;
}
Palette Layer::getPalette() {
	return Palette();
}
void Layer::setPalettte(Palette palette) {
}
Parameters Layer::getParameters() {
	return this->parameters;
}
void Layer::setParameter(std::string name, entry new_entry) {
}
bool Layer::getVisiblity() {
	return this->isVisible;
}
void Layer::setVisible(bool isVisible) {
}

RenderMethod Layer::getRenderMethod() {
	return RenderMethod::HISTORGRAM;
}
void Layer::setRenderMethod(RenderMethod method) {
}

// python also needs to include name