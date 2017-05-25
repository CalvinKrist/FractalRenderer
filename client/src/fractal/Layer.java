package fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import util.Constants;
import util.Parameters;
import util.Point;
import util.Vector2;

public abstract class Layer implements Serializable {
	
	protected Palette palette;
	protected Point location;
	protected double zoom; //actual zoom: eg, higher number means it's more zoomed in
	protected Dimension screenResolution;
	protected Point realResolution;
	protected long bailout;
	protected int maxIterations;
	protected int layer;
	protected boolean autoBailout = true;
	protected boolean autoMaxIterations = true;
	
	protected String name;
	
	public Layer(Palette palette, int layer) {
		this.palette = palette;
		this.layer = layer - 1;
	}
	
	public void render(int[][] pixels) {
		render(pixels, screenResolution.width, screenResolution.height, realResolution.x, realResolution.y, location.x, location.y);
	}
	
	public abstract void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos, double yPos);

	protected abstract void calculateIterationsAndBailout(double rWidth, double rHeight);
	
	public void setColorPalette(Palette palette) {
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
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract Parameters getParameters();
	
	public abstract void setParameters(Parameters newProperties);
	
	public void save(String fractalName) throws IOException {
		File f = new File(Constants.FRACTAL_FILEPATH + "/" + fractalName);
		if(!f.exists()) {
			f.mkdirs();
			f.createNewFile();
		}
		FileWriter writer = new FileWriter(new File(Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".fractal"));
		writer.write("<name:" + name + ">");
		writer.write("<bailout:" + bailout + ">");
		writer.write("<maxIterations:" + maxIterations + ">");
		writer.write("<palette:" + Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".palette>");
		palette.writeTo(Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".palette");
		writer.close();
	}
	
}
