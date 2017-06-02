package application;

public class MetaLayer {
	private String name, type;
	private boolean delete;
	private int opacity;

	public MetaLayer(String name, String type) {
		this.name = name;
		this.type = type;
		delete = false;
	}

	public MetaLayer(String name, String type, boolean delete) {
		this.name = name;
		this.type = type;
		this.delete = delete;
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

	public int getOpacity() {
		return opacity;
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}


}
