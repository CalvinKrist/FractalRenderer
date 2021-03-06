package application;

import javafx.application.Application;
import javafx.stage.Stage;
import util.Log;


/**
 *
 * @author David, Calvin
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Log log = new Log();
			log.setLogLevel(Log.LEVEL_LOG);
			log.setPrintLevel(Log.LEVEL_LOG);
			log.setPrintStream(System.out);

			// Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		        primaryStage.setTitle("Fractal Editor");
		        FractalEditor scene = new FractalEditor(log);
		        primaryStage.setOnCloseRequest(e-> {
		        	System.exit(0);
		        });
		        primaryStage.setScene(scene);
		        primaryStage.centerOnScreen();
		        primaryStage.setMaximized(true);
		        scene.initialize();
		        primaryStage.show();
		        scene.gradient.repaint();
		        primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(2));
		        primaryStage.minHeightProperty().bind(scene.widthProperty().divide(2));

		        scene.updateFractalImage();

		        Thread t = null;
				t = new Thread(()-> {
					try {
						Thread.sleep(200);
						scene.gradient.repaint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				t.start();

		/*	BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
>>>>>>> eada197eba6f258eeae6a24cb51e418a83f3d7f4
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setMaximized(true);
			scene.initialize();
			primaryStage.show();
			scene.gradient.repaint();
			primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(2));
			primaryStage.minHeightProperty().bind(scene.widthProperty().divide(2));

			scene.updateFractalImage();

			Thread t = null;
			t = new Thread(() -> {
				try {
					t.sleep(200);
					scene.gradient.repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			t.start();

			/*
			 * BorderPane root = new BorderPane(); Scene scene = new
			 * Scene(root,400,400);
			 * scene.getStylesheets().add(getClass().getResource(
			 * "application.css").toExternalForm());
			 * primaryStage.setScene(scene); primaryStage.show();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
