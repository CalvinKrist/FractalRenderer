package application;

import com.sun.javafx.scene.control.skin.CustomColorDialog;

import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ColorBox {

	public static void display(SquareButton button){
		Stage window = new Stage();
		window.setTitle("Color Picker");
		window.setMinWidth(250);
        window.setMinHeight(200);
        window.initModality(Modality.APPLICATION_MODAL);

         CustomColorDialog palette = new CustomColorDialog(window);
         
        palette.setCurrentColor(convertColortoFX(button.getData()));
         
        VBox layout = new VBox();
        layout.getChildren().addAll(palette);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
	}
	public static javafx.scene.paint.Color convertColortoFX( java.awt.Color c){
		java.awt.Color awtColor = c;
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
	}
}
