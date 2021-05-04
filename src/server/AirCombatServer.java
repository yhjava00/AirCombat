package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class AirCombatServer {
	
	private ServerSocket server;
	
	public static Map<String, GameController> gameMap = new HashMap<>();
	
	public AirCombatServer() {
		try {
			
			gameMap.put("", null);
			
			server = new ServerSocket(1234);
			
			System.out.println("server start");
			
			while(true) {
				
				Socket sck = server.accept();
				
				System.out.println("connect client");
				
				Thread ct = new ClientThread(sck);
				sck.setTcpNoDelay(true);
				
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
