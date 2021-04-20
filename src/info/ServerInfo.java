package info;

import java.io.Serializable;
import java.util.Set;

public class ServerInfo implements Serializable  {

	public Set<int[]> bulletSet;
	
	public int[] p1;
	public int[] p2;

	public int p1_gauge;
	public int p2_gauge;
	
	public int p1_HP;
	public int p2_HP;

	public boolean end;
}
