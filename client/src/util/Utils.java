package util;

import java.awt.Color;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import fractal.Palette;

/**
 * A series of public static methods that are written to provide utility to other classes
 * @author Calvin
 *
 */
public class Utils {

	/**
	 * Will return a color evenly interpolated between two others assuming one is stepCount between them out of totalSteps. This
	 * method uses linear interpolation
	 * @param color1 the first color. If stepCount was 0, this color would be returned.
	 * @param color2 the second color. If stepCount equaled totalSteps, this color would be returned.
	 * @param stepCount the amount of steps one is between the two colors
	 * @param totalSteps the total amount of steps between the two colors
	 * @return a new color which is the result of interpolating the two colors
	 */
	public static Color interpolateColors(Color color1, Color color2, double stepCount, double totalSteps) {
		return interpolateColors(color1, color2, stepCount / totalSteps);
	}

	/**
	 * Will return a color evenly interpolated between the two assuming one is fraction% of the way between them. This method uses
	 * linear interpolation.
	 * @param color1 the first color. if fraction is 0, this color will be returned.
	 * @param color2 the second color. if fraction is 1, this color will be returned.
	 * @param fraction the percent of the way between the two colors where interpolation needs to take place
	 * @return a new Color which is the result of interpolating the two colors
	 */
	public static Color interpolateColors(Color color1, Color color2, double fraction) {
		int red = (int) ((color2.getRed() - color1.getRed()) * fraction);
		int green = (int) ((color2.getGreen() - color1.getGreen()) * fraction);
		int blue = (int) ((color2.getBlue() - color1.getBlue()) * fraction);
		int a = (int) ((color2.getAlpha() - color1.getAlpha()) * fraction);
		return new Color(color1.getRed() + red, color1.getGreen() + green, color1.getBlue() + blue,
				color1.getAlpha() + a);
	}

	/**
	 * Interpolates two doubles using linear interpolation assuming one is (stepCount / totalSteps)% between the two colors
	 * @param d1 the first number. if stepCount is 0, this number will be returned.
	 * @param d2 the second number. If stepCount equals totalSteps, this number will be returned.
	 * @param stepCount the amount of steps one is between the two colors
	 * @param totalSteps the total amount of steps between the two colors
	 * @return a double which is the result of linearly interpolating the two doubles
	 */
	public static Double interpolateDouble(Double d1, Double d2, int stepCount, int totalSteps) {
		return (d2 - d1) * stepCount / totalSteps + d1;
	}

	/**
	 * This method finds and returns the broadcast InetAddress of the specific router being accessed
	 * @return the broadcast InetAddress of the specific router being accessed
	 */
	public static InetAddress getBroadcastAddress() {

		try {

			Set<InetAddress> set = new LinkedHashSet<>();
			Enumeration<NetworkInterface> nicList = NetworkInterface.getNetworkInterfaces();

			for (; nicList.hasMoreElements();) {
				NetworkInterface nic = nicList.nextElement();
				if (nic.isUp() && !nic.isLoopback()) {
					for (InterfaceAddress ia : nic.getInterfaceAddresses())
						set.add(ia.getBroadcast());
				}
			}
			return Arrays.asList(set.toArray(new InetAddress[0])).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * This method doesn't really belong in this class because it is used only by the HistogramLayer class. Also, now that I know I can just 
	 * call List.sort(new Comparator...); I would not write this again.
	 * Anyways, this method applies merge sort to a 2D array of doubles, sorting them based on a specific value of one element of the grid.
	 * @param array the array to be sorted
	 * @return the sorted array
	 */
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
	// result array. Second, working version.
	// pre : result is empty; left/right are sorted
	// post: result contains result of merging sorted lists;
	private static void merge(double[][] result, double[][] left, double[][] right) {
		int i1 = 0; // index into left array
		int i2 = 0; // index into right array

		for (int i = 0; i < result.length; i++) {
			if (i2 >= right.length || (i1 < left.length && left[i1][0] <= right[i2][0])) {
				result[i] = left[i1]; // take from left
				i1++;
			} else {
				result[i] = right[i2]; // take from right
				i2++;
			}
		}
	}

}
