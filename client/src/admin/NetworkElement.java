package admin;

import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

import server.Job;

public class NetworkElement extends JPanel {
	
	private String address;
	private Queue<JobElement> jobs;
	
	public NetworkElement(String address, Queue<Job> jobs) {
		this.jobs = new LinkedList<JobElement>();
		this.address = address;
		for(Job b: jobs)
			this.jobs.add(new JobElement(b));
	}
	
	public void addNewJob(Job b) {
		jobs.add(new JobElement(b));
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

}
