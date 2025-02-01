package chatr;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
	private static final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
	private static final String CONFIG_FILE = "/home/david/chatr_server/config.conf";

	public static void main(String[] args) {
		
		String dbPath;
		int port;
		Config config = new Config(CONFIG_FILE);
		
		System.out.println("Server started...");
		config.readConfig();

		dbPath = config.database;
		port = config.port;

		Database db = new Database(dbPath);
		int dbCheck = db.dbExists(dbPath);
		if (dbCheck == 1) {
			System.out.println("Database DNE");
			System.exit(1);
		} else if (dbCheck == 0) {
			System.out.println("Database good");
		}

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Server connected at: " + port);
			while(true) {
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
		 private PrintWriter out;
		 private BufferedReader in;
		 
		 public ClientHandler(Socket socket) {
			 this.socket = socket;
		 }

		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				String message;
				while ((message = in.readLine()) != null) {
					System.out.println("Recieved: " + message);
					Server.broadcast(message, this);
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
