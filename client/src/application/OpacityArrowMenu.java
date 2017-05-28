package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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

/**
 * Describes a menu for choosing an opacity of an opacity arrow
 * @author Calvin
 *
 */
@SuppressWarnings("serial")
public class OpacityArrowMenu extends JPanel {
	
	/**
	 * Updates the value of the button passed to it to the desired value
	 * @param button the button that represents a point on the palette whose opacity will be modified
	 * @param opacityList a list of all opacity buttons
	 * @param p the panel that will be updated if any changed are made
	 */
	public OpacityArrowMenu(ArrowButton<Double> button, List<ArrowButton<Double>> opacityList, JPanel p) {
		this.setLayout(new BorderLayout());
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(button.getData() * 100));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double prop = slider.getValue() / 100.0;
				button.setData(prop);
				button.setSquareColor(new Color(255 - (int)(255 * prop), 255 - (int)(255 * prop), 255 - (int)(255* prop)));
				p.repaint();
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
		JPanel temp2 = new JPanel();
		temp2.setLayout(new GridLayout(1, 2));
		temp2.add(b2, 0, 0);
		temp2.add(b, 0, 1);
		this.add(temp2, BorderLayout.SOUTH);
		f.setContentPane(this);
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setFocusable(true);
		f.requestFocus();
	}

}
