
package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fractal.Layer;
import fractal.Palette;
import fractal.RenderManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.CheckBoxTreeItem.TreeModificationEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import menus.Display;
import menus.ExpandableMenu;
import menus.ExportImageTool;
import menus.NetworkCreationTool;
import menus.RegisterLayerTool;
import server.Server;
import util.Constants;
import util.Log;
import util.Parameters;
import util.Point;

/**
 * @author David
 */
public class FractalEditor extends Scene {

	private BorderPane bp;
	public Window gradient;

	private RenderManager fractal;
	private ImageView fractalView;
	private TreeView layers, parameters;
	private int layerIndex;
	private boolean zoom = false;
	private Server network;
	private Log log;
	private Layer selectedLayer;

	/**
	 * @author David This instantiates the Fractal Editor scene
	 *
	 * @param x
	 *            width
	 * @param y
	 *            height
	 * @throws FileNotFoundException
	 */
	public FractalEditor(Log log) throws FileNotFoundException {
		super(new BorderPane(), (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.75),
				Toolkit.getDefaultToolkit().getScreenSize().height / 6);
		this.log = log;
		bp = (BorderPane) this.getRoot();
		// initialize();
	}

	/**
	 * Initializes the menus and fractal
	 *
	 * @author David
	 *
	 * @throws FileNotFoundException
	 * @throws AWTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public void initialize() throws FileNotFoundException, AWTException {
		// initializing stuff
		Layer.initializeFractalRegistry();
		File fractalDirectory = new File(Constants.FRACTAL_FILEPATH);
		if (!fractalDirectory.exists()) {
			fractalDirectory.mkdirs();
			try {
				fractalDirectory.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		fractal = new RenderManager();
		selectedLayer = fractal.getLayers().get(0);

		MenuBar menu = new MenuBar();
		SwingNode fractalEditor = new SwingNode();
		parameters = new TreeView();
		layers = new TreeView();
		VBox trees = new VBox();
		trees.minHeightProperty().bind(bp.minHeightProperty().subtract(menu.minHeightProperty()));
		trees.minWidthProperty().bind(bp.minWidthProperty().divide(6));
		trees.setAlignment(Pos.CENTER);

		Button render = new Button("Update");
		render.setFont(new Font("Ariel", 24));
		render.minWidthProperty().bind(trees.minWidthProperty());

		fractalView = new ImageView();
		fractalView.setOnMouseClicked(e -> {
			Point p = new Point(e.getScreenX(), e.getScreenY() - 52);
			double screenDistX = fractalView.getFitWidth() / 2 - p.x;
			double screenDistY = fractalView.getFitHeight() / 2 - p.y;
			double realDistX = -screenDistX * fractal.getRealResolution().x / fractal.getScreenResolution().width;
			double realDistY = screenDistY * fractal.getRealResolution().y / fractal.getScreenResolution().height;
			fractal.setLocation(
					new Point(fractal.getLocation().x + realDistX * 2, fractal.getLocation().y + realDistY * 2));
			this.updateFractalImage();
		});
		fractalView.setOnMouseEntered(e -> {
			zoom = true;
		});
		fractalView.setOnMouseExited(e -> {
			zoom = false;
		});
		this.setOnKeyPressed(e -> {
			if (zoom)
				if (e.getCode() == KeyCode.COMMA) {
					this.fractal.setZoom(this.fractal.getZoom() * 1.5);
					updateFractalImage();
				} else if (e.getCode() == KeyCode.PERIOD) {
					this.fractal.setZoom(this.fractal.getZoom() / 1.5);
					updateFractalImage();
				}
		});
		fractalView.setOnScroll(e -> {
			double zoom = e.getDeltaY() > 0 ? 1 / .9 : .9;
			this.fractal.setZoom(this.fractal.getZoom() * zoom);
			this.updateFractalImage();
		});
		this.fractal = new RenderManager();
		render.setOnAction(e -> {
			updateFractalImage();
		});
		// Fitting the image to the screen
		fractalView.fitWidthProperty().bind(bp.minWidthProperty().subtract(trees.minWidthProperty()));
		fractalView.fitHeightProperty().bind(bp.heightProperty().subtract(menu.minHeightProperty()).subtract(220));

		{// Fitting gradientEditor to full screen
			Dimension p = new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.75), 200);
			System.out.println("Gradient Dimensions: " + p);

			gradient = new Window(p, 50, this.fractal.getLayers().get(0));
		}

		{// Tree Stuff
			parameters.setRoot(new TreeItem());
			parameters.getRoot().setExpanded(true);
			parameters.setShowRoot(false);
			parameters.setEditable(true);
			// TODO if need click for params
			/**
			 * parameters.setOnMouseClicked(new EventHandler<MouseEvent>(){
			 *
			 * @Override public void handle(MouseEvent event) { // TODO
			 *           Auto-generated method stub
			 *           if(event.getClickCount()==2){
			 *           System.out.println("boop"); updateParams(); } }
			 *
			 *           });
			 */
			parameters.setCellFactory(new Callback<TreeView, TreeCell<MetaParam>>() {

				@Override
				public TreeCell call(TreeView param) {
					StringConverter s = new StringConverter() {

						@Override
						public String toString(Object object) {

							return object.toString();
						}

						@Override
						public Object fromString(String string) {
							int index = string.indexOf(":");
							if(!string.substring(index + 1, index + 2).equals(" "))
								string = string.substring(0, index + 1) + " " + string.substring(index + 1);
							Parameters params = new Parameters();
							for (Object o : parameters.getRoot().getChildren()) {
								TreeItem i = (TreeItem) o;
								String msg = i.getValue().toString();
								if(param.getSelectionModel().getSelectedItem().equals(o))
									msg = string;
								String key = msg.substring(0, msg.indexOf(":"));
								String value = msg.substring(msg.indexOf(":") + 2);
								params.put(key, value);
							}
							getSelectedLayer().setParameters(params);
							updateFractalImage();
							return new MetaParam(string.substring(0, string.indexOf(": ")),
									string.substring(string.indexOf(": ") + 2));
						}

					};
					return new TextFieldTreeCell<MetaParam>(s) {
						@Override
						public void updateItem(MetaParam item, boolean empty) {
							super.updateItem(item, empty);
						}

					};
				}
			});

