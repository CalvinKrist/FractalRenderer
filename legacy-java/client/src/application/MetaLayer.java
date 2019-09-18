package application;

public class MetaLayer {
	private String name, type;
	private boolean delete;
	private double opacity;

	public MetaLayer(String name, String type, double opacity) {
		this.name = name;
		this.type = type;
		this.opacity = opacity;
		delete = false;
	}

	public MetaLayer(String name, String type, double opacity, boolean delete) {
		this.name = name;
		this.type = type;
		this.opacity = 1;
		this.delete = delete;
	}

	public MetaLayer(String name, String type) {
		this.name = name;
		this.type = type;
		this.opacity = 1;
		this.delete = false;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
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

	public String toString() {
		return getName();
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

}
