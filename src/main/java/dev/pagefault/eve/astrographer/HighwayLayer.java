package dev.pagefault.eve.astrographer;

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
			case JB:
				if (!ctl.showJumpbridges)
					continue;
				gc.setStroke(MapStyle.CONNECTION_JB);
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
			case HWY_SUPER_KEEP:
				if (!ctl.showSuperHighway)
					continue;
				gc.setStroke(MapStyle.CONNECTION_HWY_SUPER_KEEP);
				break;
			case HWY_SUPER_FORT:
				if (!(ctl.showSuperHighway && ctl.showFortizars))
					continue;
				gc.setStroke(MapStyle.CONNECTION_HWY_SUPER_FORT);
				break;
			case HWY_REGULAR:
				if (!(ctl.showSuperHighway && ctl.showFortizars && ctl.showHighway))
					continue;
				gc.setStroke(MapStyle.CONNECTION_HWY_REGULAR);
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
