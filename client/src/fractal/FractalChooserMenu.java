package fractal;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import menus.RoundedButton;
import util.Constants;

public class FractalChooserMenu {
	
	private static RenderManager manager;
	private static boolean waiting;
	
	/**
	 * Allows a user to choose a saved fractal and set zoom speed and resolution
	 * parameters
	 * @return a RenderManger for the network to use. Will return null if no fractal if the window is closed
	 */
	public static RenderManager chooseFractal() {
		waiting = true;
		manager = null;
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		JLabel message = new JLabel(" Fractal:");
		message.setFont(Constants.mediumFont);
		p.add(message, BorderLayout.WEST);

		RoundedButton chooseFile = new RoundedButton();
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Fractal", "prop");
				fileChooser.setFileFilter(filter);
				manager = new RenderManager(fileChooser.getSelectedFile());
			}
		});
		p.add(chooseFile, BorderLayout.CENTER);
		
		f.setContentPane(p);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				waiting = false;
			}
			
		});
		f.setVisible(true);
		
		while(manager == null || waiting) {}
		return manager;
	}

}
