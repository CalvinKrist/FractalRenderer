package server;

import java.io.Serializable;

import util.Parameters;

/**
 * A Job describes an action, or a 'job', that needs to be done by a client. Currently,
 * there are only 'render' jobs--jobs that are created by a server to tell a client to render 
 * something. However, there is support for other types as well, namely 'compile' jobs that would
 * tell a client to compile lots of images together into a video. The job can also store the product
 * of a 'render' job, which can be returned to the server that assigned the job.
 * @author Calvin
 *
 */
public class Job implements Serializable {
	
	/**
	 * format of type_zoom
	 */
	private String jobId;
	private Parameters parameters;
	private int[][] image;
	
	public Job(String jobId, Parameters p) {
		this.jobId = jobId;
		parameters = p;
		image = null;
	}
	
	public String getId() {
		return jobId;
	}
	
	public Parameters getParameters() {
		return parameters;
	}
	
	public String toString() {
		return jobId + " : " + parameters;
	}
	
	public String getType() {
		return jobId.substring(0, jobId.indexOf("_"));
	}
	
	public double getZoom() {
		String[] tokens = jobId.split("_");
		return Double.valueOf(tokens[1]);
	}
	
	public void setImage(int[][] image) {
		this.image = image;
	}
	
	public int[][] getImage() {
		return image;
	}

}
