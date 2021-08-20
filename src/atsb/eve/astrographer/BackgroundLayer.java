package atsb.eve.astrographer;

import javafx.scene.canvas.GraphicsContext;

public class BackgroundLayer extends CanvasLayer {

	private CanvasController ctl;

	public BackgroundLayer(CanvasController ctl) {
		this.ctl = ctl;
	}

	@Override
	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(MapStyle.BACKGROUND);
		gc.fillRect(0, 0, ctl.width, ctl.height);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public double prefWidth(double height) {
		return getWidth();
	}

	@Override
	public double prefHeight(double width) {
		return getHeight();
	}

	@Override
	public int getLevel() {
		return 0;
	}

}
