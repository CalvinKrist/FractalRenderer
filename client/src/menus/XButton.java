package menus;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A simple JButton that displays an X for a texture instead of words. I created this class
 * because I was using this button often and the initialization code was getting repetitive.
 * @author Calvin
 *
 */
public class XButton extends JButton {
	
	/**
	 * This constructor sets an action listener and creates the button so that it displays the x image
	 * and doesn't have many of the other normal JButton things like showing a border when clicked
	 * @param listener the action listener this button should have
	 */
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
