package admin;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;

public class NetworkView extends JPanel {
	
	//TODO: computer stats
	private ArrayList<NetworkElement> elements;
	
	public NetworkView() {
		this.setPreferredSize(new Dimension(Display.DISPLAY_WIDTH, Display.DISPLAY_HEIGHT));
	}

}
