package application;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import com.sun.glass.events.KeyEvent;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		        primaryStage.setTitle("Hello World");
		        FractalEditor scene = new FractalEditor(Toolkit.getDefaultToolkit().getScreenSize().width-100,Toolkit.getDefaultToolkit().getScreenSize().height-100);
		        primaryStage.setScene(scene);
		        primaryStage.centerOnScreen();
		        primaryStage.setMaximized(true);
		        scene.initialize();
		        primaryStage.show();
		        Robot robo = new Robot();
		        Point temp = MouseInfo.getPointerInfo().getLocation();
		        robo.mouseMove((int)scene.getX()+200,(int)scene.getY()+(int)scene.getHeight()-100);
		        robo.delay(1000);
		        robo.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		        robo.delay(100);
		        robo.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		        robo.delay(100);
		        robo.mouseMove((int)temp.getX(),(int)temp.getY());
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
