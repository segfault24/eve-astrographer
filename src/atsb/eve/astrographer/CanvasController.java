package atsb.eve.astrographer;

import java.util.LinkedList;

import javafx.scene.input.MouseEvent;

public class CanvasController {

	public static final int ZOOM_MIN = 0;
	public static final int ZOOM_MAX = 100;

	private MapData mapData;

	protected double zoom = 0;
	protected double drawScale = 350;

	protected double panX = 0;
	protected double panY = 0;

	protected double width;
	protected double height;
	protected double dotSize = 1;

	protected boolean showJumpbridges = true;
	protected boolean showSuperHighway = true;
	protected boolean showHighway = false;
	protected boolean showFortizars = false;

	private LinkedList<CanvasLayer> layers = new LinkedList<CanvasLayer>();
	private SelectionMap selectionMap = new SelectionMap();
	
	public CanvasController(MapData mapData) {
		this.mapData = mapData;
	}

	public void addLayer(CanvasLayer l) {
		for (int i=0; i<layers.size(); i++) {
			if (l.getLevel() < layers.get(i).getLevel()) {
				layers.add(i, l);
				return;
			}
		}
		layers.add(l);
	}

	public void redraw() {
		for (CanvasLayer layer : layers) {
			layer.redraw();
		}
	}

	public SelectionMap getSelectionMap() {
		return selectionMap;
	}

	// find the nearest system within r pixels of the given x,y onscreen
	public SolarSystem searchNearest(double x, double y, double r) {
		SolarSystem s = mapData.findNearest(toMapX(x), toMapZ(y));
		double drawDist = Math.pow(x - toDrawX(s.getPosition().getX()), 2)
				+ Math.pow(y - toDrawY(s.getPosition().getZ()), 2);
		if (drawDist <= r * r) {
			return s;
		}
		return null;
	}

	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public static double dist(MouseEvent a, MouseEvent b) {
		return dist(a.getX(), a.getY(), b.getX(), b.getY());
	}

	protected double toDrawX(double mapX) {
		return ((mapX - mapData.universeCenter.getX()) / mapData.universeScale - panX) * drawScale
				+ width / 2;
	}

	protected double toMapX(double drawX) {
		return ((drawX - width / 2) / drawScale + panX) * mapData.universeScale
				+ mapData.universeCenter.getX();
	}

	protected double toDrawY(double mapZ) {
		return (-(mapZ - mapData.universeCenter.getZ()) / mapData.universeScale - panY) * drawScale
				+ height / 2;
	}

	protected double toMapZ(double drawY) {
		return mapData.universeCenter.getZ()
				- ((drawY - height / 2) / drawScale + panY) * mapData.universeScale;
	}

}
