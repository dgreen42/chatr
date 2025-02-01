package chatr;

import java.io.*;
import java.util.*;

public class Config {
	private String configFile;
	private String database;
	private String ip;
	private int port;
	
	public Config(String file) {
		this.configFile = file;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void readConfig() {
		System.out.println("Reading config...");
		try (BufferedReader buffer = new BufferedReader(new FileReader(configFile))) {
			String line;
			while((line = buffer.readLine()) != null) {
				String[] lineSplit = line.split(":");
				if (lineSplit[0].equalsIgnoreCase("database")) {
					database = lineSplit[1];
					System.out.println("Database path set");
				} else if (lineSplit[0].equals("ip")) {
					ip = lineSplit[1];
					System.out.println("IP set");
				} else if (lineSplit[0].equals("port")) {
					port = Integer.parseInt(lineSplit[1].toString());
					System.out.println("Port set");
				} else {
					System.out.println("Invalid config parameter " + line);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
