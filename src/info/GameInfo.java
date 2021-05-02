package info;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GameInfo implements Serializable {

	public int[][] bulletSet;
	public int[][] itemBox;
	
	public int[] p1;
	public int[] p2;
	
	public String p1Move;
	public String p2Move;
	
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
	
}
