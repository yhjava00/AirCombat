package client;

import javax.swing.JFrame;

import panel.StartPanel;
import panel.WaitingPanel;

public class WaitingBoard extends JFrame {
	
	GameInfo info;
	
	StartPanel startPage;
	WaitingPanel waitingPage;
	
	public WaitingBoard(GameInfo info) {
		
		this.info = info;
		
		startPage = new StartPanel(this, info);
		waitingPage = new WaitingPanel(this, info);
		
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
		info.cInfo.request[0] = "makeRoom";
		startPage.setVisible(false);
		waitingPage.setVisible(true);
	}
	
	public void sendCode(String code) {
		info.cInfo.request[0] = "code";
		info.cInfo.request[1] = code;
		
		waitingPage.setCode(code);
		
		startPage.setVisible(false);
		waitingPage.setVisible(true);
	}
	
	public void setCode(String code) {
		waitingPage.setCode(code);
	}
	
}
