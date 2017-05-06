package fractal;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import util.Parameters;
import util.Point;
import util.Vector2;

public class RenderManager {
	
	private Renderer[] layers;
	private Point location;
	private double zoom;
	protected Dimension screenResolution;
	protected Point realResolution;
	
	public RenderManager(Renderer[] layers) {
		this.layers = layers;
		for(int i = 0; i < layers.length; i++)
			layers[i].setLayerNumber(i);
	}
	
	public RenderManager(Parameters params) {
		location = params.removeParameter("location", Point.class);
		zoom = 1 / params.getParameter("radius", Double.class);
		screenResolution = params.getParameter("screenResolution", Dimension.class);
		int count = 0;
		Iterator<String> names = params.keyIterator();
		while(names.hasNext())
			if(names.next().indexOf("layer") != -1)
				count++;
		layers = new Renderer[count];
		names = params.keyIterator();
		while(names.hasNext()) {
			String name = names.next();
			if(name.indexOf("layer") != -1) {
				int layerNum = Integer.valueOf(name.substring("layer".length())) - 1;
				layers[layerNum] = params.getParameter(name, Renderer.class);
				layers[layerNum].setLayerNumber(layerNum);
			}
		}
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}
	
	public void render(int[][] pixels) {
		for(Renderer r: layers)
			r.render(pixels);
	}
	
	public int[][] render() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		for(Renderer r: layers)
			r.render(pixels);
		return pixels;
	}
	
	public void render(String filePath, String title) {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
		for(Renderer r: layers)
			r.render(pixels);
		setPixels(img, pixels);
		renderImage(filePath, title, img);
	}
	
	public void renderMovie(String filePath, double zoomSpeed, String title) {
		int frame = 1;
		while(true) {
			
			BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
			int[][] pixels = new int[screenResolution.width][screenResolution.height];
			for(Renderer r: layers)
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
		for(Renderer r: layers)
			r.setLocation(location);
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
		for(Renderer r: layers)
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
		for(Renderer r: layers)
			r.setScreenResolution(screenResolution);
	}

}
