package util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Parameters implements Serializable {
	
	private Map<String, Serializable> parameters;
	
	public Parameters(Map<String, Serializable> map) {
		parameters = map;
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
	
	public String toString() {
		String s = "";
		for(String key : keySet()) {
			s += "[" + key + ":" + parameters.get(key) + "]";
		}
		return s;
	}
	
}
