package util;

import java.util.Comparator;

import server.Job;

public class JobComparator implements Comparator<Job>{

	@Override
	public int compare(Job o1, Job o2) {
		String[] id1 = o1.getId().split("_");
		String[] id2 = o2.getId().split("_");
		if(id1[0].equals("render") && id2[0].equals("compile"))
			return -1;
		if(id2[0].equals("render") && id1[0].equals("compile"))
			return 1;
		if(Double.valueOf(id1[1]) > Double.valueOf(id2[1]))
			return -1;
		if(Double.valueOf(id1[1]) < Double.valueOf(id2[1]))
			return 1;
		
		return -(Integer.valueOf(id1[2]) - Integer.valueOf(id2[2]));
	}

}
