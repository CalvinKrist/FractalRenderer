package menus;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import server.Job;

/**
 * Represents a job in the NetowkrDisplay. Currently incomplete and disfunctional.
 * @author Calvin
 *
 */
public class JobElement extends JPanel {
	
	public JobElement(Job b) {
		//this.add(new JLabel("TEEEEEESTING TESTING TESTING"));
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}

}