			layers.setRoot(new TreeItem());
			layers.getRoot().setExpanded(true);
			layers.setShowRoot(false);
			// layers.setEditable(true);

			// Use a custom callback to determine the style of the tree item
			layers.setCellFactory(new Callback<TreeView, TreeCell>() {
				@Override
				public TreeCell call(TreeView param) {
					return new CheckBoxTreeCell() {
						@Override
						public void updateItem(Object item, boolean empty) {
							super.updateItem(item, empty);
							// If there is no information for the Cell, make it
							// empty
							if (empty) {
								setGraphic(null);
								setText(null);
								// Otherwise if it's not representation as an
								// item of the tree
								// is not a CheckBoxTreeItem, remove the
								// checkbox item
							} else if (!(getTreeItem() instanceof CheckBoxTreeItem)) {
								BufferedImage image;
								try {
									image = ImageIO.read(new File("textures\\plusButton.png"));
									Image plusImage = SwingFXUtils.toFXImage(image, null);
									ImageView plusView = new ImageView(plusImage);
									plusView.setFitHeight(16);
									plusView.setFitWidth(16);
									setGraphic(plusView);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							setOnDragDetected(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent mouseEvent) {
									System.out.println("wofenh");
									layers.getRoot().getChildren().remove(mouseEvent.getSource());
									layers.layout();
								}
							});

						}
					};
				}
			});
			
			
			TreeItem add = new TreeItem();

			layerIndex = 1;
			CheckBoxTreeItem item = getNewTreeItem();

