package dev.pagefault.eve.astrographer;

public class SystemConnection {

	public enum GateType {
		NORMAL, CONSTELLATION, REGIONAL, JB, JB_HOSTILE, HWY_REGULAR, HWY_SUPER_FORT, HWY_SUPER_KEEP;

		public static GateType valueOf(int i) {
			switch (i) {
			case 7:
				return GateType.HWY_SUPER_KEEP;
			case 6:
				return GateType.HWY_SUPER_FORT;
			case 5:
				return GateType.HWY_REGULAR;
			case 4:
				return GateType.JB_HOSTILE;
			case 3:
				return GateType.JB;
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
