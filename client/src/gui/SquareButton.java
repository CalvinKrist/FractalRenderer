package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class SquareButton {
	
	private Color data;
	private int size;
	private Point position; //position of center of rectangle
	
	public SquareButton(int size, Point position) {
		this.size = size;
		this.position = position;
		data = Color.BLACK;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(data);
		g.setStroke(new BasicStroke(1));
		g.fillRect(position.x - size / 2, position.y - size / 2, size, size);
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(2));
		g.drawRect(position.x - size / 2, position.y - size / 2, size, size);
	}
	
	public Color getData() {
		return data;
	}
	
	public void setData(Color c) {
		data = c;
	}
	
	public boolean isClicked(Point p) {
		Rectangle r = new Rectangle(position.x - size / 2, position.y - size / 2, size, size);
		return r.contains(p);
	}
	
	public Point getLocation() {
		return position;
	}
 
}
