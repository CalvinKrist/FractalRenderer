#pragma once

#include "Layer.h"

class SimpleBandsLayer : public Layer {
	public:
		void render(unsigned char** image, double x, double y, int width, int height, double viewportWidth);
		
		std::string toString();
		
};

// python also needs to include name