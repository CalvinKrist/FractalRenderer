package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class WindowTester {

	public static void main(String[] args) {

		Window w = new Window(new Dimension(600, 200));
		JFrame f = new JFrame();
		f.setContentPane(w);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setVisible(true);
		
		while(true) {
			w.repaint();
		}
		
	}

}
