package server;

import info.GameInfo;
import info.PlayerInfo;

public class GameController extends Thread {
	
	private String code;
	
	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 31;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	private final int ITEM_HEIGHT = 30;
	private final int ITEM_WIDTH = 30;
	
	private final int CHARGING_SPEED = 5;
	
	private int p1_gauge_tick = 0;
	private int p2_gauge_tick = 0;
	
	private int ChargingTick = 0;
	private int wallTick = 0;
	private int itemTick = 0; // 추가
	
	private int p1LV2Stack = 0;
	private int p2LV2Stack = 0;
	
	public GameInfo gameInfo;
	
	public PlayerInfo p1Info;
	public PlayerInfo p2Info;
	
	protected boolean p1SendSelectLV;
	protected boolean p2SendSelectLV;
	
	protected boolean p1SendStart;
	protected boolean p2SendStart;

	protected boolean p1SendEnd;
	protected boolean p2SendEnd;
	
	protected boolean selectLV;
	
	protected boolean opponentOut;
	
	protected int numOfPlayer;
	
	private boolean inGame;

	public int wall_speed = 5;
	
	private Thread BulletLV2Thread;
	
	public GameController(String code) {
		
		this.code = code;
		
		p1SendSelectLV = false;
		p2SendSelectLV = false;
		
		p1SendStart = false;
		p2SendStart = false;

		p1SendEnd = false;
		p2SendEnd = false;
		
		selectLV = false;
		
		opponentOut = false;
		
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
				p1SendSelectLV = true;
				p2SendSelectLV = true;
				
			}
			
			if(selectLV) {
				inGame = true;
				
				p1SendStart = true;
				p2SendStart = true;
				
				BulletLV2Thread.start();
				selectLV = false;
			}
			
			while(inGame) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				playerMove();
				itemMove();
				checkBulletCharging();
				checkBulletCreate();
				bulletMoveAndCheckHit();
				wallMove();
				collectBoom();
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
		
		gameInfo.bulletSet = new int[100][5];
		gameInfo.itemBox = new int[30][6];
		
		gameInfo.p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
		gameInfo.p2 = new int[] {250, 0};
		
		gameInfo.p1Move = "stop";
		gameInfo.p2Move = "stop";
		
		gameInfo.p1_gauge = 0;
		gameInfo.p2_gauge = 0;
		
		gameInfo.p1_gauge_lv = 0;
		gameInfo.p2_gauge_lv = 0;
		
		gameInfo.p1_HP = 50;
		gameInfo.p2_HP = 50;
		
		gameInfo.p1Super = 0;
		gameInfo.p2Super = 0;
		
		gameInfo.wall = new int[3][4]; // 추가
		
		gameInfo.msg = "";
		
		gameInfo.chooseP1 = false;
		gameInfo.chooseP2 = false;

		gameInfo.boom = new int[10][3]; // 추가
		
		p1LV2Stack = 0;
		p2LV2Stack = 0;
		
		inGame = false;
		
		ChargingTick = 0;
		wallTick = 0;
		
		for(int i=0; i<gameInfo.wall.length; i++) {
			createWall(i);
		}
		
