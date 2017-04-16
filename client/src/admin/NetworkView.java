package admin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;

public class NetworkView extends JPanel {
	
	//TODO: computer stats
	
	public NetworkView() {
		this.setPreferredSize(new Dimension(Display.DISPLAY_WIDTH, Display.DISPLAY_HEIGHT));
	}

	@Override
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		g.fillOval(Display.DISPLAY_WIDTH / 2 - 20, 10, 40, 40);
	}
}
