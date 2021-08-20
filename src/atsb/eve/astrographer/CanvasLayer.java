package atsb.eve.astrographer;

import javafx.scene.canvas.Canvas;

public abstract class CanvasLayer extends Canvas {

	public abstract void redraw();

	public abstract int getLevel();

}
