package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import gui.Window;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
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
	 * @throws AWTException 
	 */
	public void initialize() throws FileNotFoundException, AWTException {
		SwingNode fractalEditor = new SwingNode();
		TreeView parameters = new TreeView();
		TreeView layers = new TreeView();
		VBox trees = new VBox();
		trees.minHeightProperty().bind(bp.minHeightProperty());
		trees.minWidthProperty().bind(bp.minWidthProperty().divide(6));
		bp.setPadding(new Insets(5));


		Button render = new Button("Render");
		render.minWidthProperty().bind(trees.minWidthProperty());
		render.minHeightProperty().bind(trees.minHeightProperty().divide(6));
		render.setOnAction(e -> {

		});

		ImageView fractalView = new ImageView();
		//fractalView.setImage(new Image(new FileInputStream("C:\\Users\\David\\Pictures\\butterfly.jpg")));
		Robot robo = new Robot();
		BufferedImage capture = robo.createScreenCapture(new Rectangle(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height));
		Image image = SwingFXUtils.toFXImage(capture, null);
		fractalView.setImage(image);
		fractalView.fitWidthProperty().bind(bp.minWidthProperty().subtract(trees.minWidthProperty()));
		fractalView.fitHeightProperty().bind(bp.minHeightProperty().subtract(render.minHeightProperty().add(render.minHeightProperty().subtract(gradient.HEIGHT*65))));
		//fractalView.setVisible(false);

		Dimension p = new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.75),Toolkit.getDefaultToolkit().getScreenSize().height/6);
		System.out.println(p);

		gradient = new Window(p, 50);

		parameters.setRoot(new TreeItem("Parameters"));
		parameters.getRoot().setExpanded(true);

		TreeItem xPos = new TreeItem();
		
		parameters.getRoot().getChildren().addAll(xPos);
		
		layers.setRoot(new TreeItem("Layers"));
		layers.getRoot().setExpanded(true);



		fractalEditor.setOnMouseEntered(e -> gradient.repaint());
		//fractalEditor.setOnMouseClicked(e -> gradient.repaint());

		trees.getChildren().addAll(parameters, layers);
		//trees.setPadding(new Insets(5));

		VBox center = new VBox();
		center.getChildren().addAll(fractalView,fractalEditor);
		//center.setPadding(new Insets(5));


		trees.getChildren().add(render);

		bp.setCenter(center);
		bp.setRight(trees);

		bp.minWidthProperty().bind(this.widthProperty());
		bp.minHeightProperty().bind(this.heightProperty());
		fractalEditor.setContent(gradient);
	}


}
