package chatr;

import java.io.*;
import java.util.*;

public class Config {
	String configFile;
	String database;
	String ip;
	int port;
	
	public Config(String file) {
		this.configFile = file;
	}
	
	public void readConfig() {
		System.out.println("Reading config...");
		try (BufferedReader buffer = new BufferedReader(new FileReader(configFile))) {
			String line;
			while((line = buffer.readLine()) != null) {
				String[] lineSplit = line.split(":");
				if (lineSplit[0].equalsIgnoreCase("database")) {
					database = lineSplit[1];
				} else if (lineSplit[0].equals("ip")) {
					ip = lineSplit[1];
				} else if (lineSplit[0].equals("port")) {
					port = Integer.parseInt(lineSplit[1].toString());
				} else {
					System.out.println("Invalid config parameter " + line);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
