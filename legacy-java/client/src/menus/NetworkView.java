package menus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import server.Server;

/**
 * Displays all the NetworkElements and interfaces between the server and each element
 * @author 1355710
 *
 */
public class NetworkView extends JPanel {
	
	//TODO: computer stats
	private ArrayList<NetworkElement> elements;
	
	private JScrollPane scroll;
	private JPanel panel;
	
	private Server server;
	
	/**
	 * Initializes the GUI to display network elements
	 * @param server
	 */
	public NetworkView(Server server) {
		this.setPreferredSize(new Dimension(Display.DISPLAY_WIDTH, Display.DISPLAY_HEIGHT));
		this.server = server;
		elements = new ArrayList<NetworkElement>();
		panel = new JPanel();
		scroll = new JScrollPane(panel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * Used to add a new NetworkElement to the display
	 * @param element the new element to be added to the display
	 */
	public void addNewNetworkElement(NetworkElement element) {
		elements.add(element);
		panel.removeAll();
		int numRows = elements.size() >= Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT ? elements.size() : Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT;
		panel.setLayout(new GridLayout(numRows, 1));
		int count = 0;
		for(; count < numRows - elements.size(); count++) {
			JPanel temp = new JPanel();
			temp.setBackground(Color.WHITE);
			panel.add(temp, count, 0);
		}
		count = 0;
		for(NetworkElement e: elements) {
			//JSeparator sep = new JSeparator();
			//panel.add(sep, count++, 0);
			panel.add(elements.get(elements.size() - count - 1), count++, 0);
		}
		
		this.revalidate();
		this.repaint();

	}
	
	/**
	 * Used to set all the network elements of this display
	 * @param elements all the elements to be displayed
	 */
	public void setNetworkElements(ArrayList<NetworkElement> elements) {
		this.elements = elements;
		panel.removeAll();
		int numRows = elements.size() >= Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT ? elements.size() : Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT;
		panel.setLayout(new GridLayout(numRows, 1));
		int count = 0;
		for(; count < numRows - elements.size(); count++) {
			JPanel temp = new JPanel();
			temp.setBackground(Color.WHITE);
			panel.add(temp, count, 0);
		}
		count = 0;
		for(NetworkElement e: elements) {
			//JSeparator sep = new JSeparator();
			//panel.add(sep, count++, 0);
			panel.add(elements.get(elements.size() - count - 1), count++, 0);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Used to remove a network element from the display
	 * @param element the element to be removed
	 */
	public void removeNetworkElement(NetworkElement element) {
		elements.remove(element);
		panel.removeAll();
		int numRows = elements.size() >= Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT ? elements.size() : Display.DISPLAY_HEIGHT / NetworkElement.HEIGHT;
		panel.setLayout(new GridLayout(numRows, 1));
		int count = 0;
		for(; count < numRows - elements.size(); count++) {
			JPanel temp = new JPanel();
			temp.setBackground(Color.WHITE);
			panel.add(temp, count, 0);
		}
		count = 0;
		for(NetworkElement e: elements) {
			//JSeparator sep = new JSeparator();
			//panel.add(sep, count++, 0);
			panel.add(elements.get(elements.size() - count - 1), count++, 0);
		}
		
		this.revalidate();
		this.repaint();
	}

}
