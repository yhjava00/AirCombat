package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import info.Client1Info;

public class AirCombatServer {

	private static ServerSocket server;
	
	private static  List<Thread> threadList = new LinkedList<>();
	
	//인터페이스 사용해서 클라 정보 처리하기
	public static void main(String[] args) {
		new GameRunning();
		
		init();
	}
	
	public static void init() { 
		try {
			server = new ServerSocket(1234);
			
			while(true) {
				System.out.println("wait client");
				
				Socket sck = server.accept();
				ClientThread ct = new ClientThread(sck);
				
				threadList.add(ct);
				
				ct.start();
				
			}
		} catch (IOException e) {}
		
	}

}

class ClientThread extends Thread {
	
	private Socket sck;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public ClientThread(Socket sck) {
		this.sck = sck;
	}
	
	@Override
	public void run() {
		try {
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			oos.writeObject(GameRunning.sInfo);
			oos.writeObject(GameRunning.cInfo);
			oos.flush();
			
			String state = "";

			while(!(state=(String)ois.readObject()).equals("exit")) {
				
				oos.writeObject(state);
				oos.flush();

				GameRunning.cInfo = (Client1Info) ois.readObject();
				
				oos.writeObject(GameRunning.sInfo);
				oos.flush();
				oos.reset();
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}