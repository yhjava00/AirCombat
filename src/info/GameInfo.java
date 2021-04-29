package info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GameInfo implements Serializable {

	public int[][] bulletSet;
	
	public int[] p1;
	public int[] p2;
	
	public int p1_gauge;
	public int p2_gauge;

	public int p1_gauge_lv;
	public int p2_gauge_lv;
	
	public int p1_HP;
	public int p2_HP;

	public int[][] wall; // 추가
	
	public String msg;
	
	public boolean chooseP1;
	public boolean chooseP2;

	public int boom[][]; // 추가

	public static GameInfo copy(GameInfo info) {
		
		GameInfo newInfo = new GameInfo();
		
		newInfo.bulletSet = new int[100][5];
		
		for(int i=0; i<info.bulletSet.length; i++) {
			newInfo.bulletSet[i] = info.bulletSet[i].clone();
		}
		
		newInfo.p1 = info.p1;
		newInfo.p2 = info.p2;
		
		newInfo.p1_gauge = info.p1_gauge;
		newInfo.p2_gauge = info.p2_gauge;
		
		newInfo.p1_gauge_lv = info.p1_gauge_lv;
		newInfo.p2_gauge_lv = info.p2_gauge_lv;
		
		newInfo.p1_HP = info.p1_HP;
		newInfo.p2_HP = info.p2_HP;
		
		newInfo.wall = info.wall;
		
		newInfo.msg = info.msg;
		
		newInfo.chooseP1 = info.chooseP1;
		newInfo.chooseP2 = info.chooseP2;
		
		newInfo.boom = info.boom;

		return newInfo;
	}
	
}
