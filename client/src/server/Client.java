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
import util.Parameters;
import util.SocketWrapper;
import util.Utils;

public class Client {

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
	private DatabaseCommunicator database;
	
	/**
	 * 
	 */
	private RenderManager fractal;
	
	private String ipAdress;
	
	public Client() {
		fractal = null;
		jobs = new LinkedList<Job>();
		try {
			database = new DatabaseCommunicator("eoggPPnSY7QAAAAAAAAASuUXGkHwlV-0cO-lQYLiB0oZF8znalh0XXdg7sCipTuT");
		} catch (DbxException e2) {
			e2.printStackTrace();
		}
		ipAdress = Utils.getServerIpAdress(database);
		System.out.println("Attempting to connect...");
		
		initializeServer();
		System.out.println("Starting...");
		doJob();
	}
	
	public void handleString(String s) {
		
	}
	
	private void initializeServer() {
		try {
			server = new SocketWrapper(new Socket(ipAdress, Constants.PORT));
			server.addNoConnectionListener(new NoConnectionListener() {
				public void response(Exception e) {
					System.out.println("Disconnected from server.");
					checkServer();
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
					}
				}
			});
			System.out.println("Connected to Server " + ipAdress);
			while(fractal == null) {
				try {
					Thread.currentThread().wait(100);
				} catch(Exception e) {}
			}
		} catch (Exception e) {
			System.out.println("Server not available.");
			checkServer();
		}
	}
	
	//Checks to see if there is a new server to connect to. If not, attempts to become server.
	private void checkServer() {
		String ipFile = database.downloadFileAsString("ipAdress.txt");
		if(ipFile.contains(ipAdress)) {
			
		} else {
			fractal = null;
			String newIp = Utils.getServerIpAdress(database);
			System.out.println("Connecting to new server at " + newIp);
			while(jobs.size() != 0)
				jobs.remove();
			try {
				server.join();
				initializeServer();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
	public void handleJob(Job j) {
		synchronized(jobs) {
			System.out.println("job added");
			jobs.add(j);
		}
	}
	
	private void doJob() {
		while(true) {
			synchronized(jobs) {
				Job j = jobs.poll();
				if(j != null) {
					if(j.getType().equals("render")) {
						renderJob(j);
					} else if(j.getType().equals("compile")) {
						compileJob(j);
					} else {
						System.out.println("not a render job: " + j.getId());
					}
				}
			}
		}
	}
	
	private void renderJob(Job j) {
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

}
