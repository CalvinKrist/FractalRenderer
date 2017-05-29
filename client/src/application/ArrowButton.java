package application;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * 
 * @author Calvin
 *
 *A button resembling a triangle with a square at one end. It can be oriented
 *either up or down and the color of the square can be changed. It can
 *store and return a data type. It waS built specifically for use in the
 *gradient editor and is not fit for other use.
 *
 * @param <E> 
 */
public class ArrowButton<E extends Serializable> implements Serializable {
	
	/**
	 * Describes the orientation of the button.
	 */
	private boolean down; //if true, points down. else, points up
	
	/**
	 * Describes whether or not the button is selected
	 */
	private boolean selected; //if true, diamond is white. Else is black.
	
	/**
	 * The color of the square
	 */
	private Color squareColor;
	
	/**
	 * The buttons position on the screen
	 */
	private Point location;
	
	/**
	 * the x location of the button on the palette
	 */
	private int x;
	
	/** 
	 * the data the button contains
	 */
	private E data; 
	
	/**
	 * This constructor does nothing. All parameters must be set using the setter methods,
	 */
	public ArrowButton() {
	
	}
	
	/**
	 * Draws the arrow on the screen
	 * @param g a graphics object used to draw the arrow
	 */
	public void draw(Graphics2D g) {
		Color c = selected ? Color.white : Color.BLACK;

		if(down) {
			g.setColor(squareColor);
			g.fillRect(location.x - 6, location.y - 12 - 9, 13, 12);
			g.setColor(Color.black);
			g.drawRect(location.x - 6, location.y - 12 - 9, 12, 12);
			
			int[] xPoints = {location.x, location.x - 6, location.x + 6};
			int[] yPoints = {location.y, location.y - 9, location.y - 9};
			g.setColor(c);
			g.fillPolygon(xPoints, yPoints, 3);
			g.setColor(Color.black);
			g.drawPolygon(xPoints, yPoints, 3);
		} else {
			int[] xPoints = {location.x, location.x - 6, location.x + 6};
			int[] yPoints = {location.y, location.y + 9, location.y + 9};
			g.setColor(c);
			g.fillPolygon(xPoints, yPoints, 3);
			g.setColor(Color.black);
			g.drawPolygon(xPoints, yPoints, 3);
			
			g.setColor(squareColor);
			g.fillRect(location.x - 6, location.y + 9, 13, 12);
			g.setColor(Color.black);
			g.drawRect(location.x - 6, location.y + 9, 12, 12);
		}
	}
	
	/**
	 * @param p a point on the screen that was clicked
	 * @return whether or not the arrow contains the point
	 */
	public boolean isClicked(Point p) {
		if(down) {
			int[] xPoints = {location.x, location.x - 6, location.x + 6};
			int[] yPoints = {location.y, location.y - 9, location.y - 9};
			Polygon poly = new Polygon(xPoints, yPoints, 3);
			Rectangle r = new Rectangle(location.x - 6, location.y - 12 - 9, 13, 12);
			return poly.contains(p) || r.contains(p);
		}
		
		int[] xPoints = {location.x, location.x - 6, location.x + 6};
		int[] yPoints = {location.y, location.y + 9, location.y + 9};
		Polygon poly = new Polygon(xPoints, yPoints, 3);
		Rectangle r = new Rectangle(location.x - 6, location.y + 9, 13, 12);
		return poly.contains(p) || r.contains(p);
	}
	
	/**
	 * 
	 * @param p a point on the screen that was clicked
	 * @return whether or not the square was clicked
	 */
	public boolean isSquareClicked(Point p) {
		Rectangle r = null;
		if(down)
			r = new Rectangle(location.x - 6, location.y - 12 - 9, 13, 12);
		else
			r = new Rectangle(location.x - 6, location.y + 9, 13, 12);
		return r.contains(p);
	}
	
	/* GETTERS AND SETTERS */
	/**
	 * @return whether or not the button points downwards. If falsem the button points upwards.
	 */
	public boolean isDown() {
		return down;
	}
	/**
	 * @param down the new value for down. If true, the button will point down. If false, the button will point up.
	 */
	public void setDown(boolean down) {
		this.down = down;
	}
	/**
	 * @return whether or not the button is selected
	 */
	public boolean isSelected() {
		return selected;
	}
	/**
	 * @param selected the new value for whether or not this button is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	/**
	 * @return the color of the square part of the button
	 */
	public Color getSquareColor() {
		return squareColor;
	}
	/**
	 * @param squareColor a new color for the square section of the button
	 */
	public void setSquareColor(Color squareColor) {
		this.squareColor = squareColor;
	}
	/**
	 * @param x the new x value of the button. This value is how the palette keeps track of which order
	 * all the buttons are in. This value does NOT represent the x location of the button on the screen.
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the x value of the button. This value is how the palette keeps track of which order
	 * all the buttons are in. This value does NOT represent the x location of the button on the screen.
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return the location of the button on the screen.
	 */
	public Point getLocation() {
		return location;
	}
	/**
	 * @param location a new location for the button on the screen
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	/**
	 * @return the data that the button stores.
	 */
	public E getData() {
		return data;
	}
	/**
	 * @param data can be used to set custom data for the button to store.
	 */
	public void setData(E data) {
		this.data = data;
	}

}
