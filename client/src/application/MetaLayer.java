package application;

public class MetaLayer {
	private String name, type;
	public MetaLayer(String name, String type){
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String toString(){
		return getName();
	}
	
}
