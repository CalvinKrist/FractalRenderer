package fractal;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import util.Constants;
import util.DynamicCompiler;
import util.Parameters;
import util.Point;

/**
 * Represents a layer of a fractal. It will be asked to render in a specific way.
 * Users can create their own layers and choose to use them from the GUI.
 * 
 * @author Calvin
 *
 */
@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public abstract class Layer implements Serializable {

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
	 * The length in real coordinates of the longest edge of the window being drawn.
	 */
	protected Point realResolution;
	
	/**
	 * The bailout value of the layer, eg the value at which the layer's iterative rendering will bail
	 */
	protected long bailout;
	
	/**
	 * The maximum iterations of the layer.
	 */
	protected int maxIterations;
	
	/**
	 * The layer level. eg, a value of 1 would mean this is layer 1, which is on the bottom. This
	 * is included because the coloring methods that avergae over the layers given an opacity level 
	 * wont work without it.
	 */
	protected int layer;
	
	/**
	 * whether or not the program will automatically calculate a bailout value. This is not
	 * recommended for high quality renderings. This is intended for use in deep zooms.
	 */
	protected boolean autoBailout = true;
	
	/**
	 * whether or not the program will automatically calculate a maxIterations value. This is not
	 * recommended for high quality renderings. This is intended for use in deep zooms.
	 */
	protected boolean autoMaxIterations = true;
	
	/**
	 * A description of the layer. It can be displayed as a tooltip in the Fractal modifier GUI
	 */
	protected String discription = "";
	
	/**
	 * The name of the layer. This is displayed in the layer view of the fractal GUI and is used
	 * in the saving of files
	 */
	protected String name;

	/**
	 * A list of all valid layer types. There a lots of utility methods built around this to allow users
	 * to add their own layer types and select different layer types in the layer view of the fractal editor.
	 */
	private static ArrayList<Class<? extends Layer>> fractalRegistry = new ArrayList<Class<? extends Layer>>();

	protected Layer() {
	}

	/**
	 * initializes the layer. This should always be called before the layer is used.
	 * @param palette the palette the layer will use to color itself.
	 * @param layer the layer level.
	 */
	public void init(Palette palette, int layer) {
		this.palette = palette;
		this.layer = layer - 1;
	}

	/**
	 * A render method that takes in a 2D array of pixels which represent the image. The
	 * layer will render itself to the array.
	 * @param pixels the 2D array of pixels representing the image.
	 */
	public void render(int[][] pixels) {
		render(pixels, screenResolution.width, screenResolution.height, realResolution.x, realResolution.y, location.x,
				location.y);
	}

	/**
	 * A method containing the actual rendering logic of the layer. 
	 * @param pixels the array of pixels to be drawn on
	 * @param width the width in pixels of the image being drawn
	 * @param height the height in pixels of the image being drawn
	 * @param rWidth the width in real units of the image being drawn
	 * @param rHeight the height in real units of the image being drawn
	 * @param xPos the x position in real units where the layer will center its rendering
	 * @param yPos the y position in real units where the layer will center its rendering
	 */
	public abstract void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos,
			double yPos);

	/**
	 * This method is called at the start of every rendering cycle. This gives the layer an opportunity to 
	 * automtically calculate bailout and maxIterations values
	 * @param rWidth the width in real units of the image being drawn
	 * @param rHeight the height in real units of the image being drawn
	 */
	protected abstract void calculateIterationsAndBailout(double rWidth, double rHeight);

	public void setColorPalette(Palette palette) {
		this.palette = palette;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * Sets the zoom value of the layer and takes care of any stretching issues of the
	 * layer so that the fractal is never streched, regardless of the dimension of the 
	 * image being drawn. It does based on the value of the longest edge.
	 * @param zoom
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

	/**
	 * A method returning all editable parameters of the layer. When a layer is selected on the Layer view of the
	 * fractal gui, these parameters will be displayed to the use for modification
	 * @return the modifiable parameters
	 */
	public abstract Parameters getParameters();

	/**
	 * Sets new values for the modifiable parameters
	 * @param newProperties the new values of the modifiable parameters
	 */
	public abstract void setParameters(Parameters newProperties);

	/**
	 * Saves the fractal at the specified location with the specified fractal name
	 * @param fractalName the name of the fractal this layer is a part of
	 * @throws IOException
	 */
	public void save(String fractalName) throws IOException {
		File f = new File(Constants.FRACTAL_FILEPATH + "/" + fractalName);
		if (!f.exists()) {
			f.mkdirs();
			f.createNewFile();
		}
		FileWriter writer = new FileWriter(
				new File(Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".layer"));
		writer.write("<name:" + name + ">");
		writer.write("<bailout:" + bailout + ">");
		writer.write("<maxIterations:" + maxIterations + ">");
		writer.write("<palette:" + Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".palette>");
		palette.writeTo(Constants.FRACTAL_FILEPATH + "/" + fractalName + "/" + name + ".palette");
		writer.close();
	}

	/**
	 * This static method must be called at startup of the application. It looks at the specified 
	 * directory for all custom layer files, compiles them, and loads Class representations of them
	 * into the registry so they can be created later
	 */
	public static void initializeFractalRegistry() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		while (fractalRegistry.size() != 0)
			fractalRegistry.remove(0);

		File fractalFolder = new File(Constants.CUSTOM_FRACTAL_FILEPATH);
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { fractalFolder.toURI().toURL() });
		File[] fractals = fractalFolder.listFiles();
		File classFolder = new File(Constants.CUSTOM_FRACTAL_FILEPATH + "fractal/");

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
				DynamicCompiler.compile(files);
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
		
	}

	public String getDiscription() {
		if (discription.equals(""))
			return null;
		return discription;
	}

	/**
	 * @return a list of all the valid layer types a user can choose from
	 */
	public static List<String> getLayerTypes() {
		List<String> list = new LinkedList<String>();
		for (Class<? extends Layer> c : fractalRegistry)
			list.add(c.getName());
		return list;
	}

	/**
	 * @param type a type of layer
	 * @return whether or not the type passed to this method corresponds to a real layer type
	 */
	public static boolean isValidLayer(String type) {
		for (Class c : fractalRegistry)
			if (c.getSimpleName().equals(type + ".java"))
				return true;

		return false;
	}

	/**
	 * @param type a type of layer
	 * @return an instance of the layer type requested, if it is a valid layer type. Otherwise, null.
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
		// return new HistogramLayer();
	}

	/**
	 * Used to register a new layer type
	 * @param file a file pointing to the layer being registered
	 * @return whether or not the layer was succesfully registered
	 */
	public static boolean registerLayer(File file) {
		try {
			fractalRegistry
					.add((Class<? extends Layer>) Class.forName(Constants.CUSTOM_FRACTAL_FILEPATH + file.getName()));
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

}