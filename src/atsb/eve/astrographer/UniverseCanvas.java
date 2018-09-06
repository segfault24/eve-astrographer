package atsb.eve.astrographer;

import java.util.Collection;
import java.util.HashMap;

import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UniverseCanvas extends Canvas {

	private static int ZOOM_MIN = 0;
	private static int ZOOM_MAX = 100;
	private static Color BG_COLOR = new Color(0, 0, 0, 1);
	private static Color SYSTEM_COLOR = Color.WHITE;

	private HashMap<String, SolarSystem> systems;
	private int zoom = 0;
	private int scale = 350;
	private int dot = 2;
	private int dragStartX = 0;
	private int dragStartY = 0;
	private int panX = 0;
	private int panY = 0;

	public UniverseCanvas() {
		systems = new HashMap<String, SolarSystem>();
		widthProperty().addListener(evt -> redraw());
		heightProperty().addListener(evt -> redraw());

		setOnScroll(e -> {
			zoom += (int) (e.getDeltaY() / 8);
			zoom = Math.max(zoom, ZOOM_MIN);
			zoom = Math.min(zoom, ZOOM_MAX);
			scale = (int) (350 * Math.pow(Math.E, 0.0353 * zoom));
			dot = zoom / 20 + 2;
			redraw();
		});
		setOnMousePressed(e -> {
			dragStartX = (int) e.getX();
			dragStartY = (int) e.getY();
		});
		setOnMouseDragged(e -> {
			panX += (int) e.getX() - dragStartX;
			panY += (int) e.getY() - dragStartY;
			dragStartX = (int) e.getX();
			dragStartY = (int) e.getY();
			redraw();
		});
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		Double w = getWidth();
		Double h = getHeight();

		// fill background
		gc.setFill(BG_COLOR);
		gc.fillRect(0, 0, w, h);

		// draw systems
		gc.setFill(SYSTEM_COLOR);
		double tx = w / 2 + panX;
		double ty = h / 2 + panY;
		for (SolarSystem s : systems.values()) {
			Double x = s.getPosition().getX();
			Double z = s.getPosition().getZ();
			gc.fillOval(x * scale + tx, -z * scale + ty, dot, dot);
		}
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

}
