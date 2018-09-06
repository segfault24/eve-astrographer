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

import javafx.geometry.Point3D;

public class SystemLoader {

	private static Logger logger = Logger.getLogger(SystemLoader.class.toString());

	private static final String SYSTEM_SELECT_SQL = "SELECT `solarSystemName`,`solarSystemID`,`x`,`y`,`z`,`security` FROM mapSolarSystems WHERE regionID<11000000";

	public SystemLoader() {
	}

	public List<SolarSystem> loadSystems() {
		List<SolarSystem> systems = new ArrayList<SolarSystem>();

		DbInfo dbInfo = new DbInfo();
		Connection db = null;
		try {
			db = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getUser(), dbInfo.getPass());
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to open database connection: " + e.getLocalizedMessage());
			return systems;
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

		return systems;
	}

	public List<Connection> loadGateConnections() {
		return null;
	}
}
