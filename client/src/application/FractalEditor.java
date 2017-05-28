package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import fractal.Layer;
import fractal.RenderManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import util.Point;

/**
 * @author David
 */
public class FractalEditor extends Scene {

	private BorderPane bp;
	protected int width, height;
	public Window gradient;
	
	private RenderManager fractal;
	private ImageView fractalView;

	/**@author David
	 * This instantiates the Fractal Editor scene
	 *
	 * @param x
	 *            width
	 * @param y
	 *            height
	 * @throws FileNotFoundException
	 */
	public FractalEditor(int x, int y) throws FileNotFoundException {
		super(new BorderPane(),x, y);
		bp = (BorderPane) this.getRoot();
		width = x;
		height = y;
		// initialize();
	}

	/**@author David, Calvin
	 *
	 * @throws FileNotFoundException
	 * @throws AWTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initialize() throws FileNotFoundException, AWTException {
		//initializing stuff
		try {
			Layer.initializeFractalRegistry();
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		fractal = new RenderManager();
		
		MenuBar menu = new MenuBar();
		SwingNode fractalEditor = new SwingNode();
		TreeView parameters = new TreeView();
		TreeView layers = new TreeView();
		VBox trees = new VBox();
		trees.minHeightProperty().bind(bp.minHeightProperty().subtract(menu.minHeightProperty()));
		trees.minWidthProperty().bind(bp.minWidthProperty().divide(6));
		bp.setPadding(new Insets(5));


		Button render = new Button("Update");
		render.minWidthProperty().bind(trees.minWidthProperty());
		render.minHeightProperty().bind(trees.minHeightProperty().divide(6));


		fractalView = new ImageView();
		fractalView.setOnMouseClicked(e -> {
			Point p = new Point(e.getScreenX(), e.getScreenY());
			System.out.println(p);
			//TODO: handle point
		});
		this.fractal = new RenderManager();
		render.setOnAction(e -> {
			updateFractalImage();
		});
		//Fitting the image to the screen
		fractalView.fitWidthProperty().bind(bp.minWidthProperty().subtract(trees.minWidthProperty()));
		fractalView.fitHeightProperty().bind(bp.heightProperty().subtract(menu.minHeightProperty()).subtract(200));
		
		{//Fitting gradientEditor to full screen
		Dimension p = new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.75),200);
		System.out.println("Gradient Dimensions: "+p);

		gradient = new Window(p, 50);
		}
		{//Tree Stuff
		parameters.setRoot(new TreeItem("params"));
		parameters.getRoot().setExpanded(true);

		TreeItem xPos = new TreeItem();
		parameters.getRoot().getChildren().addAll(xPos);

		layers.setRoot(new TreeItem("Layers"));
		layers.getRoot().setExpanded(true);
		}

		//Trying to get this to work
		fractalEditor.setOnMouseEntered(e -> gradient.repaint());


		{//This is the menu stuff
			Menu network = new Menu("Network");
			Menu fractal = new Menu("Fractal");
		
			MenuItem newNet = new MenuItem("Create New Network");
			MenuItem viewNet = new MenuItem("View Network");
			
			network.getItems().addAll(newNet,viewNet);
			
			MenuItem newFract = new MenuItem("New Fractal");
			newFract.setOnAction(e -> {
				this.fractal = new RenderManager();
				this.updateFractalImage();
			});
			MenuItem openFract = new MenuItem("Open Fractal");
			openFract.setOnAction(e -> {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Open Fractal");
				chooser.setInitialDirectory(new File("fractals"));
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fractals (*.fractal)", "*.fractal");
				chooser.getExtensionFilters().add(filter);
				chooser.showOpenDialog(null);
			});
			MenuItem saveFract = new MenuItem("Save Fractal");
			saveFract.setOnAction(e -> {
				this.fractal.saveFractal();
			});
			MenuItem saveFractAs = new MenuItem("Save Fractal As");
			saveFractAs.setOnAction(e -> {
				this.fractal.saveFractalAs();
			});
			MenuItem newLayer = new MenuItem("New Layer Type");
			
			fractal.getItems().addAll(newFract, openFract, saveFract, saveFractAs, newLayer);
		
			/*MenuItem exit = new MenuItem("Exit");
			exit.setOnAction(e -> {
				System.exit(0);
			});*/
				
			menu.getMenus().addAll(fractal);
			menu.getMenus().addAll(network);
		}

		VBox center = new VBox();
		center.getChildren().addAll(fractalView,fractalEditor);
		trees.getChildren().addAll(parameters,layers,render);

		bp.setCenter(center);
		bp.setRight(trees);
		//This is where the menu is located on the border pane
		bp.setTop(menu);

		bp.minWidthProperty().bind(this.widthProperty());
		bp.minHeightProperty().bind(this.heightProperty());
		fractalEditor.setContent(gradient);
		fractalEditor.minHeight(200);

	}
	
	/**
	 * @author Calvin
	 * Updates the fractal display if it has been changed
	 */
	public void updateFractalImage() {
		fractal.setScreenResolution(new Dimension((int)fractalView.getFitWidth(), (int)fractalView.getFitHeight()));
		fractalView.setImage(SwingFXUtils.toFXImage(this.fractal.getImage(), null));
		bp.layout();
	}

}
