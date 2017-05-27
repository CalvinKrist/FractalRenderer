package fractal;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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

@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public abstract class Layer implements Serializable {

	protected Palette palette;
	protected Point location;
	protected double zoom; // actual zoom: eg, higher number means it's more
							// zoomed in
	protected Dimension screenResolution;
	protected Point realResolution;
	protected long bailout;
	protected int maxIterations;
	protected int layer;
	protected boolean autoBailout = true;
	protected boolean autoMaxIterations = true;
	protected String discription = "";

	protected String name;

	public static ArrayList<Class<? extends Layer>> fractalRegistry = new ArrayList<Class<? extends Layer>>();

	protected Layer() {
	}

	public void init(Palette palette, int layer) {
		this.palette = palette;
		this.layer = layer - 1;
	}

	public void render(int[][] pixels) {
		render(pixels, screenResolution.width, screenResolution.height, realResolution.x, realResolution.y, location.x,
				location.y);
	}

	public abstract void render(int[][] pixels, int width, int height, double rWidth, double rHeight, double xPos,
			double yPos);

	protected abstract void calculateIterationsAndBailout(double rWidth, double rHeight);

	public void setColorPalette(Palette palette) {
		this.palette = palette;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

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

	public abstract Parameters getParameters();

	public abstract void setParameters(Parameters newProperties);

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

	public static List<String> getLayerTypes() {
		List<String> list = new LinkedList<String>();
		for (Class<? extends Layer> c : fractalRegistry)
			list.add(c.getName());
		return list;
	}

	public static boolean isValidLayer(String type) {
		for (Class c : fractalRegistry)
			if (c.getSimpleName().equals(type + ".java"))
				return true;

		return false;
	}

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

	public static boolean registerFractal(File file) {
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
