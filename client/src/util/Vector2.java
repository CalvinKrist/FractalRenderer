package util;

import java.io.Serializable;

public class Vector2 implements Serializable {
	
	public double x;
	public double y;
	
	public Vector2() {
		x = 0;
		y = 0;
	}
	
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	public Vector2 subtract(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);
	}
	
	public String toString() {
		return x + ", " + y;
	}

}
