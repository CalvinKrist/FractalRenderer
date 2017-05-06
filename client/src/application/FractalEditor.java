package application;

import java.awt.Dimension;

import gui.Window;
import javafx.embed.swing.SwingNode;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class FractalEditor extends Scene {
	
	BorderPane bp;

	public FractalEditor() {
		super(new BorderPane());
		bp = (BorderPane) this.getRoot();
		initialize();
	}
	private void initialize(){
		
		ImageView fractalView = new ImageView();
		SwingNode fractalEditor = new SwingNode();
		TreeView parameters = new TreeView();
		TreeView layers = new TreeView();
		VBox trees = new VBox();
		Dimension d = new Dimension(850,200);
		Window gradient = new Window(d);

		fractalEditor.setContent(gradient);

		
		trees.getChildren().addAll(parameters,layers);

		bp.setCenter(fractalView);
		bp.setBottom(fractalEditor);
		bp.setRight(trees);
	}

}
