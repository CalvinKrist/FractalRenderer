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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import util.Constants;

public class OpacityArrowMenu extends JPanel {
	
	public OpacityArrowMenu(ArrowButton<Double> button, List<ArrowButton<Double>> opacityList) {
		this.setLayout(new BorderLayout());
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(button.getData() * 100));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double prop = slider.getValue() / 100.0;
				button.setData(prop);
				button.setSquareColor(new Color(255 - (int)(255 * prop), 255 - (int)(255 * prop), 255 - (int)(255* prop)));
			}
		});
		slider.setMajorTickSpacing(20);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		this.add(slider, BorderLayout.CENTER);
		JLabel label = new JLabel("Opacity");
		label.setFont(Constants.mediumFont);
		JPanel temp = new JPanel();
		temp.add(label);
		this.add(temp, BorderLayout.NORTH);
		JFrame f = new JFrame();
		JButton b = new JButton("DELETE");
		b.setFont(Constants.mediumFont);
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(opacityList.size() != 1)
					opacityList.remove(button);
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
		JPanel temp2 = new JPanel();
		temp2.setLayout(new GridLayout(1, 2));
		temp2.add(b2, 0, 0);
		temp2.add(b, 0, 1);
		this.add(temp2, BorderLayout.SOUTH);
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
