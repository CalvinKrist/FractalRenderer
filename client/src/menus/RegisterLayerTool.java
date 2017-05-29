package menus;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import util.Constants;

public class RegisterLayerTool {
	
	private File file;
	
	public RegisterLayerTool() {}
	
	public boolean registerLayer() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Register Layer");
		alert.setHeaderText(null);
		alert.setContentText("Choose a file:");

		ButtonType buttonTypeOne = new ButtonType("Choose");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		String directory = "";
		if (result.get() == buttonTypeOne) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose Layer");
			fileChooser.setInitialDirectory(new File(Constants.CUSTOM_FRACTAL_FILEPATH));
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Layers (*.java)",
					"*.java");
			fileChooser.getExtensionFilters().add(filter);
			File selectedFile = fileChooser.showOpenDialog(null);

			if (selectedFile == null) {
				return false;
			} else {
				file = selectedFile;
			}
		} else {
			return false;
		}
		
		
		return true;
	}
	
	public File getFile() {
		return file;
	}

}
