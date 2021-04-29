package client;

import java.awt.Color;
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
	
	private Map<String, Object> info;
	
	protected static String code = "";
	
	private GameBoard gameBoard;
	private GamePanel gamePanel;
	
	private WaitingBoard waitingBoard;
	
	public AirCombatClient() {
		try {
			sck = new Socket("192.168.1.32", 1234);			
			sck.setTcpNoDelay(true);

			System.out.println("Server Connect");
			
			clientRequest = new HashSet<String>();
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			info = (Map)ois.readObject();
			
			gamePanel = new GamePanel(clientRequest);
			
			waitingBoard = new WaitingBoard(clientRequest);
			
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
			case "joinRoom":
				info.put("joinRoom", code);
				break;
			case "i want p1":
				info.put("i want p1", null);
				break;
			case "i want p2":
				info.put("i want p2", null);
				break;
			case "gameOut":
				gameBoard.dispose();
				gamePanel.gameStop();
				info.put("gameOut", null);
				break;
			case "pInfo":
				info.put("pInfo", gamePanel.pInfo);
				break;
			case "select lv1": // 추가	
				info.put("select", 1);
				break;
			case "select lv2": // 추가
				info.put("select", 2);
				break;
			case "select lv3": // 추가
				info.put("select", 3);
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
				
				waitingBoard.startPage.setVisible(false);
				waitingBoard.waitingPage.setVisible(true);

				gamePanel.addLabelAndButton();
				
				gameBoard = new GameBoard(gamePanel);
				
				gamePanel.gameStart();
				
				break;
			case "gameInfo":
				gamePanel.gameInfo = (GameInfo) info.get("gameInfo");
				if(gamePanel.gameInfo.chooseP1) {
					gamePanel.p1Btn.setBackground(Color.GREEN);
				}
				if(gamePanel.gameInfo.chooseP2) {
					gamePanel.p2Btn.setBackground(Color.GREEN);
				}
//				System.out.println(gamePanel.gameInfo.boom);
				clientRequest.add("pInfo");
				break;
			case "selectLV":
				gamePanel.removeLabelAndButton();
				gamePanel.addSelectLevelButton(); // 추가
				break;
			case "gameStart":
				gamePanel.removeSelectLevelButton(); // 추가
				break;
			case "gameEnd":
				gamePanel.addLabelAndButton();
				break;
			}
		}
	}
}
