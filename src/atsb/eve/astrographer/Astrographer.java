package atsb.eve.astrographer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Astrographer extends Application {

	private static Logger logger = Logger.getLogger(Astrographer.class.toString());

	private UniverseCanvas canvas;
	private List<SolarSystem> systems;

	@Override
	public void start(Stage primary) throws Exception {

		SystemLoader sl = new SystemLoader();
		systems = sl.loadSystems();
		logger.log(Level.INFO, "Loaded " + systems.size() + " systems");

		canvas = new UniverseCanvas();
		canvas.addSystems(systems);

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
