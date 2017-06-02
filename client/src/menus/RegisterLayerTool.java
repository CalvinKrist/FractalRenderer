package menus;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import util.Constants;

/**
 * This is a tool that takes the user through a series of menus to register a new layer type while the program is still
 * running. Alternatively, new layer types can be registered by just restarting the application.
 * @author Calvin
 *
 */
public class RegisterLayerTool {
	
	private File file;
	
	/**
	 * An empty constructor
	 */
	public RegisterLayerTool() {}
	
	/**
	 * This method takes the user through a series of menus that, at the end, will locate the file
	 * containing the data for the new layer type. It will then store that file internally for access later.
	 * @return true if the file was located, false if the user cancelled.
	 */
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
	
	/**
	 * Used to access the file stored by the registerLayer() method
	 * @return the file located by the menu
	 */
	public File getFile() {
		return file;
	}

}
