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
				Thread ct = new ClientThread(sck, info, 1);
				sck.setTcpNoDelay(true);
				
				clientList.add(ct);
				ct.start();
				
			}
		} catch (IOException e) {}
		
	}
	
	class ClientThread extends Thread {
		
		private Socket sck;
		private InfoController info;
		private int clientNum;
		
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		
		public ClientThread(Socket sck, InfoController info, int clientNum) {
			this.sck = sck;
			this.info = info;
			this.clientNum = clientNum;
		}
		
		private String makeCode() {
			
			String code = "";
			
			for(int i=0; i<6; i++) {
				code += codeSource[(int)(Math.random()*codeSource.length)];
			}
			
			return code;
		}
		
		private void makeRoom() {
			
			GameController game = new GameController();
			
			game.gameSetting();

			game.connectP1(info);

			info.sInfo.p1Request[0] = "code";
			info.sInfo.p1Request[1] = makeCode();
			
			gameMap.put(info.sInfo.p1Request[1], game);
		}
		
		private void connectRoom(String code) {
			GameController game = gameMap.get(code);
			clientNum = 2;
			game.connectP2(info);
		}
		
		private void ProcessClientRequest(int clientNum, String[] request) {
			switch (clientNum + request[0]) {
			case "1":
				break;
			case "2":
				break;
			case "1ready":
				info.sInfo.p1State = "ready";
				break;
			case "2ready":
				info.sInfo.p2State = "ready";
				break;
			case "1makeRoom":
				makeRoom();
				break;
			case "1code":
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

					try {
						Thread.sleep(1);
					} catch (Exception e) {}
					
					info.cInfo = (ClientInfo) ois.readObject();

					ProcessClientRequest(clientNum, info.cInfo.request);
					
					oos.writeObject(info.sInfo);
					oos.flush();
					oos.reset();
					
					if(clientNum==1) {
						if(!info.sInfo.p1Request[0].equals("")) {
							info.sInfo.p1Request[0] = "";
							info.sInfo.p1Request[1] = "";
						}
					}else {
						if(!info.sInfo.p2Request[0].equals("")) {
							info.sInfo.p2Request[0] = "";
							info.sInfo.p2Request[1] = "";
						}
					}
					
				}
				sck.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}