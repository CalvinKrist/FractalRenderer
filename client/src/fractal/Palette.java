package fractal;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Palette implements Serializable {
	
	private static final long serialVersionUID = -816922891626500737L;
	private Color[] gradient;
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
	
	public Color colorAt(int i) {
		return gradient[i];
	}
	
	public Color colorAt(double d) {
		return gradient[(int)(d * numColors())];
	}
	
	public Color getBackground() {
		return background;
	}
	
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
