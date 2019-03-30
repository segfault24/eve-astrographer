package atsb.eve.astrographer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import atsb.eve.astrographer.model.SolarSystem;
import atsb.eve.astrographer.model.SystemConnection;
import atsb.eve.astrographer.model.SystemConnection.GateType;
import javafx.geometry.Point3D;

public class SystemLoader {

	private static Logger logger = Logger.getLogger(SystemLoader.class.toString());

	private static final String SYSTEM_SELECT_SQL = "SELECT `solarSystemName`,`solarSystemID`,`x`,`y`,`z`,`security` FROM mapSolarSystems WHERE regionID<11000000";
	private static final String CONNECTION_SELECT_SQL = "SELECT `fromRegionID`,`fromConstellationID`,`fromSolarSystemID`,`toRegionID`,`toConstellationID`,`toSolarSystemID` FROM mapSolarSystemJumps";

	private List<SolarSystem> systems;
	private List<SystemConnection> connections;
	
	public SystemLoader() {
		systems = new ArrayList<SolarSystem>();
		connections = new ArrayList<SystemConnection>();
	}

	public List<SolarSystem> getSystems() {
		return systems;
	}

	public List<SystemConnection> getConnections() {
		return connections;
	}

	public void load() {
		loadSystems();
		loadConnections();
	}

	private void loadSystems() {
		systems.clear();
		DbInfo dbInfo = new DbInfo();
		Connection db = null;
		try {
			db = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getUser(), dbInfo.getPass());
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to open database connection: " + e.getLocalizedMessage());
			return;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = db.prepareStatement(SYSTEM_SELECT_SQL);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				int sysId = rs.getInt(2);
				Double x = rs.getDouble(3);
				Double y = rs.getDouble(4);
				Double z = rs.getDouble(5);
				Double sec = rs.getDouble(6);
				systems.add(new SolarSystem(name, sysId, new Point3D(x, y, z), sec));
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to retrieve systems from db: " + e.getLocalizedMessage());
		}

		IOUtils.closeQuietly(rs);
		IOUtils.closeQuietly(stmt);
		IOUtils.closeQuietly(db);
	}

	private void loadConnections() {
		connections.clear();

		DbInfo dbInfo = new DbInfo();
		Connection db = null;
		try {
			db = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getUser(), dbInfo.getPass());
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to open database connection: " + e.getLocalizedMessage());
			return;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = db.prepareStatement(CONNECTION_SELECT_SQL);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int fromRegionId = rs.getInt(1);
				int fromConstellationId = rs.getInt(2);
				int fromSystemId = rs.getInt(3);
				int toRegionId = rs.getInt(4);
				int toConstellationId = rs.getInt(5);
				int toSystemId = rs.getInt(6);
				GateType g;
				if (fromRegionId != toRegionId) {
					g = GateType.REGIONAL;
				} else if (fromConstellationId != toConstellationId) {
					g = GateType.CONSTELLATION;
				} else {
					g = GateType.NORMAL;
				}
				SolarSystem a = findSystem(fromSystemId);
				SolarSystem b = findSystem(toSystemId);
				if (a != null && b != null) {
					connections.add(new SystemConnection(a, b, g));
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to retrieve systems from db: " + e.getLocalizedMessage());
		}

		IOUtils.closeQuietly(rs);
		IOUtils.closeQuietly(stmt);
		IOUtils.closeQuietly(db);
	}

	private SolarSystem findSystem(int systemId) {
		for (SolarSystem s : systems) {
			if (s.getSystemId() == systemId) {
				return s;
			}
		}
		return null;
	}
}
