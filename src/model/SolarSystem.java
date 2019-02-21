package model;

import javafx.geometry.Point3D;

public class SolarSystem {

	public enum SecClass {
		HISEC, LOWSEC, NULSEC
	}

	private String name;
	private int systemId;
	private Point3D position;
	private double security;
	private SecClass sec;

	public SolarSystem(String name, int systemId, Point3D position, double security) {
		this.name = name;
		this.systemId = systemId;
		this.position = position;
		setSecurity(security);
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

}
