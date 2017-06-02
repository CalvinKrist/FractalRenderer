package menus;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A simple class to simplify the process of displaying an alert menu when an error occurs.
 * @author Calvin
 *
 */
public class AlertMenu {
	
	/**
	 * This constructor creates and displays the alert menu with the specified text
	 * @param headerText the text of the header of the alert. This is the large text at the top.
	 * @param contentText This is the text that contains the actual message. It appears smaller and farther down
	 */
	public AlertMenu(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

}
