package atsb.eve.astrographer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Astrographer extends Application {

	private final String CAPITAL = "Capital 7ly";
	private final String SUPER = "Super 6ly";
	private final String BLOPS = "Blops 8ly";
	private final String JF = "JF 10ly";

	@Override
	public void start(Stage stage) throws Exception {

		MapData data = DataLoader.load();
		CanvasController ctl = new CanvasController(data);
		ctl.showJumpbridges = true;

		stage.setTitle("Eve Astrographer");
		stage.setMinWidth(800);
		stage.setMinHeight(600);
		stage.setWidth(1024);
		stage.setHeight(768);

		BorderPane borderPane = new BorderPane();
		UniverseLayer universeLayer = new UniverseLayer(ctl, data);
		UILayer uiLayer = new UILayer(ctl, data);
		Pane canvasPane = new Pane();
		canvasPane.getChildren().add(universeLayer);
		canvasPane.getChildren().add(uiLayer);
		uiLayer.toFront();
		ctl.addLayer(universeLayer);
		ctl.addLayer(uiLayer);
		universeLayer.widthProperty().bind(canvasPane.widthProperty());
		universeLayer.heightProperty().bind(canvasPane.heightProperty());
		uiLayer.widthProperty().bind(canvasPane.widthProperty());
		uiLayer.heightProperty().bind(canvasPane.heightProperty());

		TabPane tabPane = new TabPane();
		tabPane.setPrefWidth(200);
		Tab controlTab = new Tab("Controls");
		controlTab.setClosable(false);
		VBox controlTabVbox = new VBox();
		controlTabVbox.setPadding(new Insets(10));
		CheckBox showJumpbridges = new CheckBox("Jumpbridges");
		showJumpbridges.setSelected(true);
		showJumpbridges.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showJumpbridges = newVal;
				ctl.redraw();
			}
		});
		CheckBox showFortizars = new CheckBox("Fortizars");
		showFortizars.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showFortizars = newVal;
				ctl.redraw();
			}
		});
		CheckBox showKeepstars = new CheckBox("Keepstars");
		showKeepstars.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showKeepstars = newVal;
				ctl.redraw();
			}
		});
		CheckBox showHighway = new CheckBox("Capital Highway");
		showHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showHighway = newVal;
				ctl.redraw();
			}
		});
		CheckBox showSuperHighway = new CheckBox("Super Highway");
		showSuperHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showSuperHighway = newVal;
				ctl.redraw();
			}
		});
		ObservableList<String> options = FXCollections.observableArrayList(CAPITAL, SUPER, BLOPS, JF);
		ComboBox<String> rangeSelect = new ComboBox<String>(options);
		rangeSelect.getSelectionModel().select(CAPITAL);
		rangeSelect.getSelectionModel().selectedItemProperty().addListener((opts, oldValue, newValue) -> {
			switch (newValue) {
			case CAPITAL:
				uiLayer.range = 7;
				break;
			case SUPER:
				uiLayer.range = 6;
				break;
			case BLOPS:
				uiLayer.range = 8;
				break;
			case JF:
				uiLayer.range = 10;
				break;
			}
		});
		controlTabVbox.getChildren().addAll(showJumpbridges, showFortizars, showKeepstars, showHighway,
				showSuperHighway, rangeSelect);
		controlTab.setContent(controlTabVbox);
		tabPane.getTabs().add(controlTab);

		borderPane.setLeft(tabPane);
		borderPane.setCenter(canvasPane);

		stage.setScene(new Scene(borderPane));
		stage.show();
		ctl.redraw();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
