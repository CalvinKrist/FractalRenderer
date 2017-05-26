package menus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import server.Job;
import server.Server;
import util.Constants;

public class NetworkElement extends JPanel {
	
	private InetAddress address;
	private LinkedList<JobElement> jobs;
	
	private JLabel displayName;
	private XButton xButton;
	
	private Server server;
	
	public static final int HEIGHT = 50;
	
	public NetworkElement(Server server, NetworkView view) {
		this.server = server;
		this.setBackground(Color.white);		
		this.setPreferredSize(new Dimension(Display.DISPLAY_WIDTH, 15));
		this.setLayout(new GridBagLayout());
		
		this.jobs = new LinkedList<JobElement>();
		xButton = new XButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.removeByInetAddress(address);
				
			}
		});
		displayName = new JLabel();
		displayName.setFont(Constants.mediumFont);	
		
		placeNameAndXButton();
	}
	
	private void placeNameAndXButton() {
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel container = new JPanel();
		container.add(xButton);
		container.add(displayName);
		container.setBackground(Color.WHITE);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		this.add(container, c);
		
		c.gridx = 1;
		c.ipadx = 120;
		c.gridwidth = 2;
		JPanel temp = new JPanel();
		temp.setBackground(Color.WHITE);
		this.add(temp, c);
		
		c.gridx = 3;
		c.gridwidth = 1;
		c.ipadx = 0;
		JButton log = new JButton("LOG");
		log.setFocusPainted(false);
		log.setBorderPainted(false);
		log.setBackground(new Color(230, 230, 230));
		log.setFont(Constants.smallFont);
		this.add(log, c);
		
		/*c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 1;
		c.gridwidth = 4;
		//c.ipadx = 200;
		//c.ipady = 30;
		this.add(jobPane, c); */
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(new JSeparator(), c);
		
		//TODO: add JSeparator
	}
	
	private void createAndPlaceJobPanel() {
		this.revalidate();
		this.repaint();
	}
	
	public void addNewJob(Job b) {
		jobs.add(new JobElement(b));
		createAndPlaceJobPanel();
	}
	
	public void removeJob(Job b) {
		
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
		displayName.setText(address.getHostName());
	}
	
	public String getDisplayName() {
		return displayName.getText();
	}

}
