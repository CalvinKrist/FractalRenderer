package util;

import java.io.Serializable;

public class Parameter <E extends Serializable> implements Serializable {
	
	private int currentParameter;
	private E[] parameters;
	
	public Parameter(E[] parameters) {
		this.parameters = parameters;
		currentParameter = 0;
	}
	
	public E getNextParameter() {
		if(currentParameter >= parameters.length)
			currentParameter = 0;
		return parameters[currentParameter++];
	}
	
	public int getNumParameters() {
		return parameters.length;
	}
	
	public String toString() {
		String s = "";
		for(E e : parameters)
			s += "[" + e + "]";
		return s;
	}

}
