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

/**
 * Manages all the layers that make up a fractal. It could, in many ways, be thought of as a fractal itself.
 * @author Calvin
 *
 */
public class RenderManager {
	
	/**
	 * A list of all the layers making up a fractal
	 */
	private ArrayList<Layer> layers;
	/**
	 * The location in real coordinates the fractal is centred on
	 */
	private Point location;
	/**
	 * This is the zoom level of the fractal, except sometimes its the radius of the fractal. The radius and zoom re inverses of eachother.
	 */
	private double zoom;
	/**
	 * The resolution at which any images of the fractal will be generated
	 */
	protected Dimension screenResolution;
	/**
	 * The resolution of the fractal in real world coordinates. This variable exists in order to resolve any stretching that
	 * can occur when the screenResolution isn't square.
	 */
	protected Point realResolution;
	/**
	 * The name of the fractal.
	 */
	protected String name;
	
	/**
	 * Initializes a fractal with a black to white palette centered at tbe point 0,0. It has a zoom level of .25 and a 
	 * single HistogramLayer
	 */
	public RenderManager() {
		location = new Point(0, 0);
		zoom = .25;
		screenResolution = new Dimension(1600, 1600);
		name = "";
		layers = new ArrayList<Layer>();
		Layer l = Layer.getLayerByType("HistogramLayer");
		
		l.init(new Palette(), 1);
		l.setName("Layer 1");
		layers.add(l);
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}
	
	/**
	 * Initialized a fractal with the given parameters
	 * @param params the parameters of the fractal to be initialized
	 */
	public RenderManager(Parameters params) {
		init(params);
	}
	
	/**
	 * initialized the fractal will the specifie dparameters
	 * @param params the parameters of the fractal to be initialized
	 */
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
	
	/**
	 * Initializes a fractal from a file. This name points to a saved fractal.
	 * @param name of a saved fractal
	 */
	public RenderManager(String name) {
		//TODO: implement constructor
	}
	
	/**
	 * Renders the fractal using the given 2D array of pixels.
	 * @param pixels the pixels the fractal will be drawn to.
	 */
	public void render(int[][] pixels) {
		for(Layer r: layers)
			r.render(pixels);
	}
	
	/**
	 * @return a 2D array representing the rendered image of the fractal
	 */
	public int[][] render() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		for(Layer r: layers)
			r.render(pixels);
		return pixels;
	}
	
	/**
	 * Renders the fractal and saves it as an png at the designated filepath and with the designated title
	 * @param filePath the filePath pointing to the directory of the png
	 * @param title the title of the png
	 */
	public void render(String filePath, String title) {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
		for(Layer r: layers) 
			r.render(pixels);
		setPixels(img, pixels);
		renderImage(filePath, title, img);
	}
	
	/**
	 * @return the rendered fractal as a BufferedImage
	 */
	public BufferedImage getImage() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height, BufferedImage.TYPE_INT_ARGB);
		for(Layer r: layers)
			r.render(pixels);
		setPixels(img, pixels);
		return img;
	}
	
	/**
	 * Makes a deep zoom of the fractal until the program is stopped
	 * @param filePath the directory where the rendered images will be saved
	 * @param zoomSpeed the speed at which the fractal zooms in
	 * @param title the title of the rendered fractal
	 */
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
	
	/**
	 * A static method created to save a BufferedImage as a png to a specified location
	 * @param filePath the directory where the image will be saved
	 * @param title the title of the saved png
	 * @param img the image to be saved as a png
	 */
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

	/**
	 * Sets the pixels of the BufferedImage to those of the 2D array of color data. Assumes they are the same size.
	 * @param img the image whose color data will be set
	 * @param pixels the color data used to set the BufferedImage
	 */
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
	
	/**
	 * Sets the zoom value of the fractal and takes care of any stretching issues of the
	 * layer so that the fractal is never streched, regardless of the dimension of the 
	 * image being drawn. It does based on the value of the longest edge.
	 * @param zoom
	 */
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
	
	public Palette getPalette(int layer) {
		return layers.get(layer - 1).getPalette();
	}
	
	public Dimension getScreenResolution() {
		return screenResolution;
	}
	
	public Point getRealResolution() {
		return realResolution;
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
	
	/**
	 * Saves the fractal, its layers, and their palettes to the specified directory based on the fractal's name
	 */
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
	
	/**
	 * used when the user wants to save the fractal with a different name, but not change the name of the 
	 * current instance.
	 */
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
	
	/**
	 * Adds a new layer to the fractal at the top of the list
	 * @param layerType the type of layer to be added to the fractal
	 */
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
