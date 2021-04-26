package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.GameInfo;
import info.PlayerInfo;

public class ClientThread extends Thread {

	private Socket sck;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Set<String> clientRequest;
	protected Set<String> serverRequest;
	
	public Map<String, Object> info;
	
	public GameController gameController;

	private char[] codeSource = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z'
			};
	
	private String code = "";
	
	private int pNum = 0;
	
	public ClientThread(Socket sck) {
		this.sck = sck;
	}
	
	@Override
	public void run() {
		try {
			info = new HashMap<String, Object>();

			serverRequest = new HashSet<String>();
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			oos.writeObject(info);
			oos.flush();
			oos.reset();
			
			while(!info.containsKey("exit")) {

				Thread.sleep(1);
				
				info = (Map) ois.readObject();
				
				// 클라이언트 요청 처리
				processClientRequest();
				
				info.clear();
				
				// 서버의 요청 저장
				saveServerRequest();
				
				oos.writeObject(info);
				oos.flush();
				oos.reset();
				
			}
			sck.close();
		} catch (Exception e) {
//			e.printStackTrace();
			
			System.out.println("Client Disconnect");
			
			gameController.numOfPlayer--;
			
		}
	}
	

	private void processClientRequest() {
		
		if(info.isEmpty())
			return;
		
		clientRequest = info.keySet();
		
		for(String request : clientRequest) {
			switch (request) {
			case "makeRoom":
				makeRoom();
				break;
			case "joinRoom":
				joinRoom();
				break;
			case "i want p1":
				if(pNum==0&&!gameController.gameInfo.chooseP1) {
					pNum = 1;
					gameController.gameInfo.chooseP1 = true;
				}
				break;
			case "i want p2":
				if(pNum==0&&!gameController.gameInfo.chooseP2) {
					pNum = 2;
					gameController.gameInfo.chooseP2 = true;
				}
				break;
			case "gameOut":
				code = "";
				
				gameController.numOfPlayer--;
				
				pNum = 0;
				
				gameController = null;
				serverRequest.remove("gameInfo");
				break;
			case "pInfo":
				synchronized(gameController.p1Info) {
					if(pNum==1) {
						if(gameController.p1SendStart) {
							serverRequest.add("gameStart");
							gameController.p1SendStart = false;
						}
						if(gameController.p1SendEnd) {
							serverRequest.add("gameEnd");
							gameController.p1SendEnd = false;
						}
						gameController.p1Info = (PlayerInfo) info.get("pInfo");					
					}
					else {
						if(gameController.p2SendStart) {
							serverRequest.add("gameStart");
							gameController.p2SendStart = false;
						}
						if(gameController.p2SendEnd) {
							serverRequest.add("gameEnd");
							gameController.p2SendEnd = false;
						}
						gameController.p2Info = (PlayerInfo) info.get("pInfo");					
					}
				}
				serverRequest.add("gameInfo");
				break;
			}
		}
	}
	
	private void saveServerRequest() {
		
		for(String request : serverRequest) {
			switch (request) {
			case "roomIn":
				info.put("roomIn", code);
				break;
			case "gameInfo":
				synchronized(gameController.gameInfo) {
					info.put("gameInfo", GameInfo.copy(gameController.gameInfo));
				}
				break;
			case "gameStart":
				info.put("gameStart", null);
				break;
			case "gameEnd":
				pNum = 0;
				info.put("gameEnd", null);
				break;
			}
		}
		serverRequest.clear();
	}
	
	private void makeRoom() {
		
		serverRequest.add("gameInfo");
		serverRequest.add("roomIn");

		makeCode();
		
		gameController = new GameController(code);
		
		gameController.gameInfo.msg = "AIR COMBAT";
		
		gameController.start();
		
		AirCombatServer.gameMap.put(code, gameController);
	}
	
	private void joinRoom() {

		code = (String) info.get("joinRoom");
		
		if(!AirCombatServer.gameMap.containsKey(code)) 
			return;
		
		gameController = AirCombatServer.gameMap.get(code);
		
		if(gameController.numOfPlayer>=2) {
			return;
		}
		
		gameController.numOfPlayer++;
		
		gameController.gameInfo.msg = "AIR COMBAT";
		
		serverRequest.add("gameInfo");
		serverRequest.add("roomIn");
	}

	private void makeCode() {
		
		code = "";
		
		for(int i=0; i<4; i++) {
//			code += codeSource[(int)(Math.random()*codeSource.length)];
			code += codeSource[(int)(Math.random()*10)];
		}
		
		if(AirCombatServer.gameMap.containsKey(code))
			makeCode();
		
	}
	
	
}