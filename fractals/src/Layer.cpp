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
void Layer::setVisible(bool isVisible) {
}

RenderMethod Layer::getRenderMethod() {
	return RenderMethod::HISTORGRAM;
}
void Layer::setRenderMethod(RenderMethod method) {
}

// python also needs to include name