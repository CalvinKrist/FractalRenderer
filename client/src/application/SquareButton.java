package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * A square button built for use with the palette chooser only. It can either represent and display the color if the inside
 * of the fractal or display an image. It contains methods to see if it is being clicked but contains no behaviour for what to
 * do in such a situation.
 * @author Calvin
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
	 * @param size the size of one edge of the square button in pixels
	 * @param position the position in pixels of the button on the screen
	 */
	public SquareButton(int size, Point position) {
		this.size = size;
		this.position = position;
		data = Color.BLACK;
		img = null;
	}
	
	/**
	 * draws the button. If it stores an image, it will draw that. Otherwise, it will fill itself with the color
	 * it stores. The image will be cropped to match the size of the button and if the image is smaller that the
	 * button I think there will be an error, but I haven't tested it.
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
	
	/**
	 * returns the color stored and displayed by this button
	 * @return the color stored and displayed by this button
	 */
	public Color getData() {
		return data;
	}
	
	/**
	 * takes in the new image for the button to display. The image should be square and have the same dimensions of the button.
	 * Otherwise, it will not fit properly and could even throw exceptions.
	 * @param img the new image for the button to display. The image should be square and have the same dimensions of the button.
	 * Otherwise, it will not fit properly and could even throw exceptions.
	 */
	public void setImage(BufferedImage img) {
		this.img = img;
	}
	
	/**
	 * takes in the new color for the button to store and display.
	 * @param c the new color for the button to store and display.
	 */
	public void setData(Color c) {
		data = c;
	}
	
	/**
	 * returns whether or not the point passed this method is inside the button
	 * @param p the point that is being checked
	 * @return whether the point is inside the button
	 */
	public boolean isClicked(Point p) {
		Rectangle r = new Rectangle(position.x - size / 2, position.y - size / 2, size, size);
		return r.contains(p);
	}
	
	/**
	 * returns the location of the button on the screen
	 * @return the location of the button on the screen
	 */
	public Point getLocation() {
		return position;
	}
 
}
