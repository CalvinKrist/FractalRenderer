package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;

import fractal.Layer;
import fractal.RenderManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import menus.Display;
import menus.NetworkCreationTool;
import server.Server;
import util.Log;
import util.Point;

/**
 * @author David
 */
public class FractalEditor extends Scene {

	private BorderPane bp;
	public Window gradient;

	private RenderManager fractal;
	private ImageView fractalView;
	private int layerIndex;
	private boolean zoom = false;
	private Server network;
	private Log log;

	/**@author David
	 * This instantiates the Fractal Editor scene
	 *
	 * @param x
	 *            width
	 * @param y
	 *            height
	 * @throws FileNotFoundException
	 */
	public FractalEditor(Log log) throws FileNotFoundException {
		super(new BorderPane(),(int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.75), Toolkit.getDefaultToolkit().getScreenSize().height/6);
		this.log = log;
		bp = (BorderPane) this.getRoot();
		// initialize();
		//TODO remove size parameters from constructor
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
		//bp.setPadding(new Insets(5));


		Button render = new Button("Update");
		render.minWidthProperty().bind(trees.minWidthProperty());
		render.minHeightProperty().bind(trees.minHeightProperty().divide(6));


		fractalView = new ImageView();
		fractalView.setOnMouseClicked(e -> {
			Point p = new Point(e.getScreenX(), e.getScreenY() - 52);
			double screenDistX = fractalView.getFitWidth() / 2 - p.x;
			double screenDistY = fractalView.getFitHeight() / 2 - p.y;
			double realDistX = -screenDistX * fractal.getRealResolution().x / fractal.getScreenResolution().width;
			double realDistY = screenDistY * fractal.getRealResolution().y / fractal.getScreenResolution().height;
			fractal.setLocation(new Point(fractal.getLocation().x + realDistX * 2, fractal.getLocation().y + realDistY * 2));
			this.updateFractalImage();
		});
		fractalView.setOnMouseEntered(e-> {
			zoom = true;
		});
		fractalView.setOnMouseExited(e-> {
			zoom = false;
		});
		this.setOnKeyPressed(e-> {
			if(zoom)
				if(e.getCode() == KeyCode.COMMA) {
					this.fractal.setZoom(this.fractal.getZoom() * 1.5);
					updateFractalImage();
				}
				else if(e.getCode() == KeyCode.PERIOD) {
					this.fractal.setZoom(this.fractal.getZoom() / 1.5);
					updateFractalImage();
				}
		});
		fractalView.setOnScroll(e-> {
			double zoom = e.getDeltaY() > 0 ? 1/.9 : .9;
			this.fractal.setZoom(this.fractal.getZoom() * zoom);
			this.updateFractalImage();
		});
		this.fractal = new RenderManager();
		render.setOnAction(e -> {
			updateFractalImage();
		});
		//Fitting the image to the screen
		fractalView.fitWidthProperty().bind(bp.minWidthProperty().subtract(trees.minWidthProperty()));
		fractalView.fitHeightProperty().bind(bp.heightProperty().subtract(menu.minHeightProperty()).subtract(220));

		{//Fitting gradientEditor to full screen
		Dimension p = new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.75),200);
		System.out.println("Gradient Dimensions: "+p);

		gradient = new Window(p, 50, this.fractal.getLayers().get(0));
		}
		{//Tree Stuff
		parameters.setRoot(new TreeItem("params"));
		parameters.getRoot().setExpanded(true);

		TreeItem xPos = new TreeItem();
		parameters.getRoot().getChildren().addAll(xPos);

		layers.setRoot(new CheckBoxTreeItem("Layers"));
		layers.getRoot().setExpanded(true);
		layers.setShowRoot(false);
		layers.setEditable(true);
		layers.setCellFactory(new Callback<TreeView,CheckBoxTreeCell>(){
            @Override
            public CheckBoxTreeCell call(TreeView p) {
                return new CheckCell();
            }
        });
		
		CheckBoxTreeItem add = new CheckBoxTreeItem("ADD");
		/**
		try{
		BufferedImage image = ImageIO.read(new File("client\\textures\\plusButton.jpg"));
		Image plusImage = SwingFXUtils.toFXImage(image, null);
		add.setGraphic(new ImageView(plusImage));
		}catch(Exception e){
			e.printStackTrace();
		}*/
		layerIndex = 1;
		add.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), e -> {
			if(((CheckBoxTreeItem)(e.getSource())).isSelected()){
				((CheckBoxTreeItem)(e.getSource())).getParent().getChildren().add(0, new CheckBoxTreeItem("Layer"+incrementLayers()));
			((CheckBoxTreeItem)(e.getSource())).setSelected(false);
			}
		layers.getRoot().addEventHandler(layers.getRoot().childrenModificationEvent(), e -> {
			e.getSource();
		});});
		layers.getRoot().getChildren().addAll(add);
		
		}

		//Trying to get this to work
		fractalEditor.setOnMouseEntered(e -> gradient.repaint());


		{//This is the menu stuff
			Menu network = new Menu("Network");
			Menu fractal = new Menu("Fractal");

			MenuItem newNet = new MenuItem("Create New Network");
			MenuItem viewNet = new MenuItem("View Network");

			newNet.setOnAction(e-> {
				NetworkCreationTool createNet = new NetworkCreationTool();
				if(createNet.createNetwork()) {
					this.network = createNet.getServer();
					this.network.init(log);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Network Message");
					alert.setHeaderText(null);
					alert.setContentText("Network succesfully created.");
					viewNet.setDisable(false);
					alert.showAndWait();
				}
			});
			viewNet.setDisable(true);
			viewNet.setOnAction(e->{
				Display display = new Display(this.network);
				
			});
			
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
				File f = chooser.showOpenDialog(null);
				this.fractal = new RenderManager(f);
				gradient.updateLayer(this.fractal.getLayers().get(0));
				this.updateFractalImage();
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
	private int incrementLayers(){
		return layerIndex++;
	}
	/**
	 * @author Calvin
	 * Updates the fractal display if it has been changed
	 */
	public void updateFractalImage() {
		gradient.repaint();
		fractal.setScreenResolution(new Dimension((int)fractalView.getFitWidth(), (int)fractalView.getFitHeight()));
		fractalView.setImage(SwingFXUtils.toFXImage(this.fractal.getImage(), null));
		bp.layout();
	}

}
