package atsb.eve.astrographer;

import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SolarSystem.CapType;
import atsb.eve.astrographer.model.SystemConnection;
import javafx.scene.canvas.GraphicsContext;

public class Painter {

	private MapData mapData;

	protected String labelS = "";
	protected double labelX = 0;
	protected double labelY = 0;

	protected int dotSize = 2;

	public Painter(MapData data) {
		this.mapData = data;
	}

	public void paint(UniverseCanvas c) {
		GraphicsContext gc = c.getGraphicsContext2D();
		gc.setFont(MapStyle.LABEL_FONT);

		// fill background
		gc.setFill(MapStyle.BACKGROUND);
		gc.fillRect(0, 0, c.getWidth(), c.getHeight());

		// draw connections
		gc.setLineWidth(1);
		for (SystemConnection con : mapData.getConnections()) {
			switch (con.getGateType()) {
			case HIGHWAY:
				if (!c.showHighway)
					continue;
				gc.setStroke(MapStyle.CONNECTION_HIGHWAY);
				break;
			case SUPER_HIGHWAY:
				if (!c.showHighway && !c.showSuperHighway)
					continue;
				gc.setStroke(MapStyle.CONNECTION_SUPER_HIGHWAY);
				break;
			case JUMPBRIDGE:
				if (!c.showJumpbridges)
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
			gc.strokeLine(c.toDrawX(con.getA().getPosition().getX()), c.toDrawY(con.getA().getPosition().getZ()),
					c.toDrawX(con.getB().getPosition().getX()), c.toDrawY(con.getB().getPosition().getZ()));
		}

		// draw systems
		for (SolarSystem s : mapData.getSystems().values()) {
			Double x = c.toDrawX(s.getPosition().getX());
			Double y = c.toDrawY(s.getPosition().getZ());
			if (x < 0 || x > c.getWidth() || y < 0 || y > c.getHeight()) {
				continue;
			}

			// draw the dot + selection ring
			if (s.isSelectedPrimary()) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - 8, y - 8, 16, 16);
				gc.setFill(MapStyle.SYSTEM_SELECTED);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			} else if (s.isSelectedSecondary()) {
				gc.setStroke(MapStyle.SYSTEM_SELECTED);
				gc.strokeOval(x - 8, y - 8, 16, 16);
				gc.setFill(MapStyle.SYSTEM_SELECTED_SECONDARY);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			} else if (s.getCapType() == CapType.CAP) {
				gc.setFill(MapStyle.SYSTEM_HIGHWAY);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			} else {
				gc.setFill(MapStyle.SYSTEM);
				gc.fillOval(x - dotSize / 2, y - dotSize / 2, dotSize, dotSize);
			}

			// draw the text
			if (s.isSelectedPrimary()) {
				gc.setFill(MapStyle.SYSTEM_LABEL_SELECTED);
				gc.fillText(s.getName(), x + 10, y + 2);
			} else if (c.zoom >= 70) {
				gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
				gc.fillText(s.getName(), x + 10, y + 2);
			}
		}

		// text dumb thing
		if (!labelS.isEmpty()) {
			gc.setFill(MapStyle.SYSTEM_LABEL_HOVER);
			gc.fillText(labelS, labelX, labelY);
		}
	}

}
