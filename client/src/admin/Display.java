package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

import util.Constants;
import util.SocketWrapper;

public class Display extends JPanel {
	
	private JTextField zoom, xPos, yPos, zoomSpeed;
	private Color bgColor = new Color(244, 244, 244);
	private Font labelFont = Constants.smallFont;
	
	private JLabel numUsersLabel, frameCountLabel, avgRenderTimeLabel;
	
	public static final int DISPLAY_WIDTH = 320;
	public static final int DISPLAY_HEIGHT = 320;
	
	private SocketWrapper server;
	
	public Display() {
		//TODO: get serve iP
		String serverIP = "";
		try {
			server = new SocketWrapper(new Socket(serverIP, Constants.PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setLayout(new BorderLayout());
		this.setBackground(bgColor);
		
		JPanel left = new JPanel();
		left.setBackground(bgColor);
		left.setLayout(new BorderLayout());
		left.add(createParameters(), BorderLayout.NORTH);
		left.add(createStatistics(), BorderLayout.SOUTH);
		this.add(left, BorderLayout.WEST);
		
		this.add(new NetworkView(), BorderLayout.CENTER);
		
		this.add(menus(), BorderLayout.NORTH);
		
	}
	
	private JMenuBar menus() {
		JMenuBar bar = new JMenuBar();
		JMenu view = new JMenu("View");
		view.add(new JMenuItem("Current Frame"));
		view.add(new JMenuItem("Network"));
		bar.add(view);
		return bar;
	}
	
	private JPanel createParameters() {
		zoom = new JTextField(10);
		zoom.setFont(labelFont);
		zoom.setText("l8r");
		xPos = new JTextField(10);
		xPos.setText("l8r");
		xPos.setFont(labelFont);
		yPos = new JTextField(10);
		yPos.setFont(labelFont);
		yPos.setText("l8r");
		zoomSpeed = new JTextField(10);
		zoomSpeed.setFont(labelFont);
		zoomSpeed.setText("l8r");
		
		JPanel p = new JPanel();
		p.setBackground(bgColor);
		p.setLayout(new GridLayout(5, 2));
		
		//zoomSpeed
		JPanel zSpeed = new JPanel();
		zSpeed.setBackground(bgColor);
		JLabel speedLabel = new JLabel("Speed:");
		speedLabel.setFont(labelFont);
		zSpeed.add(speedLabel);
		zSpeed.add(zoomSpeed);
		zoomSpeed.setAlignmentY(0);
		JButton speedAccept = new JButton("Submit");
		speedAccept.setFont(new Font("Arial", 12, 14));
		speedAccept.setBackground(Color.white);
		speedAccept.setAlignmentY(1);
		speedAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
						
			}
		});
		zSpeed.add( speedAccept);
		p.add(zSpeed, 3, 0);
		
		//zoom
		JPanel zoomPanel = new JPanel();
		zoomPanel.setBackground(bgColor);
		JLabel zoomLabel = new JLabel("Zoom:");
		zoomLabel.setFont(labelFont);
		zoomPanel.add(zoomLabel);
		zoomPanel.add(zoom);
		zoom.setAlignmentY(0);
		
		JButton zoomAccept = new JButton("Submit");
		zoomAccept.setFont(new Font("Arial", 12, 14));
		zoomAccept.setBackground(Color.white);
		zoomAccept.setAlignmentY(1);
		zoomAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		zoomPanel.add(zoomAccept);
		
		p.add(zoomPanel, 2, 0);
		
		//yPos
		JPanel yPosP = new JPanel();
		yPosP.setBackground(bgColor);
		JLabel yPosLabel = new JLabel("Y-Pos:");
		yPosLabel.setFont(labelFont);
		yPosP.add(yPosLabel);
		yPosP.add(yPos);
		yPos.setAlignmentY(0);
		JButton yPosAccept = new JButton("Submit");
		yPosAccept.setFont(new Font("Arial", 12, 14));
		yPosAccept.setBackground(Color.white);
		yPosAccept.setAlignmentY(1);
		yPosAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		yPosP.add(yPosAccept);
		p.add(yPosP, 1, 0);
		
		//xPos
		JPanel xPosP = new JPanel();
		xPosP.setBackground(bgColor);
		JLabel xPosLabel = new JLabel("X-Pos:");
		xPosLabel.setFont(labelFont);
		xPosP.add(xPosLabel);
		xPosP.add(xPos);
		xPos.setAlignmentY(0);
		JButton xPosAccept = new JButton("Submit");
		xPosAccept.setFont(new Font("Arial", 12, 14));
		xPosAccept.setBackground(Color.white);
		xPosAccept.setAlignmentY(1);
		xPosAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		xPosP.add(xPosAccept);
		p.add(xPosP, 0, 0);
		
		JLabel title = new JLabel("Parameters");
		title.setFont(Constants.mediumFont);
		title.setOpaque(true);
		title.setBackground(bgColor);
		JPanel wrapper = new JPanel();
		wrapper.setBackground(bgColor);
		wrapper.add(title);
		p.add(wrapper, 4, 0);
		
		return p;
		
	}
	
	private JPanel createStatistics() {
		JPanel p = new JPanel();
		p.setBackground(bgColor);
		p.setLayout(new GridLayout(4, 1));
		
		frameCountLabel = new JLabel("  Frame Count: l8r");
		frameCountLabel.setAlignmentY(0);
		frameCountLabel.setFont(labelFont);
		avgRenderTimeLabel = new JLabel("  Average Render Time: l8r");
		avgRenderTimeLabel.setAlignmentY(0);
		avgRenderTimeLabel.setFont(labelFont);
		numUsersLabel = new JLabel("  User Count: l8r");
		numUsersLabel.setAlignmentY(0);
		numUsersLabel.setFont(labelFont);
		
		p.add(frameCountLabel, 3, 0);
		p.add(avgRenderTimeLabel, 2, 0);
		p.add(numUsersLabel, 1, 0);
		
		
		JLabel title = new JLabel("Statistics");
		title.setFont(Constants.mediumFont);
		title.setOpaque(true);
		title.setBackground(bgColor);
		JPanel wrapper = new JPanel();
		wrapper.setBackground(bgColor);
		wrapper.add(title);
		p.add(wrapper, 0, 0);
		
		return p;
	}
	
}
