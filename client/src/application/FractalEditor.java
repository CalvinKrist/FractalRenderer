package application;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gui.Window;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class FractalEditor extends Scene {

	private BorderPane bp;
	protected int width, height;
	public Window gradient;

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
		//initialize();
	}
	/**
	 * 
	 * @throws FileNotFoundException
	 */
	public void initialize() throws FileNotFoundException {
		SwingNode fractalEditor = new SwingNode();
		TreeView parameters = new TreeView();
		TreeView layers = new TreeView();
		VBox trees = new VBox();
		trees.minHeightProperty().bind(bp.minHeightProperty());
		trees.minWidthProperty().bind(bp.minWidthProperty().divide(6));
		Dimension d = new Dimension(width-350, 200);
		gradient = new Window(d, 50);
		
		Button render = new Button("Render");
		render.minWidthProperty().bind(trees.minWidthProperty());
		render.minHeightProperty().bind(trees.minHeightProperty().divide(6));
		render.setOnAction(e -> {
			
		});
		

		ImageView fractalView = new ImageView();
		fractalView.setImage(new Image(new FileInputStream("C:\\Users\\David\\Pictures\\butterfly.jpg")));
		fractalView.fitWidthProperty().bind(bp.minWidthProperty().subtract(trees.minWidthProperty()));
		fractalView.fitHeightProperty().bind(bp.minHeightProperty().subtract(render.minHeightProperty()));
		//fractalView.setFitWidth(width-200);
		//fractalView.setFitHeight(height-200);
		
		
		
		fractalEditor.setOnMouseEntered(e -> gradient.repaint());
		fractalEditor.setOnMouseClicked(e -> gradient.repaint());

		trees.getChildren().addAll(parameters, layers);
		trees.setPadding(new Insets(5));
		
		VBox center = new VBox();
		center.getChildren().addAll(fractalView,fractalEditor);
		center.setPadding(new Insets(20));
		
		
		trees.getChildren().add(render);

		bp.setCenter(center);
		bp.setRight(trees);
		
		bp.minWidthProperty().bind(this.widthProperty());
		bp.minHeightProperty().bind(this.heightProperty());
		fractalEditor.setContent(gradient);
	}
	

}
