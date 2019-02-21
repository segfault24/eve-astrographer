package atsb.eve.astrographer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.SolarSystem;
import model.SystemConnection;

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
	private double zoom = 0;
	private double drawScale = 350;
	private int dotSize = 2;
	private double dragStartX = 0;
	private double dragStartY = 0;
	private double centerX = 0;
	private double centerY = 0;

	private String labelS = "";
	private double labelX = 0;
	private double labelY = 0;

	public UniverseCanvas() {
		systems = new HashMap<String, SolarSystem>();
		connections = new ArrayList<SystemConnection>();
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
			centerX -= (e.getX() - dragStartX) / drawScale;
			centerX = Math.max(centerX, -1);
			centerX = Math.min(centerX, 1);
			centerY -= (e.getY() - dragStartY) / drawScale;
			centerY = Math.max(centerY, -1);
			centerY = Math.min(centerY, 1);
			dragStartX = e.getX();
			dragStartY = e.getY();
			redraw();
		});
		setOnMouseMoved(e -> {
			SolarSystem s = dumbThing(e.getX(), e.getY());
			if (s != null) {
				labelS = s.getName();
				labelX = toDrawX(s.getPosition().getX());
				labelY = toDrawY(s.getPosition().getZ());
				redraw();
			} else {
				labelS = "";
				redraw();
			}
		});
	}

	// TODO: dear lord this is so brute forced
	private SolarSystem dumbThing(double x, double y) {
		SolarSystem ret = null;
		double d = Double.MAX_VALUE;
		for (SolarSystem s : systems.values()) {
			double asdf = Math.pow(x - toDrawX(s.getPosition().getX()), 2) + Math.pow(y - toDrawY(s.getPosition().getZ()), 2);
			if (asdf < d) {
				d = asdf;
				ret = s;
			}
		}
		if (d > 100) {
			ret = null;
		}
		return ret;
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		Double w = getWidth();
		Double h = getHeight();

		// fill background
		gc.setFill(BACKGROUND_COLOR);
		gc.fillRect(0, 0, w, h);

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
			gc.strokeLine(toDrawX(c.getA().getPosition().getX()), toDrawY(c.getA().getPosition().getZ()), toDrawX(c.getB().getPosition().getX()), toDrawY(c.getB().getPosition().getZ()));
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
			System.out.println(labelS);
		}
	}

	private Double toDrawX(double mapX) {
		return (mapX - centerX) * drawScale + getWidth() / 2;
	}

	private Double toDrawY(double mapZ) {
		return (-mapZ - centerY) * drawScale + getHeight() / 2;
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

	public void addSystems(Collection<SolarSystem> systems) {
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
		Double scale = Math.max(spanx, spanz);
		Double centerx = minx + spanx / 2;
		Double centerz = minz + spanz / 2;

		// normalize the positions
		for (SolarSystem s : systems) {
			Double x = s.getPosition().getX();
			Double y = s.getPosition().getY();
			Double z = s.getPosition().getZ();
			Double nx = (x - centerx) / (scale / 2);
			Double ny = y;
			Double nz = (z - centerz) / (scale / 2);
			s.setPosition(new Point3D(nx, ny, nz));
		}
	}

	public void addConnections(Collection<SystemConnection> connections) {
		this.connections.addAll(connections);
	}
}
