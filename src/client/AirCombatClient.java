package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import info.ClientInfo;
import info.ServerInfo;

public class AirCombatClient {
	
	private Socket sck;
	
	protected GameField field;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public AirCombatClient() {
		init();
	}
	
	public void init() {
		try {
			sck = new Socket("localhost", 1234);
			System.out.println("Server Connect");			
			sck.setTcpNoDelay(true);
			field = new GameField();
			
			oos = new ObjectOutputStream(sck.getOutputStream());
			ois = new ObjectInputStream(sck.getInputStream());
			
			field.sInfo = (ServerInfo)ois.readObject();
			field.cInfo = (ClientInfo)ois.readObject();
			
			new Board(field);
			field.fieldStart();
			
			field.cInfo.request = "ready";
			
			while(!field.sInfo.p1State.equals("exit")) {

				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				oos.writeObject(field.cInfo);
				oos.flush();
				oos.reset();
				
				if(!field.cInfo.request.equals("")) {
					field.cInfo.request = "";
				}
				
				field.sInfo = (ServerInfo)ois.readObject();
				
				if(field.sInfo.p1Request.equals("end")) {
					field.cInfo.state = "end";
				}
				
			}
			sck.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
