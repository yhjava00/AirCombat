package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import info.Client1Info;
import info.ServerInfo;

public class AirCombatClient {

	private static Socket sck;

	private static ObjectOutputStream oos;
	private static ObjectInputStream ois;
	
	public static void main(String[] args) {
		init();
	}
	
	public static void init() {
		try {
			sck = new Socket("localhost", 1234);
			System.out.println("Server Connect");
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());

			GameField.sInfo = (ServerInfo)ois.readObject();
			GameField.cInfo = (Client1Info)ois.readObject();
			
			new Board();
			
			String state = "play";

			oos.writeObject(state);
			oos.flush();
			
			while(!(state=(String)ois.readObject()).equals("exit")) {

				try {
					Thread.sleep(1);
				} catch (Exception e) {}

				oos.writeObject(GameField.cInfo);
				oos.flush();
				
				GameField.sInfo = (ServerInfo)ois.readObject();

				oos.writeObject(state);
				oos.flush();
				oos.reset();
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
