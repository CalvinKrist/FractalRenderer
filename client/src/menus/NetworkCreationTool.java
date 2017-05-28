package menus;

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

public class NetworkCreationTool {

	private Server server;

	public NetworkCreationTool() {
	}

	public boolean createNetwork() {

		File[] fractals = new File(Constants.FRACTAL_FILEPATH).listFiles();
		List<String> choices = new ArrayList<>();
		for (File f : fractals) {
			if(!f.getName().equals("palettes"))
			choices.add(f.getName());
		}

		ChoiceDialog<String> dialog1 = new ChoiceDialog<>(choices.get(0), choices);
		dialog1.setTitle("Create Network");
		dialog1.setHeaderText("Step 1");
		dialog1.setContentText("Choose a fractal:");

		// Traditional way to get the response value.
		RenderManager fractal = null;
		try {
			fractal = new RenderManager(dialog1.showAndWait().get());
		} catch (Exception e) {
			return false;
		}

		Pair<Integer, Integer> dimension = displayDialog2();
		if (dimension == null)
			return false;

		Double zoomSpeed = getZoomSpeed();
		if (zoomSpeed == null)
			return false;

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

		this.server = new Server(fractal, zoomSpeed.doubleValue(), directory);

		return true;
	}

	private Double getZoomSpeed() {
		TextInputDialog dialog3 = new TextInputDialog("1.1");
		dialog3.setTitle("Create Network");
		dialog3.setHeaderText("Step 3");
		dialog3.setContentText("Zoom speed:");

		// Traditional way to get the response value.
		Double d = null;
		try {
			d = Double.valueOf(dialog3.showAndWait().get());
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("");
			alert.setHeaderText("INVALID INPUT: Must be a real number.");
			alert.setContentText("Please try again.");
			alert.showAndWait();
			return getZoomSpeed();
		} catch (Exception e) {
			return null;
		}
		if (d <= 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("");
			alert.setHeaderText("INVALID INPUT: Zoom speed must be greater than 0.");
			alert.setContentText("Please try again.");
			alert.showAndWait();
			return getZoomSpeed();
		}
		return d;
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

		TextField widthField = new TextField("1600");
		widthField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					widthField.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		TextField heightField = new TextField("1600");
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
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("");
			alert.setHeaderText("INVALID INPUT: Must be an integer.");
			alert.setContentText("Please try again.");
			alert.showAndWait();
			return displayDialog2();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new Pair<Integer, Integer>(width, height);
	}

	public Server getServer() {
		return server;
	}

}
