package fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.Serializable;

import util.Point;
import util.Vector2;

public abstract class Renderer implements Serializable {
	
	protected Color[] palette;
	protected Point location;
	protected double zoom; //actual zoom: eg, higher number means it's more zoomed in
	protected Dimension screenResolution;
	protected Point realResolution;
	protected long bailout;
	protected int maxIterations;
	protected int layer;
	
	public void render(int[][] pixels) {
		render(pixels, screenResolution.width, screenResolution.height, realResolution.x, realResolution.y, location.x, location.y);
	}
	
	public abstract void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos);

	protected abstract void calculateIterationsAndBailout(double rWidth, double rHeight);
	
	public void setColorPalette(Color[] palette) {
		this.palette = palette;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
		double ratio = screenResolution.height > screenResolution.width ? (double)screenResolution.width / screenResolution.height : (double)screenResolution.height / screenResolution.width;
		double radius = 1/zoom;
		if(realResolution == null)
			realResolution = new Point();
		if(screenResolution.height > screenResolution.width) {
			realResolution.x = radius * ratio;
			realResolution.y = radius;
		} else {
			realResolution.x = radius;
			realResolution.y = radius * ratio;
		}
	}
	
	public void setScreenResolution(Dimension screenResolution) {
		this.screenResolution = screenResolution;
	}
	
	public void setLayerNumber(int layer) {
		this.layer = layer;
	}
	
}
