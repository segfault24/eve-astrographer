package dev.pagefault.eve.astrographer;

import javafx.scene.layout.Pane;

public class CanvasPane extends Pane {

	public CanvasPane() {
		super();
	}

	public void add(CanvasLayer c) {
		getChildren().add(c);
		c.widthProperty().bind(this.widthProperty());
		c.heightProperty().bind(this.heightProperty());
	}

}
