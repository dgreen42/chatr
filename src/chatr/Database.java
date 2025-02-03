package chatr;

import java.sql.*;
import java.util.*;

import org.sqlite.core.DB;

import java.net.InetAddress;
import java.nio.file.*;

public class Database {
	private String conPath;
	private String driveConPath;

	Database(String con) {
		this.conPath = con;
		this.driveConPath = "jdbc:sqlite:" + conPath;
	}
	
	public String getConPath() {
		return conPath;
	}
	
	public String getDriveConPath() {
		return driveConPath;
	}
	
	public void checkUserStatus(String ip) {
		//TODO check if the user exists, retrieve the username and check if they are banned. 
	}
	
	public int checkTables(String con) {
		int check = 0;
		try (Connection dbCon = DriverManager.getConnection(con)) {
			Statement userCheck = dbCon.createStatement();
			userCheck.executeUpdate("SELECT * FROM Users");
			dbCon.close();
		} catch (SQLException e) {
			System.out.println("User Table not found");
			check++;
			createUserTable(con);
		}

		try (Connection dbCon = DriverManager.getConnection(con)) {
			Statement statusCheck = dbCon.createStatement();
			statusCheck.executeUpdate("SELECT * FROM Status");
			dbCon.close();
		} catch (SQLException e) {
			System.out.println("Status Table not found");
			check++;
			createStatusTable(con);
		}
		return check;
	}
	
	public static void createUserTable(String con) {
		try (Connection dbCon = DriverManager.getConnection(con)) {
			Statement stmt = dbCon.createStatement();
			stmt.executeUpdate("CREATE TABLE Users (id VARCHAR(225), username VARCHAR(225), ip VARCHAR(225))");
			System.out.println("User table created");
			dbCon.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void createStatusTable(String con) {
		try (Connection dbCon = DriverManager.getConnection(con)) {
			Statement stmt = dbCon.createStatement();
			stmt.executeUpdate("CREATE TABLE Status (id VARCHAR(225), auth INT, banned INT)");
			System.out.println("Status table created");
			dbCon.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
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
	
	public String getUserNames(InetAddress address) {
		String userNames = "rUN";
		try (Connection dbCon = DriverManager.getConnection(driveConPath)) {
			try(Statement userQuery = dbCon.createStatement()) {
				ResultSet userResult = userQuery.executeQuery("SELECT * FROM Users WHERE ip == '%s'".formatted(address));
				while(userResult.next() == true) {
					userNames = userNames + ":" + userResult.getString("username");
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return userNames;
	}
		
	public UUID createUser(String username, InetAddress address) {
		UUID uuid = generateUUID();

		try (Connection dbCon = DriverManager.getConnection(driveConPath)) {
			if (dbCon != null) {
				try (Statement unQuery = dbCon.createStatement()) {
					unQuery.executeUpdate("INSERT INTO Users (id, username, ip) VALUES ('%s', '%s', '%s')".formatted(uuid, username, address.toString()));
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				dbCon.close();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return uuid;
	}
}
