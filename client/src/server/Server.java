package server;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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
import fractal.Palette;
import fractal.RenderManager;
import fractal.Renderer;
import util.Constants;
import util.DataTag;
import util.JobComparator;
import util.Log;
import util.Parameters;
import util.Point;
import util.SocketWrapper;
import util.Utils;

/**
 * This class represents the server that manages all the clients
 * 
 * @author Calvin
 *
 */
public class Server extends NetworkNode {

	/**
	 * A queue of unassigned jobs that need to be done
	 */
	private LinkedList<Job> unnasignedJobs;

	/**
	 * A list of jobs that were assigned but haven't yet been completed
	 */
	private Map<SocketWrapper, Queue<Job>> uncompletedJobs;

	/**
	 * contains a list of wrappers to communicate with all children
	 */
	private ArrayList<SocketWrapper> children;

	/**
	 * A list of all admins connected to the server
	 */
	private ArrayList<SocketWrapper> admins;

	private Parameters parameters;

	private SocketAdder adder;

	public Server(Log log) {
		super(log);
		log.blankLine();
		log.newLine("Creating new server.");

		try {

			String externalIP;
			externalIP = Utils.getExternalIP();
			InetAddress i = InetAddress.getLocalHost();
			String internalIP = i.getHostAddress();

			database.uploadByString("<external:" + externalIP + ">\n<internal:" + internalIP + ">", "ipAdress.txt");
			log.newLine("IPAdress added to database.");
		} catch (IOException e) {
			e.printStackTrace();
			log.addError(e);
		}

		HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
		Scanner s = new Scanner(database.downloadFileAsString("parameters.txt"));
		Dimension screenResolution = new Dimension(Integer.valueOf(new DataTag(s.nextLine()).getValue()),
				Integer.valueOf(new DataTag(s.nextLine()).getValue()));
		log.newLine("Screen resolution: " + screenResolution.toString());
		Double zoomPercent = Double.valueOf(new DataTag(s.nextLine()).getValue());
		log.newLine("Zoom percent: " + zoomPercent);
		Point position = new Point(Double.valueOf(new DataTag(s.nextLine()).getValue()),
				Double.valueOf(new DataTag(s.nextLine()).getValue()));
		log.newLine("Position: " + position.toString());
		while (s.hasNextLine()) {
			try {
				DataTag tag = new DataTag(s.nextLine());
				Class<?> c = Class.forName("fractal." + tag.getValue());
				Renderer r = (Renderer) c.newInstance();
				database.downloadFile(tag.getId() + ".palette", tag.getId() + ".palette");
				r.setColorPalette(new Palette(tag.getId() + ".palette", true));
				log.newLine("New render layer: " + tag.getId() + " " + c.getName());
				parameters.put(tag.getId(), r);
			} catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.addError(e);
			}
		}

		try {
			parameters.put("radius", database.getViewWidth("images", "videos") * zoomPercent);
		} catch(DbxException e) {
			log.addError(e);
		}
		parameters.put("dZoom", zoomPercent);
		parameters.put("screenResolution", screenResolution);
		parameters.put("location", position);
		this.parameters = new Parameters(parameters);

		children = new ArrayList<SocketWrapper>();
		unnasignedJobs = new LinkedList<Job>();
		uncompletedJobs = new HashMap<SocketWrapper, Queue<Job>>();
		admins = new ArrayList<SocketWrapper>();

		adder = new SocketAdder(children, this);
		adder.start();

		log.blankLine();
		log.newLine("Server started.");
	}

	public void handleMessage(Object o, SocketWrapper sender) {
		if (o instanceof Job) {
			uncompletedJobs.get(sender).remove((Job) o);
			assignJob(sender);
		} else if (o instanceof String) {
			String s = (String) o;
			switch (s) {
			case "admin":
				log.newLine("User " + sender.getInetAdress() + " promoted to ADMIN.");
				admins.add(sender);
				updateAdmin(sender);
				break;
			case "update":
				updateAdmin(sender);
				break;
			case "logRequest":
				sender.sendMessage(new DataTag("log", log.getLog()));
				break;
			}
		}
	}

	private void updateAdmin(SocketWrapper admin) {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		double zoomLevel;
		double zoom = parameters.getParameter("radius", Double.class);
		for (Job b : unnasignedJobs)
			zoom /= parameters.getParameter("dZoom", Double.class);
		params.put("zoom", zoom);
		zoomLevel = zoom;

		params.put("location", parameters.getParameter("location"));
		params.put("zSpeed", parameters.getParameter("dZoom"));
		params.put("userCount", children.size());
		try {
			params.put("frameCount",
					(int) (Math.log(zoomLevel / 4) / Math.log(parameters.getParameter("dZoom", Double.class))));
		} catch (Exception e) {
			log.addError(e);
		}
		Parameters param = new Parameters(params);
		admin.sendMessage(param);
	}

	public void createNextRenderJobSet() {
		log.newLine("New render job created at " + 1 / parameters.getParameter("radius", Double.class));
		Map<String, Serializable> params = new HashMap<String, Serializable>(4);
		params.put("zoom", 1 / parameters.getParameter("radius", Double.class));
		Parameters p = new Parameters(params);
		Job b = new Job("render_" + parameters.getParameter("radius") + "_", p);
		unnasignedJobs.add(b);
		parameters.put("radius",
				parameters.getParameter("radius", Double.class) * parameters.getParameter("dZoom", Double.class));
	}

	public void assignJob(SocketWrapper w) {
		while (unnasignedJobs.size() < 2 || unnasignedJobs.size() < children.size() + 4)
			createNextRenderJobSet();
		Job b = unnasignedJobs.remove();
		if (b != null) {
			w.sendMessage(b);
			if (!uncompletedJobs.containsKey(w))
				uncompletedJobs.put(w, new LinkedList<Job>());
			uncompletedJobs.get(w).add(b);
		}
	}

	/**
	 * @param w
	 *            is a socket wrapper Moves the job w was working on from
	 *            uncompleted to unassigned
	 */
	public void moveFromUncompletedToUnassigned(SocketWrapper w) {
		if (uncompletedJobs == null)
			return;
		Queue<Job> jobs = uncompletedJobs.remove(w);
		if (jobs == null)
			return;
		synchronized (unnasignedJobs) {
			for (int i = 0; i < jobs.size(); i++)
				unnasignedJobs.addAll(jobs);
			unnasignedJobs.sort(new JobComparator());
		}
	}

	public Parameters getParameters() {
		System.out.println("returning parametrs " + parameters);
		return parameters;
	}

	public ArrayList<SocketWrapper> getAdmins() {
		return admins;
	}

	public void kill() {
		// TODO: shut down server
	}

}
