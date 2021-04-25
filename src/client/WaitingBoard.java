package client;

import java.util.Map;
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
		
		startPage.setVisible(false);
		waitingPage.setVisible(true);
	}
	
	public void sendCode(String code) {
		clientRequest.add("code");
		
		AirCombatClient.code = code;
		
		WaitingPanel.setCode(code);
		
		startPage.setVisible(false);
		waitingPage.setVisible(true);
	}
	
}
