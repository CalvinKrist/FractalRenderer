package application;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import fractal.Layer;
import fractal.Palette;
import fractal.RenderManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import menus.Display;
import menus.ExportImageTool;
import menus.NetworkCreationTool;
import menus.RegisterLayerTool;
import server.Server;
import util.Constants;
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
	private TreeView layers, parameters;
	private int layerIndex;
	private boolean zoom = false;
	private Server network;
	private Log log;

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
		if(!fractalDirectory.exists()) {
			fractalDirectory.mkdirs();
			try {
				fractalDirectory.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		fractal = new RenderManager();

		MenuBar menu = new MenuBar();
		SwingNode fractalEditor = new SwingNode();
		parameters = new TreeView();
		layers = new TreeView();
		VBox trees = new VBox();
		trees.minHeightProperty().bind(bp.minHeightProperty().subtract(menu.minHeightProperty()));
		trees.minWidthProperty().bind(bp.minWidthProperty().divide(6));
		// bp.setPadding(new Insets(5));

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
			//TODO if need click for params
			/**parameters.setOnMouseClicked(new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					if(event.getClickCount()==2){
						System.out.println("boop");
						updateParams();
					}
				}
				
			});*/
			parameters.setCellFactory(new Callback<TreeView, TreeCell<MetaParam>>(){

				@Override
				public TreeCell call(TreeView param) {
					// TODO Auto-generated method stub
					StringConverter s = new StringConverter(){

						@Override
						public String toString(Object object) {
							// TODO Auto-generated method stub
							return object.toString();
						}

						@Override
						public Object fromString(String string) {
							// TODO Auto-generated method stub
							return new MetaParam(string.substring(0,string.indexOf(": ")),string.substring(string.indexOf(": ")+2));
						}
						
					};
					return new TextFieldTreeCell<MetaParam>(s) {
						@Override
						public void updateItem(MetaParam item, boolean empty){
							super.updateItem(item, empty);
						}
							
					};
				}
			});
			

			layers.setRoot(new TreeItem());
			layers.getRoot().setExpanded(true);
			layers.setShowRoot(false);
			//layers.setEditable(true);

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
					if(mouseEvent.getClickCount() == 1){
						if(layers.getSelectionModel().getSelectedItem()!=add){
							updateParams();
						}
					}
					if (mouseEvent.getClickCount() == 2) {
						if (layers.getSelectionModel().getSelectedItem() == add) {
							CheckBoxTreeItem i = getNewTreeItem();
							layers.getRoot().getChildren().add(0, i);
							fractal.addLayer("HistogramLayer");
							updateFractalImage();
						} else {
							((TreeItem) layers.getSelectionModel().getSelectedItem()).setValue(GradientMenus
									.displayLayerMenu((TreeItem<MetaLayer>) layers.getSelectionModel().getSelectedItem()));
							MetaLayer meta = ((MetaLayer) ((TreeItem) layers.getSelectionModel().getSelectedItem())
									.getValue());
							int index = layers.getRoot().getChildren().size() - 2 - layers.getRoot().getChildren()
									.indexOf(layers.getSelectionModel().getSelectedItem());
							Layer l = fractal.getLayers().get(index);
							if (!meta.getType().equals(l.getClass().getSimpleName())) {
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

			layers.getRoot().getChildren().addAll(item, add);
			{// TODO I DONT KNOW WHY THIS ISNT WORKING IUEAWBIUBFAI
				/**
				 * layers.getRoot().addEventHandler(layers.getRoot().childrenModificationEvent(),e
				 * -> { for(Object i :((TreeItem)e.getSource()).getChildren()){
				 * if(i!=add)
				 * if(((MetaLayer)((TreeItem)(i)).getValue()).isDelete()){
				 * layers.getSelectionModel().select(i);
				 * layers.getSelectionModel().clearSelection(); } } });
				 */
			}
		}

		fractalEditor.setOnMouseEntered(e -> gradient.repaint());

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
				//TODO: kill won't stop network without getting a response from all clients
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

			system.getItems().addAll(newLayer);

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
		fractalEditor.setContent(gradient);
		fractalEditor.minHeight(200);

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
		int index = layers.getRoot().getChildren().size() - 2 - layers.getRoot().getChildren()
				.indexOf(layers.getSelectionModel().getSelectedItem());
		return fractal.getLayers().get(index);
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
	
	private void updateParams(){
		while(!parameters.getRoot().getChildren().isEmpty())
			parameters.getRoot().getChildren().remove(0);
		for(String i : getSelectedLayer().getParameters().keySet()){
			parameters.getRoot().getChildren().add(new TreeItem( new MetaParam(i,getSelectedLayer().getParameters().getParameter(i))));

		}
	}
}
