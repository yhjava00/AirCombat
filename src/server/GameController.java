package server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import info.GameInfo;
import info.PlayerInfo;

public class GameController extends Thread {
	
	private String code;
	
	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	private final int WALL_SPEED = 5;
	private final int CHARGING_SPEED = 1;

	private int ChargingTick = 0;
	private int wallTick = 0;
	
	public GameInfo gameInfo;
	
	public PlayerInfo p1Info;
	public PlayerInfo p2Info;
	
	private int[] rd_x; // 추가
	private int[] check_y; // 추가
	
	protected boolean p1SendStart;
	protected boolean p2SendStart;

	protected boolean p1SendEnd;
	protected boolean p2SendEnd;
	
	protected int numOfPlayer;
	
	private boolean inGame;
	
	public GameController(String code) {
		
		this.code = code;
		
		p1SendStart = false;
		p2SendStart = false;

		p1SendEnd = false;
		p2SendEnd = false;
		
		numOfPlayer = 1;
		
		gameSetting();
	}
	
	@Override
	public void run() {
		
		while(numOfPlayer>0) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {}
			
			if(gameInfo.chooseP1&&gameInfo.chooseP2) {
				gameSetting();

				p1SendStart = true;
				p2SendStart = true;
				
				inGame = true;
			}
			
			while(inGame) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				playerMove();
				checkBulletCharging();
				checkBulletCreate();
				bulletMoveAndCheckHit();
				wallMove();
				if(checkEnd()) {
					inGame = false;
					
					p1SendEnd = true;
					p2SendEnd = true;
				}
			}
		}
		
		synchronized(AirCombatServer.gameMap) {
			AirCombatServer.gameMap.remove(code);
		}
	}
	
	protected void gameSetting() {
		
		gameInfo = new GameInfo();
		
		p1Info = new PlayerInfo();
		p2Info = new PlayerInfo();

		p1Info.move = "stop";
		p1Info.charging = false;
		
		p2Info.move = "stop";
		p2Info.charging = false;
		
		gameInfo.bulletSet = new HashSet<int[]>();
		
		gameInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		gameInfo.p2 = new int[] {250, 0};
		
		gameInfo.p1_gauge = 0;
		gameInfo.p2_gauge = 0;
		
		gameInfo.p1_HP = 50;
		gameInfo.p2_HP = 50;

		rd_x = new int[3]; // 추가			
		check_y = new int[3]; // 추가	
		gameInfo.wall = new int[3][2]; // 추가
		
		gameInfo.msg = "";
		
		gameInfo.chooseP1 = false;
		gameInfo.chooseP2 = false;
		
		inGame = false;
		
		ChargingTick = 0;
		wallTick = 0;
		
		createWall(0); // 추가
		createWall(1); // 추가
		createWall(2); // 추가
		
	}
	
	private void playerMove() {
		
		if(p2Info.move.equals("left") &&
				0 < gameInfo.p2[0] - (PLANE_WIDTH/2)) {
			gameInfo.p2[0]--;
		}else if(p2Info.move.equals("right") &&
				MAP_WIDTH + (PLANE_WIDTH/2) > gameInfo.p2[0] + PLANE_WIDTH) {
			gameInfo.p2[0]++;
		}
		
		if(p1Info.move.equals("left") &&
				0 < gameInfo.p1[0] - (PLANE_WIDTH/2)) {
			gameInfo.p1[0]--;
		}else if(p1Info.move.equals("right") &&
				MAP_WIDTH + (PLANE_WIDTH/2) > gameInfo.p1[0] + PLANE_WIDTH) {
			gameInfo.p1[0]++;
		}
	}
	
	private void checkBulletCharging() {
		
		if(ChargingTick < CHARGING_SPEED) {
			ChargingTick++;
			return;
		}
		
		ChargingTick = 0;
		
		if(p1Info.charging&&(gameInfo.p1_gauge<PLANE_WIDTH)) {
			gameInfo.p1_gauge++;
		}
		if(p2Info.charging&&(gameInfo.p2_gauge<PLANE_WIDTH)) {
			gameInfo.p2_gauge++;
		}
	}
	
	private void checkBulletCreate() {

		if(!p1Info.charging) {
			if(gameInfo.p1_gauge==PLANE_WIDTH)
				createBullet(0);
			gameInfo.p1_gauge = 0;
		}
		if(!p2Info.charging) {
			if(gameInfo.p2_gauge==PLANE_WIDTH)
				createBullet(1);
			gameInfo.p2_gauge = 0;
		}
	}
	
	private void bulletMoveAndCheckHit() {
		
		Iterator<int[]> iter = gameInfo.bulletSet.iterator();
		
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
			
			if(bullet[1]<(PLANE_HEIGHT)&&(bullet[0]>=gameInfo.p2[0]-(PLANE_WIDTH/2)&&bullet[0]<=gameInfo.p2[0]+(PLANE_WIDTH/2))) {
				iter.remove();
				gameInfo.p2_HP -= 10;
			}
			if(bullet[1]>(MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20)&&(bullet[0]>=gameInfo.p1[0]-25&&bullet[0]<=gameInfo.p1[0]+25)) {
				iter.remove();
				gameInfo.p1_HP -= 10;
			}
			
			// 추가
			for(int i=0; i<3; i++) {
				if ((bullet[0] >= gameInfo.wall[i][0]) && (bullet[0] <= (gameInfo.wall[i][0] + 120))) { // 총알이 벽1에 막혔을때 총알 삭제
					if (bullet[1] == gameInfo.wall[i][1] + 12)// 아래쪽 플레이어 총알이 벽에 막혔을때 총알 삭제
						bullet[2]=2;
					if (bullet[1] == gameInfo.wall[i][1] - 15)// 위쪽 플레이어 총알이 벽에 막혔을때 총알 삭제
						bullet[2]=1;
				}						
			}
		}
	}

	// 추가
	private void wallMove() {
		
		if(wallTick<WALL_SPEED) {
			wallTick++;
			return;
		}
		
		wallTick = 0;
		
		for(int i=0; i<3; i++) {
			if (rd_x[i] == 0) {
				gameInfo.wall[i][0]++;
				if (gameInfo.wall[i][0] == MAP_WIDTH) {
					createWall(i);
				}
			}else {
				gameInfo.wall[i][0]--;
				if (gameInfo.wall[i][0] == -140) {
					createWall(i);
				}
			}
		}
	}
		
	private void createBullet(int direction) {
		if(direction==0) {
			gameInfo.bulletSet.add(new int[] {gameInfo.p1[0], MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1});			
		}else {
			gameInfo.bulletSet.add(new int[] {gameInfo.p2[0], PLANE_HEIGHT, 2});
		}
	}
		
	// 추가
	private void createWall(int w1) {//
		
		int w2, w3;
		
		w2 = (w1==0) ? 1 : 0;
		
		w3 = 3-w1-w2;
		
		Random rd = new Random();
		rd_x[w1] = rd.nextInt(2);
		if(rd_x[w1] == 0) {
			gameInfo.wall[w1][0] = (int)((Math.random()* -200) - 50);
		}else if(rd_x[w1] == 1) {
			gameInfo.wall[w1][0] = (int)((Math.random()* 100) + 700);
		}
		while (true) {
			check_y[w1] = (int) ((Math.random() * 300) + 200); 
			 if(!((check_y[w1] >= (gameInfo.wall[w2][1] - 10)) && (check_y[w1] <= (gameInfo.wall[w2][1] + 30))
				||((check_y[w1] >= (gameInfo.wall[w3][1] - 10)) && (check_y[w1] <= (gameInfo.wall[w3][1] + 30))))) {
				 gameInfo.wall[w1][1] = check_y[w1];
				break;
			}
		}
	}

	private boolean checkEnd() {
		
		if(numOfPlayer<2) {
			gameInfo.msg = "AIR COMBAT";
			return true;
		}	
			
		if(gameInfo.p1_HP<=0&&gameInfo.p2_HP<=0) {
			gameInfo.msg = "DRAW!";
			return true;	
		}
		if(gameInfo.p1_HP<=0) {
			gameInfo.msg = "PLAYER2 WIN!";
			return true;
		}
		if(gameInfo.p2_HP<=0) {
			gameInfo.msg = "PLAYER1 WIN!";
			return true;
		}
		return false;
	}
}
