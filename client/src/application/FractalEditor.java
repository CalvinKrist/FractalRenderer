package application;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gui.Window;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class FractalEditor extends Scene {

	BorderPane bp;
	int width, height;

	/**
	 * This instantiates the Fractal Editor scene
	 * 
	 * @param x
	 *            width
	 * @param y
	 *            height
	 * @throws FileNotFoundException 
	 */
	public FractalEditor(int x, int y) throws FileNotFoundException {
		super(new BorderPane(),x,y);
		bp = (BorderPane) this.getRoot();
		width = x;
		height = y;
		initialize();
	}

	private void initialize() throws FileNotFoundException {

		ImageView fractalView = new ImageView();
		fractalView.setImage(new Image(new FileInputStream("C:\\Users\\David\\Pictures\\butterfly.jpg")));
		fractalView.setFitWidth(width-300);
		fractalView.setFitHeight(height-200);
		
		SwingNode fractalEditor = new SwingNode();
		TreeView parameters = new TreeView();
		TreeView layers = new TreeView();
		VBox trees = new VBox();
		Dimension d = new Dimension(width-300, 200);
		Window gradient = new Window(d, 50);

		fractalEditor.setContent(gradient);
		fractalEditor.setOnMouseEntered(e -> gradient.repaint());
		

		trees.getChildren().addAll(parameters, layers);
		
		VBox center = new VBox();
		center.getChildren().addAll(fractalView,fractalEditor);

		bp.setCenter(center);
		bp.setRight(trees);
	}
	

}
