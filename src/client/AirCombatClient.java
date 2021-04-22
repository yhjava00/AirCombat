package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import info.ClientInfo;
import info.ServerInfo;

public class AirCombatClient {
	
	private Socket sck;

	public GameInfo info;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private WaitingBoard waitingBoard;
	
	public AirCombatClient() {
		init();
	}

	private void ProcessServerRequest(String[] request) {
		switch (request[0]) {
		case "":
			break;
		case "end":
			info.cInfo.state = "end";
			break;
		case "code":
			waitingBoard.setCode(request[1]);
			new GameBoard(info);
			break;
		}
	}
	
	public void init() {
		try {
			sck = new Socket("localhost", 1234);
			System.out.println("Server Connect");			
			sck.setTcpNoDelay(true);
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			info = new GameInfo();
			
			info.sInfo = (ServerInfo)ois.readObject();
			info.cInfo = (ClientInfo)ois.readObject();
			
			waitingBoard = new WaitingBoard(info);
//			new GameBoard(info);
			
			info.cInfo.request = new String[] {"", ""};
			
			while(!info.sInfo.p1State.equals("exit")) {

				try {
					Thread.sleep(1);
				} catch (Exception e) {}

				oos.writeObject(info.cInfo);
				oos.flush();
				oos.reset();

				if(!info.cInfo.request[0].equals("")) {
					info.cInfo.request[0] = "";
				}
				
				info.sInfo = (ServerInfo)ois.readObject();
				
				ProcessServerRequest(info.sInfo.p1Request);
				
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
