package fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import menus.AlertMenu;
import util.Constants;
import util.Parameters;
import util.Point;

/**
 * Manages all the layers that make up a fractal. It could, in many ways, be
 * thought of as a fractal itself.
 * 
 * @author Calvin
 *
 */
public class RenderManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5287342340788760671L;
	/**
	 * A list of all the layers making up a fractal
	 */
	private ArrayList<Layer> layers;
	/**
	 * The location in real coordinates the fractal is centred on
	 */
	private Point location;
	/**
	 * This is the zoom level of the fractal, except sometimes its the radius of
	 * the fractal. The radius and zoom re inverses of eachother.
	 */
	private double zoom;
	/**
	 * The resolution at which any images of the fractal will be generated
	 */
	protected Dimension screenResolution;
	/**
	 * The resolution of the fractal in real world coordinates. This variable
	 * exists in order to resolve any stretching that can occur when the
	 * screenResolution isn't square.
	 */
	protected Point realResolution;
	
	/**
	 * The name of the fractal.
	 */
	protected String name;
	
	private String filePath = null;

	/**
	 * Initializes a fractal with a black to white palette centered at tbe point
	 * 0,0. It has a zoom level of .25 and a single HistogramLayer
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
		update();
	}

	/**
	 * Initializes a fractal with the given parameters
	 * 
	 * @param params
	 *            the parameters of the fractal to be initialized
	 */
	public RenderManager(Parameters params) {
		init(params);
	}

	/**
	 * initializes the fractal will the specific parameters
	 * 
	 * @param params
	 *            the parameters of the fractal to be initialized
	 */
	private void init(Parameters params) {
		location = new Point(params.removeParameter("location", String.class));
		zoom = 1 / Double.valueOf(params.removeParameter("radius", String.class));
		String[] dims = params.removeParameter("resolution", String.class).split(",");
		screenResolution = new Dimension(Integer.valueOf(dims[0]), Integer.valueOf(dims[1]));

		name = params.removeParameter("name", String.class);
		Iterator<String> names = params.keyIterator();
		this.layers = new ArrayList<Layer>(params.getSize());
		while (names.hasNext()) {
			String name = names.next();
			if (name.indexOf("layer") != -1) {
				this.layers.add(Integer.valueOf(name.substring("layer".length())) - 1, Layer.getLayerByAddress(
						Constants.FRACTAL_FILEPATH + this.name + "/" + params.getParameter(name, String.class)));
			}
		}
		update();
	}

	/**
	 * Initializes a fractal from a file. This name points to a saved fractal.
	 * 
	 * @param name
	 *            of a saved fractal
	 */
	public RenderManager(String name) {
		this(new File(Constants.FRACTAL_FILEPATH + name));
	}

	/**
	 * Initializes a fractal from a .fractal file
	 * 
	 * @param f
	 *            a .fractal file
	 */
	public RenderManager(File f) {
		try {
			ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(f));
			RenderManager o = (RenderManager)(objIn.readObject());
			this.layers = o.layers;
			this.location = o.location;
			this.zoom = o.zoom;
			System.out.println(o.zoom);
			this.screenResolution = o.screenResolution;
			this.realResolution = o.realResolution;
			this.name = o.name;
			objIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders the fractal using the given 2D array of pixels.
	 * 
	 * @param pixels
	 *            the pixels the fractal will be drawn to.
	 */
	public void render(int[][] pixels) {
		ArrayList<Color[][]> layers = new ArrayList<Color[][]>();
		for(Layer l : this.layers) {
			Color[][] cols = l.render();
			if(cols != null) {
				layers.add(cols);
			}
		}
		System.out.println();
		for(int l = 0; l < layers.size(); l++)
			for(int i = 0; i < pixels.length; i++)
				for(int k = 0; k < pixels[i].length; k++) {
					double w = layers.get(l)[i][k].getAlpha() / 255.0;
					Color c = new Color(pixels[i][k]);
					Color c2 = layers.get(l)[i][k];
					double w2 = 1 - w;
					Color nC = new Color((int)(c.getRed() * w2 + c2.getRed() * w), (int)(c.getGreen() * w2 + c2.getGreen() * w), (int)(c.getBlue() * w2 + c2.getBlue() * w));
					pixels[i][k] = nC.getRGB();
				}
	}

	/**
	 * used to get a 2D array of pixels representing the image rendered by the
	 * layers
	 * 
	 * @return a 2D array representing the rendered image of the fractal
	 */
	public int[][] render() {
		int[][] pixels = new int[screenResolution.width][screenResolution.height];
		render(pixels);
		return pixels;
	}

	/**
	 * Renders the fractal and saves it as an png at the designated filepath and
	 * with the designated title
	 * 
	 * @param filePath
	 *            the filePath pointing to the directory of the png
	 * @param title
	 *            the title of the png
	 */
	public void render(String filePath, String title) {
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height,
				BufferedImage.TYPE_INT_ARGB);
		int[][] pixels = render();
		setPixels(img, pixels);
		renderImage(filePath, title, img);
	}
	
	/**
	 * Used to change the visibility of any layer
	 * @param index the index of the layer whose visibility is being edited
	 * @param visible the new visibility of the indicated layer
	 */
	public void setLayerVisiblity(int index, boolean visible) {
		layers.get(index).setVisible(visible);
	}

	/**
	 * used to get a BufferedImage of the rendered fractal
	 * 
	 * @return the rendered fractal as a BufferedImage
	 */
	public BufferedImage getImage() {
		BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height,
				BufferedImage.TYPE_INT_ARGB);

		int[][] pixels = render();
		setPixels(img, pixels);
		return img;
	}

	/**
	 * Makes a deep zoom of the fractal until the program is stopped
	 * 
	 * @param filePath
	 *            the directory where the rendered images will be saved
	 * @param zoomSpeed
	 *            the speed at which the fractal zooms in
	 * @param title
	 *            the title of the rendered fractal
	 */
	public void renderMovie(String filePath, double zoomSpeed, String title) {
		int frame = 1;
		while (true) {

			BufferedImage img = new BufferedImage(screenResolution.width, screenResolution.height,
					BufferedImage.TYPE_INT_ARGB);
			int[][] pixels = render();
			setPixels(img, pixels);
			renderImage(filePath, title + frame++, img);
			zoom *= zoomSpeed;
			this.setZoom(zoom);
		}
	}

	/**
	 * A static method created to save a BufferedImage as a png to a specified
	 * location
	 * 
	 * @param filePath
	 *            the directory where the image will be saved
	 * @param title
	 *            the title of the saved png
	 * @param img
	 *            the image to be saved as a png
	 */
	public static void renderImage(String filePath, String title, BufferedImage img) {
		try {
			File f1 = new File(filePath);
			f1.mkdirs();
			File f = new File(filePath + "/" + title + ".png");
			f.createNewFile();
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the pixels of the BufferedImage to those of the 2D array of color
	 * data. Assumes they are the same size.
	 * 
	 * @param img
	 *            the image whose color data will be set
	 * @param pixels
	 *            the color data used to set the BufferedImage
	 */
	public static void setPixels(BufferedImage img, int[][] pixels) {
		for (int i = 0; i < pixels.length; i++)
			for (int k = 0; k < pixels[i].length; k++) {
				img.setRGB(i, k, pixels[i][k]);
			}
	}

	/**
	 * Used to change the location on the Mandelbrot set that the fractal
	 * renders
	 * 
	 * @param location
	 *            the new location on the Mandelbrot set
	 */
	public void setLocation(Point location) {
		this.location = location;
		for (Layer r : layers)
			r.setLocation(location);
	}
	
	/**
	 * Returns whether or not a certain layer is visible
	 * @param index the index of the layer in question
	 * @return whether or not the layer is visible
	 */
	public boolean getVisibility(int index) {
		return layers.get(index).getVisible();
	}
	
	/**
	 * Used to get the opacity of a layer at a specific index
	 * @param index the index of the layer in question
	 * @return the opacity of the layer in question
	 */
	public double getOpacity(int index) {
		return layers.get(index).getOpacity();
	}

	/**
	 * Sets the zoom value of the fractal and takes care of any stretching
	 * issues of the layer so that the fractal is never streched, regardless of
	 * the dimension of the image being drawn. It does based on the value of the
	 * longest edge.
	 * 
	 * @param zoom
	 *            the new zoom level of the fractal
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
		for (Layer r : layers)
			r.setZoom(zoom);
		double ratio = screenResolution.height > screenResolution.width
				? (double) screenResolution.width / screenResolution.height
				: (double) screenResolution.height / screenResolution.width;
		double radius = 1 / zoom;
		if (realResolution == null)
			realResolution = new Point();
		if (screenResolution.height > screenResolution.width) {
			realResolution.x = radius * ratio;
			realResolution.y = radius;
		} else {
			realResolution.x = radius;
			realResolution.y = radius * ratio;
		}
	}

	/**
	 * used to get the palette of a certain layer
	 * 
	 * @param layer
	 *            the layer number of the layer in question. this number ranges
	 *            from 1 to infinity
	 * @return the palette used by the layer at index layer - 1
	 */
	public Palette getPalette(int layer) {
		return layers.get(layer - 1).getPalette();
	}

	/**
	 * used to get the resolution the fractal renders at
	 * 
	 * @return a dimension containing the fractal resolution
	 */
	public Dimension getScreenResolution() {
		return screenResolution;
	}

	/**
	 * used to get the dimension of the fractal in real coordinates
	 * 
	 * @return the dimensions of the fractal in read coordinates. the x value
	 *         represents the width and the y value represents the height
	 */
	public Point getRealResolution() {
		return realResolution;
	}

	/**
	 * used to set the resolution the fractal renders at
	 * 
	 * @param screenResolution
	 *            the new resolution the fractal will render at
	 */
	public void setScreenResolution(Dimension screenResolution) {
		this.screenResolution = screenResolution;
		for (Layer r : layers)
			r.setScreenResolution(screenResolution);
		setZoom(getZoom());
	}

	public String toString() {
		String s = "";
		s += "Location: " + location.toString();
		s += "    Zoom: " + zoom;
		s += "    Screen Resolution: " + screenResolution.toString();
		s += "    Real Resolution: " + realResolution.toString();
		for (int i = 0; i < layers.size(); i++)
			s += "    Layer " + (i + 1) + ": " + layers.get(i).getClass().getName();
		return s;
	}

	/**
	 * Saves the fractal, its layers, and their palettes to the specified
	 * directory based on the fractal's name
	 */
	public void saveFractal() {
		if (filePath == null) {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Save Fractal");
			chooser.setInitialDirectory(new File("fractals"));
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fractals (*.fractal)",
					"*.fractal");
			chooser.getExtensionFilters().add(filter);
			File f = chooser.showSaveDialog(null);
			filePath = f.getAbsolutePath();
			setName(f.getName().substring(0, f.getName().indexOf(".")));
		}
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(new File(filePath)));
			objOut.writeObject(this);
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * used when the user wants to save the fractal with a different name, but
	 * not change the name of the current instance.
	 */
	public void saveFractalAs() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save Fractal");
		chooser.setInitialDirectory(new File("fractals"));
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fractals (*.fractal)",
				"*.fractal");
		chooser.getExtensionFilters().add(filter);
		File f = chooser.showSaveDialog(null);
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(f));
			objOut.writeObject(this);
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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

	public String getName() {
		return name;
	}

	public double getZoom() {
		return zoom;
	}

	public ArrayList<Layer> getLayers() {
		return layers;
	}

	/**
	 * Adds a new layer to the fractal at the top of the list
	 * 
	 * @param layerType
	 *            the type of layer to be added to the fractal
	 */
	public void addLayer(String layerType) {
		Layer l = Layer.getLayerByType(layerType);
		layers.add(l);
		l.init(new Palette(), layers.size());
		update();
	}
	
	/**
	 * Used to update the fields of all the layers. 
	 */
	public void update() {
		this.setScreenResolution(screenResolution);
		this.setLocation(location);
		this.setZoom(zoom);
	}

}
