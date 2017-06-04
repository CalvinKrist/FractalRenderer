package application;

import java.io.Serializable;

public class MetaParam {
	private String key;
	private Serializable value; 
	public MetaParam(String key, Serializable value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	@Override
	public String toString(){
		return key+": "+value.toString();
	}
	
}
