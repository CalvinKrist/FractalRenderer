package application;

import fractal.Layer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LayerBox {

    public static MetaLayer display(TreeItem t) {
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Layer");
        window.setMinWidth(250);
        window.setMinHeight(200);

        VBox layout = new VBox(10);

        Label name = new Label();
        name.setText("Enter Name of Layer:");
        TextField nameIn = new TextField();
        nameIn.setPromptText("Layer Name");
        nameIn.minWidthProperty().bind(window.minWidthProperty().divide(2));
        nameIn.maxWidthProperty().bind(window.minWidthProperty().multiply(.75));
        Label type = new Label();
        type.setText("Select a Layer Type:");
        ChoiceBox typeIn = new ChoiceBox();
        typeIn.getItems().addAll(Layer.getLayerTypes());
        typeIn.minWidthProperty().bind(window.minWidthProperty().divide(2));
        typeIn.maxWidthProperty().bind(window.minWidthProperty().multiply(.75));
        Button closeButton = new Button("Submit");
        closeButton.setOnAction(e -> window.close());


        layout.getChildren().addAll(name,nameIn,type,typeIn,closeButton);
        layout.setAlignment(Pos.CENTER);

        if(((MetaLayer)t.getValue()).getName()!=null)
        	nameIn.setText(((MetaLayer)t.getValue()).getName());
        if(((MetaLayer)t.getValue()).getType()!=null){
        	typeIn.getSelectionModel().select(((MetaLayer)t.getValue()).getType());
        }
        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
		return new MetaLayer(nameIn.getText(),typeIn.getSelectionModel().getSelectedItem().toString());
    }



}
