package atsb.eve.astrographer;

import java.util.logging.Logger;

import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SystemConnection;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

public class UniverseCanvas extends Canvas {

	private static Logger logger = Logger.getLogger(UniverseCanvas.class.toString());

	private static final int ZOOM_MIN = 0;
	private static final int ZOOM_MAX = 100;

	private MapData mapData;

	private double zoom = 0;
	private double drawScale = 350;
	private int dotSize = 2;

	private MouseEvent pressStart;
	private MouseEvent dragStart;
	private double panX = 0;
	private double panY = 0;

	private String labelS = "";
	private double labelX = 0;
	private double labelY = 0;

	public UniverseCanvas(MapData mapData) {
		this.mapData = mapData;

		widthProperty().addListener(evt -> redraw());
		heightProperty().addListener(evt -> redraw());

		setOnScroll(e -> {
			zoom += e.getDeltaY() / 8;
			zoom = Math.max(zoom, ZOOM_MIN);
			zoom = Math.min(zoom, ZOOM_MAX);
			drawScale = 350 * Math.pow(Math.E, 0.0353 * zoom);
			dotSize = (int) zoom / 20 + 2;
			redraw();
		});
		setOnMousePressed(e -> {
			pressStart = e;
			dragStart = e;
		});
		setOnMouseReleased(e -> {
			if (dist(pressStart, e) < 15) {
				SolarSystem s = searchNearest(e.getX(), e.getY(), 15);
				if (s != null) {
					s.toggleSelectedPrimary();
				}
				redraw();
			}
		});
		setOnMouseDragged(e -> {
			panX -= (e.getX() - dragStart.getX()) / drawScale;
			panX = Math.max(panX, -1);
			panX = Math.min(panX, 1);
			panY -= (e.getY() - dragStart.getY()) / drawScale;
			panY = Math.max(panY, -1);
			panY = Math.min(panY, 1);
			dragStart = e;
			SolarSystem s = searchNearest(e.getX(), e.getY(), 10);
			if (s != null) {
				labelS = s.getName();
				labelX = toDrawX(s.getPosition().getX()) + 10;
				labelY = toDrawY(s.getPosition().getZ()) + 2;
			} else {
				labelS = "";
			}
			redraw();
		});
		setOnMouseMoved(e -> {
			SolarSystem s = searchNearest(e.getX(), e.getY(), 10);
			if (s != null) {
				labelS = s.getName();
				labelX = toDrawX(s.getPosition().getX()) + 10;
				labelY = toDrawY(s.getPosition().getZ()) + 2;
			} else {
				labelS = "";
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
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFont(MapStyle.LABEL_FONT);

		// fill background
		gc.setFill(MapStyle.BACKGROUND);
		gc.fillRect(0, 0, getWidth(), getHeight());

		// draw connections
		gc.setLineWidth(1);
		for (SystemConnection c : mapData.getConnections()) {
			switch (c.getGateType()) {
			case JUMPBRIDGE:
				gc.setStroke(MapStyle.CONNECTION_JUMPBRIDGE);
				break;
			case JB_HOSTILE:
				gc.setStroke(MapStyle.CONNECTION_JB_HOSTILE);
				break;
			case REGIONAL:
				gc.setStroke(MapStyle.CONNECTION_REGIONAL);
				break;
			case CONSTELLATION:
				gc.setStroke(MapStyle.CONNECTION_CONSTELLATION);
				break;
			case NORMAL:
			default:
				gc.setStroke(MapStyle.CONNECTION_NORMAL);
				break;
			}
			gc.strokeLine(toDrawX(c.getA().getPosition().getX()), toDrawY(c.getA().getPosition().getZ()),
					toDrawX(c.getB().getPosition().getX()), toDrawY(c.getB().getPosition().getZ()));
		}

		// draw systems
		for (SolarSystem s : mapData.getSystems().values()) {
			Double x = toDrawX(s.getPosition().getX());
			Double y = toDrawY(s.getPosition().getZ());
			if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
				continue;
			}
			if (s.isSelectedPrimary()) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - 8, y - 8, 16, 16);
				gc.setFill(MapStyle.SYSTEM_SELECTED);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
				gc.setFill(MapStyle.SYSTEM_LABEL_SELECTED);
				gc.fillText(s.getName(), toDrawX(s.getPosition().getX()) + 10, toDrawY(s.getPosition().getZ()) + 2);
			} else if (s.isSelectedSecondary()) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - 8, y - 8, 16, 16);
				gc.setFill(MapStyle.SYSTEM_SELECTED_SECONDARY);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			} else {
				gc.setFill(MapStyle.SYSTEM);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			}
		}

		// text dumb thing
		if (!labelS.isEmpty()) {
			gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
			gc.fillText(labelS, labelX, labelY);
		}
	}

	private double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	private double dist(MouseEvent a, MouseEvent b) {
		return dist(a.getX(), a.getY(), b.getX(), b.getY());
	}

	private double toDrawX(double mapX) {
		return ((mapX - mapData.getUniverseCenter().getX()) / mapData.getUniverseScale() - panX) * drawScale + getWidth() / 2;
	}

	private double toMapX(double drawX) {
		return ((drawX - getWidth() / 2) / drawScale + panX) * mapData.getUniverseScale() + mapData.getUniverseCenter().getX();
	}

	private double toDrawY(double mapZ) {
		return (-(mapZ - mapData.getUniverseCenter().getZ()) / mapData.getUniverseScale() - panY) * drawScale + getHeight() / 2;
	}

	private double toMapZ(double drawY) {
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
