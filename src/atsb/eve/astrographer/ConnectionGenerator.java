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
import model.SolarSystem;
import model.SolarSystem.SecClass;

public class ConnectionGenerator {

	private static Logger logger = Logger.getLogger(ConnectionGenerator.class.toString());

	private static double METERS_PER_LY = 9.461e15;
	private static final String CONNECTION_TRUNCATE_SQL = "TRUNCATE TABLE mapConnections";
	private static final String SYSTEM_SELECT_SQL = "SELECT `solarSystemName`,`solarSystemID`,`x`,`y`,`z`,`security` FROM mapSolarSystems";
	private static final String CONNECTION_INSERT_SQL = "INSERT INTO mapConnections ("
			+ "`systemA`,`systemB`,`distance`,`gate`,`jdrive`,`jbridge`" + ") VALUES (?,?,?,?,?,?)";

	private DbInfo dbInfo;

	public ConnectionGenerator() {
		dbInfo = new DbInfo();
	}

	public void generateConnections() {
		// open database connection
		Connection db;
		try {
			db = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getUser(), dbInfo.getPass());
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to open database connection: " + e.getLocalizedMessage());
			return;
		}
		PreparedStatement stmt;

		// get all the k-space systems
		List<SolarSystem> systems = new ArrayList<SolarSystem>();
		try {
			stmt = db.prepareStatement(SYSTEM_SELECT_SQL);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(1);
				int sysId = rs.getInt(2);
				Double x = rs.getDouble(3);
				Double y = rs.getDouble(4);
				Double z = rs.getDouble(5);
				Double sec = rs.getDouble(6);
				systems.add(new SolarSystem(name, sysId, new Point3D(x, y, z), sec));
			}
			logger.log(Level.INFO, "Loaded " + systems.size() + " k-space systems");
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Failed to retrieve systems from db: " + e.getLocalizedMessage());
			return;
		}

		// generate and save connections to db
		try {
			db.setAutoCommit(false);

			stmt = db.prepareStatement(CONNECTION_TRUNCATE_SQL);
			stmt.execute();

			stmt = db.prepareStatement(CONNECTION_INSERT_SQL);
			int count = 0;
			for (int i = 0; i < systems.size(); i++) {
				for (int j = i + 1; j < systems.size(); j++) {
					SolarSystem a = systems.get(i);
					SolarSystem b = systems.get(j);
					if (a.getSecClass() == SecClass.HISEC && b.getSecClass() == SecClass.HISEC) {
						continue;
					}
					double ly = a.getPosition().distance(b.getPosition()) / METERS_PER_LY;

					// ignore everything beyond the max jf range
					if (ly > 10) {
						continue;
					}

					stmt.setLong(1, a.getSystemId());
					stmt.setLong(2, b.getSystemId());
					stmt.setDouble(3, ly);
					stmt.setBoolean(4, false);
					stmt.setBoolean(5, true);
					stmt.setBoolean(6, false);

					stmt.addBatch();
					count++;
					if (count % 1000 == 0) {
						try {
							stmt.executeBatch();
							logger.log(Level.INFO, "Added 1000 connections (" + count + " so far)");
						} catch (SQLException ex) {
							logger.log(Level.WARNING, "Failed to insert some connections: " + ex.getLocalizedMessage());
						}
					}
				}
			}
			try {
				stmt.executeBatch();
			} catch (SQLException ex) {
			}

			db.commit();
			db.setAutoCommit(true);
			logger.log(Level.INFO, "Inserted " + count + " connections");
		} catch (SQLException e) {
			logger.log(Level.WARNING, "Unexpected failure while generating connections");
		}

		try {
			db.close();
		} catch (SQLException e) {

		}
	}

	public static void main(String[] args) {
		ConnectionGenerator congen = new ConnectionGenerator();
		congen.generateConnections();
	}
}
