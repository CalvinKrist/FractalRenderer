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
import server.SocketWrapper;
import util.Constants;

/**
 * Represents an element of the network: in other words, represents computers that are connected to the
 * network. It provides buttons for kicking those computers and for requesting and displaying their logs.
 * @author Calvin
 *
 */
public class NetworkElement extends JPanel {
	
	private InetAddress address;
	private LinkedList<JobElement> jobs;
	
	private JLabel displayName;
	private XButton xButton;
	
	private Server server;
	
	/**
	 * The height of the element in pixels
	 */
	public static final int HEIGHT = 50;
	
	/**
	 * Initializes the network element given the server and the SocketWrapper.
	 * @param server the server whose data this element can modify
	 * @param wrapper the SocketWrapper this element represents
	 */
	public NetworkElement(Server server, SocketWrapper wrapper) {
		address = wrapper.getInet();
		displayName = new JLabel();
		displayName.setFont(Constants.mediumFont);	
		try {
			displayName.setText(address.getHostName());
		} catch(Exception e) {
			displayName.setText(address.getHostAddress());
		}
		
		this.server= server;
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(Display.DISPLAY_WIDTH, 15));
		this.setLayout(new GridBagLayout());
		
		this.jobs = new LinkedList<JobElement>();
		xButton = new XButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.removeByInetAddress(address);
			}
		});
		
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
	
	/**
	 * Used to add a new job to the submenu of this NetworkElement
	 * @param b the new job to be added to the submenu. Currently doesn't work.
	 */
	public void addNewJob(Job b) {
		jobs.add(new JobElement(b));
		createAndPlaceJobPanel();
	}
	
	/**
	 * Used to remove a job from the submenu of this element
	 * @param b the job to be removed from the submenu. Currently doesn't work.
	 */
	public void removeJob(Job b) {
		
	}
	
	/**
	 * Used to set the InetAddress of this NetworkElement
	 * @param address the new InetAddress
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
		displayName.setText(address.getHostName());
	}
	
	/**
	 * Used to get the name displayed for this network element
	 * @return the name displayed for this network element
	 */
	public String getDisplayName() {
		return displayName.getText();
	}

}
