package server;

import java.io.Serializable;

import util.Parameter;

public class Job implements Serializable {
	
	/**
	 * format of type_zoom_subframe_numFrames
	 */
	private String jobId;
	private Parameter parameters;
	private int[][] image;
	
	public Job(String jobId, Parameter p) {
		this.jobId = jobId;
		parameters = p;
		image = null;
	}
	
	public String getId() {
		return jobId;
	}
	
	public Parameter getParameters() {
		return parameters;
	}
	
	public String toString() {
		return jobId + " : " + parameters;
	}
	
	public int getNumFrames() {
		return Integer.valueOf(jobId.substring(jobId.lastIndexOf("_") + 1));
	}
	
	public String getType() {
		return jobId.substring(0, jobId.indexOf("_"));
	}
	
	public int getSubframe() {
		String[] tokens = jobId.split("_");
		return Integer.valueOf(tokens[2]);
	}
	
	public double getZoom() {
		String[] tokens = jobId.split("_");
		return Double.valueOf(tokens[1]);
	}
	
	public void setImage(int[][] image) {
		this.image = image;
	}

}
