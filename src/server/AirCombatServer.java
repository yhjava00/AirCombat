package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import info.ClientInfo;

public class AirCombatServer {

	private ServerSocket server;
	
	private GameRunning game;
	private List<Thread> threadList = new LinkedList<>();

	public AirCombatServer() {
		game = new GameRunning();
		
		Thread gameStartThread = new GameStartThread(game);
		gameStartThread.start();
		
		game.gameSetting();
		init();
	}
	
	public void init() { 
		try {
			server = new ServerSocket(1234);
			
			while(true) {
				System.out.println("wait client");
				
				Socket sck = server.accept();
				Thread ct1 = new Client1Thread(sck, game);
				sck.setTcpNoDelay(true);
				threadList.add(ct1);
				ct1.start();
				
				Socket sck2 = server.accept();
				Thread ct2 = new Client2Thread(sck2, game);
				sck2.setTcpNoDelay(true);
				threadList.add(ct2);
				ct2.start();
				
			}
		} catch (IOException e) {}
		
	}

}
class GameStartThread extends Thread {
	
	GameRunning game;
	
	public GameStartThread(GameRunning game) {
		this.game = game;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {}
			
			if(game.sInfo.p1State.equals("ready")&&game.sInfo.p2State.equals("ready")) {

				game.gameSetting();
				game.sInfo.p1State = "run";
				game.sInfo.p2State = "run";
				game.gameStart();
			}
		}
	}
}
class Client1Thread extends Thread {
	
	private Socket sck;
	private GameRunning game;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public Client1Thread(Socket sck, GameRunning game) {
		this.sck = sck;
		this.game = game;
	}
	
	@Override
	public void run() {
		try {
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			oos.writeObject(game.sInfo);
			oos.writeObject(game.c1Info);
			oos.flush();
			
			while(!game.sInfo.p1State.equals("exit")) {
				
				game.c1Info = (ClientInfo) ois.readObject();

				if(game.c1Info.request.equals("ready")) {
					game.sInfo.p1State = "ready";
				}
				
				oos.writeObject(game.sInfo);
				oos.flush();
				oos.reset();
				
				if(!game.sInfo.p1Request.equals("")) {
					game.sInfo.p1Request = "";
				}
				
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
class Client2Thread extends Thread {
	
	private Socket sck;
	private GameRunning game;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public Client2Thread(Socket sck, GameRunning game) {
		this.sck = sck;
		this.game = game;
	}
	
	@Override
	public void run() {
		try {
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			oos.writeObject(game.sInfo);
			oos.writeObject(game.c2Info);
			oos.flush();
			
			while(!game.sInfo.p1State.equals("exit")) {
				
				game.c2Info = (ClientInfo) ois.readObject();

				if(game.c2Info.request.equals("ready")) {
					game.sInfo.p2State = "ready";
				}
				
				oos.writeObject(game.sInfo);
				oos.flush();
				oos.reset();

				if(!game.sInfo.p2Request.equals("")) {
					game.sInfo.p2Request = "";
				}
				
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}