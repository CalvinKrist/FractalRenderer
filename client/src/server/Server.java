package server;

import java.awt.image.BufferedImage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import util.SocketWrapper;

/**
 * This class represents the server that manages all the clients
 * 
 * @author Calvin
 *
 */
public class Server extends Thread {
	
	/**
	 * A queue of unassigned jobs that need to be done
	 */
	private Queue<Job> unnasignedJobs;
	
	/**
	 * A list of jobs that were assigned but haven't yet been completed
	 */
	private LinkedList<Job> uncompletedJobs;
	
	/**
	 * Jobs that have been completed and need to be compiled into a product
	 */
	private TreeSet<BufferedImage> uncompiledJobs;
	
	/**
	 * contains a list of wrappers to communicate with all children
	 */
	private ArrayList<SocketWrapper> children;
	
	/**
	 * the thread that the server socket will run on
	 */
	private SocketAdder adder;
	
	public Server() {
		children = (ArrayList<SocketWrapper>) Collections.synchronizedList(new ArrayList<SocketWrapper>());
		adder = new SocketAdder(children);
		adder.start();
		
		//TODO: add ip to database
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
