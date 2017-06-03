package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OpacityBox {


	public static double display(ArrowButton button){
		//TODO this isn't working correctly
		Slider opacityLevel = new Slider(0,1,(Double)button.getData());
		Label opacityCaption = new Label("Opacity Level:");
		Label opacityValue = new Label(Double.toString(opacityLevel.getValue()));

		Stage window  = new Stage();
		window.setTitle("Opacity Picker");
		window.setMinWidth(450);
        window.setMinHeight(100);
        window.initModality(Modality.APPLICATION_MODAL);

        Group root = new Group();
        Scene scene = new Scene(root);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(70);

        scene.setRoot(grid);

        GridPane.setConstraints(opacityCaption, 0, 1);
        grid.getChildren().add(opacityCaption);


        opacityLevel.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                    System.out.println(new_val.doubleValue());
                    opacityValue.setText(String.format("%.2f", new_val));
            }
        });

        GridPane.setConstraints(opacityLevel, 1, 1);
        grid.getChildren().add(opacityLevel);

        GridPane.setConstraints(opacityValue, 2, 1);
        grid.getChildren().add(opacityValue);

        Button b = new Button("Submit");
        b.setOnAction(e -> {
        	window.close();
        });

        GridPane.setConstraints(b, 2, 2);
        grid.getChildren().add(b);

        window.setOnCloseRequest(e -> {

        });

        window.setScene(scene);
        window.show();

        return opacityLevel.getValue();
	}

}
