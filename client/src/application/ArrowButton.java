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
	 * This constructor does nothing
	 */
	public ArrowButton() {
	
	}
	
	/**
	 * Draws the arrow in the screen
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
	public boolean isDown() {
		return down;
	}
	public void setDown(boolean down) {
		this.down = down;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Color getSquareColor() {
		return squareColor;
	}
	public void setSquareColor(Color squareColor) {
		this.squareColor = squareColor;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public E getData() {
		return data;
	}
	public void setData(E data) {
		this.data = data;
	}

}
