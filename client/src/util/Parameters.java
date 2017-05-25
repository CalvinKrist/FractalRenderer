package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Parameters implements Serializable {
	
	private Map<String, Serializable> parameters;
	
	public Parameters(Map<String, Serializable> map) {
		parameters = map;
	}
	
	public Parameters(String filePath) {
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
	
	public Serializable getParameter(String name) {
		return parameters.get(name);
	}
	
	public boolean contains(String key) {
		return parameters.containsKey(key);
	}
	
	public <E> E getParameter(String name, Class<E> c) {
		return (E) parameters.get(name);
	}
	
	public void put(String key, Serializable value) {
		parameters.put(key, value);
	}
	
	public Serializable removeParameter(String name) {
		return parameters.remove(name);
	}
	
	public <E> E removeParameter(String name, Class<E> c) {
		return (E) parameters.remove(name);
	}
	
	public Iterator<String> keyIterator() {
		return parameters.keySet().iterator();
	}

	public Set<String> keySet() {
		return parameters.keySet();
	}
	
	public int getSize() {
		return parameters.size();
	}
	
	public String toString() {
		String s = "";
		for(String key : keySet()) {
			s += "<" + key + ":" + parameters.get(key) + ">";
		}
		return s;
	}
	
}
