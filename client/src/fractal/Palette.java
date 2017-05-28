package fractal;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import application.ArrowButton;
import application.Window;
import util.Constants;
import util.Utils;
/**
 * Represents a palette that layers will use to color themselves. It stores a color gradient and a color value for the inside of fractals.
 * @author Calvin
 *
 */
public class Palette implements Serializable {
	
	private static final long serialVersionUID = -816922891626500737L;
	
	/**
	 * A list of all arrow buttons that store color data. Each button represents a point on the gradient
	 */
	private List<ArrowButton<Color>> colorList;
	
	/**
	 * A list of all arrow buttons that store opacity data. Each button represents a point on the gradient.
	 */
	private List<ArrowButton<Double>> opacityList;
	
	/**
	 * The color of inside parts of fractals
	 */
	private Color background;
	
	public int size;
	
	public Palette() {
		size = 1000;
		background = new Color(0, 0, 0);
		opacityList = new ArrayList<ArrowButton<Double>>();
		colorList = new ArrayList<ArrowButton<Color>>();
		ArrowButton<Double> ob1 = new ArrowButton<Double>();
		ob1.setData(1.0);
		ob1.setX(0);
		ArrowButton<Double> ob2 = new ArrowButton<Double>();
		ob2.setData(1.0);
		ob2.setX(size);
		opacityList.add(ob2);
		opacityList.add(ob1);
		
		ArrowButton<Color> cb1 = new ArrowButton<Color>();
		cb1.setData(Color.black);
		cb1.setX(0);
		ArrowButton<Color> cb2 = new ArrowButton<Color>();
		cb2.setData(Color.white);
		cb2.setX(1000);
		colorList.add(cb1);
		colorList.add(cb2);
		
		this.sortColorList();
		this.sortOpacityList();
	}
	
	public Palette(String filePath, boolean remove) {
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(filePath));
			Palette p = ((Palette)(o.readObject()));
			this.size = p.size;
			this.colorList = p.colorList;
			this.opacityList = p.opacityList;
			this.background = p.background;
			o.close();
			if(remove) {
				File f = new File(filePath);
				if(!f.delete())
					System.out.println(filePath + " failed to delete.");
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<ArrowButton<Double>> getOpacityList() {
		return opacityList;
	}
	
	public List<ArrowButton<Color>> getColorList() {
		return colorList;
	}
	
	/**
	 * @param x
	 * @return a percent opacity at that point
	 */
	public Double getOpacityAtPoint(int x) {
		if(opacityList.size() == 1)
			return opacityList.get(0).getData();
		if(x <= opacityList.get(0).getLocation().x)
			return opacityList.get(0).getData();
		for(int i = 0; i < opacityList.size() - 1; i++) {
			if(x >= opacityList.get(i).getLocation().x && x < opacityList.get(i + 1).getLocation().x)
				return Utils.interpolateDouble(opacityList.get(i).getData(), opacityList.get(i + 1).getData(), x - opacityList.get(i).getLocation().x, opacityList.get(i + 1).getLocation().x - opacityList.get(i).getLocation().x);
		}
		return opacityList.get(opacityList.size() - 1).getData();
	}
	
	public void sortOpacityList() {
		opacityList.sort((Object o1, Object o2) -> {
			ArrowButton<?> b1 = (ArrowButton<?>)o1;
			ArrowButton<?> b2 = (ArrowButton<?>)o2;
			int num = b1.getX() < b2.getX() ? -1 : 1;
			return num;
		});
	}
	
	public void sortColorList() {
		colorList.sort((Object o1, Object o2) -> {
			ArrowButton<?> b1 = (ArrowButton<?>)o1;
			ArrowButton<?> b2 = (ArrowButton<?>)o2;
			int num = b1.getX() < b2.getX() ? -1 : 1;
			return num;
		});
	}
	
	public Color getColorAtPoint(int x) {
		if(colorList.size() == 1)
			return colorList.get(0).getData();
		if(x <= colorList.get(0).getLocation().x)
			return colorList.get(0).getData();
		for(int i = 0; i < colorList.size() - 1; i++) {
			if(x >= colorList.get(i).getLocation().x && x < colorList.get(i + 1).getLocation().x)
				return Utils.interpolateColors(colorList.get(i).getData(), colorList.get(i + 1).getData(), x - colorList.get(i).getLocation().x, colorList.get(i + 1).getLocation().x - colorList.get(i).getLocation().x);
		}
		return colorList.get(colorList.size() - 1).getData();
	}
	
	public void setBackground(Color background) {
		this.background = background;
	}
	
	/**
	 * @param d a proportion representing a percent of the gradient
	 * @return the color at the specified proportion of the gradient
	 */
	public Color colorAt(double d) {
		Color c = getColorAtPoint(((int)(d * size)));
		int opacity = (int)(getOpacityAtPoint((int)(d * size)) * 255);
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity);
	}
	
	public Color getBackground() {
		return background;
	}
	
	/**
	 * Saves the palette as a .palette file
	 * @param filePath the file path where the palette should save itself to
	 */
	public void writeTo(String name) {
		try {
			File f = new File(Constants.FRACTAL_FILEPATH + "palettes/");
			if(!f.exists())
				f.mkdirs();
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(Constants.FRACTAL_FILEPATH + "palettes/" + name + ".palette"));
			s.writeObject(this);
			s.flush();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
