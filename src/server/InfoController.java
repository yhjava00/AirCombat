package server;

import java.util.HashSet;

import info.ClientInfo;
import info.ServerInfo;

public class InfoController {
	
	public ServerInfo sInfo;
	public ClientInfo cInfo;

	protected final int MAP_HEIGHT = 700;
	protected final int MAP_WIDTH = 500;
	
	protected final int PLANE_HEIGHT = 37;
	protected final int PLANE_WIDTH = 50;
	
	protected final int BULLET_HEIGHT = 22;
	protected final int BULLET_WIDTH = 25;
	
	protected final int BULLET_SPEED = 1;
	protected final int CHARGING_SPEED = 5;

	public InfoController() {
		cInfo = new ClientInfo();
	}

	protected void Setting() {

		sInfo = new ServerInfo(); 
		
		sInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		sInfo.p2 = new int[] {250, 0};
		
        sInfo.bulletSet = new HashSet<>();
        
    	sInfo.p1_gauge = 0;
    	sInfo.p2_gauge = 0;
    	
    	sInfo.p1_HP = 50;
    	sInfo.p2_HP = 50;
    	
    	cInfo.move = "stop";
    	
    	cInfo.charging = false;
    	
    	cInfo.state = "";
    	
    	sInfo.p1State = "";
    	sInfo.p2State = "";

    	sInfo.request = new String[] {"", ""};
	}
	
	
}
