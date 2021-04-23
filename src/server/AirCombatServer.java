package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import info.ClientInfo;

public class AirCombatServer {

	private ServerSocket server;
	
	private InfoController info;
	
	private List<Thread> clientList = new LinkedList<>();
	
	private Map<String, GameController> gameMap = new HashMap<>();
	
	private char[] codeSource = {
								'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
								'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
								'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
								'U', 'V', 'W', 'X', 'Y', 'Z'
								};

	public AirCombatServer() {
		init();
	}
	
	public void init() { 
		try {
			server = new ServerSocket(1234);
			
			while(true) {
				
				info = new InfoController();
				info.Setting();
				
				System.out.println("wait client");
				
				Socket sck = server.accept();
				Thread ct = new ClientThread(sck, info);
				sck.setTcpNoDelay(true);
				
				clientList.add(ct);
				ct.start();
				
			}
		} catch (IOException e) {}
		
	}
	
	class ClientThread extends Thread {
		
		private Socket sck;
		private InfoController info;
		
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		
		private boolean check = false;
		
		public ClientThread(Socket sck, InfoController info) {
			this.sck = sck;
			this.info = info;
		}
		
		private String makeCode() {
			
			String code = "";
			
			for(int i=0; i<4; i++) {
				code += codeSource[(int)(Math.random()*codeSource.length)];
			}
			
			return code;
		}
		
		private void makeRoom() {
			
			GameController game = new GameController();
			
			game.gameSetting();

			game.connectP1(info);

			info.sInfo.request[0] = "code";
			info.sInfo.request[1] = makeCode();
			
			gameMap.put(info.sInfo.request[1], game);
		}
		
		private void connectRoom(String code) {
			GameController game = gameMap.get(code);
			game.connectP2(info);
			check = true;
		}
		
		private void ProcessClientRequest(String[] request) {
			switch (request[0]) {
			case "":
				break;
			case "ready":
				if(!info.sInfo.p1State.equals("ready"))
					info.sInfo.p1State = "ready";
				else
					info.sInfo.p2State = "ready";
				break;
			case "makeRoom":
				makeRoom();
				break;
			case "code":
				connectRoom(request[1]);
				break;
			}
		}
		
		@Override
		public void run() {
			try {
				oos = new ObjectOutputStream(sck.getOutputStream());
				ois = new ObjectInputStream(sck.getInputStream());
				
				oos.writeObject(info.sInfo);
				oos.writeObject(info.cInfo);
				oos.flush();
				
				while(!info.sInfo.p1State.equals("exit")) {

					Thread.sleep(1);
					
					info.cInfo = (ClientInfo) ois.readObject();

					ProcessClientRequest(info.cInfo.request);
					
					// 오류 java.util.ConcurrentModificationException

					oos.writeObject(info.sInfo);
					oos.flush();
					oos.reset();
					
					if(!info.sInfo.request[0].equals("")) {
						info.sInfo.request[0] = "";
						info.sInfo.request[1] = "";
					}
					
				}
				sck.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}