package atsb.eve.astrographer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class UILayer extends CanvasLayer {

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
			if (ctl.zoom == 100 && e.getDeltaY() < 0) {
				// dont pan on zoom if at max zoom
			} else if (ctl.zoom == 0 && e.getDeltaY() > 0) {
				// dont pan on zoom if at min zoom
			} else {
				// zoom in towards the mouse cursor
				if (e.getDeltaY() < 0) {
					ctl.panX += (e.getX() - ctl.width / 2) / ctl.drawScale / 6;
					ctl.panY += (e.getY() - ctl.height / 2) / ctl.drawScale / 6;
				} else {
					ctl.panX -= (e.getX() - ctl.width / 2) / ctl.drawScale / 6;
					ctl.panY -= (e.getY() - ctl.height / 2) / ctl.drawScale / 6;
				}
				ctl.panX = Math.max(ctl.panX, -1);
				ctl.panX = Math.min(ctl.panX, 1);
				ctl.panY = Math.max(ctl.panY, -1);
				ctl.panY = Math.min(ctl.panY, 1);
			}

			ctl.zoom -= e.getDeltaY() / 8;
			ctl.zoom = Math.max(ctl.zoom, CanvasController.ZOOM_MIN);
			ctl.zoom = Math.min(ctl.zoom, CanvasController.ZOOM_MAX);
			ctl.drawScale = 350 * Math.pow(Math.E, 0.0353 * ctl.zoom);
			ctl.dotSize = ctl.zoom / 30 + 1;

			relabel(e.getX(), e.getY());
			ctl.redraw();
		});
		setOnMousePressed(e -> {
			pressStart = e;
			dragStart = e;
		});
		setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (CanvasController.dist(pressStart, e) < 15) {
					SolarSystem s = ctl.searchNearest(e.getX(), e.getY(), 15);
					if (s != null) {
						if (ctl.getSelectionMap().isSelected(s)) {
							ctl.getSelectionMap().removeAll(s);
						} else {
							ctl.getSelectionMap().setSelected(s);
							ctl.getSelectionMap().addAll(s, mapData.findWithinRange(s, range * MapData.METERS_PER_LY));
						}
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
			relabel(e.getX(), e.getY());
			redraw();
		});
	}

	private void relabel(double x, double y) {
		SolarSystem s = ctl.searchNearest(x, y, 10);
		if (s != null) {
			labelS = s.getName();
			labelX = ctl.toDrawX(s.getPosition().getX());
			labelY = ctl.toDrawY(s.getPosition().getZ());
		} else {
			labelS = "";
		}
	}

	@Override
	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, ctl.width, ctl.height);
		gc.setFont(MapStyle.LABEL_FONT);

		// text dumb thing
		if (!labelS.isEmpty()) {
			gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
			gc.fillText(labelS, labelX + 10, labelY + 2);
			gc.setStroke(MapStyle.SYSTEM_LABEL_HOVER);
			gc.strokeOval(labelX - ctl.dotSize - 2, labelY - ctl.dotSize - 2, 2 * ctl.dotSize + 4, 2 * ctl.dotSize + 4);
		}
	}

	@Override
	public int getLevel() {
		return Integer.MAX_VALUE;
	}

}