		BulletLV2Thread = new BulletLV2Thread();
	}
	
	private void playerMove() {
		
		gameInfo.p1Move = p1Info.move;
		gameInfo.p2Move = p2Info.move;
		
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
	
	// 추가
	private void itemMove() {
		if(itemTick < 3) {
			itemTick++;
			return;
		}
		itemTick = 0;
		
		for(int i=0; i<gameInfo.itemBox.length; i++) {
			if(gameInfo.itemBox[i][4]==0)
				continue;
			
			switch(gameInfo.itemBox[i][3]) {
			case 0:
				gameInfo.itemBox[i][0] -= gameInfo.itemBox[i][5];
				gameInfo.itemBox[i][1] -= gameInfo.itemBox[i][5];
				if(gameInfo.itemBox[i][0] <= 0) {
					gameInfo.itemBox[i][3] = 1;
				}else if(gameInfo.itemBox[i][1] <= 0) {
					gameInfo.itemBox[i][3] = 3;
				}
				break;
			case 1:
				gameInfo.itemBox[i][0] += gameInfo.itemBox[i][5];
				gameInfo.itemBox[i][1] -= gameInfo.itemBox[i][5];
				if(gameInfo.itemBox[i][1] <= 0) {
					gameInfo.itemBox[i][3] = 2;
				}else if(gameInfo.itemBox[i][0] >= MAP_WIDTH-ITEM_WIDTH) {
					gameInfo.itemBox[i][3] = 0;
				}
				break;
			case 2:
				gameInfo.itemBox[i][0] += gameInfo.itemBox[i][5];
				gameInfo.itemBox[i][1] += gameInfo.itemBox[i][5];
				if(gameInfo.itemBox[i][0] >= MAP_WIDTH-ITEM_WIDTH) {
					gameInfo.itemBox[i][3] = 3;
				}else if(gameInfo.itemBox[i][1] >= MAP_HEIGHT-ITEM_HEIGHT) {
					gameInfo.itemBox[i][3] = 1;
				}
				break;
			case 3:
				gameInfo.itemBox[i][0] -= gameInfo.itemBox[i][5];
				gameInfo.itemBox[i][1] += gameInfo.itemBox[i][5];
				if(gameInfo.itemBox[i][1] >= MAP_HEIGHT-ITEM_HEIGHT) {
					gameInfo.itemBox[i][3] = 0;
				}else if(gameInfo.itemBox[i][0] <= 0) {
					gameInfo.itemBox[i][3] = 2;
				}
				break;
			}
			
			if((int)(Math.random()*500)==0) {
				gameInfo.itemBox[i][2] = (int)((Math.random()*3));
			}
			
			if((gameInfo.itemBox[i][0] + ITEM_WIDTH >= gameInfo.p1[0]-(PLANE_WIDTH/2) && gameInfo.itemBox[i][0] <= gameInfo.p1[0]+(PLANE_WIDTH/2)) 
				&& (gameInfo.itemBox[i][1] + ITEM_HEIGHT >= gameInfo.p1[1] && gameInfo.itemBox[i][1] <= gameInfo.p1[1]+PLANE_HEIGHT)) {
				gameInfo.itemBox[i][4] = 0;
				if(gameInfo.itemBox[i][2]==1 && gameInfo.p1_HP != 50) {
					gameInfo.p1_HP += 10;
				}else if(gameInfo.itemBox[i][2]==0) {
					gameInfo.p1_HP -= 10;
				}else if(gameInfo.itemBox[i][2]==2) {
					gameInfo.p1Super += 10;
				}
			}
			
			if((gameInfo.itemBox[i][0]+ITEM_WIDTH >= gameInfo.p2[0]-(PLANE_WIDTH/2) && gameInfo.itemBox[i][0] <= gameInfo.p2[0]+(PLANE_WIDTH/2)) 
					&& (gameInfo.itemBox[i][1] >= gameInfo.p2[1] && gameInfo.itemBox[i][1] <= gameInfo.p2[1]+PLANE_HEIGHT)) {
				gameInfo.itemBox[i][4] = 0;
				if(gameInfo.itemBox[i][2]==1 && gameInfo.p2_HP != 50) {
					gameInfo.p2_HP += 10;
				}else if(gameInfo.itemBox[i][2]==0) {
					gameInfo.p2_HP -= 10;
				}else if(gameInfo.itemBox[i][2]==2) {
					gameInfo.p2Super += 10;
				}
			}
			
		}
	}
	
	private void checkBulletCharging() {
		
		if(ChargingTick < CHARGING_SPEED) {
			ChargingTick++;
			return;
		}
		
		ChargingTick = 0;
		
		if(p1Info.charging) {
			if(gameInfo.p1_gauge<PLANE_WIDTH) {
				gameInfo.p1_gauge++;				
			}else if(gameInfo.p1_gauge_lv<2) {
				if(p1_gauge_tick<50) {
					p1_gauge_tick++;
				}else {
					p1_gauge_tick = 0;
					gameInfo.p1_gauge = 0;
					gameInfo.p1_gauge_lv++;					
				}
			}
		}
		if(p2Info.charging) {
			if(gameInfo.p2_gauge<PLANE_WIDTH) {
				gameInfo.p2_gauge++;				
			}else if(gameInfo.p2_gauge_lv<2) {
				if(p2_gauge_tick<50) {
					p2_gauge_tick++;
				}else {
					p2_gauge_tick = 0;
					gameInfo.p2_gauge = 0;
					gameInfo.p2_gauge_lv++;					
				}
			}
		}
	}
	
	private void checkBulletCreate() {

		if(!p1Info.charging) {
			
			if(gameInfo.p1_gauge==PLANE_WIDTH)
				createBullet(1, gameInfo.p1_gauge_lv);
			else if(gameInfo.p1_gauge!=0) {
				if(gameInfo.p1Super>0) {
					gameInfo.p1Super--;
					createBullet(1, 1);
				}
				else
					createBullet(1, gameInfo.p1_gauge_lv-1);
			}
			
			gameInfo.p1_gauge = 0;
			p1_gauge_tick = 0;
			gameInfo.p1_gauge_lv = 0;
		}
		if(!p2Info.charging) {

			if(gameInfo.p2_gauge==PLANE_WIDTH)
				createBullet(2, gameInfo.p2_gauge_lv);
			else if(gameInfo.p2_gauge!=0) {
				if(gameInfo.p2Super>0) {
					gameInfo.p2Super--;
					createBullet(2, 1);
				}
				else
					createBullet(2, gameInfo.p2_gauge_lv-1);					
			}
			
			gameInfo.p2_gauge = 0;
			p2_gauge_tick = 0;
			gameInfo.p2_gauge_lv = 0;
		}
	}
	
	private void bulletMoveAndCheckHit() {
		
		for(int i=0; i<gameInfo.bulletSet.length; i++) {
			int[] bullet = gameInfo.bulletSet[i];
			
			if(bullet[4]==0)
				continue;

			if(bullet[2]==1) {
				bullet[1]--;
			}else if(bullet[2]==2) {
				bullet[1]++;
			}

			if(bullet[1]<0||bullet[1]>MAP_HEIGHT-BULLET_HEIGHT) {
				bullet[4] = 0;
			}

			if(bullet[1]<(PLANE_HEIGHT)&&(bullet[0]>=gameInfo.p2[0]-(PLANE_WIDTH/2)&&bullet[0]<=gameInfo.p2[0]+(PLANE_WIDTH/2))) {
				bullet[4] = 0;
				createBoom(bullet, 2);
				createItem();
				if(gameInfo.p2_HP>0)
					gameInfo.p2_HP -= 10;
			}
			if(bullet[1]>(MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20)&&(bullet[0]>=gameInfo.p1[0]-(PLANE_WIDTH/2)&&bullet[0]<=gameInfo.p1[0]+(PLANE_WIDTH/2))) {
				bullet[4] = 0;
				createBoom(bullet, 1);
				createItem();
				if(gameInfo.p1_HP>0)
					gameInfo.p1_HP -= 10;
			}

			// 추가
			for(int j=0; j<gameInfo.wall.length; j++) {
				if ((bullet[0] >= gameInfo.wall[j][0]) && (bullet[0] <= (gameInfo.wall[j][0] + 120))&&bullet[3]<0) { // 총알이 벽1에 막혔을때 총알 삭제
					if (bullet[1] == gameInfo.wall[j][1] + 12)// 아래쪽 플레이어 총알이 벽에 막혔을때 총알 삭제
						bullet[2]=2;
					if (bullet[1] == gameInfo.wall[j][1] - 15)// 위쪽 플레이어 총알이 벽에 막혔을때 총알 삭제
						bullet[2]=1;
				}						
			}
		}
	}

	// 추가
	private void wallMove() {
		
		if(wallTick<wall_speed) {
			wallTick++;
			return;
		}
		
		wallTick = 0;
		
		for(int i=0; i<gameInfo.wall.length; i++) {
			if (gameInfo.wall[i][2] == 0) {
				gameInfo.wall[i][0] += gameInfo.wall[i][3];
				if (gameInfo.wall[i][0] > MAP_WIDTH) {
					createWall(i);
				}
			}else {
				gameInfo.wall[i][0] -= gameInfo.wall[i][3];
				if (gameInfo.wall[i][0] < -140) {
					createWall(i);
				}
			}
		}
	}
	
	private void collectBoom() {
		for(int i=0; i<gameInfo.boom.length; i++) {
			if(gameInfo.boom[i][2]>0) {
				gameInfo.boom[i][2]--;
			}
		}
	}
	
	private void createBullet(int direction, int lv) {
		
		if(direction==1) {
			if(lv==2)
				p1LV2Stack = 6;
			else
				addBulletSet(new int[] {gameInfo.p1[0], MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1, lv, 1});
		}else {
			if(lv==2)
				p2LV2Stack = 6;
			else
				addBulletSet(new int[] {gameInfo.p2[0], PLANE_HEIGHT, 2, lv, 1});
		}
	}
	
	private void addBulletSet(int[] bullet) {
		for(int i=0; i<gameInfo.bulletSet.length; i++) {
			if(gameInfo.bulletSet[i][4]==0) {
				gameInfo.bulletSet[i] = bullet;
				break;
			}
		}
	}

	private void createItem() {
		
		int n = (int)((Math.random()*4));
		
		while(n-->0) {
			;
			
			for(int idx=0;idx<gameInfo.itemBox.length; idx++) {
				
				if(gameInfo.itemBox[idx][4]!=0) {
					continue;
				}
				
				gameInfo.itemBox[idx][0] = (int)((Math.random()*100)+200);
				gameInfo.itemBox[idx][1] = (int)((Math.random()*100)+200);
				gameInfo.itemBox[idx][2] = (int)((Math.random()*3));
				gameInfo.itemBox[idx][3] = (int)((Math.random()*4));
				gameInfo.itemBox[idx][4] = 1;
				gameInfo.itemBox[idx][5] = (int)((Math.random()*3)+1);
				
				break;
			}
		}
	}
	
	// 추가
	private void createWall(int w1) {
		gameInfo.wall[w1][2] = (int)(Math.random()*2);
		
		if(gameInfo.wall[w1][2] == 0) {
			gameInfo.wall[w1][0] = (int)((Math.random()* -200) - 50);
		}else {
			gameInfo.wall[w1][0] = (int)((Math.random()* 100) + 700);
		}
		
		while(true) {
			
			gameInfo.wall[w1][1] = (int) ((Math.random() * 300) + 200); 
			
			int i=0;
			for(; i<gameInfo.wall.length; i++) {
				if(i==w1)
					continue;
				if((gameInfo.wall[w1][1] >= (gameInfo.wall[i][1] - 10)) && (gameInfo.wall[w1][1] <= (gameInfo.wall[i][1] + 30)))
						break;
			}
			
			if(i==gameInfo.wall.length)
				break;
		}
		
		gameInfo.wall[w1][3] = (int)(Math.random()*5)+1;
	}
	
	private void createBoom(int[] bullet, int p) {
		
		int n = p==1?10:-10;
		
		for(int i=0; i<gameInfo.boom.length; i++) {
			if(gameInfo.boom[i][2]<=0) {
				gameInfo.boom[i][0] = bullet[0];
				gameInfo.boom[i][1] = bullet[1] + n;
				gameInfo.boom[i][2] = 100;
				break;
			}
		}
	}
	
	protected void moreWall(int n) {
		gameInfo.wall = new int[n][4];
		
		for(int i=0; i<gameInfo.wall.length; i++) {
			createWall(i);
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
			gameInfo.msg = "p2 win";
			return true;
		}
		if(gameInfo.p2_HP<=0) {
			gameInfo.msg = "p1 win";
			return true;
		}
		return false;
	}
	
	class BulletLV2Thread extends Thread {
		
		@Override
		public void run() {
			
			while(inGame) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {}
				if(p1LV2Stack>0) {
					p1LV2Stack--;
					synchronized(gameInfo.bulletSet) {
						addBulletSet(new int[] {gameInfo.p1[0]+PLANE_WIDTH/2, MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1, 2, 1});
						addBulletSet(new int[] {gameInfo.p1[0]-PLANE_WIDTH/2, MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1, 2, 1});						
					}
				}
				if(p2LV2Stack>0) {
					p2LV2Stack--;
					synchronized(gameInfo.bulletSet) {
						addBulletSet(new int[] {gameInfo.p2[0]+PLANE_WIDTH/2, PLANE_HEIGHT, 2, 2, 1});
						addBulletSet(new int[] {gameInfo.p2[0]-PLANE_WIDTH/2, PLANE_HEIGHT, 2, 2, 1});
					}
				}
			}
		}
	}
	
}
