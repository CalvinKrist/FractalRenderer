package menus;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertMenu {
	
	public AlertMenu(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

}
