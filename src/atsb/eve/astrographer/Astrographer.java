package atsb.eve.astrographer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Astrographer extends Application {

	@Override
	public void start(Stage stage) throws Exception {

		MapData data = new MapData();
		data.load();
		UniverseCanvas canvas = new UniverseCanvas(data);

		VBox v = new VBox();
		v.getChildren().add(canvas);
		canvas.widthProperty().bind(v.widthProperty());
		canvas.heightProperty().bind(v.heightProperty());

		TabPane tabs = new TabPane();
		tabs.setPrefWidth(300);
		tabs.setMinWidth(300);
		tabs.setMaxWidth(300);
		Tab controls = new Tab("Controls");
		controls.setClosable(false);
		VBox cv = new VBox();
		cv.setPadding(new Insets(10));
		CheckBox showJumpbridges = new CheckBox("Jumpbridges");
		showJumpbridges.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				canvas.showJumpbridges = newVal;
				canvas.redraw();
			}
		});
		CheckBox showFortizars = new CheckBox("Fortizars");
		showFortizars.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				canvas.showFortizars = newVal;
				canvas.redraw();
			}
		});
		CheckBox showKeepstars = new CheckBox("Keepstars");
		showKeepstars.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				canvas.showKeepstars = newVal;
				canvas.redraw();
			}
		});
		CheckBox showHighway = new CheckBox("Capital Highway");
		showHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				canvas.showHighway = newVal;
				canvas.redraw();
			}
		});
		CheckBox showSuperHighway = new CheckBox("Super Highway");
		showSuperHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				canvas.showSuperHighway = newVal;
				canvas.redraw();
			}
		});
		cv.getChildren().addAll(showJumpbridges, showFortizars, showKeepstars, showHighway, showSuperHighway);
		controls.setContent(cv);
		tabs.getTabs().addAll(controls);

		SplitPane split = new SplitPane();
		split.getItems().addAll(v, tabs);
		stage.setTitle("Eve Astrographer");
		stage.setScene(new Scene(split));stage.setResizable(false);
		stage.setWidth(1024);
		stage.setHeight(768);
		stage.setMinWidth(800);
		stage.setMinHeight(600);

		stage.show();
		canvas.redraw();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
