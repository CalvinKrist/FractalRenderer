package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dropbox.core.DbxException;

import dropbox.DatabaseCommunicator;
import fractal.RenderManager;
import server.Client;
import server.Job;
import server.MessageListener;
import server.NoConnectionListener;
import util.Constants;
import util.DataTag;
import util.Log;
import util.Parameters;
import util.SocketWrapper;
import util.Utils;

public class Display extends JPanel implements Runnable {
	
	private JTextField zoom, xPos, yPos, zoomSpeed;
	private Color bgColor = new Color(244, 244, 244);
	private Font labelFont = Constants.smallFont;
	
	private JLabel numUsersLabel, frameCountLabel, avgRenderTimeLabel;
	
	public static final int DISPLAY_WIDTH = 320;
	public static final int DISPLAY_HEIGHT = 320;
	
	private SocketWrapper server;
	
	//private AdminClient client;
	
	private DatabaseCommunicator database;
	
	private DecimalFormat df;
	
	private Thread t;
	
	private Log log;
	
	public Display() {
		log = new Log();
		log.setLogLevel(Log.LEVEL_LOG);
		log.setPrintLevel(Log.LEVEL_LOG);
		log.setPrintStream(System.out);
		//client = new AdminClient(log);
		df = new DecimalFormat("0.###E0");
		try {
			database = new DatabaseCommunicator("eoggPPnSY7QAAAAAAAAASuUXGkHwlV-0cO-lQYLiB0oZF8znalh0XXdg7sCipTuT");
			log.newLine("Database connection established.");
		} catch (DbxException e2) {
			e2.printStackTrace();
			log.newLine("Unable to connect to database.");
			log.addError(e2);
		}
		String serverIP = Utils.getServerIpAdress(database);
		try {
			log.newLine("Connecting to server at " + serverIP + ".");
			server = new SocketWrapper(new Socket(serverIP, Constants.PORT), log);
			log.newLine("Connected to server.");
			server.addMessageListener(new MessageListener() {
				public void messageRecieved(Object m) {
					handleMessage(m);
				}
			});
			server.addNoConnectionListener(new NoConnectionListener() {
				public void response(Exception e) {
					log.newLine("Server diconnected.");
					log.addError(e);
					//TODO: if there's no connection...
				}
			});
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Server not available.");
			System.exit(0);
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
		t = new Thread(this);
		t.start();
	}
	
	private JMenuBar menus() {
		ToolTipManager.sharedInstance().setInitialDelay(0);
		JMenuBar bar = new JMenuBar();
		JMenu view = new JMenu("View");
		JMenuItem serverLog = new JMenuItem("Server Log");
		serverLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server.sendMessage("logRequest");
			}
		});
		serverLog.setToolTipText("Displays the log of the current server.");
		view.add(serverLog);
		JMenuItem adminLog = new JMenuItem("Admin Log");
		adminLog.setToolTipText("Displays the log created by this admin instance.");
		view.add(adminLog);
		bar.add(view);
		return bar;
	}
	
	private JPanel createParameters() {
		zoom = new JTextField(10);
		zoom.setFont(labelFont);
		zoom.setText("");
		xPos = new JTextField(10);
		xPos.setText("");
		xPos.setFont(labelFont);
		yPos = new JTextField(10);
		yPos.setFont(labelFont);
		yPos.setText("");
		zoomSpeed = new JTextField(10);
		zoomSpeed.setFont(labelFont);
		zoomSpeed.setText("");
		
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
		
		frameCountLabel = new JLabel("  Frame Count: ");
		frameCountLabel.setAlignmentY(0);
		frameCountLabel.setFont(labelFont);
		avgRenderTimeLabel = new JLabel("  Average Render Time: ");
		avgRenderTimeLabel.setAlignmentY(0);
		avgRenderTimeLabel.setFont(labelFont);
		numUsersLabel = new JLabel("  User Count: ");
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
	
	@Override
	public void run() {
		server.sendMessage("admin");
		while(true) {
			try {
				t.sleep(5000);
			} catch (InterruptedException e) {
				log.addError(e);
			}
			sendMessage("update");
		}
	}
	
	public void sendMessage(Serializable j) {
		synchronized(server) {
			server.sendMessage(j);
		}
	}
	
	private boolean allTextFieldsEmpty() {
		return zoom.getText().equals("") && xPos.getText().equals("") && yPos.getText().equals("") && zoomSpeed.getText().equals("");
	}
	
	public void handleMessage(Object o) {
		if(o instanceof Parameters) {
			Parameters params = (Parameters)o;
			if(params.contains("screenResolution")) {
				//client.setFractal(new RenderManager(params));
				return;
			}
			else if(!params.contains("zoom"))
				return;
			if(allTextFieldsEmpty()) {
				zoom.setText(df.format(1 / params.getParameter("zoom", Double.class)) + "");
				xPos.setText(params.getParameter("location", util.Point.class).x + "");
				yPos.setText(params.getParameter("location", util.Point.class).y + "");
				zoomSpeed.setText(1 / params.getParameter("zSpeed", Double.class) + "");
			} else {
				if(!zoom.isFocusOwner())
					zoom.setText(df.format(1 / params.getParameter("zoom", Double.class)) + "");
				if(!xPos.isFocusOwner())
					xPos.setText(params.getParameter("location", util.Point.class).x + "");
				if(!yPos.isFocusOwner())
					yPos.setText(params.getParameter("location", util.Point.class).y + "");
				if(!zoomSpeed.isFocusOwner())
					zoomSpeed.setText(1 / params.getParameter("zSpeed", Double.class) + "");
			}
			numUsersLabel.setText("  User Count: " + params.getParameter("userCount"));
			frameCountLabel.setText("  Frame Count: " + params.getParameter("frameCount"));
		} else if(o instanceof DataTag) {
			DataTag tag = (DataTag)o;
			if(tag.getId().equals("log")) {
				JFrame f = new JFrame("Server Log");
				JTextArea textArea = new JTextArea(35, 55);
				JScrollPane scroll = new JScrollPane(textArea);
				
				textArea.setText(tag.getValue());
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				f.setLayout(new BorderLayout());
				f.add(scroll, BorderLayout.CENTER);
				JButton b = new JButton("Save");
				b.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
						fileChooser.setFileFilter(filter);
						fileChooser.setCurrentDirectory(new File("fractals/logs"));
						fileChooser.showSaveDialog(null);
						String dir = fileChooser.getSelectedFile().getPath();
						if(!dir.substring(dir.lastIndexOf(".") + 1).equals("txt")) 
							dir = dir.substring(0, dir.lastIndexOf(".")) + ".txt";
						System.out.println("\n\n" + dir + "\n\n");
						try {
							PrintWriter out = new PrintWriter(dir);
							Scanner s = new Scanner(textArea.getText());
							while(s.hasNextLine())
								out.println(s.nextLine());
							out.flush();
							out.close();
						} catch (FileNotFoundException e1) {
							log.addError(e1);
						}
					}
				});
				JPanel tempPanel = new JPanel();
				tempPanel.add(b);
				f.add(tempPanel, BorderLayout.SOUTH);
				f.pack();
				f.setLocationRelativeTo(null);
				f.setResizable(true);
				f.setVisible(true);
			}
		} else if(o instanceof Job) {
			//client.doJob((Job)o);
		}
		
	}
	
}
