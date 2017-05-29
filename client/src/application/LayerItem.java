package application;

import javafx.scene.control.CheckBoxTreeItem;

public class LayerItem extends CheckBoxTreeItem {

	private MetaLayer m;

	public LayerItem(){
		super();
		System.err.print("Wrong Input:");
	}
	public LayerItem(String display){
		super(display);
		System.err.println("Wrong Input:");
	}
	public LayerItem(MetaLayer m){
	super(m.getName());
	this.m = m;
	}

}
