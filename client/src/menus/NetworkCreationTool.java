package menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fractal.RenderManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import server.Server;
import util.Constants;

public class NetworkCreationTool {
	
	private Server server;
	
	public NetworkCreationTool() {}
	
	public boolean createNetwork() {
		
		RenderManager fractal;
		double zoomSpeed;
		Server s;
		
		File[] fractals = new File(Constants.FRACTAL_FILEPATH).listFiles();
		List<String> choices = new ArrayList<>();
		for(File f: fractals)
			choices.add(f.getName().substring(0, f.getName().indexOf(".")));

		ChoiceDialog<String> dialog1 = new ChoiceDialog<>(choices.get(0), choices);
		dialog1.setTitle("Create Network");
		dialog1.setHeaderText("Step 1");
		dialog1.setContentText("Choose a fractal:");

		// Traditional way to get the response value.
		try {
			fractal = new RenderManager(dialog1.showAndWait().get());
		} catch(Exception e) {return false;}
		
		Dialog<Pair<String, String>> dialog2 = new Dialog<>();
		dialog2.setTitle("Create Network");
		dialog2.setHeaderText("Step 2");
		dialog2.setContentText("Choose an image resolution:");
		
		dialog2.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField widthField = new TextField();
		widthField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
	                widthField.setText(newValue.replaceAll("[^\\d]", ""));
	            }
			}
		});
		TextField heightField = new TextField();
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

		Optional<Pair<String, String>> result = dialog2.showAndWait();

		try {
			int width = Integer.valueOf(result.get().getKey());
			int height = Integer.valueOf(result.get().getValue());
		} catch()
		
		
		return true;
	}
	
	public Server getServer() {
		return server;
	}

}
