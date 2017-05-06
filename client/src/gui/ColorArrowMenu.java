package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.Constants;

public class ColorArrowMenu extends JPanel {
	
	public ColorArrowMenu(ArrowButton<Color> button, List<ArrowButton<Color>> colorList) {
		this.setLayout(new BorderLayout());
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setPreviewPanel(new JPanel());
		this.add(colorChooser, BorderLayout.CENTER);
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Color newColor = colorChooser.getColor();
				button.setSquareColor(newColor);
				button.setData(newColor);
			}
		});
		JFrame f = new JFrame();
		JButton b = new JButton("DELETE");
		b.setFont(Constants.mediumFont);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(colorList.size() != 1)
					colorList.remove(button);
				f.dispose();
			}
		});
		JButton b2 = new JButton("SELECT");
		b2.setFont(Constants.mediumFont);
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.dispose();
			}
		});
		JPanel temp = new JPanel();
		temp.setLayout(new GridLayout(1, 2));
		temp.add(b2, 0, 0);
		temp.add(b, 0, 1);
		this.add(temp, BorderLayout.SOUTH);
		f.setContentPane(this);
		f.setVisible(true);
		f.pack();
		Point location = new Point(button.getLocation());
		location.x += f.getWidth() / 1.6;
		if(location.x + f.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().width)
			location.x -= f.getWidth() * 2 / 1.75;
		f.setLocation(location);
		f.setResizable(false);
	}
	
}
