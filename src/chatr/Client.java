package chatr;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;


public class Client {
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 8010;

	public static void main(String[] args) {
		try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
				) {

			System.out.println("Connected to server");
			System.out.println("Type message. Type 'exit' to quit");
			
			new Thread(() -> {
				String serverMessage;
				try {
					while((serverMessage = in.readLine()) != null) {
						System.out.println("user : " + serverMessage);
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
		
		System.out.println("Client killed");
	}
}