			layers.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if (mouseEvent.getClickCount() == 1) {
						if (layers.getSelectionModel().getSelectedItem() != add) {
							updateParams();
						}
						if (layers.getSelectionModel().getSelectedItem() == add) {
							CheckBoxTreeItem i = getNewTreeItem();
							layers.getRoot().getChildren().add(0, i);
							fractal.addLayer("HistogramLayer");
							fractal.getLayers().get(fractal.getLayers().size() - 1).setName("Layer " + layerIndex);
							updateFractalImage();
						}
					}
					if (mouseEvent.getClickCount() == 2) {
						if (layers.getSelectionModel().getSelectedItem() == add) {
							
						} else {
							((TreeItem) layers.getSelectionModel().getSelectedItem())
									.setValue(GradientMenus.displayLayerMenu(
											(TreeItem<MetaLayer>) layers.getSelectionModel().getSelectedItem()));
							MetaLayer meta = ((MetaLayer) ((TreeItem) layers.getSelectionModel().getSelectedItem())
									.getValue());
							int index = layers.getRoot().getChildren().size() - 2 - layers.getRoot().getChildren()
									.indexOf(layers.getSelectionModel().getSelectedItem());
							Layer l = fractal.getLayers().get(index);
							if (meta.isDelete()) {
								fractal.getLayers().remove(index);
								updateFractalImage();
								layers.getRoot().getChildren().remove(layers.getSelectionModel().getSelectedItem());
								return;
							} else if (!meta.getType().equals(l.getClass().getSimpleName())) {
								Layer newLayer = Layer.getLayerByType(meta.getType());
								newLayer.init(new Palette(), index + 1);
								l = newLayer;
								fractal.getLayers().set(index, newLayer);
								fractal.update();
							}
							l.setOpacity(meta.getOpacity());
							fractal.getLayers().get(index).setName(meta.getName());
							updateFractalImage();
						}
					} else if (mouseEvent.getClickCount() == 1 && layers.getSelectionModel().getSelectedItem() != add) {
						int index = layers.getRoot().getChildren().size() - 2
								- layers.getRoot().getChildren().indexOf(layers.getSelectionModel().getSelectedItem());
						Layer l = fractal.getLayers().get(index);
						gradient.updateLayer(l);
					}
				}
			});
			// TODO LAYER UP LAYER DOWN
			layers.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
				if (e.getCode() == KeyCode.RIGHT) {
					moveUp((TreeItem) layers.getSelectionModel().getSelectedItem());
				}
				if (e.getCode() == KeyCode.LEFT) {

				}
			});

			layers.getRoot().getChildren().addAll(item, add);

		}

		{// This is the menu stuff
			Menu network = new Menu("Network");
			Menu fractal = new Menu("Fractal");
			Menu system = new Menu("System");

			MenuItem newNet = new MenuItem("Create New Network");
			MenuItem viewNetLog = new MenuItem("View Network Log");
			MenuItem viewNet = new MenuItem("View Network");
			MenuItem endNet = new MenuItem("Close Network");

			newNet.setOnAction(e -> {
				NetworkCreationTool createNet = new NetworkCreationTool();
				if (createNet.createNetwork()) {
					this.network = createNet.getServer();
					this.network.init(log);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Network Message");
					alert.setHeaderText(null);
					alert.setContentText("Network succesfully created.");
					viewNet.setDisable(false);
					endNet.setDisable(false);
					newNet.setDisable(true);
					viewNetLog.setDisable(false);
					alert.showAndWait();
				}
			});
			viewNet.setDisable(true);
			endNet.setDisable(true);
			viewNetLog.setDisable(true);
			viewNet.setOnAction(e -> {
				Display display = new Display(this.network);

				this.network.setDisplay(display);
				display.updateNetworkView(this.network.getChildren(), this.network.getUncompletedJobs());
			});
			endNet.setOnAction(e -> {
				// TODO: kill won't stop network without getting a response from
				// all clients
				this.network.kill();
				viewNet.setDisable(true);
				endNet.setDisable(true);
				viewNetLog.setDisable(true);
				newNet.setDisable(false);
			});
			viewNetLog.setOnAction(e -> {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Network Log");
				alert.setHeaderText(null);
				alert.setContentText(this.network.getLog().getLog());

				alert.showAndWait();
			});

			network.getItems().addAll(newNet, viewNet, viewNetLog, endNet);

			MenuItem newFract = new MenuItem("New Fractal");
			newFract.setOnAction(e -> {
				this.fractal = new RenderManager();
				gradient.updateLayer(this.fractal.getLayers().get(0));
				deleteLayers();
				CheckBoxTreeItem i = getNewTreeItem();
				layers.getRoot().getChildren().add(0, i);
				this.fractal.addLayer("HistogramLayer");
				this.updateFractalImage();
			});
			MenuItem openFract = new MenuItem("Open Fractal");
			openFract.setOnAction(e -> {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Open Fractal");
				chooser.setInitialDirectory(new File("fractals"));
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fractals (*.fractal)",
						"*.fractal");
				chooser.getExtensionFilters().add(filter);
				File f = chooser.showOpenDialog(null);
				this.fractal = new RenderManager(f);
				gradient.updateLayer(this.fractal.getLayers().get(0));
				
				deleteLayers();
				for(int k = 0; k < this.fractal.getLayers().size(); k++) {
					Layer l = this.fractal.getLayers().get(k);
					CheckBoxTreeItem i = getNewTreeItem();
					MetaLayer meta = (MetaLayer)(i.getValue());
					meta.setName(l.getName());
					meta.setOpacity(l.getOpacity());
					meta.setType(l.getClass().getSimpleName());
					layers.getRoot().getChildren().add(0, i);
				}
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
			MenuItem exportFract = new MenuItem("Export Fractal");
			exportFract.setOnAction(e -> {
				ExportImageTool export = new ExportImageTool();
				export.exportImage(this.fractal);
			});

			fractal.getItems().addAll(newFract, openFract, saveFract, saveFractAs, exportFract);

			MenuItem newLayer = new MenuItem("New Layer Type");
			newLayer.setOnAction(e -> {
				RegisterLayerTool register = new RegisterLayerTool();
				register.registerLayer();
				Layer.registerLayer(register.getFile());
			});
			MenuItem help = new MenuItem("Help");
			help.setOnAction(e-> {
				ExpandableMenu.displayInformation("Help", "Instructions:", getHelpText());
			});

			system.getItems().addAll(newLayer, help);

			menu.getMenus().addAll(fractal);
			menu.getMenus().addAll(network);
			menu.getMenus().addAll(system);

		}

		VBox center = new VBox();
		center.getChildren().addAll(fractalView, fractalEditor);
		trees.getChildren().addAll(parameters, layers, render);

		bp.setCenter(center);
		bp.setRight(trees);
		// This is where the menu is located on the border pane
		bp.setTop(menu);

		bp.minWidthProperty().bind(this.widthProperty());
		bp.minHeightProperty().bind(this.heightProperty());
		render.minHeightProperty().bind(trees.minHeightProperty().subtract(fractalView.fitHeightProperty()));
		fractalEditor.setContent(gradient);
		fractalEditor.minHeight(200);

		updateParams(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CheckBoxTreeItem getNewTreeItem() {
		CheckBoxTreeItem i = new CheckBoxTreeItem(new MetaLayer("Layer" + incrementLayers(), "HistogramLayer"));
		i.setSelected(true);
		i.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(),
				new EventHandler<CheckBoxTreeItem.TreeModificationEvent>() {
					public void handle(TreeModificationEvent event) {
						int index = layers.getRoot().getChildren().size() - 2
								- layers.getRoot().getChildren().indexOf(i);
						fractal.setLayerVisiblity(index, i.selectedProperty().get());
						updateFractalImage();
					}
				});
		return i;
	}

	private Layer getSelectedLayer() {
		try {
			int index = layers.getRoot().getChildren().size() - 2
					- layers.getRoot().getChildren().indexOf(layers.getSelectionModel().getSelectedItem());
			selectedLayer = fractal.getLayers().get(index);
		} finally {
			return selectedLayer;
		}
	}

	private int incrementLayers() {
		return layerIndex++;
	}

	/**
	 * Updates the fractal display if it has been changed
	 *
	 * @author Calvin
	 */
	public void updateFractalImage() {
		gradient.repaint();
		fractal.setScreenResolution(new Dimension((int) fractalView.getFitWidth(), (int) fractalView.getFitHeight()));
		fractalView.setImage(SwingFXUtils.toFXImage(this.fractal.getImage(), null));
		bp.layout();
	}

	public Server getServer() {
		return network;
	}

	private void updateParams() {
		while (!parameters.getRoot().getChildren().isEmpty())
			parameters.getRoot().getChildren().remove(0);
		for (String i : getSelectedLayer().getParameters().keySet()) {
			parameters.getRoot().getChildren()
					.add(new TreeItem(new MetaParam(i, getSelectedLayer().getParameters().getParameter(i))));
			}

		}
		private void updateParams(int waste) {
			while (!parameters.getRoot().getChildren().isEmpty())
				parameters.getRoot().getChildren().remove(0);
			layers.getSelectionModel().select(1);
			for (String i : getSelectedLayer().getParameters().keySet()) {
				parameters.getRoot().getChildren()
						.add(new TreeItem(new MetaParam(i, getSelectedLayer().getParameters().getParameter(i))));

			}
	}

	static void moveUp(TreeItem item) {
		System.out.println("moveUp");
		if (item.getParent() instanceof TreeItem) {
			TreeItem parent = item.getParent();
			List<TreeItem> list = new ArrayList<TreeItem>();
			Object prev = null;
			for (Object child : parent.getChildren()) {
				if (child == item) {
					list.add((TreeItem) child);
				} else {
					if (prev != null)
						list.add((TreeItem) prev);
					prev = child;
				}
			}
			if (prev != null)
				list.add((TreeItem) prev);
			parent.getChildren().clear();
			parent.getChildren().addAll(list);
		}
	}

	public void deleteLayers(){
		while(layers.getRoot().getChildren().size()>1){
			layers.getRoot().getChildren().remove(0);
		}
	}

	
	private static String getHelpText() {
		return "Navigating the fractal:\r\n" + 

	"-click around on the fractal to move the viewport\r\n" +

	"-Click ',' to zoom in and '.' to zoom out\r\n\r\n" +


"Editing the fractal:\r\n" +

	"-The fractal consists of a series of layers that are rendered in a certain order. There are different types of layers and each can have a different gradient.\r\n"
	+ "-To add a new layer, double click the '+' button.\r\n" + 

	"-To edit the type of layer, the name of the layer, and the opacity of the layer, double click the layer.\r\n" +

	"-To change the order in which layers are rendered, use the the left and the right arrow keys while selecting a fractal.\r\n" +

	"-When a layer is selected, its gradient will be displayed below the fractal. Arrow buttons on the bottom of the gradient represent color data points, and arrow buttons on top of the gradient represent opacity data points.\r\n" +

	"-New points can be added by clicking above or below the gradient. The data of each of the points can be edited by clicking the points.\r\n" +

	"-The colored, square rectangle to the right of the gradient is the color the inside of the fractal will be colord. It can be modified by clicking the square.\r\n" +

	"-The two buttons below the square represent ways to save and load in palettes. By default, palettes are saved as a part of the fractal, but if you create one that you especially love and wish to use later save it to the palettes folder. You can then load it in to other fractals!\r\n" +

	"-When a layer is selected, layer-based parameters that can be edited will show up in the upper right hand corner of the screen. Double click those parameters to edit them.\r\n\r\n" +


"Menus:" +

	"-'New Fractal', under the 'Frctal' menu, will open up a new, default fractal. All changes made to the previous fractal will be lost, so save first.\r\n" +

	"-'Open Fractal', under the 'Fractal' menu, will open a dialog alowing the user to open up a previously saved fractal. Opening this fractal will result in all changes to the old one being lost.\r\n" +

	"-'Save Fractal', under the 'Fractal' menu, will save the fractal. If the fractal has never been saved before, it will open a dialog alowing the user to name the fracral and choose a file location. Otherwise, it will save to the file location.\r\n" +

	"-'Save Fractal As', under the 'Fractal' menu, will save the fractal as a new file. It will not change the name of file location of the fractal. This is useful for version control.\r\n" +

	"-'Export Fractal', under the 'Fractal' menu, will alow the user to export the fractal as a .png or a .jpg image to a specified file location. The user can also choose a resolution for the saved image.\r\n" +

	"-'Create New Network', under the 'Network' menu, opens up a series of dialogs helping the user set up a network for use in a distributed zoom. The user chooses a fractal to zoom in on and sets parameters. The user can only choose from fractals stored in the 'fractals' folder.\r\n" +

	"-'View Network' will, under the 'Network' menu, will, if a network has been started, alow the user to visualy view the network. It will display statistics about the network, alow the user to modify the parameters, and give the user some basic control over all computers connected to the distributed network. There are more menus at the top of the Network View as well.\r\n" +

	"-'View Network Log', under the 'Network' menu, alows the user to view the log of the network, assuming a network has been started. The Network log can also be viewed through the 'Network View'.\r\n" +

	"-'Close Network', under the 'Network' menu, will shut down the network.\r\n" +

	"-'New Layer Type', under the 'System' menu, will alow the user to register a new layer type while the application is still running. The layer could also be registered by placing it in the 'custom' folder and restarting the application.\r\n" +
	
	"-'Edit Log Options', under the 'System' menu, will alow the user to modify the log options. There are two main categories: log and print. Log controls what is saved to the log file, while print controls what is printed to the cmd (if it is used to run the application). LEVEL_ERROR prints only errors, LEVEL_LOg prints everything, and LEVEL_NONE prints nothing.\r\n\r\n" +


"Running a Network:" +

	"-First, there must be a fractal file saved in the 'fractals' folder. You will be able to choose between all fractals in that folder for the network to zoom in on\r\n" +

	"-Next, follow the menus that will help set up the network by clicking the 'Create New Network' menu option. After that is up and running, you can view the network by clicking 'View Network'. You can also view the network log either through the fractal editor menus or through the Network View menus.\r\n" +

	"-To connect clients to the network, run the 'Client' application. The clients must be on the local network. If there are issues, ensure that ports 6664 and 8888 are open and that your router allows for UDP broadcasts. \r\n" +

	"-The images rendered by the clients will save to the directory selected during the Network creation process. You can compile those images into a video however you want.\r\n" +

	"-You can close the network when you are done by either using the 'Close Network' option in the menus or by closing the fractal editor. The client computers do not need to only render the fractals: feel free to use them while they render.\r\n\r\n" +


"Creating new layer types:" +

	"-If you wish to create and use your own, custom layer types, you are free to do so. There are two ways to add them to the program.\r\n" +

	"-The easiest is to place the .java file in the 'custom' folder or, if you have a .class file, place that in the 'custom/fractal' folder. Upon startup, any layer files located in these folders will be added to the registry and made usable.\r\n" +

	"-Alternatively, if you have the .java file, you can use the 'New Layer Type' menu to import it into the application without restarting it. This layer type will be removed if the application is retarted unless the .java file is in the 'custom' folder.\r\n" +

	"-IMPORTANT: Adding custom layers to the application is done through dynamic rendering with the Reflections API. This will ONLY work if the application is run through a jdk. If it is run with a jre, your custm layers will not be added.\r\n" +

	"-The actual programming of the layer file is more complicated. Start by downloading the source code for this application. Then, create a new class that extends Layer. From there, you must implement a number of abstract methods in order for the layer to work. For more information on what needs to be implements and what the methods do, refer to the Layer documentation and the HistogramLayer and TriangleAverageLayer source code.";

	}
}
