package util;

import java.io.Serializable;

/**
 * This class provides an easy way to represent and read pairs of data. This is incredibly useful when loading in parameters.
 * 
 * All DataTags follow this format: <key:value>. An example use of this is in how fractals are read into the program.
 * A fractal is saved as a lot of different DataTags on each line, and then a Scanner reads each line and each DataTag. Then, when, say, the 
 * <bailout:1000> DataTag apears, it's easy to read that it describes the bailout value and the value is 1000.
 * @author 1355710
 *
 */
public class DataTag implements Serializable {
	
	private String id, value;
	
	/**
	 * A constructor for a DataTag which is used when one has two data elements that have String representations, but they are not
	 * yet combined into a DataTag.
	 * @param id the first element of the DataTag. 
	 * @param value the second element of the DataTag
	 */
	public DataTag(String id, String value) {
		this.id = id;
		this.value = value;
	}
	
	/**
	 * This constructor is used when one has a String representation of a DataTag and needs to turn it into a real DataTag,
	 * often to easily access the data stored in the String. For example, a fractal is saved as a series of DataTags, each on a 
	 * different line. A scanner reads these lines in one at a time and uses that line and this constructor to create a DataTag. After that,
	 * the data stored in the original String can be easily accessed.
	 * The format of any String this constructor takes in is as follows: <key:value>
	 * @param dataTag the String representation of a DataTag
	 */
	public DataTag(String dataTag) {
		this.id = dataTag.substring(1, dataTag.indexOf(":"));
		this.value = dataTag.substring(dataTag.indexOf(":") + 1, dataTag.length() - 1);
	}
	
	/**
	 * @return the first element of the DataTag
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the second element of the DataTag
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 *@return the DataTag in String format, following the format <id:value>
	 */
	public String toString() {
		return "<" + id + ":" + value + ">";
	}

}
