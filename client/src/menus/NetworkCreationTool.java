package menus;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fractal.RenderManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;
import server.Server;
import util.Constants;

/**
 * This tool, upon initialization, will bring the user through a series of menus helping them 
 * set up a Network for use. It will then use this process to create a server which can be 
 * accessed afterwards
 * @author Calvin
 *
 */
public class NetworkCreationTool {

	/**
	 * The server created by this tool
	 */
	private Server server;

	public NetworkCreationTool() {
	}

	/**
	 * This method, when called, will bring the user through a series of menus helping them
	 * configure the Network they wish to set up.
	 * @return whether or not the network was successfully created.
	 */
	public boolean createNetwork() {

		File[] fractals = new File(Constants.FRACTAL_FILEPATH).listFiles();
		List<String> choices = new ArrayList<>();
		for (File f : fractals) {
			if(!f.getName().equals("palettes"))
			choices.add(f.getName());
		}

		ChoiceDialog<String> dialog1 = null;
		try {
			dialog1 = new ChoiceDialog<>(choices.get(0), choices);
		} catch(IndexOutOfBoundsException e) {
			AlertMenu alarm = new AlertMenu("Cannot create network: no fractal found on this computer.", "Fractals are searched for in the fractals folder. Please save a fractal to that folder and try again.");
		}
		dialog1.setTitle("Create Network");
		dialog1.setHeaderText("Step 1");
		dialog1.setContentText("Choose a fractal:");
		System.out.println("here...");
		RenderManager fractal = null;
		try {
			fractal = new RenderManager(dialog1.showAndWait().get());
		} catch (Exception e) {
			return false;
		}
		Pair<Integer, Integer> dimension = displayDialog2();
		if (dimension == null)
			return false;
		fractal.setScreenResolution(new Dimension(dimension.getKey(), dimension.getValue()));

		Pair<Double, Double> params = getParams();
		if (params == null)
			return false;
		if(params.getKey() == null)
			return false;
		if(params.getValue() == null)
			return false;
		
		fractal.setZoom(params.getKey());

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Create Network");
		alert.setHeaderText("Step 4");
		alert.setContentText("Choose a directory:");

		ButtonType buttonTypeOne = new ButtonType("Choose");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		String directory = "";
		if (result.get() == buttonTypeOne) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(null);

			if (selectedDirectory == null) {
				return false;
			} else {
				directory = selectedDirectory.getPath();
			}
		} else {
			return false;
		}

		this.server = new Server(fractal, params.getValue(), directory);

		return true;
	}

	private Pair<Double, Double> getParams() {
		Dialog<Pair<String, String>> dialog3 = new Dialog<>();
		dialog3.setTitle("Create Network");
		dialog3.setHeaderText("Step 3");
		dialog3.setContentText("Choose Parameters:");
		
		dialog3.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField zoomField = new TextField(".25");
		TextField zoomSpeedField = new TextField("1.05");

		grid.add(new Label("Zoom Level:"), 0, 0);
		grid.add(zoomField, 1, 0);
		grid.add(new Label("Zoom Speed:"), 0, 1);
		grid.add(zoomSpeedField, 1, 1);

		dialog3.getDialogPane().setContent(grid);
		dialog3.setResultConverter(dialogButton->{
			if(dialogButton == ButtonType.OK)
				return new Pair<String, String>(zoomField.getText(), zoomSpeedField.getText());
			return null;
		});

		Optional<Pair<String, String>> result = dialog3.showAndWait();

		// Traditional way to get the response value.
		Double speed = null;
		try {
			speed = Double.valueOf(result.get().getValue());
		} catch (NumberFormatException e) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Zoom speed must be a real number.", "Please try again.");
			return getParams();
		} catch (Exception e) {
			return null;
		}
		if (speed <= 0) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Zoom speed must be greater than 0.", "Please try again");
			return getParams();
		}
		
		Double zoom = null;
		try {
			zoom = Double.valueOf(result.get().getKey());
		} catch (NumberFormatException e) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Zoom level must be a real number.", "Please try again.");
			return getParams();
		} catch (Exception e) {
			return null;
		}
		if (zoom <= 0) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Zoom level must be greater than 0.", "Please try again");
			return getParams();
		}
		
		return new Pair<Double, Double>(zoom, speed);
	}

	private Pair<Integer, Integer> displayDialog2() {
		Dialog<Pair<String, String>> dialog2 = new Dialog<>();
		dialog2.setTitle("Create Network");
		dialog2.setHeaderText("Step 2");
		dialog2.setContentText("Choose an image resolution:");

		dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField widthField = new TextField("1920");
		widthField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					widthField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		TextField heightField = new TextField("1080");
		heightField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					heightField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		grid.add(new Label("Width:"), 0, 0);
		grid.add(widthField, 1, 0);
		grid.add(new Label("Height:"), 0, 1);
		grid.add(heightField, 1, 1);

		dialog2.getDialogPane().setContent(grid);
		dialog2.setResultConverter(dialogButton->{
			if(dialogButton == ButtonType.OK)
				return new Pair<String, String>(widthField.getText(), heightField.getText());
			return null;
		});

		Optional<Pair<String, String>> result = dialog2.showAndWait();

		int width = 0;
		int height = 0;
		try {
			width = Integer.valueOf(result.get().getKey());
			height = Integer.valueOf(result.get().getValue());
		} catch (NumberFormatException e) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Must be an integer.", "Please try again");
			return displayDialog2();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new Pair<Integer, Integer>(width, height);
	}

	/**
	 * This method should only be called after createNetwork() has been called, and returned true. If that has
	 * happened, it will return the server created in the createNetwork() method.
	 * @return the server created in the createNetwork() method.
	 */
	public Server getServer() {
		return server;
	}

}
