package chatr;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;


public class Client {
	private static final String SERVER_ADDRESS = "192.168.0.101";
	private static final int SERVER_PORT = 4010;

	public static void main(String[] args) {
		try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
				) {

			System.out.println("Connected to server");
			System.out.println("Type message. Type 'exit' to quit");
			
			ClientInfo clientInfo = new ClientInfo(SERVER_ADDRESS, SERVER_PORT);
			int registryValidation = clientInfo.getResgisteredUserNames(in);
			if (registryValidation == 1) {
				System.out.println("Invalid username registry");
			} else {
				System.out.println("Valid username registry");
			}
			clientInfo.displayRegisteredUserNames();
			clientInfo.createUserName(consoleInput, out);
			
			new Thread(() -> {
				String serverMessage;
				try {
					while((serverMessage = in.readLine()) != null) {
						System.out.println(serverMessage);
					}
				} catch (IOException e) {
					System.out.println("Disconnected form server");
				}
			}).start();
			
			String clientMessage;
			while ((clientMessage = consoleInput.readLine()) != null) {
				if (clientMessage.equalsIgnoreCase("exit")) {
					out.println("Has left");
					break;
				}
				out.println(clientMessage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Client Shutdown");
	}
	
	static class ClientInfo {
		String ip;
		String userName;
		Vector<String> registeredUserNames;
		int port;
		
		public ClientInfo(String ip, int port) {
			this.ip = ip;
			this.port = port;
			this.registeredUserNames = new Vector<String>();
		}
		
		public int getResgisteredUserNames(BufferedReader in) {
			try { 
				String names = in.readLine();
				String[] split = names.split(":");
				for (int n = 0; n < split.length; n++) {
					registeredUserNames.add(split[n]);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
			if (!registeredUserNames.get(0).equals("rUN")) {
				return 1;
			} else {
				return 0;
			}
		}
		
		public void displayRegisteredUserNames() {
			System.out.println("Usernames");
			// start with 1 cause first is for validation
			for (int name = 1; name < registeredUserNames.size(); name++) {
				System.out.println(registeredUserNames.get(name));
			}
		}
		
		public void createUserName(BufferedReader consoleInput, PrintWriter out) {
			new Thread(() -> {
				try {
					System.out.println("Enter user name");
					userName = consoleInput.readLine();
					out.println("setUserNameVariable:" + userName);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}).start();
		}
		
	}
}
