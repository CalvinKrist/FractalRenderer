package menus;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A small class to create a simple, expandable menu for when large amounts of text need to be displayed at once
 * @author Calvin
 *
 */
public class ExpandableMenu {
	
	/**
	 * The method that actually displays the expandable menu as an information notification
	 * @param header the header text for the menu
	 * @param labelText a label for the TextArea
	 * @param body the body text for the menu. This is what will go in the scrolalble TextArea
	 */
	public static void displayInformation(String header, String labelText, String body) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(header);
		
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.write(body);
		String exceptionText = sw.toString();

		Label label = new Label(labelText);

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
		
	}
	
	/**
	 * The method that actually displays the expandable menu as an alert
	 * @param header the header text for the menu
	 * @param labelText a label for the TextArea
	 * @param body the body text for the menu. This is what will go in the scrolalble TextArea
	 */
	public static void displayAlert(String header, String labelText, String body) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		
		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.write(body);
		String exceptionText = sw.toString();

		Label label = new Label(labelText);

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

}
