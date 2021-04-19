package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameField3 extends JPanel implements ActionListener{
	
	private static final int MAP_HEIGHT = 700;
	private static final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	private final int BULLET_SIZE = 15;
	
	private final int BULLET_SPEED = 1;
	
	private final int CHARGING_SPEED = 5;
	
	private final int DELAY = 10;
	
	private Set<int[]> bulletSet;
	
	private Thread bulletThread = new BulletThread();
	private Thread bulletChargingThread = new BulletChargingThread();
	private Thread playerMoveThread = new PlayerMoveThread();
	
	private Image plane1 = new ImageIcon(GameField.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GameField.class.getResource("../image/plane2.png")).getImage();
	private Image p1Shot = new ImageIcon(GameField.class.getResource("../image/p1Shot.png")).getImage();
	private Image p1Wave = new ImageIcon(GameField.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Shot = new ImageIcon(GameField.class.getResource("../image/p2Shot.png")).getImage();
	private Image p2Wave = new ImageIcon(GameField.class.getResource("../image/p2Wave.png")).getImage();
	
	
	private int[] p1 = {225, MAP_HEIGHT-PLANE_HEIGHT-20};
	private int[] p2 = {225, 0};
	private int p1_gauge = 0;
	private int p2_gauge = 0;
	private int p1_HP = 50;
	private int p2_HP = 50;
	
	private String p1Move = "stop";
	private String p2Move = "stop";
	private boolean p1charging = false;
	private boolean p2charging = false;
	private Timer timer;
	
	protected GameField3() {
		
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        bulletSet = new HashSet<>();
        
        playerMoveThread.start();
        bulletThread.start();
        bulletChargingThread.start();
        
        timer = new Timer(DELAY, this);
        timer.start();
	}
	
	private void createBullet(int direction) {
		if(direction==0) {
			bulletSet.add(new int[] {p1[0] + (PLANE_WIDTH-BULLET_SIZE)/2, MAP_HEIGHT - PLANE_HEIGHT - 10, 0});			
		}else {
			bulletSet.add(new int[] {p2[0] + (PLANE_WIDTH-BULLET_SIZE)/2, PLANE_HEIGHT + 10, 1});
		}
	}
	
	private void removeBullet() {
		
		Iterator<int[]> iter = bulletSet.iterator();
		
		while(iter.hasNext()) {
			int[] bullet = iter.next();
			if(bullet[1]<0||bullet[1]>MAP_HEIGHT) {
				iter.remove();;
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics hp = this.getGraphics();
		
		g.setColor(Color.yellow);
		
		g.drawImage(plane1, p1[0], p1[1], null);
		g.fillRect(p1[0], p1[1]+37, p1_gauge, 10);
		
		g.drawImage(plane2, p2[0], p2[1], null);
		g.fillRect(p2[0], p2[1]+37, p2_gauge, 10);
		
		g.setColor(Color.RED);
		g.fillRect(p1[0], p1[1]+47 , p1_HP , 10);
		g.fillRect(p2[0], p2[1]+47 , p2_HP , 10);
		
		for(int[] bullet : bulletSet) {
			if(bullet[2]==0) {
				g.drawImage(p1Shot, bullet[0], bullet[1], null);
			}else {
				g.drawImage(p2Shot, bullet[0], bullet[1], null);
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		removeBullet();
		repaint();
	}
	
	class BulletThread extends Thread {
		@Override
		public void run() {
			
			while(true) {
				
				try {
					Thread.sleep(BULLET_SPEED);
				} catch (Exception e) {}
				
				for(int[] bullet : bulletSet) {
					if(bullet[2]==0) {
						bullet[1]--;
					}else {
						bullet[1]++;
					}
					
					if(bullet[1]<(PLANE_HEIGHT+10)&&(bullet[0]>=p2[0]+10&&bullet[0]<=p2[0]+40)) {
						p2_HP -= 10;
						System.out.println("p2 hit by p1");
					}
					if(bullet[1]>(MAP_HEIGHT - PLANE_HEIGHT - 10)&&(bullet[0]>=p1[0]+10&&bullet[0]<=p1[0]+40)) {
						p1_HP -= 10;
						System.out.println("p1 hit by p2");
					}
				}
			}	
		}
	}
	
	class BulletChargingThread extends Thread {
		@Override
		public void run() {

			while(true) {
				try {
					Thread.sleep(CHARGING_SPEED);
				} catch (Exception e) {}
				
				if(p1charging&&(p1_gauge<PLANE_WIDTH)) {
					p1_gauge++;
				}
				if(p2charging&&(p2_gauge<PLANE_WIDTH)) {
					p2_gauge++;
				}
			}
			
		}
	}
	
	class PlayerMoveThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(p2Move.equals("left")) {
					if(0<p2[0])
						p2[0] -= 1;
				}else if(p2Move.equals("right")) {
					if(MAP_WIDTH > p2[0] + PLANE_WIDTH)
						p2[0] += 1;
				}
				
				if(p1Move.equals("left")) {
					if(0<p1[0])
						p1[0] -= 1;
				}else if(p1Move.equals("right")) {
					if(MAP_WIDTH > p1[0] + PLANE_WIDTH)
						p1[0] += 1;
				}
			}
		}
	}
	
	class Plane1Adapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) { //키 입력
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_A:
				p2Move = "left";
				break;
			case KeyEvent.VK_D:
				p2Move = "right";
				break;
				case KeyEvent.VK_SPACE:
					p2charging = true;
				break;
			case KeyEvent.VK_LEFT:	
				p1Move = "left";			
				break;
			case KeyEvent.VK_RIGHT:
				p1Move = "right";	
				break;
			case KeyEvent.VK_NUMPAD0:
				p1charging = true;
            	break;            
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_A:
				p2Move = "stop";
				break;
			case KeyEvent.VK_D:
				p2Move = "stop";
				break;
			case KeyEvent.VK_SPACE:
				if(p2_gauge==PLANE_WIDTH)
					createBullet(1);
				p2_gauge = 0;
				p2charging = false;
				break;
			case KeyEvent.VK_LEFT:	
				p1Move = "stop";			
				break;
			case KeyEvent.VK_RIGHT:
				p1Move = "stop";	
				break;
            case KeyEvent.VK_NUMPAD0:
            	if(p1_gauge==PLANE_WIDTH)
					createBullet(0);
            	p1_gauge = 0;
            	p1charging = false;
            	break;
            }
		}
	}
}
