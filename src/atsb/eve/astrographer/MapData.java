package atsb.eve.astrographer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Point3D;
import atsb.eve.astrographer.model.KDTree;
import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SystemConnection;

public class MapData {

	public static final double METERS_PER_LY = 9.461e15;

	HashMap<Integer, SolarSystem> systems = new HashMap<Integer, SolarSystem>();
	List<SystemConnection> connections = new ArrayList<SystemConnection>();
	List<SystemConnection> jumpbridges = new ArrayList<SystemConnection>();
	List<SystemConnection> highway = new ArrayList<SystemConnection>();
	KDTree searchTree;
	Point3D universeCenter;
	double universeScale = 1;

	public SolarSystem findSystemById(int systemId) {
		return systems.get(systemId);
	}

	public SolarSystem findSystemByName(String search) {
		for (SolarSystem s : systems.values()) {
			if (search.equalsIgnoreCase(s.getName())) {
				return s;
			}
		}
		return null;
	}

	public SolarSystem findNearest(double x, double z) {
		return searchTree.nearest(x, z);
	}

	// TODO: use the kdtree
	public List<SolarSystem> findWithinRange(double x, double y, double z, double r) {
		ArrayList<SolarSystem> ss = new ArrayList<SolarSystem>();
		for (SolarSystem s : systems.values()) {
			double d = Math.sqrt(Math.pow(x - s.getPosition().getX(), 2) + Math.pow(y - s.getPosition().getY(), 2)
					+ Math.pow(z - s.getPosition().getZ(), 2));
			if (d < r && d != 0) {
				ss.add(s);
			}
		}
		return ss;
	}

	public List<SolarSystem> findWithinRange(SolarSystem s, double r) {
		return findWithinRange(s.getPosition().getX(), s.getPosition().getY(), s.getPosition().getZ(), r);
	}

	public static double distLY(SolarSystem a, SolarSystem b) {
		return Math.sqrt(Math.pow(a.getPosition().getX() - b.getPosition().getX(), 2)
				+ Math.pow(a.getPosition().getY() - b.getPosition().getY(), 2)
				+ Math.pow(a.getPosition().getZ() - b.getPosition().getZ(), 2)) / METERS_PER_LY;
	}

}
