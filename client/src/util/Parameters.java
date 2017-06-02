package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This class is meant to simplify the process of storing and loading various parameters from files by proving useful utility methods
 * and constructors. For the most part, however, it is just a standard Map. Each parameter, just like an element of a map, is
 * described by a key and a value. In this case, the kay is always a String and the value is always a Serializable. Each parameter must be a 
 * Serializable because this object is often sent across ObjectPrintStreams.
 * @author 1355710
 *
 */
public class Parameters implements Serializable {
	
	/**
	 * The map where the actual parameters are stored. Each parameter must be a Serializable because
	 * this object is often sent across ObjectPrintStreams.
	 */
	private Map<String, Serializable> parameters;
	
	/**
	 * This constructor will create itself with the parameters described in the map it takes in. 
	 * @param map the map that is used to cotnruct the parameters
	 */
	public Parameters(Map<String, Serializable> map) {
		parameters = map;
	}
	
	/**
	 * This contructor creates itself with no parameters.
	 */
	public Parameters() {
		parameters = new HashMap<String, Serializable>();
	}
	
	/**
	 * This constructor takes in a file path and turns the elements of that file into a series of parameters. It is assumed that 
	 * the file is formated as a DataTag on each line and no more than one DataTag per line. 
	 * @param filePath the path pointing to the file to be read in.
	 */
	public Parameters(String filePath) {
		parameters = new HashMap<String, Serializable>();
		try {
			Scanner s = new Scanner(new File(filePath));
			while(s.hasNextLine()) {
				DataTag tag = new DataTag(s.nextLine());
				parameters.put(tag.getId(), tag.getValue());
			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param name the name of the parameter
	 * @return the parameter as a Serializale
	 */
	public Serializable getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * @param key the key to a parameter
	 * @return whether or not this object contains a parameter pointed to by key
	 */
	public boolean contains(String key) {
		return parameters.containsKey(key);
	}
	
	/**
	 * Similar to the getParameter(String name) method. The difference is that this one will return the parameter as a specific type,
	 * removing the need to cast it afterwards. For example, a call of getParameter("zoom", Double.class) would return an object of
	 * type Double.
	 * @param name the name of the parameter to be returned
	 * @param <E> the type of object the parameter should be returned as
	 * @param c the object type the parameter should be returned as
	 * @return the parameter named'name' as type c
	 */
	public <E> E getParameter(String name, Class<E> c) {
		return (E) parameters.get(name);
	}
	
	/**
	 * adds a new parameter to the internal Map of parameters
	 * @param key the name of the parameter
	 * @param value the value of the parameter
	 */
	public void put(String key, Serializable value) {
		parameters.put(key, value);
	}
	
	/**
	 * removes and returns a parameter
	 * @param name the name of the parameter to be deleted and returned
	 * @return the parameter that was deleted
	 */
	public Serializable removeParameter(String name) {
		return parameters.remove(name);
	}
	
	/**
	 * Similar to the removeParameter(String name) method, except it will return the parameter as a specific type
	 * removing the need to cast it afterwards. For example, a call of removeParameter("zoom", Double.class) would return an object of
	 * type Double.
	 * @param name the name of the parameter to be deleted and returned.
	 * @param <E> the type of object the parameter should be returned as
	 * @param c the type of object the parameter should be returned as
	 * @return the parameter that was deleted
	 */
	public <E> E removeParameter(String name, Class<E> c) {
		return (E) parameters.remove(name);
	}
	
	/**
	 * @return an iterator that goes over all the parameters
	 */
	public Iterator<String> keyIterator() {
		return parameters.keySet().iterator();
	}

	/**
	 * @return the set of keys for all the parameters
	 */
	public Set<String> keySet() {
		return parameters.keySet();
	}
	
	/**
	 * @return the number of parameters stored by this instance
	 */
	public int getSize() {
		return parameters.size();
	}
	
	/**
	 * Prints the contents of the parameters as a series of DataTags, one on each line. It follows the format &lt;key:value&gt;
	 */
	public String toString() {
		String s = "";
		for(String key : keySet()) {
			s += "<" + key + ":" + parameters.get(key) + ">\r\n";
		}
		return s;
	}
	
}
