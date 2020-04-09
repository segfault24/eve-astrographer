package atsb.eve.astrographer.model;

public class SystemConnection {

	public enum GateType {
		NORMAL, CONSTELLATION, REGIONAL, JUMPBRIDGE, JB_HOSTILE, HIGHWAY, SUPER_HIGHWAY;

		public static GateType valueOf(int i) {
			switch (i) {
			case 6:
				return GateType.SUPER_HIGHWAY;
			case 5:
				return GateType.HIGHWAY;
			case 4:
				return GateType.JB_HOSTILE;
			case 3:
				return GateType.JUMPBRIDGE;
			case 2:
				return GateType.REGIONAL;
			case 1:
				return GateType.CONSTELLATION;
			case 0:
			default:
				return GateType.NORMAL;
			}
		}
	}

	private SolarSystem sysA;
	private SolarSystem sysB;
	private GateType g;

	public SystemConnection(SolarSystem a, SolarSystem b, GateType g) {
		sysA = a;
		sysB = b;
		this.g = g;
	}

	public SolarSystem getA() {
		return sysA;
	}

	public SolarSystem getB() {
		return sysB;
	}

	public GateType getGateType() {
		return g;
	}

}
