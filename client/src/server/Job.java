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
	 * 
	 */
	private static final long serialVersionUID = -8695628289122490725L;
	/**
	 * format of type_zoom
	 */
	private String jobId;
	private Parameters parameters;
	private int[][] image;
	
	/**
	 * Initializes the job with the given parameters
	 * @param jobId the id of the job
	 * @param p the parameters of the job
	 */
	public Job(String jobId, Parameters p) {
		this.jobId = jobId;
		parameters = p;
		image = null;
	}
	
	/**
	 * Used to get the id of the job
	 * @return the id of the job
	 */
	public String getId() {
		return jobId;
	}
	
	/**
	 * Used to get the parameters of the job
	 * @return the parameters of the job
	 */
	public Parameters getParameters() {
		return parameters;
	}
	
	/**
	 * Prints the job in the formal jobIb : parameters
	 */
	public String toString() {
		return jobId + " : " + parameters;
	}
	
	/**
	 * Used to get the type of job (ie, render or compile). The type is stored in the jobId
	 * @return the type of job
	 */
	public String getType() {
		return jobId.substring(0, jobId.indexOf("_"));
	}
	
	/**
	 * Used to get the zoom level of this job. The zoom level is stored in the jobId
	 * @return the zoom level of this job
	 */
	public double getZoom() {
		String[] tokens = jobId.split("_");
		return Double.valueOf(tokens[1]);
	}
	
	/**
	 * Used to set the image this job stores
	 * @param image the image for the job to store. The image is represented as a 2D array if integer color data
	 */
	public void setImage(int[][] image) {
		this.image = image;
	}
	
	/**
	 * Used to get the image the job stores
	 * @return the image the job stores
	 */
	public int[][] getImage() {
		return image;
	}
	
	@Override
	public int hashCode() {
		return jobId.hashCode();
	}

}
