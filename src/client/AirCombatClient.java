package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import info.GameInfo;
import panel.GamePanel;
import panel.WaitingPanel;

public class AirCombatClient {

	private Socket sck;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	protected Set<String> clientRequest;
	private Set<String> serverRequest;
	
	public Map<String, Object> info;
	
	protected static String code = "";
	
	public static GamePanel gamePanel;
	
	public AirCombatClient() {
		try {
			sck = new Socket("localhost", 1234);			
			sck.setTcpNoDelay(true);

			System.out.println("Server Connect");
			
			clientRequest = new HashSet<String>();
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			info = (Map)ois.readObject();
			
			gamePanel = new GamePanel();
			
			new WaitingBoard(clientRequest);
			
			while(!info.containsKey("exit")) {

				// 클라이언트의 요청 저장
				saveClientRequest();
				
				oos.writeObject(info);
				oos.flush();
				oos.reset();

				info = (Map)ois.readObject();
				
				// 서버의 요청 처리
				processServerRequest();
				
				info.clear();				
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveClientRequest() {
		
		for(String request : clientRequest) {
			switch (request) {
			case "makeRoom":
				info.put("makeRoom", null);
				break;
			case "code":
				info.put("code", code);
				break;
			case "pInfo":
				info.put("pInfo", gamePanel.pInfo);
				break;
			}
		}
		
		clientRequest.clear();
	}
	
	private void processServerRequest() {
		
		if(info.isEmpty())
			return;
		
		serverRequest = info.keySet();
		
		for(String request : serverRequest) {
			switch (request) {
			case "roomIn":
				WaitingPanel.setCode((String) info.get("roomIn"));
				new GameBoard(gamePanel);
				break;
			case "gameInfo":
				gamePanel.gameInfo = (GameInfo) info.get("gameInfo");
				gamePanel.repaint();
				clientRequest.add("pInfo");
				break;
			case "gameStart":
				gamePanel.pInfo.ready = false;
				break;
			case "gameEnd":
				gamePanel.addLabelAndButton();
				break;
			}
		}
	}
}
