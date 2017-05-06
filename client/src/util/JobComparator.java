package util;

import java.util.Comparator;

import server.Job;

public class JobComparator implements Comparator<Job>{

	@Override
	public int compare(Job o1, Job o2) {
		return new Double(o1.getZoom()).compareTo(new Double(o2.getZoom()));
	}

}
