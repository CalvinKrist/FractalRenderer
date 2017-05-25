package fractal;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import util.Constants;
import util.Parameters;
import util.Point;
import util.Vector2;

public class RenderManager {
	
	private ArrayList<Layer> layers;
	private Point location;
	private double zoom;
	protected Dimension screenResolution;
	protected Point realResolution;
	protected String name;
	
	public RenderManager(Parameters params) {
		location = params.removeParameter("location", Point.class);
		zoom = 1 / params.removeParameter("radius", Double.class);
		screenResolution = params.removeParameter("resolution", Dimension.class);
		name = params.removeParameter("name", String.class);
		Iterator<String> names = params.keyIterator();
		this.layers = new ArrayList<Layer>(params.getSize());
		while(names.hasNext()) {
			String name = names.next();
			if(name.indexOf("layer") != -1) {
				Layer layer = params.getParameter(name, Layer.class);
				
				this.layers.add(Integer.valueOf(name.substring("layer".length())) - 1, params.getParameter(name, Layer.class));
			}
		}
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}
	
	public void render(int[][] pixels) {
		for(Layer r: layers)
			r.render(pixels);
	}
	
	public int[][] render() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		for(Layer r: layers)
			r.render(pixels);
		return pixels;
	}
	
	public void render(String filePath, String title) {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
		for(Layer r: layers)
			r.render(pixels);
		setPixels(img, pixels);
		renderImage(filePath, title, img);
	}
	
	public BufferedImage getImage() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
		for(Layer r: layers)
			r.render(pixels);
		setPixels(img, pixels);
		return img;
	}
	
	public void renderMovie(String filePath, double zoomSpeed, String title) {
		int frame = 1;
		while(true) {
			
			BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
			int[][] pixels = new int[screenResolution.width][screenResolution.height];
			for(Layer r: layers)
				r.render(pixels);
			setPixels(img, pixels);
			renderImage(filePath, title + frame++, img);
			zoom *= zoomSpeed;
			this.setZoom(zoom);
		}
	}
	
	public static void renderImage(String filePath, String title, BufferedImage img) {
		try {
			File f1 = new File(filePath);
			f1.mkdirs();
			File f = new File(filePath + "/" + title + ".png");
			f.createNewFile();
			ImageIO.write(img, "png", f);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void setPixels(BufferedImage img, int[][] pixels) {
		for(int i = 0; i < pixels.length; i++)
			for(int k = 0; k < pixels[i].length; k++) {
				img.setRGB(i, k, pixels[i][k]);
			}
	}
	
	public void setLocation(Point location) {
		this.location = location;
		for(Layer r: layers)
			r.setLocation(location);
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
		for(Layer r: layers)
			r.setZoom(zoom);
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
	
	public Dimension getScreenResolution() {
		return screenResolution;
	}
	
	public void setScreenResolution(Dimension screenResolution) {
		this.screenResolution = screenResolution;
		for(Layer r: layers)
			r.setScreenResolution(screenResolution);
	}
	
	public String toString() {
		String s = "";
		s += "Location: " + location.toString();
		s += "    Zoom: " + zoom;
		s += "    Screen Resolution: " + screenResolution.toString();
		s += "    Real Resolution: " + realResolution.toString();
		for(int i = 0; i < layers.size(); i++)
			s += "    Layer " + (i + 1) + ": " + layers.get(i).getClass().getName();
		return s;
	}
	
	public void saveFractal() {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("name", name);
		params.put("location", getLocation().toString());
		params.put("radius", getRadius());
		params.put("resolution", getScreenResolution().width + "," + getScreenResolution().height);
		for(int i = 0; i < getNumLayers(); i++) {
			params.put("layer" + (i + 1), getLayers().get(i).getName() + ".fract");
			try {
				getLayers().get(i).save(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(new File(Constants.FRACTAL_FILEPATH + "/" + name + "/" + name + ".prop"));
			writer.write(new Parameters(params).toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFractal(String name) {
		setName(name);
		saveFractal();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public int getNumLayers() {
		return layers.size();
	}
	
	public double getRadius() {
		return 1 / zoom;
	}
	
	public ArrayList<Layer> getLayers() {
		return layers;
	}

}
