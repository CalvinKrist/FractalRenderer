package server;

import java.awt.Point;
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

import fractal.Layer;
import fractal.RenderManager;
import menus.Display;
import util.Constants;
import util.DataTag;
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

	private Parameters parameters;

	private SocketAdder adder;

	private Display display;

	private String directory;
	
	private RenderManager fractal;
	
	private double zoomSpeed;
	
	private int frameCount = 0;
	
	private LinkedList<Long> times;
	private Map<Job, Long> assignedTimes;

	/**
	 * Creates a server using the given fractal, the given zoom speed, and that will save images to the selected directory
	 * @param fractal the fractal the network render
	 * @param zoomSpeed the speed at which the rendered images will zoom into the fractal
	 * @param directory the directory the rendered images will be saved to
	 */
	public Server(RenderManager fractal, double zoomSpeed, String directory) {
		this.fractal = fractal;
		this.zoomSpeed = zoomSpeed;
		this.directory = directory + "\\";
		
		parameters = new Parameters();
		parameters.put("location", fractal.getLocation());
		parameters.put("radius", fractal.getRadius());
		parameters.put("resolution", fractal.getScreenResolution());
		parameters.put("name", fractal.getName());
		
		times = new LinkedList<Long>();
		assignedTimes = new HashMap<Job, Long>();
	}

	/**
	 * This method actually starts the server using the given log to output information/
	 * @param log the log where information will be output to
	 */
	public void init(Log log) {
		this.log = log;

		log.blankLine();
		log.newLine("Creating new server.");

		children = new ArrayList<SocketWrapper>();
		unnasignedJobs = new LinkedList<Job>();
		uncompletedJobs = new HashMap<SocketWrapper, Queue<Job>>();

		adder = new SocketAdder(children, this);
		adder.start();

		log.blankLine();
		log.newLine("Server started.");
	}

	/**
	 * When a SocketWrapper recieves a message of any kind, it call that method. Hence, this method described what to do
	 * when different types of messages are recieved.
	 * @param o the message recieved
	 * @param sender the SocketWrapper that revieved the message (or, from the perspective of the server, the client that sent the message)
	 */
	public void handleMessage(Object o, SocketWrapper sender) {
		if (o instanceof Job) {
			uncompletedJobs.get(sender).remove((Job) o);
			int[][] pixels = ((Job) (o)).getImage();
			BufferedImage img = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
			RenderManager.setPixels(img, pixels);
			double zoom = (1 / parameters.getParameter("radius", Double.class));
			File dir = new File(directory + zoom + ".png");
			dir.mkdirs();
			frameCount++;
			Long elapsed = System.currentTimeMillis() - assignedTimes.get(o);
			System.out.println("elapsed: " + elapsed);
			times.add(elapsed);
			if(times.size() > 5)
				times.removeFirst();
			try {
				ImageIO.write(img, "png", dir);
			} catch (IOException e) {
				log.addError(e);
			}
			assignJob(sender);
		}
	}

	/**
	 * Returns the parameters that admins might be concerned with. This includes parameters and statistics. All of this 
	 * data can be viewed from the NetworkView menu
	 * @return the parameters admins might be concerned with.
	 */
	public Parameters getAdminParameters() {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		double zoomLevel;
		double zoom = parameters.getParameter("radius", Double.class);
		for (Job b : unnasignedJobs)
			zoom /= zoomSpeed;
		params.put("zoom", zoom);
		zoomLevel = zoom;

		params.put("zSpeed", zoomSpeed);
		params.put("location", parameters.getParameter("location"));
		params.put("userCount", children.size());
		params.put("maxIterations", fractal.getLayers().get(0).getMaxIterations());
		params.put("bailout", fractal.getLayers().get(0).getBailout());
			params.put("frameCount", frameCount);
		Long avgTime = 0L;
		for(Long l : times)
			avgTime += l / times.size();
		params.put("avgTime", avgTime);
		Parameters param = new Parameters(params);
		return param;
	}
	
	/**
	 * This method is used by the Display to modify the parameters of the network. After the server updates
	 * its own fractal with the new parameters, it then sends that very fractal out to all the clients.
	 * @param params the new parameters for the fractal
	 */
	public void updateParameters(Parameters params) {
		zoomSpeed = params.getParameter("zSpeed", Double.class);
		parameters.put("zoom", params.getParameter("zoom"));
		for(Layer l : fractal.getLayers()) {
			l.setBailout(params.getParameter("bailout", Long.class));
			l.setMaxIterations(params.getParameter("maxIterations", Integer.class));
		}
		fractal.setLocation(params.getParameter("location", util.Point.class));
	}

	public void createNextRenderJobSet() {
		log.newLine("New render job created at " + 1 / parameters.getParameter("radius", Double.class));
		Map<String, Serializable> params = new HashMap<String, Serializable>(4);
		params.put("zoom", 1 / parameters.getParameter("radius", Double.class));
		params.put("location", parameters.getParameter("location"));
		Parameters p = new Parameters(params);
		Job b = new Job("render_" + parameters.getParameter("radius") + "_", p);
		unnasignedJobs.add(b);
		parameters.put("radius", parameters.getParameter("radius", Double.class) * 1 / zoomSpeed);
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
			assignedTimes.put(b, System.currentTimeMillis());
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
			unnasignedJobs.sort((Job o1, Job o2)-> {
					return new Double(o1.getZoom()).compareTo(new Double(o2.getZoom()));
			});
		}
	}

	public RenderManager getFractal() {
		return fractal;
	}

	public void removeByInetAddress(InetAddress addr) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getInet().equals(addr)) {
				children.get(i).close();
				children.get(i).sendMessage("removing");
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
	
	public ArrayList<SocketWrapper> getChildren() {
		return children;
	}

	public Map<SocketWrapper, Queue<Job>> getUncompletedJobs() {
		return uncompletedJobs;
	}

	public void kill() {
		adder.running = false;
		adder.stop();
		for(SocketWrapper w: children)
			w.close();
	}

}
