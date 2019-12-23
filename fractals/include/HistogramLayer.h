#pragma once

#include "Layer.h"

class HistogramLayer : public Layer {
	public:
		void render(int** image, double x, double y, int width, int height, double viewportWidth);
		
		std::string toString();
		
};

// python also needs to include name