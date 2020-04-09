package atsb.eve.astrographer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.geometry.Point3D;
import atsb.eve.astrographer.model.KDTree;
import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SolarSystem.CapType;
import atsb.eve.astrographer.model.SystemConnection;
import atsb.eve.astrographer.model.SystemConnection.GateType;

public class MapData {

	private static Logger logger = Logger.getLogger(MapData.class.toString());

	public static final double METERS_PER_LY = 9.461e15;

	private HashMap<Integer, SolarSystem> systems;
	private List<SystemConnection> connections;
	private KDTree searchTree;
	private Point3D universeCenter;
	private double universeScale = 1;

	public MapData() {
		systems = new HashMap<Integer, SolarSystem>();
		connections = new ArrayList<SystemConnection>();
	}

	public void load() throws IOException {
		systems.clear();
		connections.clear();
		loadSystems();
		logger.info("Loaded " + systems.size() + " systems");
		loadConnections();
		loadJumpbridges();
		logger.info("Loaded " + connections.size() + " connections");
		loadHighway();
		logger.info("Loaded capital highway");
	}

	public Map<Integer, SolarSystem> getSystems() {
		return systems;
	}

	public List<SystemConnection> getConnections() {
		return connections;
	}

	public Point3D getUniverseCenter() {
		return this.universeCenter;
	}

	public double getUniverseScale() {
		return this.universeScale;
	}

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
		return Math.sqrt(Math.pow(a.getPosition().getX() - b.getPosition().getX(), 2) +
				Math.pow(a.getPosition().getY() - b.getPosition().getY(), 2) +
				Math.pow(a.getPosition().getZ() - b.getPosition().getZ(), 2)) / METERS_PER_LY;
	}

	private void loadSystems() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("systems.csv"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line);
			if (parts.length != 6) {
				logger.warning("Failed to parse line in systems.csv: " + line);
				continue;
			}
			try {
				int id = Integer.parseInt(parts[0]);
				String name = parts[1];
				double sec = Double.parseDouble(parts[2]);
				double x = Double.parseDouble(parts[3]);
				double y = Double.parseDouble(parts[4]);
				double z = Double.parseDouble(parts[5]);
				Point3D pos = new Point3D(x, y, z);
				SolarSystem sys = new SolarSystem(name, id, pos, sec);
				systems.put(sys.getSystemId(), sys);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in systems.csv: " + line);
				continue;
			}
		}
		br.close();

		Double minx = Double.MAX_VALUE;
		Double maxx = Double.MIN_VALUE;
		Double minz = Double.MAX_VALUE;
		Double maxz = Double.MIN_VALUE;

		for (SolarSystem s : systems.values()) {
			minx = Math.min(s.getPosition().getX(), minx);
			maxx = Math.max(s.getPosition().getX(), maxx);
			minz = Math.min(s.getPosition().getZ(), minz);
			maxz = Math.max(s.getPosition().getZ(), maxz);
		}

		Double spanx = maxx - minx;
		Double spanz = maxz - minz;
		universeScale = Math.max(spanx, spanz) / 2;
		universeCenter = new Point3D(minx + spanx / 2, 0, minz + spanz / 2);

		searchTree = new KDTree(systems.values());
	}

	private void loadConnections() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("connections.csv"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line);
			if (parts.length != 3) {
				logger.warning("Failed to parse line in connections.csv: " + line);
				continue;
			}
			try {
				SolarSystem a = findSystemById(Integer.parseInt(parts[0]));
				SolarSystem b = findSystemById(Integer.parseInt(parts[1]));
				GateType g = GateType.valueOf(Integer.parseInt(parts[2]));
				SystemConnection con = new SystemConnection(a, b, g);
				connections.add(con);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in connections.csv: " + line);
				continue;
			}
		}
		br.close();
	}

	private void loadJumpbridges() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("jumpbridges.csv"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line);
			if (parts.length != 2 && parts.length != 3) {
				logger.warning("Failed to parse line in jumpbridges.csv: " + line);
				continue;
			}
			try {
				SolarSystem a = findSystemByName(parts[0]);
				SolarSystem b = findSystemByName(parts[1]);
				GateType g = GateType.JUMPBRIDGE;
				if (parts.length == 3 && parts[2].equalsIgnoreCase("hostile")) {
					g = GateType.JB_HOSTILE;
				}
				if (a == null || b == null) {
					logger.warning("Failed to parse line in jumpbridges.csv: " + line);
					continue;
				}
				SystemConnection con = new SystemConnection(a, b, g);
				connections.add(con);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in jumpbridges.csv: " + line);
				continue;
			}
		}
		br.close();
	}

	private void loadHighway() throws IOException {
		ArrayList<SolarSystem> hwy = new ArrayList<SolarSystem>();
		BufferedReader br = new BufferedReader(new FileReader("highway.csv"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line);
			if (parts.length != 2) {
				logger.warning("Failed to parse line in highway.csv: " + line);
				continue;
			}
			SolarSystem s = findSystemByName(parts[0]);
			if (s == null) {
				logger.warning("Failed to recognize system in highway.csv: " + line);
				continue;
			}
			if (parts[1].equalsIgnoreCase("1")) {
				s.setCapType(CapType.SUPER);
			} else {
				s.setCapType(CapType.CAP);
			}
			hwy.add(s);
		}
		br.close();

		// calculate connections
		for (SolarSystem i : hwy) {
			for (SolarSystem j : hwy) {
				if (i == j) {
					continue;
				}
				if (distLY(i, j) < 6) {
					SystemConnection c = new SystemConnection(i, j, GateType.SUPER_HIGHWAY);
					connections.add(c);
				} else if (distLY(i, j) < 7) {
					SystemConnection c = new SystemConnection(i, j, GateType.HIGHWAY);
					connections.add(c);
				}
			}
		}
	}

	// check for null/blank/comment, then break up line by comma and trim everything
	private static String[] prep(String line) {
		if (line == null) {
			return new String[0];
		}
		line = line.trim();
		if (line.isEmpty() || line.startsWith("#")) {
			return new String[0];
		}
		String[] parts = line.split(",");
		for (int i=0; i<parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parts;
	}

}
