package server;

import java.util.HashSet;
import java.util.Iterator;

import info.ClientInfo;
import info.ServerInfo;

public class GameRunning {
	
	public ServerInfo sInfo;
	public ClientInfo c1Info;
	public ClientInfo c2Info;

	protected static final int MAP_HEIGHT = 700;
	protected static final int MAP_WIDTH = 500;
	
	protected final int PLANE_HEIGHT = 37;
	protected final int PLANE_WIDTH = 50;
	
	protected final int BULLET_HEIGHT = 22;
	protected final int BULLET_WIDTH = 25;
	
	protected final int BULLET_SPEED = 1;
	protected final int CHARGING_SPEED = 5;

	protected Thread checkEndThread;
	protected Thread bulletThread;
	protected Thread bulletChargingThread;
	protected Thread playerMoveThread;
	protected Thread bulletCreateThread;
	
	public GameRunning() {
		c1Info = new ClientInfo();
		c2Info = new ClientInfo();
	}

	protected void gameSetting() {

		sInfo = new ServerInfo(); 
		
		sInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		sInfo.p2 = new int[] {250, 0};
		
        sInfo.bulletSet = new HashSet<>();
        
    	sInfo.p1_gauge = 0;
    	sInfo.p2_gauge = 0;
    	
    	sInfo.p1_HP = 50;
    	sInfo.p2_HP = 50;
    	
    	c1Info.move = "stop";
    	c2Info.move = "stop";
    	
    	c1Info.charging = false;
    	c2Info.charging = false;
    	
    	c1Info.state = "";
    	c2Info.state = "";
    	
    	sInfo.end = false;
    	
    	sInfo.p1State = "";
    	sInfo.p2State = "";
    	
    	sInfo.p1Request = "";
    	sInfo.p2Request = "";
	}
	
	protected void gameStart() {
		checkEndThread = new CheckEndThread();
		bulletThread = new BulletThread();
		bulletChargingThread = new BulletChargingThread();
		playerMoveThread = new PlayerMoveThread();
		bulletCreateThread = new BulletCreateThread();
		
		checkEndThread.start();
        playerMoveThread.start();
        bulletThread.start();
        bulletChargingThread.start();
		bulletCreateThread.start();
	}

	private void createBullet(int direction) {
		if(direction==0) {
			sInfo.bulletSet.add(new int[] {sInfo.p1[0], MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1});			
		}else {
			sInfo.bulletSet.add(new int[] {sInfo.p2[0], PLANE_HEIGHT, 2});
		}
	}
		
	private boolean checkEnd() {
		if(sInfo.p1_HP<=0) {
			sInfo.end_msg = "PLAYER2 WIN!";
			return true;
		}
		if(sInfo.p2_HP<=0) {
			sInfo.end_msg = "PLAYER1 WIN!";
			return true;
		}
		return false;
	}

	class CheckEndThread extends Thread {
		@Override
		public void run() {
			while(!sInfo.end) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(checkEnd()) {
					if(sInfo.p1_HP==sInfo.p2_HP)
						sInfo.end_msg = "DRAW";
					sInfo.p1Request = "end";
					sInfo.p2Request = "end";
					sInfo.end = true;
				}
			}
		}
	}
	
	class BulletThread extends Thread {
		@Override
		public void run() {
			
			while(!sInfo.end) {
				
				try {
					Thread.sleep(BULLET_SPEED);
				} catch (Exception e) {}

				Iterator<int[]> iter = sInfo.bulletSet.iterator();
				
				while(iter.hasNext()) {
					int[] bullet = iter.next();
					
					if(bullet[2]==1) {
						bullet[1]--;
					}else if(bullet[2]==2) {
						bullet[1]++;
					}
					
					if(bullet[1]<0||bullet[1]>MAP_HEIGHT-BULLET_HEIGHT) {
						iter.remove();
					}
					
					if(bullet[1]<(PLANE_HEIGHT)&&(bullet[0]>=sInfo.p2[0]-(PLANE_WIDTH/2)&&bullet[0]<=sInfo.p2[0]+(PLANE_WIDTH/2))) {
						iter.remove();
						sInfo.p2_HP -= 10;
					}
					if(bullet[1]>(MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20)&&(bullet[0]>=sInfo.p1[0]-25&&bullet[0]<=sInfo.p1[0]+25)) {
						iter.remove();
						sInfo.p1_HP -= 10;
					}
				}
			}	
		}
	}
	
	class BulletCreateThread extends Thread {
		@Override
		public void run() {
			
			while(!sInfo.end) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(!c1Info.charging) {
					if(sInfo.p1_gauge==PLANE_WIDTH)
						createBullet(0);
					sInfo.p1_gauge = 0;
				}
				if(!c2Info.charging) {
					if(sInfo.p2_gauge==PLANE_WIDTH)
						createBullet(1);
					sInfo.p2_gauge = 0;
				}
			}
			
		}
	}
	
	class BulletChargingThread extends Thread {
		@Override
		public void run() {

			while(!sInfo.end) {
				try {
					Thread.sleep(CHARGING_SPEED);
				} catch (Exception e) {}
				
				if(c1Info.charging&&(sInfo.p1_gauge<PLANE_WIDTH)) {
					sInfo.p1_gauge++;
				}
				if(c2Info.charging&&(sInfo.p2_gauge<PLANE_WIDTH)) {
					sInfo.p2_gauge++;
				}
			}
			
		}
	}
	
	class PlayerMoveThread extends Thread {
		@Override
		public void run() {
			while(!sInfo.end) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(c2Info.move.equals("left")) {
					if(0<sInfo.p2[0]-(PLANE_WIDTH/2))
						sInfo.p2[0] -= 1;
				}else if(c2Info.move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > sInfo.p2[0] + PLANE_WIDTH)
						sInfo.p2[0] += 1;
				}
				
				if(c1Info.move.equals("left")) {
					if(0<sInfo.p1[0]-(PLANE_WIDTH/2))
						sInfo.p1[0] -= 1;
				}else if(c1Info.move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > sInfo.p1[0] + PLANE_WIDTH)
						sInfo.p1[0] += 1;
				}
			}
		}
	}
	
}
