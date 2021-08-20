package atsb.eve.astrographer;

import javafx.scene.canvas.Canvas;

public abstract class CanvasLayer extends Canvas {

	public abstract void redraw();

	public abstract int getLevel();

	@Override
	public final boolean isResizable() {
		return true;
	}

	@Override
	public final double prefWidth(double height) {
		return getWidth();
	}

	@Override
	public final double prefHeight(double width) {
		return getHeight();
	}

}
