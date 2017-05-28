package fractal;

import java.awt.Color;
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
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.scene.control.TextInputDialog;
import util.Constants;
import util.Parameters;
import util.Point;
import util.Utils;

public class RenderManager {
	
	private ArrayList<Layer> layers;
	private Point location;
	private double zoom;
	protected Dimension screenResolution;
	protected Point realResolution;
	protected String name;
	
	public RenderManager() {
		location = new Point(0, 0);
		zoom = .25;
		screenResolution = new Dimension(1600, 1600);
		name = "";
		layers = new ArrayList<Layer>();
		Layer l = Layer.getLayerByType("HistogramLayer");
		l.init(new Palette(Utils.getDefaultColorPalate(), Color.BLACK), 1);
		l.setName("Layer 1");
		layers.add(l);
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}
	
	public RenderManager(Parameters params) {
		init(params);
	}
	
	private void init(Parameters params) {
		location = params.removeParameter("location", Point.class);
		zoom = 1 / params.removeParameter("radius", Double.class);
		screenResolution = params.removeParameter("resolution", Dimension.class);
		name = params.removeParameter("name", String.class);
		Iterator<String> names = params.keyIterator();
		this.layers = new ArrayList<Layer>(params.getSize());
		while(names.hasNext()) {
			String name = names.next();
			if(name.indexOf("layer") != -1) {				
				this.layers.add(Integer.valueOf(name.substring("layer".length())) - 1, params.getParameter(name, Layer.class));
			}
		}
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}
	
	public RenderManager(File file) {
		//TODO: implement constructor
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
		setZoom(getZoom());
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
		if(name.equals("")) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setContentText("Fractal name:");
			dialog.setHeaderText(null);
			setName(dialog.showAndWait().get());
		}
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("name", name);
		params.put("location", getLocation().toString());
		params.put("radius", getRadius());
		params.put("resolution", getScreenResolution().width + "," + getScreenResolution().height);
		for(int i = 0; i < getNumLayers(); i++) {
			params.put("layer" + (i + 1), getLayers().get(i).getName() + ".layer");
			try {
				getLayers().get(i).save(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter writer = new FileWriter(new File(Constants.FRACTAL_FILEPATH + "/" + name + "/" + name + ".fractal"));
			writer.write(new Parameters(params).toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFractalAs() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setContentText("Fractal name:");
		dialog.setHeaderText(null);

		Optional<String> result = dialog.showAndWait();
		String name = result.get();
		
		String currName = name;
		setName(name);
		saveFractal();
		setName(currName);
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
	
	public double getZoom() {
		return zoom;
	}
	
	public ArrayList<Layer> getLayers() {
		return layers;
	}
	
	public void addLayer(String layerType) {
		try {
			Class<?> clazz = Class.forName(Constants.CUSTOM_FRACTAL_FILEPATH + name);
			Layer l = (Layer)(clazz.newInstance());
			layers.add(l);
			l.setLayer(layers.size());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
