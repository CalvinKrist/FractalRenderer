package server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;

import com.dropbox.core.DbxException;

import dropbox.DatabaseCommunicator;
import util.Constants;
import util.DataTag;
import util.JobComparator;
import util.Parameter;
import util.Point;
import util.SocketWrapper;
import util.Utils;

/**
 * This class represents the server that manages all the clients
 * 
 * @author Calvin
 *
 */
public class Server {
	
	/**
	 * A queue of unassigned jobs that need to be done
	 */
	private LinkedList<Job> unnasignedJobs;
	
	/**
	 * A list of jobs that were assigned but haven't yet been completed
	 */
	private Map<SocketWrapper, Queue<Job>> uncompletedJobs;
	
	/**
	 * Jobs that have been completed and need to be compiled into a product
	 */
	private TreeSet<Job> uncompiledJobs;
	
	/**
	 * contains a list of wrappers to communicate with all children
	 */
	private ArrayList<SocketWrapper> children;
	
	/**
	 * A list of all admins connected to the server
	 */
	private ArrayList<SocketWrapper> admins;
	
	/**
	 * 
	 */
	DatabaseCommunicator database;
	
	private double viewWidth;
	private double zoomPercent;
	private Point position;
	
	public Server() {
		
		try {
			database = new DatabaseCommunicator("eoggPPnSY7QAAAAAAAAASuUXGkHwlV-0cO-lQYLiB0oZF8znalh0XXdg7sCipTuT");
			
			Scanner s = new Scanner(database.downloadFileAsString("parameters.txt"));
			viewWidth = Double.valueOf(new DataTag(s.nextLine()).getValue());
			zoomPercent = Double.valueOf(new DataTag(s.nextLine()).getValue());
			position = new Point(Double.valueOf(new DataTag(s.nextLine()).getValue()), Double.valueOf(new DataTag(s.nextLine()).getValue()));
			
		} catch (DbxException e1) {
			e1.printStackTrace();
		}
		try {
			
			String externalIP;
			externalIP = Utils.getExternalIP();
			InetAddress i = InetAddress.getLocalHost();
			String internalIP = i.getHostAddress();
			
			database.uploadByString("<external:" + externalIP + ">\n<internal:" + internalIP + ">", "ipAdress.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		children = new ArrayList<SocketWrapper>();
		unnasignedJobs = new LinkedList<Job>();
		uncompletedJobs = new HashMap<SocketWrapper, Queue<Job>>();
		uncompiledJobs = new TreeSet<Job>(new JobComparator());
		
		SocketAdder adder = new SocketAdder(children, this);
		adder.start();
		
		System.out.println("Server Started.");
	}
	
	public void handleMessage(Object o, SocketWrapper sender) {
		if(o instanceof Job) {
			Job j = (Job)o;
			uncompiledJobs.add(j);
			uncompletedJobs.get(sender).remove(j);
			assignJob(sender);
			System.out.println("New Job Assigned.");
		} else if(o instanceof String) {
			String s = (String)o;
		}
	}
	
	private LinkedList<Job> createNextRenderJobSet(int numChildren) {
		LinkedList<Job> jobs = new LinkedList<Job>();
		for(int i = 0; i < numChildren; i++) {
			Double[] params = new Double[6];
			params[0] = new Double(position.getX());
			params[1] = new Double(position.getY());
			params[2] = new Double(viewWidth / numChildren);
			params[3] = new Double(viewWidth);
			params[4] = new Double(Constants.WIDTH / numChildren);
			params[5] = new Double(Constants.WIDTH);
			Parameter<Double> p = new Parameter<Double>(params);
			Job b = new Job("render_" + viewWidth + "_" + (1 + i) + "_" + numChildren, p);
			jobs.add(b);
		}
		viewWidth *= zoomPercent;
		return jobs;
	}
	
	public void createNextRenderJobSet() {
		unnasignedJobs.addAll(createNextRenderJobSet(children.size()));
	}
	
	public void assignJob(SocketWrapper w) {
		if(unnasignedJobs.size() < 2 || unnasignedJobs.size() < children.size() * 1.5)
			createNextRenderJobSet();
		Job b = unnasignedJobs.remove();
		if(b != null) {
			w.sendMessage(b);
			if(!uncompletedJobs.containsKey(w))
				uncompletedJobs.put(w, new LinkedList<Job>());
			uncompletedJobs.get(w).add(b);
		}
	}
	
	/**
	 * @param w is a socket wrapper 
	 * Moves the job w was working on from uncompleted to unassigned
	 */
	public void moveFromUncompletedToUnassigned(SocketWrapper w) {
		Queue<Job> jobs = uncompletedJobs.remove(w);
		synchronized(unnasignedJobs) {
			for(int i = 0; i < jobs.size(); i++) 
				unnasignedJobs.addAll(jobs);
			unnasignedJobs.sort(new JobComparator());
		}
	}

}
