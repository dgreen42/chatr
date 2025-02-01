package chatr;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	private static final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
	private static final String CONFIG_FILE = "/home/david/chatr_server/config.conf";
	private static Database db;

	public static void main(String[] args) {
		
		String dbPath;
		String ip;
		int port;
		Config config = new Config(CONFIG_FILE);
		
		System.out.println("Server started...");
		config.readConfig();

		dbPath = config.getDatabase();
		port = config.getPort();
		ip = config.getIp();

		String driveConPath = db.getDriveConPath();
		db = new Database(dbPath);
		int dbCheck = db.dbExists(dbPath);
		if (dbCheck == 1) {
			System.out.println("Database DNE. Creating Database at" + dbPath);
			Database.createUserTable(driveConPath);
			Database.createStatusTable(driveConPath);
		} else if (dbCheck == 0) {
			System.out.println("Database found");
		}
		System.out.println("Checking tables...");
		int tableCheck = db.checkTables(driveConPath);
		if (tableCheck == 0) {
			System.out.println("Tables found");
		} else if (tableCheck == 2) {
			System.out.println("All tables created");
		} else {
			System.out.println("Necessary tables created");
		}

		try (ServerSocket serverSocket = new ServerSocket()) {
			InetAddress ipAddress = InetAddress.getByName(ip);
			serverSocket.bind(new InetSocketAddress(ipAddress, port));
			System.out.println("Server connected at: " + ipAddress + ":" + port);
			while(serverSocket.isBound()) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client connected: " + clientSocket);
				ClientHandler handler = new ClientHandler(clientSocket);
				clientHandlers.add(handler);
				new Thread(handler).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	static void broadcast(String message, ClientHandler sender) {
		synchronized (clientHandlers) {
			for (ClientHandler client : clientHandlers) {
				if (client != sender) {
					client.sendMessage(message);
				}
			}
		}
	}
	 static void removeClient(ClientHandler clientHandler) {
		 clientHandlers.remove(clientHandler);
	 }
	 
	 static class ClientHandler implements Runnable {
		 private final Socket socket;
		 private final InetAddress address;
		 private final int port;
		 private String userName;
		 private PrintWriter out;
		 private BufferedReader in;
		 
		 public ClientHandler(Socket socket) {
			 this.socket = socket;
			 this.address = socket.getInetAddress();
			 this.port = socket.getPort();
		 }

		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				String message;
				while ((message = in.readLine()) != null) {
					if (message.split(":")[0].equals("setUserNameVariable")) {
						userName = message.split(":")[1];
						System.out.println(address+ ":" + port + "; User name set to: " + userName);
						Server.broadcast(userName + " has connected", this);
						db.createUser(userName, address);
					} else {
						System.out.println("Recieved from: " + userName + ": " + message);
						Server.broadcast(userName + ": " + message, this);
					}
				}
			} catch (IOException e) {
				System.out.println("Client disconnected: " + socket);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Server.removeClient(this);
			}
		}
		
		void sendMessage(String message) {
			out.println(message);
		}
	 }
}
