package admin;

import java.io.File;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import dropbox.DatabaseCommunicator;
import fractal.RenderManager;
import server.Job;
import server.MessageListener;
import server.NetworkNode;
import server.NoConnectionListener;
import util.Constants;
import util.Log;
import util.Parameters;
import util.SocketWrapper;
import util.Utils;

public class AdminClient extends NetworkNode {

	private Display client;

	/**
	 * 
	 */
	private DatabaseCommunicator database;

	/**
	 * 
	 */
	private RenderManager fractal;

	private String ipAdress;

	public boolean running = true;

	public AdminClient(Log log) {
		super(log);
		log.blankLine();
		log.newLine("Creating new client.");
		fractal = null;
	}

	public void handleString(String s) {

	}

	public void doJob(Job j) {
		if (j != null) {
			if (j.getType().equals("render")) {
				renderJob(j);
			} else if (j.getType().equals("compile")) {
				compileJob(j);
			} else {

			}

		}
	}

	/**
	 * This method creates the fractal image described by the job and uploads it
	 * to the server
	 * 
	 * @param j
	 *            The job that describes what needs to be rendered
	 */
	private void renderJob(Job j) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				log.newLine("Starting job " + j);
				Parameters params = j.getParameters();
				fractal.setZoom(params.getParameter("zoom", Double.class));
				fractal.render("fractals", "img_" + (1 / j.getZoom()));
				database.uploadByFilePath("fractals/img_" + (1 / j.getZoom()) + ".png",
						"images/img_" + (1 / j.getZoom()) + ".png");
				File f = new File("fractals/img_" + (1 / j.getZoom()) + ".png");
				f.delete();
				client.sendMessage(j);
			}

		});
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
		// TODO: implement kill method properly
	}

}
