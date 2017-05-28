package application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import com.sun.glass.events.KeyEvent;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		        primaryStage.setTitle("Fractal Editor");
		        FractalEditor scene = new FractalEditor((int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.75),Toolkit.getDefaultToolkit().getScreenSize().height/6);
		        primaryStage.setScene(scene);
		        primaryStage.centerOnScreen();
		        primaryStage.setMaximized(true);
		        scene.initialize();
		        primaryStage.show();
		        scene.gradient.repaint();
		        primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(2));
		        primaryStage.minHeightProperty().bind(scene.widthProperty().divide(2));
		        
		        scene.updateFractalImage();
		        
		/*	BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();*/
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
