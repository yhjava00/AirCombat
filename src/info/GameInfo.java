package info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GameInfo implements Serializable {

	public Set<int[]> bulletSet;
	
	public int[] p1;
	public int[] p2;

	public int p1_gauge;
	public int p2_gauge;
	
	public int p1_HP;
	public int p2_HP;

	public int[][] wall; // 추가
	
	public String end_msg;
	
	public static GameInfo copy(GameInfo info) {
		
		GameInfo newInfo = new GameInfo();
		
		newInfo.bulletSet = new HashSet<int[]>();
		
		synchronized(info.bulletSet) {
			for(int[] bullet : info.bulletSet) {
				newInfo.bulletSet.add(bullet.clone());
			}
		}
		
		newInfo.p1 = info.p1;
		newInfo.p2 = info.p2;
		
		newInfo.p1_gauge = info.p1_gauge;
		newInfo.p2_gauge = info.p2_gauge;
		
		newInfo.p1_HP = info.p1_HP;
		newInfo.p2_HP = info.p2_HP;
		
		newInfo.wall = info.wall;
		
		newInfo.end_msg = info.end_msg;
		
		return newInfo;
	}
	
}
