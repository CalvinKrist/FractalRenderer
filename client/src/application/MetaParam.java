package application;

import java.io.Serializable;

public class MetaParam {
	private String key;
	private Serializable value;
	public MetaParam(String key, Serializable value){
		this.key = key;
		this.value = value;
	}

	/**
	 *
	 * @return returns the name of the parameter
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the name of the parameter
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 *
	 * @return the value of the parameter
	 */
	public Serializable getValue() {
		return value;
	}

	/**
	 * Sets the value of the parameter
	 * @param value
	 */
	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	/**
	 * @return returns the name of the paremeter, followed by its value
	 */
	public String toString(){
		return key+": "+value.toString();
	}

}
