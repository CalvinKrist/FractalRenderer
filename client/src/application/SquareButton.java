package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * A square button built for use with the palette chooser only. It represents the color of the inside of a fractal
 * @author 1355710
 *
 */
public class SquareButton {
	
	/**
	 * The color data the button stores
	 */
	private Color data;
	
	/**
	 * the size of the one of the square button's sides in pixels
	 */
	private int size;
	
	/**
	 * the location of the center of the square on the screen
	 */
	private Point position; //position of center of rectangle
	
	/**
	 * an optional image that can be displayed in the button. Assumes the img is the same size as the button.
	 */
	private BufferedImage img;
	
	/**
	 *constructs the button with the specified parameters
	 * @param size 
	 * @param position
	 */
	public SquareButton(int size, Point position) {
		this.size = size;
		this.position = position;
		data = Color.BLACK;
		img = null;
	}
	
	/**
	 * draws the button
	 * @param g a graphics object so the button can draw itself
	 */
	public void draw(Graphics2D g) {
		if(img == null) {
			g.setColor(data);
			g.setStroke(new BasicStroke(1));
			g.fillRect(position.x - size / 2, position.y - size / 2, size, size);
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(2));
			g.drawRect(position.x - size / 2, position.y - size / 2, size, size);
		} else {
			g.drawImage(img, position.x - size / 2, position.y - size / 2, null);
		}
	}
	
	public Color getData() {
		return data;
	}
	
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	
	public void setData(Color c) {
		data = c;
	}
	
	/**
	 * returns whether or not the point is inside the button
	 * @param p
	 * @return
	 */
	public boolean isClicked(Point p) {
		Rectangle r = new Rectangle(position.x - size / 2, position.y - size / 2, size, size);
		return r.contains(p);
	}
	
	public Point getLocation() {
		return position;
	}
 
}
