package application;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		        primaryStage.setTitle("Hello World");
		        primaryStage.setScene(new FractalEditor(850,600));
		        primaryStage.centerOnScreen();
		        primaryStage.show();
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
