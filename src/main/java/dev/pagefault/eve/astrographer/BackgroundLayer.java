package dev.pagefault.eve.astrographer;

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
	public int getLevel() {
		return Integer.MIN_VALUE;
	}

}
