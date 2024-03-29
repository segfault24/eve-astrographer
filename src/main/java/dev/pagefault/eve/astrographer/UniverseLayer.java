package dev.pagefault.eve.astrographer;

import javafx.scene.canvas.GraphicsContext;

public class UniverseLayer extends CanvasLayer {

	private CanvasController ctl;
	private MapData mapData;

	public UniverseLayer(CanvasController ctl, MapData mapData) {
		this.ctl = ctl;
		this.mapData = mapData;
	}

	@Override
	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, ctl.width, ctl.height);
		gc.setFont(MapStyle.LABEL_FONT);

		// draw connections
		gc.setLineWidth(1);
		for (SystemConnection con : mapData.connections) {
			switch (con.getGateType()) {
			case REGIONAL:
				gc.setStroke(MapStyle.CONNECTION_REGIONAL);
				break;
			case CONSTELLATION:
				gc.setStroke(MapStyle.CONNECTION_CONSTELLATION);
				break;
			case NORMAL:
				gc.setStroke(MapStyle.CONNECTION_NORMAL);
				break;
			default:
				continue;
			}
			gc.strokeLine(ctl.toDrawX(con.getA().getPosition().getX()), ctl.toDrawY(con.getA().getPosition().getZ()),
					ctl.toDrawX(con.getB().getPosition().getX()), ctl.toDrawY(con.getB().getPosition().getZ()));
		}

		// draw systems
		for (SolarSystem s : mapData.systems.values()) {
			Double x = ctl.toDrawX(s.getPosition().getX());
			Double y = ctl.toDrawY(s.getPosition().getZ());
			if (x < 0 || x > ctl.width || y < 0 || y > ctl.height) {
				continue;
			}

			// draw the dot
			double dot = ctl.dotSize;
			if (s.hasBeacon()) {
				gc.setFill(MapStyle.SYSTEM_BEACON);
				gc.fillOval(x - dot / 2, y - dot / 2, dot, dot);
			} else {
				gc.setFill(MapStyle.SYSTEM);
				gc.fillOval(x - dot / 2, y - dot / 2, dot, dot);
			}

			// draw the selection ring
			if (ctl.getSelectionMap().isSelected(s)) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			} else if (ctl.getSelectionMap().getNumBacklinks(s) == 1) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED_1);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			} else if (ctl.getSelectionMap().getNumBacklinks(s) == 2) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED_2);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			} else if (ctl.getSelectionMap().getNumBacklinks(s) >= 3) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED_3);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			}

			// draw the text
			if (ctl.getSelectionMap().isSelected(s)) {
				gc.setFill(MapStyle.SYSTEM_LABEL_SELECTED);
				gc.fillText(s.getName(), x + 10, y + 2);
			} else if (ctl.zoom >= 70 && ctl.getSelectionMap().getNumBacklinks(s) >= 1) {
				gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
				gc.fillText(s.getName(), x + 10, y + 2);
			}
		}
	}

	@Override
	public int getLevel() {
		return 0;
	}

}
