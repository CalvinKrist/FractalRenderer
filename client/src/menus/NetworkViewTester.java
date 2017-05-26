package menus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;

import server.Job;
import util.Parameters;

public class NetworkViewTester {

	public static void main(String[] args) {
		
		NetworkView view = new NetworkView();
		
		JFrame f = new JFrame();
		f.setContentPane(view);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("name", "test job");
		map.put("radius", 4.05);
		Job b = new Job("id", new Parameters(map));
		NetworkElement e = new NetworkElement();
		e.addNewJob(b);
		e.setDisplayName("TEST");
		NetworkElement e2 = new NetworkElement();
		e2.setDisplayName("TRY 2");
		NetworkElement e3 = new NetworkElement();
		e3.setDisplayName("TRY 3");
		NetworkElement e4 = new NetworkElement();
		e4.setDisplayName("TRY 4");
		e2.addNewJob(b);
		e3.addNewJob(b);
		e4.addNewJob(b);
		e3.addNewJob(new Job("id", new Parameters(map)));
		view.addNewNetworkElement(e);
		view.addNewNetworkElement(e2);
		view.addNewNetworkElement(e3);
		view.addNewNetworkElement(e4);
	}

}
