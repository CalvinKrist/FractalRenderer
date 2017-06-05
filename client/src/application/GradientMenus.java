package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fractal.Layer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GradientMenus {


	public static double displayOpacityMenu(ArrowButton button, Window gradient) {
		//TODO this isn't working correctly
		Slider opacityLevel = new Slider(0,1,(Double)button.getData());
		Label opacityCaption = new Label("Opacity Level:");
		Label opacityValue = new Label(Double.toString(opacityLevel.getValue()));
		opacityValue.setText(String.format("%.2f", opacityLevel.getValue()));
		double prevVal = (double) button.getData();


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
                    button.setData(new_val.doubleValue());
                    DecimalFormat df = new DecimalFormat("0.00");
                    opacityValue.setText((df.format((double)button.getData())));
                    gradient.repaint();
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
        Button d = new Button("Delete");
        d.setOnAction(e -> {
        	gradient.getPalette().getOpacityList().remove(button);
        	gradient.repaint();
        	window.close();
        	
        });
        GridPane.setConstraints(d,3,2);
        grid.getChildren().add(d);

        GridPane.setConstraints(b, 2, 2);
        grid.getChildren().add(b);

        window.setOnCloseRequest(e -> {
        	button.setData(prevVal);
        	gradient.repaint();
        });

        window.setScene(scene);
        window.show();

        return opacityLevel.getValue();
	}

	public static void displayColorMenus(ArrowButton<Color> button, Window window) {
		Color prev = button.getData();

		JFrame f = new JFrame();
		JColorChooser p = new JColorChooser();
		p.setPreviewPanel(new JPanel());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.CENTER);

		JButton submit = new JButton("Submit");
		submit.addActionListener(e-> {
			f.dispose();
		});
		panel.add(submit, BorderLayout.SOUTH);

		p.getSelectionModel().addChangeListener(e-> {
			button.setData(p.getColor());
			window.repaint();
		});

		f.setContentPane(panel);
		f.setResizable(false);
		f.setFocusable(true);
		f.pack();
		f.setLocationRelativeTo(null);
		f.requestFocus();
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				button.setData(prev);
				window.repaint();
			}
		});
	}

	public static void displayColorMenus(SquareButton button, Window window) {
		Color prev = button.getData();

		JFrame f = new JFrame();
		JColorChooser p = new JColorChooser();
		p.setPreviewPanel(new JPanel());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.CENTER);

		JButton submit = new JButton("Submit");
		submit.addActionListener(e-> {
			f.dispose();
		});
		panel.add(submit, BorderLayout.SOUTH);

		p.getSelectionModel().addChangeListener(e-> {
			button.setData(p.getColor());
			window.repaint();
			window.getPalette().setBackground(button.getData());
		});

		f.setContentPane(panel);
		f.setResizable(false);
		f.setFocusable(true);
		f.pack();
		f.setLocationRelativeTo(null);
		f.requestFocus();
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				button.setData(prev);
				window.repaint();
				window.getPalette().setBackground(button.getData());
			}
		});
	}

	public static MetaLayer displayLayerMenu(TreeItem<MetaLayer> t) {
        Stage window = new Stage();
        Slider opacityLevel = new Slider(0,1,(Double)t.getValue().getOpacity());
		Label opacityCaption = new Label("Opacity Level:");
		Label opacityValue = new Label(Double.toString(opacityLevel.getValue()));
		opacityValue.setText(String.format("%.2f", opacityLevel.getValue()));

		opacityLevel.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            		DecimalFormat df = new DecimalFormat("0.00");
                    opacityValue.setText((df.format(opacityLevel.getValue())));
            }
        });

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Layer");
        window.setMinWidth(250);
        window.setMinHeight(210);

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
        typeIn.getSelectionModel().selectFirst();
        HBox hbox = new HBox();
        Button closeButton = new Button("Submit");
        closeButton.setOnAction(e -> window.close());
        CheckBox delete = new CheckBox("Delete");
        Canvas spacer = new Canvas();
        spacer.setWidth(30);
        Canvas push = new Canvas();
        push.setWidth(50);

        hbox.getChildren().addAll(push,closeButton,spacer,delete);

        HBox opacityBox = new HBox();
        opacityBox.maxWidthProperty().bind(window.minWidthProperty().multiply(.75));
        opacityLevel.maxWidthProperty().bind(window.minWidthProperty().multiply(.75));
        Canvas opacitySpacer = new Canvas();
        opacitySpacer.setWidth(10);
        opacityBox.getChildren().addAll(opacityCaption,opacitySpacer,opacityValue);

        Canvas vspace = new Canvas();
        vspace.setHeight(10);

        layout.getChildren().addAll(name,nameIn,type,typeIn,opacityBox,opacityLevel,hbox,vspace);
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
        if(delete.isSelected())
        	return new MetaLayer(nameIn.getText(),typeIn.getSelectionModel().getSelectedItem().toString(),opacityLevel.getValue(),true);
		return new MetaLayer(nameIn.getText(),typeIn.getSelectionModel().getSelectedItem().toString(),opacityLevel.getValue());
    }
}
