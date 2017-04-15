package util;

import java.awt.Color;
import java.util.ArrayList;

public class Utils {	
	
	public static Color interpolateColors(Color color1, Color color2, int stepCount, int totalSteps) {
		int red = (color2.getRed() - color1.getRed()) * stepCount / totalSteps;
		int green = (color2.getGreen() - color1.getGreen()) * stepCount / totalSteps;
		int blue = (color2.getBlue() - color1.getBlue()) * stepCount / totalSteps;
		
		return new Color(color1.getRed() + red, color1.getGreen() + green, color1.getBlue() + blue);
	}
	
	public static Double interpolateDouble(Double d1, Double d2, int stepCount, int totalSteps) {
		return (d2 - d1) * stepCount / totalSteps + d1;
	}
	
	public static Color[] getColorPalate() {
		int numColors = 1000;
		Color[] palate = new Color[numColors];
		int limit = (int)(numColors * 0.8);
		for(int i = 0; i < limit; i++)
			palate[i] = Utils.interpolateColors(new Color(0, 0, 0), new Color(120, 0, 120), i, limit);
		for(int i = limit; i < numColors; i++) {
			palate[i] = Utils.interpolateColors(palate[limit - 1], new Color(255, 255, 0), i - limit, numColors - limit);
		}
		return palate;
	}
	
	public static double[][] mergeSort(double[][] array) {
		int i = 0;
        if (array.length > 1) {
            // split array into two halves
            double[][] left = leftHalf(array);
            double[][] right = rightHalf(array);
            
            // recursively sort the two halves
            mergeSort(left);
            mergeSort(right);
            
            // merge the sorted halves into a sorted whole
            merge(array, left, right);
        }
        return array;
    }
    
    // Returns the first half of the given array.
    private static double[][] leftHalf(double[][] array) {
        int size1 = array.length / 2;
        double[][] left = new double[size1][3];
        for (int i = 0; i < size1; i++) {
            left[i] = array[i];
        }
        return left;
    }
    
    // Returns the second half of the given array.
    private static double[][] rightHalf(double[][] array) {
        int size1 = array.length / 2;
        int size2 = array.length - size1;
        double[][] right = new double[size2][3];
        for (int i = 0; i < size2; i++) {
            right[i] = array[i + size1];
        }
        return right;
    }
    
    // Merges the given left and right arrays into the given 
    // result array.  Second, working version.
    // pre : result is empty; left/right are sorted
    // post: result contains result of merging sorted lists;
    private static void merge(double[][] result, 
    						  double[][] left, double[][] right) {
        int i1 = 0;   // index into left array
        int i2 = 0;   // index into right array
        
        for (int i = 0; i < result.length; i++) {
            if (i2 >= right.length || (i1 < left.length && 
                    left[i1][0] <= right[i2][0])) {
                result[i] = left[i1];    // take from left
                i1++;
            } else {
                result[i] = right[i2];   // take from right
                i2++;
            }
        }
    }

}
