package server;

import java.util.HashSet;
import java.util.Iterator;

import info.Client2Info;
import info.Client1Info;
import info.ServerInfo;

public class GameRunning {
	
	public static ServerInfo sInfo;
	public static Client1Info cInfo;
	public static Client2Info c2Info;

	protected static final int MAP_HEIGHT = 700;
	protected static final int MAP_WIDTH = 500;
	
	protected final int PLANE_HEIGHT = 37;
	protected final int PLANE_WIDTH = 50;
	
	protected final int BULLET_HEIGHT = 22;
	protected final int BULLET_WIDTH = 25;
	
	protected final int BULLET_SPEED = 1;
	protected final int CHARGING_SPEED = 5;

	protected Thread bulletThread;
	protected Thread bulletChargingThread;
	protected Thread playerMoveThread;
	protected Thread bulletCreateThread;

	public GameRunning() {
		
		sInfo = new ServerInfo(); 
		cInfo = new Client1Info();
		
		sInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		sInfo.p2 = new int[] {250, 0};
		
        sInfo.bulletSet = new HashSet<>();
        
    	sInfo.p1_gauge = 0;
    	sInfo.p2_gauge = 0;
    	
    	sInfo.p1_HP = 50;
    	sInfo.p2_HP = 50;
    	
    	cInfo.p1Move = "stop";
    	c2Info.p2Move = "stop";
    	
    	cInfo.p1charging = false;
    	c2Info.p2charging = false;
    	
    	sInfo.end = false;

		bulletThread = new BulletThread();
		bulletChargingThread = new BulletChargingThread();
		playerMoveThread = new PlayerMoveThread();
		bulletCreateThread = new BulletCreateThread();
		
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
				
				if(!cInfo.p1charging) {
					if(sInfo.p1_gauge==PLANE_WIDTH)
						createBullet(0);
					sInfo.p1_gauge = 0;
				}
				if(!c2Info.p2charging) {
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
				
				if(cInfo.p1charging&&(sInfo.p1_gauge<PLANE_WIDTH)) {
					sInfo.p1_gauge++;
				}
				if(c2Info.p2charging&&(sInfo.p2_gauge<PLANE_WIDTH)) {
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
				
				if(c2Info.p2Move.equals("left")) {
					if(0<sInfo.p2[0]-(PLANE_WIDTH/2))
						sInfo.p2[0] -= 1;
				}else if(c2Info.p2Move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > sInfo.p2[0] + PLANE_WIDTH)
						sInfo.p2[0] += 1;
				}
				
				if(cInfo.p1Move.equals("left")) {
					if(0<sInfo.p1[0]-(PLANE_WIDTH/2))
						sInfo.p1[0] -= 1;
				}else if(cInfo.p1Move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > sInfo.p1[0] + PLANE_WIDTH)
						sInfo.p1[0] += 1;
				}
			}
		}
	}
	
}
