package gradient;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class Main extends JFrame implements Runnable, MouseInputListener, MouseListener, MouseWheelListener {

	private BufferStrategy bs;
	private volatile boolean running;
	private Thread gameThread;
	
	private Window window;
	private ArrowEditor editor;
	private Dimension dimension;

	public Main() {
		dimension = new Dimension(650, 200);
		window = new Window(dimension);
	}

	protected void createAndShowGUI() {

		Canvas canvas = new Canvas();
		canvas.setSize(dimension);
		canvas.setBackground(Color.BLACK);
		canvas.setIgnoreRepaint(true);
		canvas.requestFocus();
		getContentPane().add(canvas);
		setTitle("Gradient Editor");
		setIgnoreRepaint(true);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
		setVisible(true);
		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();

		editor = new ArrowEditor(window, dimension);
		window.setEditor(editor);
		
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void run() {
		running = true;

		int FPS = 60;
		long targetTime = 1000 / FPS;

		long start;
		long elapsed;
		long wait;

		while (running) {
			start = System.nanoTime();
			gameLoop();
			elapsed = System.nanoTime() - start;
			wait = targetTime - elapsed / 1000000;
			if (wait < 0)
				wait = 1;
			try {
				Thread.sleep(wait);
			} catch (Exception e) {
				System.out.println("GamePanel.THREAD Failed to Sleep");
				e.printStackTrace();
			}
		}
	}

	public void gameLoop() {
		do {
			do {
				Graphics g = null;
				try {
					g = bs.getDrawGraphics();
					g.clearRect(0, 0, getWidth(), getHeight());
					window.render((Graphics2D)g);
				} finally {
					if (g != null) {
						g.dispose();
					}
				}
			} while (bs.contentsRestored());
			bs.show();
		} while (bs.contentsLost());
	}

	protected void onWindowClosing() {
		try {
			running = false;
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void mouseMoved(MouseEvent m) {
		try {

		} catch (Exception e) {
		}
	}

	public void mouseClicked(MouseEvent m) {
		window.mouseClicked(m.getPoint());
	}

	public void mouseEntered(MouseEvent m) {
		
	}

	public void mouseExited(MouseEvent m) {
		
	}

	public void mousePressed(MouseEvent m) {
		window.mousePressed(m.getPoint());
	}

	public void mouseReleased(MouseEvent m) {
		window.mouseReleased(m);
	}

	public void mouseDragged(MouseEvent m) {
		window.mouseDragged(m);
	}

	public void mouseWheelMoved(MouseWheelEvent m) {

	}

	public static void main(String[] args) {
		final Main app = new Main();
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				app.onWindowClosing();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				app.createAndShowGUI();
			}
		});
	}
}
