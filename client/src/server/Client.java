package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javafx.application.Platform;
import javax.swing.SwingUtilities;

import com.sun.javafx.scene.paint.GradientUtils.Point;

import fractal.RenderManager;
import menus.AlertMenu;
import util.Constants;
import util.DataTag;
import util.Log;
import util.Parameters;
import util.Utils;

/**
 * A class run in combination with a Server/Network. It takes jobs from the network which
 * describe something to be rendered, renders the job, and sends it back to the network. In other words,
 * it can be used to help a network render deep zooms of a fractal.
 * @author Calvin
 *
 */
public class Client extends NetworkNode {
	
	//TODO: fix glitch where jobs revieved get printed twice
	//TODO: fix issue where client prints zoom levels in jobs to too many decimal places

	/**
	 * Contains a queue of all jobs the client has to do. The queue never has a size greater than 2 because
	 * it only gets a new job after completing an old one. It gets two jobs upon connecting to the network. It
	 * has two jobs so that after completing one, it can start on the next without waiting for the networkt to send it another 
	 * one. This reduces down time.
	 */
	private Queue<Job> jobs;
	
	/**
	 * A simple wrapper to make communicating with the server easier. This object extends a thread and must be
	 * closed when not needed any more.
	 */
	private SocketWrapper server;
	
	/**
	 * The fractal sent to it by the server upon connection. It's parameters are updated for every job, and
	 * then it uses the fractal to render the job and return it.
	 */
	private RenderManager fractal;
	
	/***
	 *The ipAdress of the server. This is stored for debugging purposes.
	 */
	private String ipAdress;
	
	/**
	 * The main loop of the Client, which continuously checks to see if the job queue has any more jobs for it and handles them, will
	 * run while this is true. Setting this to false will end that main loop. This, in combination with stopping
	 * the SocketWrapper thread, will 'kill' the client.
	 */
	private boolean running = true;
		
	public Client() {}
	
	/**
	 * Connects to the server and initializes anything that needs to be initialized.
	 * @param log The log the client and all the objects it creates will use to log and print information.
	 */
	public void init(Log log) {
		this.log = log;
		log.blankLine();
		log.newLine("Creating new client.");
		fractal = null;
		jobs = new LinkedList<Job>();
		
		initializeServer();
		doJob();
	}
	
	/**
	 * Connects to the server by broadcasting on a pre-determined port to send the server its ipAddress and
	 * then waiting for the server to connect to it using conventional Sockets. Creates the various listeners
	 * for the SocketWrapper as well.
	 */
	private void initializeServer() {
		try {
			
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] message = "JOIN_REQUEST".getBytes();
			
			DatagramPacket packet = new DatagramPacket(message, message.length, Utils.getBroadcastAddress(), 7777);
			socket.send(packet);
			log.newLine("IPAddress broadcast.");
			ServerSocket temp = new ServerSocket(Constants.PORT);
			server = new SocketWrapper(temp.accept(), log);
			temp.close();
			socket.close();
			log.newLine("Connected to server at " + server.getInetAdress());
			ipAdress = server.getInet().getHostAddress();
			server.addNoConnectionListener(new NoConnectionListener() {
				public void response(Exception e) {
					log.newLine("Disconnected from server. Shutting down.");
					log.addError(e);
					kill();
				}
			});
			server.addMessageListener(new MessageListener() {
				public void messageRecieved(Object j) {
					if(j instanceof Job)
						handleJob((Job)j);
					else if(j instanceof RenderManager) {
						fractal = (RenderManager)j;
						log.newLine("RenderManager recieved: " + fractal.toString());
					} else if(j instanceof String) {
						if(j.equals("removing")) {
							server.sendMessage("removed.");
							log.blankLine();
							log.newLine("Removed by server.");
							saveLog();
							System.exit(0);
						} if(j.equals("closing")) {
							server.sendMessage("closed");
							log.blankLine();
							log.newLine("Server closed.");
							saveLog();
							System.exit(0);
						}
					}
				}
			});
			log.newLine("Connected to server " + ipAdress);
			while(fractal == null) {
				try {
					Thread.currentThread().wait(100);
				} catch(Exception e) {}
			}
		} catch (Exception e) {
			log.addError(e);
			log.newLine("Server not available. Shutting down.");
			/*Platform.runLater(()-> {
				AlertMenu alert = new AlertMenu("Server not Available", "Shutting down. Please try again.");
			}); */
			saveLog();
			System.exit(0);
		}	
	}

	private void handleJob(Job j) {
		synchronized(jobs) {
			jobs.add(j);
		}
	}
	
	/**
	 * Handles the main logic of the client. It's a loop that deals with any jobs that need to
	 * be completed.
	 */
	private void doJob() {
		while(running) {
			synchronized(jobs) {
				Job j = jobs.poll();
				if(j != null) {
					if(j.getType().equals("render")) {
						renderJob(j);
					} else if(j.getType().equals("compile")) {
						compileJob(j);
					} else {
						
					}
				}
			}
		}
	}
	
	/**
	 * This method creates the fractal image described by the job and returns it to the server
	 * @param j The job that describes what needs to be rendered
	 */
	private void renderJob(Job j) {
		log.newLine("Starting job " + j );
		Parameters params = j.getParameters();
		fractal.setZoom(params.getParameter("zoom", Double.class));
		fractal.setLocation(params.getParameter("location", util.Point.class));
		int[][] pixels = fractal.render();
		j.setImage(pixels);
		server.sendMessage(j);
	}
	
	/**
	 * In the future, jobs could also describe compiling actions where multiple images are compiled
	 * into a video. In such a situation, those jobs would be handled here.
	 * @param j the job describing what needs to be compiled
	 */
	private void compileJob(Job j) {
		
	}
	
	/**
	 * Used to get the fractal the client uses to render
	 * @return the fractal the client uses to render
	 */
	public RenderManager getFractal() {
		return fractal;
	}
	
	/**
	 * Used to set the fractal the client uses to render
	 * @param fractal the new fractal for the client to use in rendering
	 */
	public void setFractal(RenderManager fractal) {
		this.fractal = fractal;
	}

	/**
	 * Used to close the Client when it is done being used. It will end the main loop
	 * and shut down the SocketWrapper that connects the client to the network.
	 */
	@Override
	public void kill() {
		running = false;
		server.close();
	}
	
	private void saveLog() {
		try {
			String timeStamp = log.getTimeStamp();
			timeStamp = timeStamp.substring(0, timeStamp.indexOf(":"));
			File f = new File("log_" + timeStamp + ".txt");
			PrintWriter w = new PrintWriter(f);
			w.write(log.getLog());
			w.close();
		} catch(IOException e) {
			
		}
	}

}
