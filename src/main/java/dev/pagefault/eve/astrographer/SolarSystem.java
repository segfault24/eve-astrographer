package dev.pagefault.eve.astrographer;

import javafx.geometry.Point3D;

public class SolarSystem {

	public enum SecClass {
		HISEC, LOWSEC, NULSEC
	}

	public enum CapType {
		NONE, CAP, SUPER;
	}

	private String name;
	private int systemId;
	private Point3D position;
	private double security;
	private SecClass sec;
	private CapType capType = CapType.NONE;
	private boolean beacon;

	public SolarSystem(String name, int systemId, Point3D position, double security) {
		this.name = name;
		this.systemId = systemId;
		this.position = position;
		setSecurity(security);
		this.beacon = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSystemId() {
		return systemId;
	}

	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}

	public Point3D getPosition() {
		return position;
	}

	public void setPosition(Point3D position) {
		this.position = position;
	}

	public double getSecurity() {
		return security;
	}

	public void setSecurity(double security) {
		this.security = security;
		if (security >= 0.5) {
			this.sec = SecClass.HISEC;
		} else if (security > 0.0) {
			this.sec = SecClass.LOWSEC;
		} else {
			this.sec = SecClass.NULSEC;
		}
	}

	public SecClass getSecClass() {
		return this.sec;
	}

	public CapType getCapType() {
		return capType;
	}

	public void setCapType(CapType capType) {
		this.capType = capType;
	}

	public boolean hasBeacon() {
		return beacon;
	}

	public void setBeacon(boolean beacon) {
		this.beacon = beacon;
	}

}
