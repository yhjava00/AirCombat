package client;

import java.util.Set;

import javax.swing.JFrame;

import panel.StartPanel;
import panel.WaitingPanel;

public class WaitingBoard extends JFrame {
	
	Set<String> clientRequest;
	
	StartPanel startPage;
	WaitingPanel waitingPage;
	
	public WaitingBoard(Set<String> clientRequest) {
		
		this.clientRequest = clientRequest;
		
		startPage = new StartPanel(this);
		waitingPage = new WaitingPanel(this);
		
		startPage.setVisible(true);
		waitingPage.setVisible(false);
		
		setSize(400,400);
		
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		
		add(startPage);
		add(waitingPage);
		
		setTitle("Air Combat");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void makeRoom() {
		clientRequest.add("makeRoom");
	}
	
	public void joinRoom(String code) {
		clientRequest.add("joinRoom");
		
		AirCombatClient.code = code;
	}
	
	public void gameOut() {
		clientRequest.add("gameOut");
		
		startPage.setVisible(true);
		waitingPage.setVisible(false);
	}
	
}
