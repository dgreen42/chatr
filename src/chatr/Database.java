package chatr;

import java.sql.*;
import java.util.*;
import java.nio.file.*;

public class Database {
	String conPath;
	String driveConPath;

	Database(String con) {
		this.conPath = con;
		this.driveConPath = "jdbc:sqlite:" + conPath;
	}
	
	public void checkUserStatus(String ip) {
		//TODO check if the user exists, retrieve the username and check if they are banned. Need to make a seperate table to set
		// permission status and things like that
	}
	
	private UUID generateUUID() {
		UUID uuid = new UUID(1,1);
		try (Connection dbCon = DriverManager.getConnection(driveConPath)) {
			try (Statement select = dbCon.createStatement()) {
				ResultSet selectResult = select.executeQuery("SELECT id FROM Users");
				while (selectResult.next() == true) {
					UUID testUUID = new UUID(1,1);
					testUUID = UUID.randomUUID();
					if (testUUID.toString() == selectResult.getString(1)) {
						System.out.println("UUID %s exists".formatted(testUUID.toString()));
						continue;
					} else {
						uuid = testUUID;
					}
				}

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return uuid;
	}
	
	public int dbExists(String path) {
		Path dbPath = Path.of(path);
		if (Files.exists(dbPath)) {
			return 0;
		} else {
			return 1;
		}
	}
		
	public void setUserName(String username, String ip) {
		UUID uid = generateUUID();

		int dbCheck = dbExists(this.conPath);
		if (dbCheck == 1) {
			System.out.println("Database path DNE");
			return;
		} 

		try (Connection dbCon = DriverManager.getConnection(driveConPath)) {
			if (dbCon != null) {
				System.out.println("Connected to: " + conPath.toString());
				try (Statement unQuery = dbCon.createStatement()) {
					unQuery.executeUpdate("INSERT INTO Users (id, username) VALUES ('%s', '%s', '%s')".formatted(uid, username, ip));
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				dbCon.close();
			}
		} catch (SQLException e) {
			System.out.println(e.getStackTrace());
		}

	}
}
