package menus;

import java.awt.Dimension;
import java.io.File;
import java.util.Optional;

import javax.imageio.ImageIO;

import fractal.RenderManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import server.Server;

/**
 * This class brings the user through a series of menus to configure a fractal for export as an image
 * @author Calvin
 *
 */
public class ExportImageTool {
	
	public ExportImageTool() {}
	
	/**
	 * This method is where the actual menus and user interaction happens. First, a window appears allowing the user to specify the
	 * resolution of the image. After that, the user chooses a directory and a file name and exports it as an image.
	 * When the image is done rendering and saving, a final menu will pop up informing the user that the image was succesfully
	 * exported
	 * @param fractal the fractal to be exported as an image
	 */
	public void exportImage(RenderManager fractal) {
		Dimension initRes = fractal.getScreenResolution();
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Export Fractal");
		dialog.setHeaderText("Step 1");
		dialog.setContentText("Choose a resolution:");

		ButtonType loginButtonType = new ButtonType("Continue", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

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

		dialog.getDialogPane().setContent(grid);
		dialog.setResultConverter(dialogButton->{
			if(dialogButton == loginButtonType)
				return new Pair<String, String>(widthField.getText(), heightField.getText());
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		int width = 0;
		int height = 0;
		try {
			width = Integer.valueOf(result.get().getKey());
			height = Integer.valueOf(result.get().getValue());
		} catch (NumberFormatException e) {
			AlertMenu aMenu = new AlertMenu("INVALID INPUT: Must be an integer.", "INVALID INPUT: Must be an integer.");
			exportImage(fractal);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Export Fractal");
		alert.setHeaderText("Step 2");
		alert.setContentText("Choose a directory:");

		ButtonType buttonTypeOne = new ButtonType("Choose");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

		Optional<ButtonType> result2 = alert.showAndWait();
		if (result2.get() == buttonTypeOne) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName("My Fractal.png");
			fileChooser.setTitle("Select File Destination");
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Images (*.png, *.jpg)",
					"*.png", "*.jpg");
			fileChooser.getExtensionFilters().add(filter);
			File file = fileChooser.showSaveDialog(null);
			String formatName = file.getName().substring(file.getName().indexOf(".") + 1);
			if(file != null) {
				try {
					file.createNewFile();
					fractal.setScreenResolution(new Dimension(width, height));
					ImageIO.write(fractal.getImage(), formatName, file);
					Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("Export Fractal");
					alert2.setHeaderText(null);
					alert2.setContentText("Image Succesfully Saved.");
					alert2.showAndWait();
					fractal.setScreenResolution(initRes);
				} catch(Exception e) {
					fractal.setScreenResolution(initRes);
				}
			}
		} else {
			return;
		}
	}

}
