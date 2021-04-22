package server;

import java.util.HashSet;
import java.util.Iterator;

import info.ClientInfo;
import info.ServerInfo;

public class GameController {
	
	public ServerInfo sInfo;
	public ClientInfo c1Info;
	public ClientInfo c2Info;

	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	private final int BULLET_SPEED = 1;
	private final int CHARGING_SPEED = 5;

	private Thread exchangeP1InfoThread;
	private Thread exchangeP2InfoThread;
	
	private Thread gameStartThread;
	private Thread checkEndThread;
	private Thread bulletThread;
	private Thread bulletChargingThread;
	private Thread playerMoveThread;
	private Thread bulletCreateThread;
	
	public GameController() {
		sInfo = new ServerInfo(); 
		
		gameStartThread = new GameStartThread();
		gameStartThread.start();
	}
	
	public void connectP1(InfoController p1Info) {
		
		p1Info.sInfo = sInfo;
		
		exchangeP1InfoThread = new ExchangeP1InfoThread(p1Info);
		exchangeP1InfoThread.start();
	}
	
	public void connectP2(InfoController p2Info) {
		
		p2Info.sInfo = sInfo;
		
		exchangeP2InfoThread = new ExchangeP2InfoThread(p2Info);
		exchangeP2InfoThread.start();
	}
	
	protected void gameSetting() {

		sInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		sInfo.p2 = new int[] {250, 0};
		
        sInfo.bulletSet = new HashSet<>();
        
    	sInfo.p1_gauge = 0;
    	sInfo.p2_gauge = 0;
    	
    	sInfo.p1_HP = 50;
    	sInfo.p2_HP = 50;
    	
    	sInfo.end = false;
    	
    	sInfo.p1State = "";
    	sInfo.p2State = "";

    	sInfo.p1Request = new String[] {"", ""};
    	sInfo.p2Request = new String[] {"", ""};
    	
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
	
	class ExchangeP1InfoThread extends Thread {
		
		InfoController p1Info;
		
		public ExchangeP1InfoThread(InfoController p1Info) {
			this.p1Info = p1Info;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				c1Info = p1Info.cInfo;
				p1Info.sInfo = sInfo;
				
			}
		}
	}
	class ExchangeP2InfoThread extends Thread {
		
		InfoController p2Info;
		
		public ExchangeP2InfoThread(InfoController p2Info) {
			this.p2Info = p2Info;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				c2Info = p2Info.cInfo;
				p2Info.sInfo = sInfo;
			}
		}
	}
	class GameStartThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(sInfo.p1State.equals("ready")&&sInfo.p2State.equals("ready")) {
					gameSetting();
					sInfo.p1State = "run";
					sInfo.p2State = "run";
					gameStart();
				}
			}
		}
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
					sInfo.p1Request[0] = "end";
					sInfo.p2Request[0] = "end";
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
