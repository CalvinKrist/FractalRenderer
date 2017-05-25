package util;

import java.io.Serializable;

public class Point implements Serializable {
	
	public double x, y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(String s) {
		String[] params = s.split(",");
		x = Double.valueOf(params[0]);
		y = Double.valueOf(params[1]);
	}
 	
	public Point() {
		x = 0; 
		y = 0;
	}
	
	public String toString() {
		return x + "," + y;
	}
	
	public Point(java.awt.Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double d) {
		x = d;
	}
	
	public void setY(double d) {
		y = d;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
