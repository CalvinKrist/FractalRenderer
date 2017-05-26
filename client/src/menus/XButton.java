package menus;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class XButton extends JButton {
	
	public XButton(ActionListener listener) {
		super(new ImageIcon("textures/x-button-small.jpg"));
		this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setPressedIcon(new ImageIcon("textures/x-button-small-tinted.jpg"));
		
		this.addActionListener(listener);
	}

}
