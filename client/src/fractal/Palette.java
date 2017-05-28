package fractal;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
/**
 * Represents a palette that layers will use to color themselves. It stores a color gradient and a color value for the inside of fractals.
 * @author Calvin
 *
 */
public class Palette implements Serializable {
	
	private static final long serialVersionUID = -816922891626500737L;
	/**
	 * an array of colors representing the color gradient of the palette
	 */
	private Color[] gradient;
	
	/**
	 * The color of inside parts of fractals
	 */
	private Color background;
	
	public Palette(Color[] gradient, Color color) {
		this.gradient = gradient;
		this.background = color;
	}
	
	public Palette(String filePath, boolean remove) {
		try {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(filePath));
			Palette p = ((Palette)(o.readObject()));
			this.gradient = p.gradient;
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
	
	public void setGradient(Color[] gradient) {
		this.gradient = gradient;
	}
	
	public void setBackground(Color background) {
		this.background = background;
	}
	
	public int numColors() {
		return gradient.length;
	}
	
	/**
	 * @param i an index in the gradient
	 * @return
	 */
	@Deprecated
	public Color colorAt(int i) {
		return gradient[i];
	}
	
	/**
	 * @param d a proportion representing a percent of the gradient
	 * @return the color at the specified proportion of the gradient
	 */
	public Color colorAt(double d) {
		return gradient[(int)(d * numColors())];
	}
	
	public Color getBackground() {
		return background;
	}
	
	/**
	 * Saves the palette as a .palette file
	 * @param filePath the file path where the palette should save itself to
	 */
	public void writeTo(String filePath) {
		try {
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(filePath));
			s.writeObject(this);
			s.flush();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
