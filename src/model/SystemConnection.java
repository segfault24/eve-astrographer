package model;

public class SystemConnection {

	public enum GateType {
		NORMAL, CONSTELLATION, REGIONAL, JUMPBRIDGE
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
