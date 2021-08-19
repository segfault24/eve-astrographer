package atsb.eve.astrographer;

import atsb.eve.astrographer.CanvasController.Redrawable;
import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SystemConnection;
import atsb.eve.astrographer.model.SolarSystem.CapType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class UniverseLayer extends Canvas implements Redrawable {

	private CanvasController ctl;
	private MapData mapData;

	public UniverseLayer(CanvasController ctl, MapData mapData) {
		this.ctl = ctl;
		this.mapData = mapData;
	}

	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFont(MapStyle.LABEL_FONT);

		// fill background
		gc.setFill(MapStyle.BACKGROUND);
		gc.fillRect(0, 0, ctl.width, ctl.height);

		// draw connections
		gc.setLineWidth(1);
		for (SystemConnection con : mapData.connections) {
			switch (con.getGateType()) {
			case HIGHWAY:
				if (!ctl.showHighway)
					continue;
				gc.setStroke(MapStyle.CONNECTION_HIGHWAY);
				break;
			case SUPER_HIGHWAY:
				if (!ctl.showHighway && !ctl.showSuperHighway)
					continue;
				gc.setStroke(MapStyle.CONNECTION_SUPER_HIGHWAY);
				break;
			case JUMPBRIDGE:
				if (!ctl.showJumpbridges)
					continue;
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
			int dot = ctl.dotSize;
			if (s.hasBeacon()) {
				gc.setFill(MapStyle.SYSTEM_BEACON);
				gc.fillOval(x - dot / 2, y - dot / 2, dot, dot);
			} else {
				gc.setFill(MapStyle.SYSTEM);
				gc.fillOval(x - dot / 2, y - dot / 2, dot, dot);
			}

			// draw the selection ring
			if (s.isSelectedPrimary()) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			} else if (s.getSelectedCount() == 1) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED_1);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			} else if (s.getSelectedCount() >= 2) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED_2);
				gc.strokeOval(x - dot - 2, y - dot - 2, 2 * dot + 4, 2 * dot + 4);
			}

			// draw the text
			if (s.isSelectedPrimary()) {
				gc.setFill(MapStyle.SYSTEM_LABEL_SELECTED);
				gc.fillText(s.getName(), x + 10, y + 2);
			} else if (ctl.zoom >= 70) {
				gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
				gc.fillText(s.getName(), x + 10, y + 2);
			}
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

}
