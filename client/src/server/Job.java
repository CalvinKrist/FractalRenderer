package server;

import java.io.Serializable;

import util.Parameters;

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
