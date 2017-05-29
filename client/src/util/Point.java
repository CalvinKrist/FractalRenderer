package util;

import java.io.Serializable;

/**
 * This class was created as an alternative to the standard util.Point class Java provides because those points only store
 * Integer locations, when Double locations were needed. It should be noted that there is a Point2D.Double class in newer
 * versions of Java that has double precision and more utility than this class. When this class was made, it was not known that
 * Point2D.Double existed.
 * @author Calvin 
 *
 */
public class Point implements Serializable {
	
	/**
	 * The x location stored by this point
	 */
	public double x;
	/**
	 * The y location stored by this point
	 */
	public double y;
	
	/**
	 * @param x the x location this point should have
	 * @param y the y location this point should have
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a point from a String. The String should follow the format x,y where x and y are the numerical x and y positions of the
	 * point respectively.
	 * @param s the string that will be used to create the point
	 */
	public Point(String s) {
		String[] params = s.split(",");
		x = Double.valueOf(params[0]);
		y = Double.valueOf(params[1]);
	}
 	
	/**
	 * A default constructor for the point. It initializes the x and y values to 0
	 */
	public Point() {
		x = 0; 
		y = 0;
	}
	
	/**
	 * @return a String representation of the Point following the format x,y where x and y are the numerical x and y positions of the point 
	 * respectively
	 */
	public String toString() {
		return x + "," + y;
	}
	
	/**
	 * Creates a point from the normal, integer precision point.
	 * @param p the normal integer precision point that will be used to create the double precision point
	 */
	public Point(java.awt.Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}
	
	/**
	 * @return the x location of the point
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return the y location of the point
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @param d the new x location of the point
	 */
	public void setX(double d) {
		x = d;
	}
	
	/**
	 * @param d the new y location of the point
	 */
	public void setY(double d) {
		y = d;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
