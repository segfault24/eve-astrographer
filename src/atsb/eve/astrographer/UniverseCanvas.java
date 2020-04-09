package atsb.eve.astrographer;

import atsb.eve.astrographer.model.SolarSystem;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class UniverseCanvas extends Canvas {

	private static final int ZOOM_MIN = 0;
	private static final int ZOOM_MAX = 100;

	private MapData mapData;
	private Painter painter;

	protected double zoom = 0;
	private double drawScale = 350;

	private MouseEvent pressStart;
	private MouseEvent dragStart;
	private double panX = 0;
	private double panY = 0;

	protected boolean showJumpbridges = false;
	protected boolean showFortizars = false;
	protected boolean showKeepstars = false;
	protected boolean showHighway = false;
	protected boolean showSuperHighway = false;

	public UniverseCanvas(MapData mapData) {
		this.mapData = mapData;
		this.painter = new Painter(mapData);

		widthProperty().addListener(evt -> redraw());
		heightProperty().addListener(evt -> redraw());

		setOnScroll(e -> {
			zoom += e.getDeltaY() / 8;
			zoom = Math.max(zoom, ZOOM_MIN);
			zoom = Math.min(zoom, ZOOM_MAX);
			drawScale = 350 * Math.pow(Math.E, 0.0353 * zoom);
			painter.dotSize = (int) zoom / 20 + 2;
			redraw();
		});
		setOnMousePressed(e -> {
			pressStart = e;
			dragStart = e;
		});
		setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (dist(pressStart, e) < 15) {
					SolarSystem s = searchNearest(e.getX(), e.getY(), 15);
					if (s != null) {
						s.toggleSelectedPrimary();
					}
				}
			}
			redraw();
		});
		setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				panX -= (e.getX() - dragStart.getX()) / drawScale;
				panX = Math.max(panX, -1);
				panX = Math.min(panX, 1);
				panY -= (e.getY() - dragStart.getY()) / drawScale;
				panY = Math.max(panY, -1);
				panY = Math.min(panY, 1);
				SolarSystem s = searchNearest(e.getX(), e.getY(), 10);
				if (s != null) {
					painter.labelS = s.getName();
					painter.labelX = toDrawX(s.getPosition().getX()) + 10;
					painter.labelY = toDrawY(s.getPosition().getZ()) + 2;
				} else {
					painter.labelS = "";
				}
			}
			dragStart = e;
			redraw();
		});
		setOnMouseMoved(e -> {
			SolarSystem s = searchNearest(e.getX(), e.getY(), 10);
			if (s != null) {
				painter.labelS = s.getName();
				painter.labelX = toDrawX(s.getPosition().getX()) + 10;
				painter.labelY = toDrawY(s.getPosition().getZ()) + 2;
			} else {
				painter.labelS = "";
			}
			redraw();
		});
	}

	// find the nearest system within r pixels of the given x,y onscreen
	private SolarSystem searchNearest(double x, double y, double r) {
		SolarSystem s = mapData.findNearest(toMapX(x), toMapZ(y));
		double drawDist = Math.pow(x - toDrawX(s.getPosition().getX()), 2)
				+ Math.pow(y - toDrawY(s.getPosition().getZ()), 2);
		if (drawDist <= r * r) {
			return s;
		} else {
			return null;
		}
	}

	public void redraw() {
		painter.paint(this);
	}

	private double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	private double dist(MouseEvent a, MouseEvent b) {
		return dist(a.getX(), a.getY(), b.getX(), b.getY());
	}

	protected double toDrawX(double mapX) {
		return ((mapX - mapData.getUniverseCenter().getX()) / mapData.getUniverseScale() - panX) * drawScale + getWidth() / 2;
	}

	protected double toMapX(double drawX) {
		return ((drawX - getWidth() / 2) / drawScale + panX) * mapData.getUniverseScale() + mapData.getUniverseCenter().getX();
	}

	protected double toDrawY(double mapZ) {
		return (-(mapZ - mapData.getUniverseCenter().getZ()) / mapData.getUniverseScale() - panY) * drawScale + getHeight() / 2;
	}

	protected double toMapZ(double drawY) {
		return mapData.getUniverseCenter().getZ() - ((drawY - getHeight() / 2) / drawScale + panY) * mapData.getUniverseScale();
	}

	private double toMapDist(double drawDist) {
		return drawDist / drawScale * mapData.getUniverseScale();
	}

	private double toDrawDist(double mapDist) {
		return mapDist / mapData.getUniverseScale() * drawScale;
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

}
