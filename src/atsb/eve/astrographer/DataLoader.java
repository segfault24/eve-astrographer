package atsb.eve.astrographer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import atsb.eve.astrographer.SolarSystem.CapType;
import atsb.eve.astrographer.SystemConnection.GateType;
import javafx.geometry.Point3D;

public class DataLoader {

	private static Logger logger = Logger.getLogger(DataLoader.class.toString());

	private static String SYSTEMS_FILE = "cfg/systems.csv";
	private static String CONNECTIONS_FILE = "cfg/connections.csv";
	private static String JUMPBRIDGES_FILE = "cfg/jumpbridges.txt";
	private static String BEACONS_FILE = "cfg/beacons.txt";
	private static String HIGHWAY_FILE = "cfg/highway.csv";

	public static MapData load() throws IOException {
		MapData d = new MapData();
		loadSystems(d);
		loadConnections(d);
		loadJumpbridges(d);
		loadBeacons(d);
		loadHighway(d);
		return d;
	}

	private static void loadSystems(MapData d) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(SYSTEMS_FILE));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line, ",");
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
				d.systems.put(sys.getSystemId(), sys);
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

		for (SolarSystem s : d.systems.values()) {
			minx = Math.min(s.getPosition().getX(), minx);
			maxx = Math.max(s.getPosition().getX(), maxx);
			minz = Math.min(s.getPosition().getZ(), minz);
			maxz = Math.max(s.getPosition().getZ(), maxz);
		}

		Double spanx = maxx - minx;
		Double spanz = maxz - minz;
		d.universeScale = Math.max(spanx, spanz) / 2;
		d.universeCenter = new Point3D(minx + spanx / 2, 0, minz + spanz / 2);

		d.searchTree = new KDTree(d.systems.values());
	}

	private static void loadConnections(MapData d) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(CONNECTIONS_FILE));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line, ",");
			if (parts.length != 3) {
				logger.warning("Failed to parse line in connections.csv: " + line);
				continue;
			}
			try {
				SolarSystem a = d.findSystemById(Integer.parseInt(parts[0]));
				SolarSystem b = d.findSystemById(Integer.parseInt(parts[1]));
				GateType g = GateType.valueOf(Integer.parseInt(parts[2]));
				SystemConnection con = new SystemConnection(a, b, g);
				d.connections.add(con);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in connections.csv: " + line);
				continue;
			}
		}
		br.close();
	}

	public static void loadJumpbridges(MapData d) throws IOException {
		d.jumpbridges.clear();
		BufferedReader br = new BufferedReader(new FileReader(JUMPBRIDGES_FILE));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line, "\\s+");
			if (parts.length == 0) {
				continue;
			}
			if (parts.length != 4) {
				logger.warning("Failed to parse line in jumpbridges.txt: " + line);
				continue;
			}
			try {
				SolarSystem a = d.findSystemByName(parts[1]);
				SolarSystem b = d.findSystemByName(parts[3]);
				GateType g = GateType.JB;
				if (a == null || b == null) {
					logger.warning("Failed to parse line in jumpbridges.txt: " + line);
					continue;
				}
				SystemConnection con = new SystemConnection(a, b, g);
				d.jumpbridges.add(con);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in jumpbridges.txt: " + line);
				continue;
			}
		}
		br.close();
	}

	public static void loadBeacons(MapData d) throws IOException {
		for (SolarSystem s : d.systems.values()) {
			s.setBeacon(false);
		}
		BufferedReader br = new BufferedReader(new FileReader(BEACONS_FILE));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line, ";");
			if (parts.length == 0) {
				continue;
			}
			if (parts.length != 2) {
				logger.warning("Failed to parse line in beacons.txt: " + line);
				continue;
			}
			try {
				SolarSystem a = d.findSystemByName(parts[0]);
				if (a == null) {
					logger.warning("Failed to parse line in beacons.txt: " + line);
					continue;
				}
				a.setBeacon(true);
			} catch (NumberFormatException e) {
				logger.warning("Failed to parse line in beacons.txt: " + line);
				continue;
			}
		}
		br.close();
	}

	public static void loadHighway(MapData d) throws IOException {
		for (SolarSystem s : d.highwaySystems) {
			s.setCapType(CapType.NONE);
		}
		d.highwaySystems.clear();
		BufferedReader br = new BufferedReader(new FileReader(HIGHWAY_FILE));
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = prep(line, ",");
			if (parts.length == 0) {
				continue;
			}
			if (parts.length != 2) {
				logger.warning("Failed to parse line in highway.csv: " + line);
				continue;
			}
			SolarSystem s = d.findSystemByName(parts[0]);
			if (s == null) {
				logger.warning("Failed to recognize system in highway.csv: " + line);
				continue;
			}
			if (parts[1].equalsIgnoreCase("1")) {
				s.setCapType(CapType.SUPER);
			} else if (parts[1].equalsIgnoreCase("0") && s.getCapType() != CapType.SUPER) {
				s.setCapType(CapType.CAP);
			}
			if (!d.highwaySystems.contains(s)) {
				d.highwaySystems.add(s);
			}
		}
		br.close();
		d.recalculateHighwayConnections();
	}

	// check for null/blank/comment, then break up line by specified delimiter and trim everything
	private static String[] prep(String _line, String split) {
		if (_line == null) {
			return new String[0];
		}
		String line = _line.trim();
		if (line.isEmpty() || line.startsWith("#")) {
			return new String[0];
		}
		String[] parts = line.split(split);
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parts;
	}

}
