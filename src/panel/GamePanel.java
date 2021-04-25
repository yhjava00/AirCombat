package panel;

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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import info.GameInfo;
import info.PlayerInfo;

public class GamePanel extends JPanel{

	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	public Label end_label = new Label();
	private Button ready = new Button("ready");

	private Image plane1 = new ImageIcon(GamePanel.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GamePanel.class.getResource("../image/plane2.png")).getImage();
	private Image p1Wave = new ImageIcon(GamePanel.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GamePanel.class.getResource("../image/p2Wave.png")).getImage();
	// 추가
	private Image wallImg = new ImageIcon(GamePanel.class.getResource("../image/wall.png")).getImage();
	private Image backgroundImg = new ImageIcon(GamePanel.class.getResource("../image/background.png")).getImage();
	
	public GameInfo gameInfo;
	public PlayerInfo pInfo;

	private void setLabelAndButton() {
		
		end_label.setBackground(Color.lightGray);
		end_label.setFont(new Font("Serif", Font.BOLD, 21));
		end_label.setForeground(Color.black);
		end_label.setAlignment(Label.CENTER);
		end_label.setBounds(0, 30, MAP_WIDTH, 50);
		
		ready.setBounds(MAP_WIDTH/2-37, 130, 75, 30);
		ready.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(end_label);
				remove(ready);
				pInfo.ready = true;
			}
		});
		
	}
	
	public GamePanel() {
		
		setLayout(null);
        setFocusable(true);
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        setLabelAndButton();
        
        pInfo = new PlayerInfo();
        
        pInfo.move = "stop";
        pInfo.charging = false;
        pInfo.ready = false;
        
		add(ready);
	}
	
	public void addLabelAndButton() {
		
		end_label.setText(gameInfo.end_msg);
		
		add(end_label);
		add(ready);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(backgroundImg , 0 , 0 , null); // 추가
		
		g.drawImage(plane1, gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1], null);
		g.drawImage(plane2, gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1], null);
		
		g.setColor(Color.yellow);
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+37, gameInfo.p1_gauge, 10);
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+37, gameInfo.p2_gauge, 10);

		g.setColor(Color.RED);
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+47 , gameInfo.p1_HP , 10);
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+47 , gameInfo.p2_HP , 10);

		g.drawImage(wallImg, gameInfo.wall[0][0], gameInfo.wall[0][1], null); // 추가
		g.drawImage(wallImg, gameInfo.wall[1][0], gameInfo.wall[1][1], null); // 추가
		g.drawImage(wallImg, gameInfo.wall[2][0], gameInfo.wall[2][1], null); // 추가
		
		for(int[] bullet : gameInfo.bulletSet) {
			if(bullet[2]==1) {
				g.drawImage(p1Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}else if(bullet[2]==2) {
				g.drawImage(p2Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}
		}
		
	}
	
	class Plane1Adapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) { //키 입력
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				pInfo.move = "left";
				break;
			case KeyEvent.VK_RIGHT:
				pInfo.move = "right";	
				break;
			case KeyEvent.VK_SPACE:
				pInfo.charging = true;
				break;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				pInfo.move = "stop";			
				break;
			case KeyEvent.VK_RIGHT:
				pInfo.move = "stop";
				break;
			case KeyEvent.VK_SPACE:
				pInfo.charging = false;
				break;
            }
		}
	}
}
