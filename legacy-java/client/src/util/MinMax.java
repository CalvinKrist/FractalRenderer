package util;

import java.io.Serializable;

/**
 * This is a utility class used to simplify the process of keeping track and getting the minimum and maximum values of a set of
 * numerical data. This was created in the context of Layers, which iteratively go through thousands of data points and calculate
 * various values for them/
 * @author Calvin
 *
 */
public class MinMax implements Serializable {
	
	/**
	 * The minimum value of the data set
	 */
	public Double min = null;
	/**
	 * The maximum value of the data set
	 */
	public Double max = null;
	
	/**
	 * Every time a new value of the data set is calculated, the MinMax needs to be updated with that value.
	 * This method will check to see if the new value is less than the minimum or higher than the maximum and, if so,
	 * updates the minimum and maximum values of the data set.
	 * @param d the new value of the data set 
	 */
	public void update(double d) {
		if(min == null || d < min)
			min = new Double(d);
		else if(max == null || d > max)
			max = new Double(d);
	}
	
	/**
	 * @return the range of the data set
	 */
	public double getRange() {
		return max - min;
	}
	
	/**
	 * Prints itself for debugging puposes. It follows the format "min: " + min + "   max: " + max  
	 */
	public String toString() {
		return "min: " + min + "   max: " + max;
	}

}
