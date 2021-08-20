package atsb.eve.astrographer;

import atsb.eve.astrographer.model.SystemConnection;
import javafx.scene.canvas.GraphicsContext;

public class HighwayLayer extends CanvasLayer {

	private CanvasController ctl;
	private MapData mapData;

	public HighwayLayer(CanvasController ctl, MapData mapData) {
		this.ctl = ctl;
		this.mapData = mapData;
	}

	@Override
	public void redraw() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, ctl.width, ctl.height);
		gc.setFont(MapStyle.LABEL_FONT);

		// draw jumpbridges
		gc.setLineWidth(1);
		for (SystemConnection con : mapData.jumpbridges) {
			switch (con.getGateType()) {
			case JUMPBRIDGE:
				if (!ctl.showJumpbridges)
					continue;
				gc.setStroke(MapStyle.CONNECTION_JUMPBRIDGE);
				break;
			case JB_HOSTILE:
				gc.setStroke(MapStyle.CONNECTION_JB_HOSTILE);
				break;
			default:
				continue;
			}
			gc.strokeLine(ctl.toDrawX(con.getA().getPosition().getX()), ctl.toDrawY(con.getA().getPosition().getZ()),
					ctl.toDrawX(con.getB().getPosition().getX()), ctl.toDrawY(con.getB().getPosition().getZ()));
		}

		// draw highway
		gc.setLineWidth(1);
		for (SystemConnection con : mapData.highway) {
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
			default:
				continue;
			}
			gc.strokeLine(ctl.toDrawX(con.getA().getPosition().getX()), ctl.toDrawY(con.getA().getPosition().getZ()),
					ctl.toDrawX(con.getB().getPosition().getX()), ctl.toDrawY(con.getB().getPosition().getZ()));
		}
	}

	@Override
	public int getLevel() {
		return 100;
	}

}
