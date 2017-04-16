package server;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import com.dropbox.core.DbxException;

import dropbox.DatabaseCommunicator;
import fractal.HistogramRenderer;
import util.Constants;
import util.DataTag;
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
	
	public Client() {
		try {
			database = new DatabaseCommunicator("eoggPPnSY7QAAAAAAAAASuUXGkHwlV-0cO-lQYLiB0oZF8znalh0XXdg7sCipTuT");
		} catch (DbxException e2) {
			e2.printStackTrace();
		}
		Scanner ipFile = new Scanner(database.downloadFileAsString("ipAdress.txt"));
		String ipAdress = "";
		DataTag external = new DataTag(ipFile.nextLine());
		try {
			if(!external.getValue().equals(Utils.getExternalIP()))
				ipAdress = external.getValue();
			else 
				ipAdress = new DataTag(ipFile.nextLine()).getValue();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Attempting to connect...");
		try {
			server = new SocketWrapper(new Socket(ipAdress, Constants.PORT));
			server.addNoConnectionListener(new NoConnectionListener() {
				public void response(Exception e) {
					System.out.println("Disconnected from server.");
					System.exit(0);
				}
			});
			server.addMessageListener(new MessageListener() {
				public void messageRecieved(Object j) {
					if(j instanceof Job)
						handleJob((Job)j);
					else if (j instanceof String){
						handleString((String)j);
					}
				}
			});
			System.out.println("Connected to Server");
		} catch (Exception e) {
			System.out.println("Server not available.");
			System.exit(0);
		}
		jobs = new LinkedList<Job>();
		doJob();
	}
	
	public void handleString(String s) {
		
	}
	
	public void handleJob(Job j) {
		synchronized(jobs) {
			jobs.add(j);
		}
	}
	
	private void doJob() {
		while(true) {
			//System.out.println(jobs.size());
			synchronized(jobs) {
				Job j = jobs.poll();
				if(j != null) {
					if(j.getType().equals("render")) {
						renderJob(j);
					} else if(j.getType().equals("compile")) {
						compileJob(j);
					}
				}
			}
		}
	}
	
	private void renderJob(Job j) {
		double xPos = (double) j.getParameters().getNextParameter();
		double yPos = (double) j.getParameters().getNextParameter();
		double rWidth = (double) j.getParameters().getNextParameter();
		double rHeight = (double) j.getParameters().getNextParameter();
		int width = (int) j.getParameters().getNextParameter();
		int height = (int) j.getParameters().getNextParameter();
		j.setImage(HistogramRenderer.renderHistogramColoring(width, height, rWidth, rHeight, xPos, yPos, Utils.getColorPalate()));
		server.sendMessage(j);
	}
	
	private void compileJob(Job j) {
		
	}

}
