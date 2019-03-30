package atsb.eve.astrographer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import atsb.eve.astrographer.model.KDTree;
import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SystemConnection;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UniverseCanvas extends Canvas {

	private static int ZOOM_MIN = 0;
	private static int ZOOM_MAX = 100;
	private static Color BACKGROUND_COLOR = new Color(0.1, 0.1, 0.1, 1);
	private static Color SYSTEM_COLOR = Color.WHITE;
	private static Color CONNECTION_COLOR = Color.GRAY;
	private static Color CONNECTION_REGIONAL_COLOR = Color.LIGHTGREEN;
	private static Color CONNECTION_CONSTELLATION_COLOR = Color.LIGHTBLUE;
	private static Color CONNECTION_JUMPBRIDGE_COLOR = Color.RED;

	private HashMap<String, SolarSystem> systems;
	private List<SystemConnection> connections;
	private KDTree searchTree;
	private Point3D universeCenter;
	private double universeScale = 1;

	private double zoom = 0;
	private double drawScale = 350;
	private int dotSize = 2;
	private double dragStartX = 0;
	private double dragStartY = 0;
	private double panX = 0;
	private double panY = 0;

	private String labelS = "";
	private double labelX = 0;
	private double labelY = 0;

	public UniverseCanvas() {
		systems = new HashMap<String, SolarSystem>();
		connections = new ArrayList<SystemConnection>();
		universeCenter = new Point3D(0, 0, 0);
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
			dragStartX = e.getX();
			dragStartY = e.getY();
		});
		setOnMouseDragged(e -> {
			panX -= (e.getX() - dragStartX) / drawScale;
			panX = Math.max(panX, -1);
			panX = Math.min(panX, 1);
			panY -= (e.getY() - dragStartY) / drawScale;
			panY = Math.max(panY, -1);
			panY = Math.min(panY, 1);
			dragStartX = e.getX();
			dragStartY = e.getY();
			labelS = "";
			redraw();
		});
		setOnMouseMoved(e -> {
			SolarSystem s = searchNearest(e.getX(), e.getY(), 10);
			if (s != null) {
				labelS = s.getName();
				labelX = toDrawX(s.getPosition().getX()) + 4;
				labelY = toDrawY(s.getPosition().getZ()) - 4;
				redraw();
			} else {
				labelS = "";
				redraw();
			}
		});
	}

	// TODO: dear lord this is so brute forced
	// find the nearest system within r pixels of the given x,y onscreen
	private SolarSystem searchNearest(double x, double y, double r) {
		
		/*SolarSystem s = searchTree.nearest(toMapX(x), toMapZ(y));
		if (Math.pow(x - toDrawX(s.getPosition().getX()), 2) + Math.pow(y - toDrawY(s.getPosition().getZ()), 2) < r*r) {
			return s;
		} else {
			return null;
		}*/
		
		SolarSystem ret = null;
		double d = Double.MAX_VALUE;
		for (SolarSystem s : systems.values()) {
			double asdf = Math.pow(x - toDrawX(s.getPosition().getX()), 2)
					+ Math.pow(y - toDrawY(s.getPosition().getZ()), 2);
			if (asdf < d) {
				d = asdf;
				ret = s;
			}
		}
		if (d > r*r) {
			ret = null;
		}
		return ret;
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();

		// fill background
		gc.setFill(BACKGROUND_COLOR);
		gc.fillRect(0, 0, getWidth(), getHeight());

		// draw connections
		gc.setLineWidth(1);
		for (SystemConnection c : connections) {
			switch (c.getGateType()) {
			case CONSTELLATION:
				gc.setStroke(CONNECTION_CONSTELLATION_COLOR);
				break;
			case JUMPBRIDGE:
				gc.setStroke(CONNECTION_JUMPBRIDGE_COLOR);
				break;
			case REGIONAL:
				gc.setStroke(CONNECTION_REGIONAL_COLOR);
				break;
			case NORMAL:
			default:
				gc.setStroke(CONNECTION_COLOR);
				break;
			}
			gc.strokeLine(toDrawX(c.getA().getPosition().getX()), toDrawY(c.getA().getPosition().getZ()),
					toDrawX(c.getB().getPosition().getX()), toDrawY(c.getB().getPosition().getZ()));
		}

		// draw systems
		gc.setFill(SYSTEM_COLOR);
		for (SolarSystem s : systems.values()) {
			Double x = s.getPosition().getX();
			Double z = s.getPosition().getZ();
			gc.fillOval(toDrawX(x) - dotSize / 2, toDrawY(z) - dotSize / 2, dotSize, dotSize);
		}

		// text dumb thing
		if (!labelS.isEmpty()) {
			gc.fillText(labelS, labelX, labelY);
		}
	}

	private double toDrawX(double mapX) {
		return ((mapX - universeCenter.getX()) / universeScale - panX) * drawScale + getWidth() / 2;
	}

	private double toMapX(double drawX) {
		return ((drawX - getWidth() / 2) / drawScale + panX) * universeScale + universeCenter.getX();
	}

	private double toDrawY(double mapZ) {
		return (-(mapZ - universeCenter.getZ()) / universeScale - panY) * drawScale + getHeight() / 2;
	}

	private double toMapZ(double drawY) {
		return universeCenter.getZ() - ((drawY - getHeight() / 2) / drawScale + panY) * universeScale;
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

	public void addSystems(List<SolarSystem> systems) {
		Double minx = Double.MAX_VALUE;
		Double maxx = Double.MIN_VALUE;
		Double minz = Double.MAX_VALUE;
		Double maxz = Double.MIN_VALUE;

		for (SolarSystem s : systems) {
			this.systems.put(s.getName(), s);
			minx = Math.min(s.getPosition().getX(), minx);
			maxx = Math.max(s.getPosition().getX(), maxx);
			minz = Math.min(s.getPosition().getZ(), minz);
			maxz = Math.max(s.getPosition().getZ(), maxz);
		}

		Double spanx = maxx - minx;
		Double spanz = maxz - minz;
		universeScale = Math.max(spanx, spanz) / 2;
		universeCenter = new Point3D(minx + spanx / 2, 0, minz + spanz / 2);

		searchTree = new KDTree(systems);
	}

	public void addConnections(Collection<SystemConnection> connections) {
		this.connections.addAll(connections);
	}
}
