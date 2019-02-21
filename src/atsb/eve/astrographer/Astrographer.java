package atsb.eve.astrographer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Astrographer extends Application {

	private static Logger logger = Logger.getLogger(Astrographer.class.toString());

	private UniverseCanvas canvas;

	@Override
	public void start(Stage primary) throws Exception {

		SystemLoader sl = new SystemLoader();
		sl.load();
		logger.log(Level.INFO, "Loaded " + sl.getSystems().size() + " systems");
		logger.log(Level.INFO, "Loaded " + sl.getConnections().size() + " connections");

		canvas = new UniverseCanvas();
		canvas.addSystems(sl.getSystems());
		canvas.addConnections(sl.getConnections());

		StackPane root = new StackPane();
		Scene scene = new Scene(root);

		root.getChildren().add(canvas);
		canvas.widthProperty().bind(root.widthProperty());
		canvas.heightProperty().bind(root.heightProperty());

		primary.setTitle("Eve Astrographer");
		primary.setScene(scene);
		primary.setWidth(1024);
		primary.setHeight(768);
		primary.setMinWidth(400);
		primary.setMinHeight(300);
		primary.show();

		canvas.redraw();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
