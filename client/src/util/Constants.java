package util;

import java.awt.Font;

public class Constants {

	/**
	 * The port that most of the network communications occur on
	 */
	public static final int PORT = 6664;
	/**
	 * The port used by the Client and Server to listen to broadcasts. This is used to that
	 * a server can find clients at an unknown ipAddress.
	 */
	public static final int BROADCAST_PORT = 8888;
	
	/**
	 * The directory where all fractals are saved to
	 */
	public static final String FRACTAL_FILEPATH = "fractals/";
	/**
	 * The directory where all custom layer types are stored as .java files. At the start of each instance, these layer types are
	 * compiled and turned into .class files. Alternatively, .class files can be provided and then there is no need to compile the 
	 * layer type at startup. This drastically reduces loading times.
	 */
	public static final String CUSTOM_FRACTAL_FILEPATH = "custom/";
	
	public static final Font smallFont = new Font("Ariel", 12, 12);
	public static final Font mediumFont = new Font("Ariel", 12, 18);
	public static final Font largeFont = new Font("Ariel", 12, 24);
	
}
