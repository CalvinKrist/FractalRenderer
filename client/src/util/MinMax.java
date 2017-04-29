package util;

import java.io.Serializable;

public class MinMax implements Serializable {
	
	public Double min = null, max = null;
	
	public void update(double d) {
		if(min == null || d < min)
			min = new Double(d);
		else if(max == null || d > max)
			max = new Double(d);
	}
	
	public double getRange() {
		return max - min;
	}
	
	public String toString() {
		return "min: " + min + "   max: " + max;
	}

}
