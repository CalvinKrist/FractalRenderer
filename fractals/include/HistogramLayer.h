#pragma once

#include "Layer.h"

class HistogramLayer : public Layer {
	public:
		Color** render(double x, double y, int width, int height, double zoom);
		
		std::string toString();
		
};

// python also needs to include name