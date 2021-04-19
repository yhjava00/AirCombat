package game;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
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

public class GameField extends JPanel implements ActionListener{
	
	private static final int MAP_HEIGHT = 700;
	private static final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	private final int BULLET_SPEED = 1;
	private final int CHARGING_SPEED = 5;
	private final int DELAY = 1;

	private Label end_label = new Label();
	private Button restart = new Button("restart");

	private Set<int[]> bulletSet;
	
	private Thread bulletThread;
	private Thread bulletChargingThread;
	private Thread playerMoveThread;
	
	private Image plane1 = new ImageIcon(GameField.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GameField.class.getResource("../image/plane2.png")).getImage();
	private Image p1Wave = new ImageIcon(GameField.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GameField.class.getResource("../image/p2Wave.png")).getImage();
	
	private int[] p1;
	private int[] p2;
	
	private int p1_gauge;
	private int p2_gauge;
	
	private int p1_HP;
	private int p2_HP;
	
	private String p1Move;
	private String p2Move;
	
	private boolean p1charging;
	private boolean p2charging;
	
	boolean end;
	
	private Timer timer;

	private void setLabelAndButton() {
		
		end_label.setBackground(Color.BLACK);
		end_label.setFont(new Font("Serif", Font.BOLD, 21));
		end_label.setForeground(Color.blue);
		end_label.setAlignment(Label.CENTER);
		end_label.setBounds(MAP_WIDTH/2-75, 50, 150, 75);
		
		restart.setBounds(MAP_WIDTH/2-37, 130, 75, 30);
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(end_label);
				remove(restart);
				startGame();
			}
		});
		
	}
	
	protected GameField() {
		
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        setLabelAndButton();
        startGame();
	}
	
	private void startGame() {

        bulletSet = new HashSet<>();
        
    	p1 = new int[] {250, MAP_HEIGHT-PLANE_HEIGHT-20};
    	p2 = new int[] {250, 0};
    	
    	p1_gauge = 0;
    	p2_gauge = 0;
    	
    	p1_HP = 50;
    	p2_HP = 50;
    	
    	p1Move = "stop";
    	p2Move = "stop";
    	
    	p1charging = false;
    	p2charging = false;
    	
        end = false;

        timer = new Timer(DELAY, this);
		bulletThread = new BulletThread();
		bulletChargingThread = new BulletChargingThread();
		playerMoveThread = new PlayerMoveThread();
		
        timer.start();
        playerMoveThread.start();
        bulletThread.start();
        bulletChargingThread.start();

	}
	
	private void createBullet(int direction) {
		if(direction==0) {
			bulletSet.add(new int[] {p1[0], MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20, 1});			
		}else {
			bulletSet.add(new int[] {p2[0], PLANE_HEIGHT, 2});
		}
	}
		
	private boolean checkEnd() {
		if(p1_HP<=0) {
			end_label.setText("PLAYER2 WIN");
			return true;
		}
		if(p2_HP<=0) {
			end_label.setText("PLAYER1 WIN");			
			return true;
		}
		return false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(plane1, p1[0]-(PLANE_WIDTH/2), p1[1], null);
		g.drawImage(plane2, p2[0]-(PLANE_WIDTH/2), p2[1], null);
		
		g.setColor(Color.yellow);
		g.fillRect(p1[0]-(PLANE_WIDTH/2), p1[1]+37, p1_gauge, 10);
		g.fillRect(p2[0]-(PLANE_WIDTH/2), p2[1]+37, p2_gauge, 10);

		g.setColor(Color.RED);
		g.fillRect(p1[0]-(PLANE_WIDTH/2), p1[1]+47 , p1_HP , 10);
		g.fillRect(p2[0]-(PLANE_WIDTH/2), p2[1]+47 , p2_HP , 10);
		
		for(int[] bullet : bulletSet) {
			if(bullet[2]==1) {
				g.drawImage(p1Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}else if(bullet[2]==2) {
				g.drawImage(p2Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(checkEnd()) {
			end = true;
			timer.stop();
			if(p1_HP==p2_HP) {
				end_label.setText("DRAW");
			}
			add(end_label);
			add(restart);
		}
		
		repaint();
	}
	
	class BulletThread extends Thread {
		@Override
		public void run() {
			
			while(!end) {
				
				try {
					Thread.sleep(BULLET_SPEED);
				} catch (Exception e) {}

				Iterator<int[]> iter = bulletSet.iterator();
				
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
					
					if(bullet[1]<(PLANE_HEIGHT)&&(bullet[0]>=p2[0]-(PLANE_WIDTH/2)&&bullet[0]<=p2[0]+(PLANE_WIDTH/2))) {
						iter.remove();
						p2_HP -= 10;
					}
					if(bullet[1]>(MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20)&&(bullet[0]>=p1[0]-25&&bullet[0]<=p1[0]+25)) {
						iter.remove();
						p1_HP -= 10;
					}
				}
			}	
		}
	}
	
	class BulletChargingThread extends Thread {
		@Override
		public void run() {

			while(!end) {
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
			while(!end) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				if(p2Move.equals("left")) {
					if(0<p2[0]-(PLANE_WIDTH/2))
						p2[0] -= 1;
				}else if(p2Move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > p2[0] + PLANE_WIDTH)
						p2[0] += 1;
				}
				
				if(p1Move.equals("left")) {
					if(0<p1[0]-(PLANE_WIDTH/2))
						p1[0] -= 1;
				}else if(p1Move.equals("right")) {
					if(MAP_WIDTH+(PLANE_WIDTH/2) > p1[0] + PLANE_WIDTH)
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
