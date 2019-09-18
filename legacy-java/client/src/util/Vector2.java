package util;

import java.io.Serializable;

/**
 * This class stores a 2 dimensional Vector and provides a few methods for manipulating that vector. There are many more
 * methods for vector manipulation that could be created, but if that is necessary there are numerous libraries online--
 * especially scientific ones--that provide well build Vector classe.s
 * @author Calvin
 *
 */
public class Vector2 implements Serializable {
	
	/**
	 * The length of the vector along the x axis
	 */
	public double x;
	/**
	 * the length of the vector along the y axis
	 */
	public double y;

	/**
	 * Creates a new vector with a default length of 0
	 */
	public Vector2() {
		x = 0;
		y = 0;
	}
	
	/**
	 * Creates a new vector with the x and y length provided.
	 * @param x the x length of the vector
	 * @param y the y length of the vector
	 */
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * Subtracts the vector passed to it from the current vector
	 * @param other the vector to be subtracted from this one
	 * @return the difference between the two vectors
	 */
	public Vector2 subtract(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);
	}
	
	/**
	 * @return a String representation of a vector. It follows the format x, y
	 */
	public String toString() {
		return x + ", " + y;
	}

}
