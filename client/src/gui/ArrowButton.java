package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

public class ArrowButton<E> {
	
	private boolean down; //if true, points down. else, points up
	
	private boolean selected; //if true, diamond is white. Else is black.
	
	private Color squareColor;
	
	private Point location;
	
	private E data; //stores some data type
	
	public ArrowButton() {
		
	}
	
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
