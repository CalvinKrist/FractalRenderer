package application;

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

/**
 * A menu for selecting the color of a color arrow
 * @author Calvin
 *
 */
@SuppressWarnings("serial")
public class ColorArrowMenu extends JPanel {
	
	/**
	 * Creates the color chooser menu for the square button of a palate that
	 * represents the inside of a fractal
	 * @param button the square button whose parameters will be modifed by the menu
	 * @param colorList a list of all color arrows
	 * @param p the panel calling this method. This is passed to the menu so it can be updated if the buttons data is modified
	 */
	public ColorArrowMenu(SquareButton button, List<ArrowButton<Color>> colorList, JPanel p) {
		this.setLayout(new BorderLayout());
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setPreviewPanel(new JPanel());
		this.add(colorChooser, BorderLayout.CENTER);
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Color newColor = colorChooser.getColor();
				button.setData(newColor);
				p.repaint();
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
				p.repaint();
			}
		});
		JButton b2 = new JButton("SELECT");
		b2.setFont(Constants.mediumFont);
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.dispose();
				p.repaint();
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
		f.setFocusable(true);
		f.requestFocus();
	}
	
	/**
	 * Creates the color chooser menu for the arrow button that represents a point on the palette
	 * @param button the arrow button whose parameters will be modifed by the menu
	 * @param colorList a list of all color arrows
	 * @param p the panel calling this method. This is passed to the menu so it can be updated if the buttons data is modified
	 */
	public ColorArrowMenu(ArrowButton<Color> button, List<ArrowButton<Color>> colorList, JPanel p) {
		this.setLayout(new BorderLayout());
		JColorChooser colorChooser = new JColorChooser();
		colorChooser.setPreviewPanel(new JPanel());
		this.add(colorChooser, BorderLayout.CENTER);
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Color newColor = colorChooser.getColor();
				button.setSquareColor(newColor);
				button.setData(newColor);
				p.repaint();
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
				p.repaint();
			}
		});
		JButton b2 = new JButton("SELECT");
		b2.setFont(Constants.mediumFont);
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f.dispose();
				p.repaint();
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
		f.setFocusable(true);
		f.requestFocus();
	}
	
}