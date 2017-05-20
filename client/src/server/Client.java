package server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import com.dropbox.core.DbxException;

import dropbox.DatabaseCommunicator;
import fractal.RenderManager;
import util.Constants;
import util.DataTag;
import util.Log;
import util.Parameters;
import util.SocketWrapper;
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
		ipAdress = Utils.getServerIpAdress(database);
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
			server = new SocketWrapper(new Socket(ipAdress, Constants.PORT), log);
			server.addNoConnectionListener(new NoConnectionListener() {
				public void response(Exception e) {
					log.newLine("Disconnected from server.");
					log.addError(e);
					serverNotAvailable();
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
			serverNotAvailable();
		}	
	}
	
	public void serverNotAvailable() {
		log.newLine("Server not available. Checking for another server.");
		String ipAdress = Utils.getServerIpAdress(database);
		if(ipAdress.equals(this.ipAdress)) {
			log.newLine("No new server available");
			//NetworkManager.network.clientToServer();
		} else {
			this.ipAdress = ipAdress;
			initializeServer();
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
		fractal.render("fractals", "img_" + (1 / j.getZoom()));
		database.uploadByFilePath("fractals/img_" + (1 / j.getZoom()) + ".png", "images/img_" + (1 / j.getZoom()) + ".png");
		File f = new File("fractals/img_" + (1 / j.getZoom()) + ".png");
		f.delete();
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

	@Override
	public void kill() {
		//TODO: implement kill method properly
	}

}
