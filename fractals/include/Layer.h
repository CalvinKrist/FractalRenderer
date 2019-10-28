#ifndef Layer_HEADER
#define Layer_HEADER

#include "Color.h"
#include "Palette.h"
#include "Parameters.h"

enum RenderMethod {
	HISTORGRAM
};

class Layer {
	public:
		Layer();
		~Layer();
	
		// Getters and setters
		float getOpacity();
		void setOpacity(float opacity);
		Palette getPalette();
		void setPalettte(Palette palette);
		Parameters getParameters();
		void setParameter(std::string name, entry new_entry);
		bool getVisiblity();
		void setVisible(bool isVisible);
		RenderMethod getRenderMethod();
		void setRenderMethod(RenderMethod method);
		
		Color** render(double x, double y, int width, int height, double zoom);
		
	private:
		float opacity;
		Palette palette;
		Parameters parameters;
		bool isVisible;
		RenderMethod renderMethod;
};

// python also needs to include name

#endif