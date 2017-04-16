package util;

public class DataTag {
	
	private String id, value;
	
	public DataTag(String id, String value) {
		this.id = id;
		this.value = value;
	}
	
	public DataTag(String dataTag) {
		this.id = dataTag.substring(1, dataTag.indexOf(":"));
		this.value = dataTag.substring(dataTag.indexOf(":") + 1, dataTag.length() - 1);
	}
	
	public String getId() {
		return id;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return "<" + id + ":" + value + ">";
	}

}
