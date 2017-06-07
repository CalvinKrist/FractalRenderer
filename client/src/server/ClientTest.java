package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import fractal.Layer;
import util.Log;

public class ClientTest {

	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		System.out.println("client test");
		Layer.initializeFractalRegistry();
		Log log = new Log();
		log.setPrintStream(System.out);
		log.setLogLevel(Log.LEVEL_LOG);
		log.setPrintLevel(Log.LEVEL_LOG);
		Client c = new Client();
		System.out.println("display");
		//String ip = JOptionPane.showInputDialog("IP Address");

		Thread t = new Thread(()-> {
			JFrame f = new JFrame();
			JTextArea text = new JTextArea(20, 25);
			JScrollPane scroll = new JScrollPane(text);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			f.setContentPane(scroll);
			f.pack();
			f.setLocationRelativeTo(null);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setVisible(true);
			
			while(true) {
				text.setText(log.getLog());
			}
		});
		t.start();
		System.out.println("starting");
		c.init(log, "192.168.1.4"); 

	}

}
