package atsb.eve.astrographer;

import atsb.eve.astrographer.CanvasController.Redrawable;
import atsb.eve.astrographer.model.SolarSystem;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class UILayer extends Canvas implements Redrawable {

	private CanvasController ctl;
	private MouseEvent pressStart;
	private MouseEvent dragStart;

	protected String labelS = "";
	protected double labelX = 0;
	protected double labelY = 0;
	protected int range = 7;

	public UILayer(CanvasController ctl, MapData mapData) {
		this.ctl = ctl;

		widthProperty().addListener(evt -> {
			ctl.width = getWidth();
			ctl.redraw();
		});
		heightProperty().addListener(evt -> {
			ctl.height = getHeight();
			ctl.redraw();
		});

		setOnScroll(e -> {
			ctl.zoom += e.getDeltaY() / 8;
			ctl.zoom = Math.max(ctl.zoom, CanvasController.ZOOM_MIN);
			ctl.zoom = Math.min(ctl.zoom, CanvasController.ZOOM_MAX);
			ctl.drawScale = 350 * Math.pow(Math.E, 0.0353 * ctl.zoom);
			ctl.dotSize = (int) ctl.zoom / 20 + 2;
			ctl.redraw();
		});
		setOnMousePressed(e -> {
			pressStart = e;
			dragStart = e;
		});
		setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (ctl.dist(pressStart, e) < 15) {
					SolarSystem s = ctl.searchNearest(e.getX(), e.getY(), 15);
					if (s != null) {
						s.toggleSelectedPrimary();
						//////
						for (SolarSystem j : mapData.findWithinRange(s, range * MapData.METERS_PER_LY)) {
							if (s.isSelectedPrimary()) {
								j.incrementSelected();
							} else {
								j.decrementSelected();
							}
						}
						//////
					}
				}
			}
			ctl.redraw();
		});
		setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				ctl.panX -= (e.getX() - dragStart.getX()) / ctl.drawScale;
				ctl.panX = Math.max(ctl.panX, -1);
				ctl.panX = Math.min(ctl.panX, 1);
				ctl.panY -= (e.getY() - dragStart.getY()) / ctl.drawScale;
				ctl.panY = Math.max(ctl.panY, -1);
				ctl.panY = Math.min(ctl.panY, 1);
				SolarSystem s = ctl.searchNearest(e.getX(), e.getY(), 10);
				if (s != null) {
					labelS = s.getName();
					labelX = ctl.toDrawX(s.getPosition().getX()) + 10;
					labelY = ctl.toDrawY(s.getPosition().getZ()) + 2;
				} else {
					labelS = "";
				}
			}
			dragStart = e;
			ctl.redraw();
		});
		setOnMouseMoved(e -> {
			SolarSystem s = ctl.searchNearest(e.getX(), e.getY(), 10);
			if (s != null) {
				labelS = s.getName();
				labelX = ctl.toDrawX(s.getPosition().getX()) + 10;
				labelY = ctl.toDrawY(s.getPosition().getZ()) + 2;
			} else {
				labelS = "";
			}
			ctl.redraw();
		});
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, ctl.width, ctl.height);
		gc.setFont(MapStyle.LABEL_FONT);

		// text dumb thing
		if (!labelS.isEmpty()) {
			gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
			gc.fillText(labelS, labelX, labelY);
		}
	}
}
