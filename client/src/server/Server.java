package server;

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

import javax.imageio.ImageIO;

import fractal.RenderManager;
import menus.Display;
import util.Constants;
import util.DataTag;
import util.JobComparator;
import util.Log;
import util.Parameters;

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
	
	private Display display;
	
	private String directory;
	
	private RenderManager fractal;
	
	private double zoomSpeed;

	public Server(RenderManager fractal, double zoomSpeed, String directory) {
		this.fractal = fractal;
		this.zoomSpeed = zoomSpeed;
		this.directory = directory;
		
		parameters = new Parameters();
		parameters.put("location", fractal.getLocation());
		parameters.put("radius", fractal.getRadius());
		parameters.put("name", fractal.getName());
	}
	
	public void init(Log log) {
		this.log = log;
		
		log.blankLine();
		log.newLine("Creating new server.");

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
			int[][] pixels = ((Job)(o)).getImage();
			BufferedImage img = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
			File dir = new File(Constants.FRACTAL_FILEPATH + parameters.getParameter("name", String.class) + "/images/" + (1 / parameters.getParameter("radius", Double.class)) + ".png");
			dir.mkdirs();
			try {
				ImageIO.write(img, "png", dir);
			} catch (IOException e) {
				log.addError(e);
			}
			assignJob(sender);
		} 
	}

	public Parameters getAdminParameters() {
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
		return param;
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
	
	public void removeByInetAddress(InetAddress addr) {
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).getInet().equals(addr)) {
				children.get(i).close();
				children.remove(i);
				log.blankLine();
				log.newLine("User at " + addr.getHostAddress() + " removed.");
				log.blankLine();
				break;
			}
		}
	}
	
	public void setDisplay(Display newDisplay) {
		display = newDisplay;
	}
	
	public Display getDisplay() {
		return display;
	}
	
	public Map<SocketWrapper, Queue<Job>> getUncompletedJobs() {
		return uncompletedJobs;
	}

	public void kill() {
		// TODO: shut down server
	}

}
