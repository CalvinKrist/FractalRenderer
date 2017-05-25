package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ArrowEditor extends JPanel {
	
	private Window window;
	private JTextField opacityLocation, colorLocation;
	private JTextField opacityValue;
	private Color colorValue;
	
	public ArrowEditor(Window w, Dimension d) {
		opacityLocation = new JTextField(3);
		colorLocation = new JTextField(3);
		opacityValue = new JTextField(3);		
		window = w;
		
		createMenus();
		this.setBackground(Color.white);
		JFrame f = new JFrame();
		f.setContentPane(this);
		f.pack();
		f.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - d.width / 2 - f.getWidth() - 8, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - f.getHeight() / 2);
		f.setVisible(true);
	}
	
	public void createMenus() {
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Create opacity value label and buttons
		this.add(new JLabel("Opacity:"));
		this.add(opacityValue);
		opacityValue.setEditable(false);
		opacityValue.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		
		//Create button for easy opacity value editing
		JButton b = new JButton();
		b.setIcon(new ImageIcon("rec/arrow.jpg", "Click to edit opacity."));
		this.add(b);
		
		//Create opacity location label and buttons
		this.add(new JLabel("Location:"));
		this.add(opacityLocation);
		opacityLocation.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		opacityLocation.setEnabled(false);
	}
	
	public void deselect() {
		
	}
	
	public void selectButton(ArrowButton b) {
		
	}
	
	private class ColorSliderListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
		}
		
	}

}
