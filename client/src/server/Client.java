package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import fractal.RenderManager;
import util.Constants;
import util.DataTag;
import util.Log;
import util.Parameters;
import util.Utils;

public class Client extends NetworkNode {
	
	//TODO: fix glitch where jobs revieved get printed twice
	//TODO: fix issue where client prints zoom levels in jobs to too many decimal places

	/**
	 * the job the client is assigned
	 */
	private Queue<Job> jobs;
	
	/**
	 * a wrapper for communicating with the server
	 */
	private SocketWrapper server;
	
	/**
	 * 
	 */
	private RenderManager fractal;
	
	private String ipAdress;
		
	public boolean running = true;
		
	public Client(Log log) {
		super(log);
		log.blankLine();
		log.newLine("Creating new client.");
		fractal = null;
		jobs = new LinkedList<Job>();
		log.newLine("Connecting to server at " + ipAdress + ".");
		
		initializeServer();
		log.newLine("Succesfully connected to server at " + ipAdress + ".");
		//NetworkManager.network.clientConnection(ipAdress);
		doJob();
	}
	
	public void handleString(String s) {
		
	}
	
	private void initializeServer() {
		try {
			
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] message = "JOIN_REQUEST".getBytes();
			
			DatagramPacket packet = new DatagramPacket(message, message.length, Utils.getBroadcastAddress(), Constants.BROADCAST_PORT);
			socket.send(packet);
			log.newLine("IPAddress broadcast.");
			ServerSocket temp = new ServerSocket(Constants.PORT);
			server = new SocketWrapper(temp.accept(), log);
			log.newLine("Connected to server at " + server.getInetAdress());
			
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
					else if (j instanceof String){
						handleString((String)j);
					} else if(j instanceof Parameters) {
						fractal = new RenderManager((Parameters)j);
						log.newLine("RenderManager recieved: " + fractal.toString());
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
			kill();
		}	
	}

	public void handleJob(Job j) {
		synchronized(jobs) {
			jobs.add(j);
		}
	}
	
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
	 * This method creates the fractal image described by the job and uploads it to the server
	 * @param j The job that describes what needs to be rendered
	 */
	private void renderJob(Job j) {
		log.newLine("Starting job " + j );
		Parameters params = j.getParameters();
		fractal.setZoom(params.getParameter("zoom", Double.class));
		int[][] pixels = new int[fractal.getScreenResolution().width][fractal.getScreenResolution().height];
		fractal.render(pixels);
		j.setImage(pixels);
		server.sendMessage(j);
	}
	
	private void compileJob(Job j) {
		
	}
	
	public RenderManager getFractal() {
		return fractal;
	}
	
	public void setFractal(RenderManager fractal) {
		this.fractal = fractal;
	}
	
	public void getServer() throws IOException {
		DatagramSocket socket = new DatagramSocket();
		socket.setBroadcast(true);
		
		byte[] message = "JOIN_REQUEST".getBytes();
		
		DatagramPacket packet = new DatagramPacket(message, message.length, Utils.getBroadcastAddress(), 8888);
		socket.send(packet);
		
		ServerSocket temp = new ServerSocket(Constants.PORT);
		Socket server = temp.accept();
		this.server = new SocketWrapper(server, log);
		temp.close();
	}

	@Override
	public void kill() {
		//TODO: implement kill method properly
	}

}
