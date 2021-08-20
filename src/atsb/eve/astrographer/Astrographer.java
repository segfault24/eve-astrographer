package atsb.eve.astrographer;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
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
		ctl.showSuperHighway = true;

		stage.setTitle("Eve Astrographer");
		stage.setMinWidth(800);
		stage.setMinHeight(600);
		stage.setWidth(1024);
		stage.setHeight(768);

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();
			}
		});
		MenuItem reloadHighwayItem = new MenuItem("Reload Highway");
		reloadHighwayItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					DataLoader.loadHighway(data);
					ctl.redraw();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		MenuItem reloadJumpbridgesItem = new MenuItem("Reload Jumpbridges");
		reloadJumpbridgesItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					DataLoader.loadJumpbridges(data);
					ctl.redraw();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		MenuItem reloadBeaconsItem = new MenuItem("Reload Beacons");
		reloadBeaconsItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					DataLoader.loadBeacons(data);
					ctl.redraw();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		fileMenu.getItems().addAll(reloadHighwayItem, reloadJumpbridgesItem, reloadBeaconsItem, exitItem);
		menuBar.getMenus().addAll(fileMenu);

		BorderPane borderPane = new BorderPane();
		BackgroundLayer bgLayer = new BackgroundLayer(ctl);
		UniverseLayer universeLayer = new UniverseLayer(ctl, data);
		HighwayLayer highwayLayer = new HighwayLayer(ctl, data);
		UILayer uiLayer = new UILayer(ctl, data);
		CanvasPane canvasPane = new CanvasPane();
		canvasPane.add(bgLayer);
		canvasPane.add(universeLayer);
		canvasPane.add(highwayLayer);
		canvasPane.add(uiLayer);
		uiLayer.toFront();
		ctl.addLayer(bgLayer);
		ctl.addLayer(universeLayer);
		ctl.addLayer(highwayLayer);
		ctl.addLayer(uiLayer);

		TabPane tabPane = new TabPane();
		tabPane.setPrefWidth(200);
		Tab controlTab = new Tab("Controls");
		controlTab.setClosable(false);
		VBox controlTabVbox = new VBox();
		controlTabVbox.setPadding(new Insets(10));
		CheckBox showJumpbridges = new CheckBox("Jumpbridges");
		showJumpbridges.setSelected(ctl.showJumpbridges);
		showJumpbridges.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showJumpbridges = newVal;
				ctl.redraw();
			}
		});
		CheckBox showFortizars = new CheckBox("+Fortizars");
		showFortizars.setSelected(ctl.showFortizars);
		showFortizars.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showFortizars = newVal;
				ctl.redraw();
			}
		});
		CheckBox showHighway = new CheckBox("+Capital Jumps");
		showHighway.setSelected(ctl.showHighway);
		showHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showHighway = newVal;
				ctl.redraw();
			}
		});
		CheckBox showSuperHighway = new CheckBox("Super Highway");
		showSuperHighway.setSelected(ctl.showSuperHighway);
		showSuperHighway.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				ctl.showSuperHighway = newVal;
				showFortizars.setDisable(!newVal);
				showHighway.setDisable(!newVal);
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
			default:
				break;
			}
		});
		controlTabVbox.getChildren().addAll(showJumpbridges, new Separator(), showSuperHighway, showHighway, showFortizars, new Separator(), rangeSelect);
		controlTab.setContent(controlTabVbox);
		tabPane.getTabs().add(controlTab);

		borderPane.setTop(menuBar);
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
