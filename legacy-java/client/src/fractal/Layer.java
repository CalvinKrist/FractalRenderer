package fractal;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaFileObject;

import menus.AlertMenu;
import util.Constants;
import util.DynamicCompiler;
import util.Parameters;
import util.Point;

/**
 * Represents a layer of a fractal. It will be asked to render in a specific
 * way. Users can create their own layers and choose to use them from the GUI.
 * 
 * @author Calvin
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class Layer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5915996459965340211L;

	/**
	 * The palette the layer will use to color itself
	 */
	protected Palette palette;

	/**
	 * The location the rendering will be centered around
	 */
	protected Point location;

	/**
	 * The zoom of the layer. High values zoom in, lower values zoom out.
	 */
	protected double zoom;

	/**
	 * The resolution any images created will be drawn as
	 */
	protected Dimension screenResolution;

	/**
	 * The length in real coordinates of the longest edge of the window being
	 * drawn.
	 */
	protected Point realResolution;

	/**
	 * The bailout value of the layer, eg the value at which the layer's
	 * iterative rendering will bail
	 */
	protected long bailout;

	/**
	 * The maximum iterations of the layer.
	 */
	protected int maxIterations;

	/**
	 * The layer level. eg, a value of 1 would mean this is layer 1, which is on
	 * the bottom. This is included because the coloring methods that avergae
	 * over the layers given an opacity level wont work without it.
	 */
	protected int layer;

	/**
	 * whether or not the program will automatically calculate a bailout value.
	 * This is not recommended for high quality renderings. This is intended for
	 * use in deep zooms.
	 */
	protected boolean autoBailout = false;

	/**
	 * whether or not the program will automatically calculate a maxIterations
	 * value. This is not recommended for high quality renderings. This is
	 * intended for use in deep zooms.
	 */
	protected boolean autoMaxIterations = false;

	/**
	 * A description of the layer. It can be displayed as a tooltip in the
	 * Fractal modifier GUI
	 */
	public String description = "";

	/**
	 * The name of the layer. This is displayed in the layer view of the fractal
	 * GUI and is used in the saving of files
	 */
	protected String name;
	
	/**
	 * The opacity of this layer. 1 = perfectly visible, 0 = invisible
	 */
	private double opacity = 1.0;
	
	/**
	 * Whether or not the layer is visible.
	 */
	protected boolean visible = true;

	/**
	 * A list of all valid layer types. There a lots of utility methods built
	 * around this to allow users to add their own layer types and select
	 * different layer types in the layer view of the fractal editor.
	 */
	private static ArrayList<Class<? extends Layer>> fractalRegistry = new ArrayList<Class<? extends Layer>>();

	protected Layer() {
		bailout = 2;
		maxIterations = 100;
	}

	/**
	 * initializes the layer. This should always be called before the layer is
	 * used.
	 * 
	 * @param palette
	 *            the palette the layer will use to color itself.
	 * @param layer
	 *            the layer level.
	 */
	public void init(Palette palette, int layer) {
		this.palette = palette;
		this.layer = layer - 1;
	}
	
	/**
	 * A render method that takes in a 2D array of pixels which represent the
	 * image. The layer will render itself to the array.
	 * 
	 * @return the 2D array of colors representing the image rendered by this layer. Returns null if the layer is not visible.
	 */
	public Color[][] render() {
		if(visible && opacity != 0) {
			if(autoMaxIterations)
				calculateIterations(realResolution.x, realResolution.y);
			if(autoBailout)
				calculateBailout(realResolution.x, realResolution.y);
			Color[][] pixels = new Color[screenResolution.width][screenResolution.height];
			render(pixels, screenResolution.width, screenResolution.height, realResolution.x, realResolution.y, location.x,
				location.y);
			for(int i = 0; i < pixels.length; i++)
				for(int k = 0; k < pixels[i].length; k++)
					pixels[i][k] = new Color(pixels[i][k].getRed(), pixels[i][k].getGreen(), pixels[i][k].getBlue(), (int)(pixels[i][k].getAlpha() * opacity));
			return pixels;
		}
		return null;
	}

	/**
	 * A method containing the actual rendering logic of the layer.
	 * 
	 * @param pixels
	 *            the array of colors that need to be created
	 * @param width
	 *            the width in pixels of the image being drawn
	 * @param height
	 *            the height in pixels of the image being drawn
	 * @param rWidth
	 *            the width in real units of the image being drawn
	 * @param rHeight
	 *            the height in real units of the image being drawn
	 * @param xPos
	 *            the x position in real units where the layer will center its
	 *            rendering
	 * @param yPos
	 *            the y position in real units where the layer will center its
	 *            rendering
	 */
	protected abstract void render(Color[][] pixels, int width, int height, double rWidth, double rHeight, double xPos,
			double yPos);

	/**
	 * This method is called at the start of every rendering cycle if autoMaxIterations is true. This gives
	 * the layer an opportunity to automtically calculate maxIterations values
	 * 
	 * @param rWidth
	 *            the width in real units of the image being drawn
	 * @param rHeight
	 *            the height in real units of the image being drawn
	 */
	protected abstract void calculateIterations(double rWidth, double rHeight);
	
	/**
	 * This method is called at the start of every rendering cycle if autoBailout is true. This gives
	 * the layer an opportunity to automtically calculate bailout
	 * 
	 * @param rWidth
	 *            the width in real units of the image being drawn
	 * @param rHeight
	 *            the height in real units of the image being drawn
	 */
	protected abstract void calculateBailout(double rWidth, double rHeight);

	/**
	 * Used to change this layer's color palette
	 * 
	 * @param palette
	 *            the new color palette for this layer to use
	 */
	public void setColorPalette(Palette palette) {
		this.palette = palette;
	}

	/**
	 * Used to set the location of the Mandelbrot set that this layer renders
	 * 
	 * @param location
	 *            the new location of the Mandelbrot set
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	
	/**
	 * Used to get the bailout value of the layer.
	 * @return the bailout value of the layer
	 */
	public long getBailout() {
		return bailout;
	}
	
	/**
	 * Used to change the bailout value of the layer.
	 * @param newBailout the new bailout value for the layer
	 */
	public void setBailout(long newBailout) {
		this.bailout = newBailout;
	}
	
	/**
	 * Used to get the maximum iterations of the layer.
	 * @return the maximum iterations of the layer.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}
	
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * Sets the zoom value of the layer and takes care of any stretching issues
	 * of the layer so that the fractal is never streched, regardless of the
	 * dimension of the image being drawn. It does based on the value of the
	 * longest edge.
	 * 
	 * @param zoom
	 *            the new zoom level of the layer
	 */
	public void setZoom(double zoom) {
		this.zoom = zoom;
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
	 * returns the resolution this layer renders at
	 * 
	 * @param screenResolution
	 *            the resolution this layer renders at
	 */
	public void setScreenResolution(Dimension screenResolution) {
		this.screenResolution = screenResolution;
	}

	/**
	 * This can be used to set the layer index of the layer. This is important
	 * because some rendering code will be different for layers with an index of
	 * 0 than all others.
	 * 
	 * @param layer
	 *            the new layer index for this layer
	 */
	public void setLayer(int layer) {
		this.layer = layer;
	}

	/**
	 * This can be used to set the name of the layer.
	 * 
	 * @param name
	 *            the name of the layer
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the name of the layer
	 * 
	 * @return the name of the layer
	 */
	public String getName() {
		return name;
	}

	/**
	 * A method returning all editable parameters of the layer. When a layer is
	 * selected on the Layer view of the fractal gui, these parameters will be
	 * displayed to the use for modification
	 * 
	 * @return the modifiable parameters
	 */
	public abstract Parameters getParameters();

	/**
	 * Sets new values for the modifiable parameters
	 * 
	 * @param newProperties
	 *            the new values of the modifiable parameters
	 */
	public abstract void setParameters(Parameters newProperties);

	/**
	 * returns the palette this layer uses to color the fractal
	 * 
	 * @return the palette this layer uses to color the fractal
	 */
	public Palette getPalette() {
		return palette;
	}
	
	/**
	 * Used to change the visibility of this layer
	 * @param visible the new visiblity of the layer
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Used to get the visibility of this layer
	 * @return the visibility of this layer
	 */
	public boolean getVisible() {
		return visible;
	}
	
	/**
	 * Used to get the opacity of a layer
	 * @return the opacity of the layer
	 */
	public double getOpacity() {
		return opacity;
	}
	
	/**
	 * Used to change the opacity of the layer
	 * @param opacity the new opacity of the layer
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	/**
	 * This static method must be called at startup of the application if any
	 * Layers are to be used. It looks at the specified directory for all custom
	 * layer files, compiles them, and loads Class representations of them into
	 * the registry so they can be created later. If errors are thrown, it might
	 * be because a jdk is not being used. This method will only work if a jdk
	 * is installed
	 */
	public static void initializeFractalRegistry() {
		while (fractalRegistry.size() != 0)
			fractalRegistry.remove(0);

		fractalRegistry.add(Histogram.class);
		fractalRegistry.add(TriangleAverage.class);
		fractalRegistry.add(SimpleBands.class);
		fractalRegistry.add(EvenBands.class);

		try {
			File fractalFolder = new File(Constants.CUSTOM_FRACTAL_FILEPATH);
			if(!fractalFolder.exists()){
				fractalFolder.mkdirs();
				fractalFolder.createNewFile();
			}	
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { fractalFolder.toURI().toURL() });
			File[] fractals = fractalFolder.listFiles();
			File classFolder = new File(Constants.CUSTOM_FRACTAL_FILEPATH + "fractal/");
			if(!classFolder.exists()){
				classFolder.mkdirs();
				classFolder.createNewFile();
			}
			DynamicCompiler.classOutputFolder = Constants.CUSTOM_FRACTAL_FILEPATH;
			for (File f : fractals) {
				if (!f.isDirectory()) {
					String name = f.getName().substring(0, f.getName().indexOf("."));
					File classFile = new File(Constants.CUSTOM_FRACTAL_FILEPATH + "fractal/" + name + ".class");
					if (classFile.exists())
						classFile.delete();
					DynamicCompiler.name = f.getName().substring(0, f.getName().indexOf("."));
					String classContents = new String(Files.readAllBytes(f.toPath()));
					JavaFileObject file = DynamicCompiler.getJavaFileObject(classContents);
					Iterable<? extends JavaFileObject> files = Arrays.asList(file);
					try {
						DynamicCompiler.compile(files);
					} catch (NullPointerException e) {
						AlertMenu alert = new AlertMenu("Unable to compile custom layer types.",
								"Please try again using a JDK insteaded of a JRE.");
					}
				}
			}

			URL[] urlArray = { classFolder.toURI().toURL() };
			fractals = classFolder.listFiles();
			URLClassLoader classLoader = new URLClassLoader(urlArray);
			for (File f : classFolder.listFiles()) {
				String name = f.getName();
				if (name.indexOf("$") == -1)
					try {
						Class<? extends Layer> instance = (Class<? extends Layer>) classLoader
								.loadClass("fractal." + name.substring(0, name.indexOf(".")));
						fractalRegistry.add(instance);

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This static method can be called as an alternative to initializeFractalRegistry().
	 * It will only initialize the default fractal layer types.
	 */
	public static void initializeDefaultFractalRegistry() {
		while (fractalRegistry.size() != 0)
			fractalRegistry.remove(0);

		fractalRegistry.add(Histogram.class);
		fractalRegistry.add(TriangleAverage.class);

	}

	/**
	 * Returns a list of all valid layer types a user can choose from
	 * 
	 * @return a list of all the valid layer types a user can choose from
	 */
	public static List<String> getLayerTypes() {
		List<String> list = new LinkedList<String>();
		for (Class<? extends Layer> c : fractalRegistry)
			list.add(c.getSimpleName());
		return list;
	}

	/**
	 * Determines if a layer is a valid layer
	 * 
	 * @param type
	 *            a type of layer
	 * @return whether or not the type passed to this method corresponds to a
	 *         real layer type
	 */
	public static boolean isValidLayer(String type) {
		for (Class c : fractalRegistry)
			if (c.getSimpleName().equals(type))
				return true;

		return false;
	}

	/**
	 * This method is used to get a new instance of a Layer, by type. For
	 * example, if "HistogramLayer" is passed to this method, it will return a
	 * new instance of a HistogramLayer.class, assuming that layer was
	 * initialized in the registry. It does not initialize the specific instance
	 * of the layer.
	 * 
	 * @param type
	 *            a type of layer
	 * @return an instance of the layer type requested, if it is a valid layer
	 *         type. Otherwise, null.
	 */
	public static Layer getLayerByType(String type) {
		for (Class c : fractalRegistry) {
			if (c.getSimpleName().equals(type))
				try {
					return (Layer) c.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	/**
	 * This method is used primarily for loading in layers from a file. Given a
	 * file path, it will return the layer that file points to.
	 * 
	 * @param address
	 *            the file path of the layer to be created
	 * @return the layer the address points to
	 */
	public static Layer getLayerByAddress(String address) {
		Parameters params = new Parameters(address);
		Layer l = getLayerByType(params.getParameter("type", String.class));
		l.init(new Palette(params.getParameter("palette", String.class), false),
				Integer.valueOf(params.getParameter("layer", String.class)) + 1);
		if (params.getParameter("bailout", String.class).equals("auto"))
			l.autoBailout = true;
		else
			l.bailout = Long.valueOf(params.getParameter("bailout", String.class));
		if (params.getParameter("maxIterations", String.class).equals("auto"))
			l.autoMaxIterations = true;
		else
			l.maxIterations = Integer.valueOf(params.getParameter("maxIterations", String.class));
		l.name = params.getParameter("name", String.class);
		return l;
	}

	
	//TODO: test method
	/**
	 * Used to register a new layer type
	 * 
	 * @param file
	 *            a file pointing to the layer being registered
	 * @return whether or not the layer was succesfully registered. Returns
	 *         false if the layer was already registered
	 */
	public static boolean registerLayer(File file) {
		String name = file.getName().substring(0, file.getName().indexOf("."));
		if (Layer.isValidLayer(name))
			return false;
		File classFile = new File(Constants.CUSTOM_FRACTAL_FILEPATH + "fractal/" + name + ".class");
		if (classFile.exists())
			classFile.delete();
		DynamicCompiler.name = name;
		String classContents;
		try {
			classContents = new String(Files.readAllBytes(file.toPath()));

			JavaFileObject fi = DynamicCompiler.getJavaFileObject(classContents);
			Iterable<? extends JavaFileObject> files = Arrays.asList(fi);
			try {
				DynamicCompiler.compile(files);
			} catch (NullPointerException e) {
				AlertMenu alert = new AlertMenu("Unable to compile custom layer types.",
						"Please try again using a JDK insteaded of a JRE.");
			}
			fractalRegistry.add(DynamicCompiler.instanceOf());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
