package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import fractal.Layer;
import fractal.Palette;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

/**
 * A swing based menu to modify and display a color palette
 *
 * @author Calvin
 *
 */
@SuppressWarnings({ "rawtypes", "serial", "unused", "unchecked" })
public class Window extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * The dimensions describing the colored rectangle in the center of the
	 * window
	 */
	private Dimension dimension;

	/**
	 * the palette the window displays
	 */
	private Palette palette;

	/**
	 * The amount of padding added on for the square button
	 */
	private int additionalWidth;

	/**
	 * Rectangles used to described different areas of the window to make
	 * identifying what was clicked easier
	 */
	private Rectangle opacityRect, gradientRect, colorRect;

	/**
	 * Tje currently selected arrow button. This is used to make the user
	 * controls feel natural
	 */
	private ArrowButton selectedButton;

	/**
	 * The square buttons to the side. One is to edit the color of the inside of
	 * a fractal, one to save a palette, one is to load a palette
	 */
	private SquareButton bgButton, saveButton, loadButton;

	/**
	 * The y values that all the opacity and color buttons are drawn at,
	 * respectively, relative to the origin of the window.
	 */
	private int opacityButtonHeight, colorButtonHeight;

	/**
	 * The layer whose palette this window edits
	 */
	private Layer layer;

	/**
	 * @param dimension
	 *            the desired dimension of the main section of the editor. This
	 *            is all the height of the editor and the width up to the end of
	 *            the gradient
	 * @param additionalWidth
	 *            width of the SquareButton used to store and display the color
	 *            of the inside of fractals in pixels.
	 * @param layer the layer whose palette the Window will start off displaying and editing
	 */
	public Window(Dimension dimension, int additionalWidth, Layer layer) {
		this.dimension = dimension;
		this.additionalWidth = additionalWidth;
		this.setPreferredSize(new Dimension(dimension.width + additionalWidth, dimension.height));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		opacityButtonHeight = (int) (dimension.height * 0.2);
		colorButtonHeight = (int) (dimension.height * 0.8);

		opacityRect = new Rectangle(10, 0, dimension.width - 10, opacityButtonHeight);
		gradientRect = new Rectangle(20, opacityButtonHeight, dimension.width - 30, (int) (dimension.height * 0.6));
		colorRect = new Rectangle(10, colorButtonHeight, dimension.width - 10, opacityButtonHeight);

		Point p = new Point(dimension.width + additionalWidth / 2, dimension.height / 2);
		bgButton = new SquareButton(additionalWidth, p);

		Point p1 = new Point(dimension.width + additionalWidth / 4, dimension.height - dimension.height / 4);
		saveButton = new SquareButton(30, p1);
		try {
			saveButton.setImage(ImageIO.read(new File("textures/saveSmall.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Point p2 = new Point(dimension.width + additionalWidth / 4 * 3 + 10, dimension.height - dimension.height / 4);
		loadButton = new SquareButton(30, p2);
		try {
			loadButton.setImage(ImageIO.read(new File("textures/loadSmall.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateLayer(layer);
	}

	/**
	 * This method overrides the default JPanel draw method in order to provide custom rendering code. It is
	 * responsible for drawing the editor to the screen.
	 * @param g2 a graphics object used to draw the window
	 */
	@Override
	public void paint(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (ArrowButton b : palette.getColorList())
			b.draw(g);
		for (ArrowButton b : palette.getOpacityList())
			b.draw(g);

		for (int x = gradientRect.x; x < gradientRect.x + gradientRect.width; x++) {
			Color c = getColorAtPoint(x);
			int opacity = (int) (255 * getOpacityAtPoint(x));
			g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), opacity));
			g.fillRect(x, gradientRect.y, 1, gradientRect.height);
		}
		g.setColor(Color.black);
		g.drawRect(gradientRect.x, gradientRect.y, gradientRect.width, gradientRect.height);

		bgButton.draw(g);
		saveButton.draw(g);
		loadButton.draw(g);
	}

	/**
	 * A method to return the opacity at any point along the gradient
	 *
	 * @param x
	 *            the point along the gradient that will be analyzed
	 * @return the opacity at point x
	 */
	private Double getOpacityAtPoint(int x) {
		x-=gradientRect.x;
		return palette.getOpacityAtPoint(windowToPalette(x));
	}

	/**
	 * Returns the color at any point along the gradient
	 *
	 * @param x
	 *            the point along the gradient that will be analysed
	 * @return the color at point x
	 */
	private Color getColorAtPoint(int x) {
		x-=gradientRect.x;
		return palette.getColorAtPoint(windowToPalette(x));
	}

	/**
	 * Adds a black color button at the specified location along the gradient. It will also add
	 * a color point to the palette the gradient displays after scaling the point to match the palette's size.
	 *
	 * @param x
	 *            the point where the new button will be
	 */
	public void addColorButton(int x) {
		addColorButton(x, Color.black);
	}

	/**
	 *Adds a color button at the specified location along the gradient. It will also add
	 * a color point to the palette the gradient displays after scaling the point to match the palette's size.
	 *
	 * @param x
	 *            the point where the new button will be
	 * @param c
	 *            the color of the new button
	 */
	public void addColorButton(int x, Color c) {
		ArrowButton<Color> b = new ArrowButton<Color>();
		b.setLocation(new Point(x, colorButtonHeight));
		x -= gradientRect.x;
		b.setX(windowToPalette(x));
		b.setData(c);
		b.setDown(false);
		b.setSelected(true);
		b.setSquareColor(c);
		palette.getColorList().add(b);
		palette.sortColorList();
	}

	/**
	 * takes in a new palette for the editor to display and edit
	 * @param nPalette a new palette for the editor to display and edit
	 */
	public void setPalette(Palette nPalette) {
		this.palette = nPalette;
	}

	/**
	 * Adds an opacity button with full opacity at the specified point along the
	 * gradient. It will also add an opacity point to the palette the editor represents after
	 * scaling the point to fit the palette's size
	 *
	 * @param x
	 *            the point along the gradient where the new opacity button will
	 *            be
	 */
	public void addOpacityButton(int x) {
		addOpacityButton(x, 1.0);
	}

	/**
	 * Adds an opacity button at the specified point along the
	 * gradient. It will also add an opacity point to the palette the editor represents after
	 * scaling the point to fit the palette's size
	 *
	 * @param x
	 *            the point where the new opacity button will be
	 * @param d
	 *            the opacity of the new point
	 */
	public void addOpacityButton(int x, Double d) {
		ArrowButton<Double> b = new ArrowButton<Double>();
		b.setLocation(new Point(x, opacityButtonHeight));
		x-=gradientRect.x;
		b.setX(windowToPalette(x));
		b.setData(d);
		b.setDown(true);
		b.setSelected(true);
		b.setSquareColor(Color.black);
		palette.getOpacityList().add(b);
		palette.sortOpacityList();
	}

	/**
	 * returns the palette this window describes
	 * @return the palette this window describes
	 */
	public Palette getPalette() {
		return palette;
	}

	/**
	 * This is called when a new layer is selected in the layer view in order to
	 * load and display its palette. It will also scale any color and opacity points on the palette
	 * to match the gradient and add those to the editor.
	 *
	 * @param newLayer
	 *            the new layer the window displays
	 */
	public void updateLayer(Layer newLayer) {

		this.layer = newLayer;
		this.palette = layer.getPalette();
		for (ArrowButton b : palette.getColorList()) {
			b.setLocation(new Point((int) ((double) b.getX() / palette.size * gradientRect.width) + gradientRect.x,
					colorButtonHeight));
			b.setDown(false);
			b.setSquareColor((Color) b.getData());
		}
		for (ArrowButton b : palette.getOpacityList()) {
			b.setDown(true);
			int val = (int) (255 * ((double) (b.getData())));
			b.setSquareColor(new Color(val, val, val));
			b.setLocation(
					new Point(b.getX() / palette.size * gradientRect.width + gradientRect.x, opacityButtonHeight));
		}
		this.revalidate();
		this.repaint();
	}

	/**
	 * Called when the mouse has been clicked. It determines what to do with the
	 * event.
	 */
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		repaint();
		if (selectedButton == null) {
			if (bgButton.isClicked(p)) {
				// TODO: create a color menu for the inside part of the fractal
				// and store the color like this: bgButton.setData(newColor);
				// this.repaint();
				Platform.runLater(() -> {
					ColorBox.display(bgButton);
				});
			} else if (saveButton.isClicked(p)) {
				Platform.runLater(() -> {
					TextInputDialog dialog = new TextInputDialog("");
					dialog.setTitle("");
					dialog.setHeaderText(null);
					dialog.setContentText("Save Palette As:");

					// Traditional way to get the response value.
					try {
						String result = dialog.showAndWait().get();
						if (!result.equals(""))
							palette.writeTo(result);
					} catch (Exception e5) {
					}
				});
			} else if (loadButton.isClicked(p)) {
				Platform.runLater(new Runnable() {
					public void run() {
						FileChooser chooser = new FileChooser();
						chooser.setTitle("Open Palette");
						chooser.setInitialDirectory(new File("fractals/palettes"));
						FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Palettes (*.palette)",
								"*.palette");
						chooser.getExtensionFilters().add(filter);
						File f = chooser.showOpenDialog(null);
						if(f != null) {
							palette = new Palette(f.getPath(), false);
							repaint();
							layer.setColorPalette(palette);
						}
					}
				});

			} else if (colorRect.contains(p)) {
				addColorButton(p.x);
				repaint();
			} else if (opacityRect.contains(p)) {
				addOpacityButton(p.x);
				repaint();
			}
		} else {
			if (selectedButton.isSquareClicked(p)) {
				if (selectedButton.isDown()) {

					// TODO: create a opacity menu for the selected button and
					// store the value like this:
					// selectedButton.setData(newValue); this.repaint();
					// NOTE: the value should be between 0 and 1.0
				} else {
					// TODO: create a color menu for the selected button and
					// store the color like this:
					// selectedButton.setData(newColor); this.repaint();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * Called what a mouseDragged event occurs. Determines what to do in such an
	 * event.
	 */
	public void mouseDragged(MouseEvent e) {
		if (selectedButton != null) {
			selectedButton.setLocation(new Point(e.getPoint().x, selectedButton.getLocation().y));
			if (selectedButton.getLocation().x < gradientRect.x)
				selectedButton.setLocation(new Point(gradientRect.x, selectedButton.getLocation().y));
			else if (selectedButton.getLocation().x > gradientRect.x + gradientRect.width)
				selectedButton
						.setLocation(new Point(gradientRect.x + gradientRect.width, selectedButton.getLocation().y));
			if (palette.getColorList().contains(selectedButton))
				palette.sortColorList();
			else
				palette.sortOpacityList();
			repaint();

			double prop = ((double) selectedButton.getLocation().x - gradientRect.x) / gradientRect.width;
			selectedButton.setX((int) (palette.size * prop));
		}
	}

	/**
	 * Can be used to change the Dimension of the colored inner rectangle and,
	 * as a result, the whole menu
	 *
	 * @param newDimension
	 *            the new dimension of the colored inner rectangle
	 */
	public void setGradientDimension(Dimension newDimension) {
		this.dimension = newDimension;
		this.setPreferredSize(new Dimension(dimension.width + additionalWidth, dimension.height));
		this.revalidate();
		this.repaint();
	}

	/**
	 * Called what a mousePressed event occurs. Determines what to do in such an
	 * event.
	 */
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		selectedButton = null;
		if (colorRect.contains(p)) {
			for (ArrowButton b : palette.getColorList())
				if (b.isClicked(p)) {
					selectedButton = b;
					selectedButton.setSelected(true);
					return;
				} else
					b.setSelected(false);
		} else if (opacityRect.contains(p)) {
			for (ArrowButton b : palette.getOpacityList())
				if (b.isClicked(p)) {
					selectedButton = b;
					selectedButton.setSelected(true);
					return;
				} else
					b.setSelected(false);
		}
		repaint();
	}

	/**
	 * Scales any point along the gradient to match the size of the palette.
	 * @param x the point on the gradient being scaled.
	 * @return the equivalent point on the palette
	 */
	public int windowToPalette(int x) {
		return (int) ((double) x * palette.size / gradientRect.width);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

}
