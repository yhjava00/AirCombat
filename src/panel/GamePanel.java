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
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import info.GameInfo;
import info.PlayerInfo;

public class GamePanel extends JPanel{

	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	public Label guide_label = new Label();
	public Button p1Btn = new Button("player 01");
	public Button p2Btn = new Button("player 02");
	
	public Button lv1Btn = new Button("Level 1");//
	public Button lv2Btn = new Button("Level 2");//
	public Button lv3Btn = new Button("Level 3");//

	private Image plane1 = new ImageIcon(GamePanel.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GamePanel.class.getResource("../image/plane2.png")).getImage();
	private Image p1Wave = new ImageIcon(GamePanel.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GamePanel.class.getResource("../image/p2Wave.png")).getImage();
	private Image p1WaveLV1 = new ImageIcon(GamePanel.class.getResource("../image/p1WaveLV1.png")).getImage();
	private Image p2WaveLV1 = new ImageIcon(GamePanel.class.getResource("../image/p2WaveLV1.png")).getImage();
	// 추가
	private Image boomImg = new ImageIcon(GamePanel.class.getResource("../image/boom.png")).getImage();
	private Image wallImg = new ImageIcon(GamePanel.class.getResource("../image/wall.png")).getImage();
	private Image backgroundImg = new ImageIcon(GamePanel.class.getResource("../image/background.png")).getImage();
	
	private boolean checkRepaint;
	private boolean backgroundMove;
	
	private int backgroundLocate = 0;
	
	public GameInfo gameInfo;
	public PlayerInfo pInfo;

	protected Set<String> clientRequest;
	
	private Thread backgroundMoveThread;
	private Thread repaintThread;
	
	private void setLabelAndButton() {
		
		guide_label.setBackground(Color.lightGray);
		guide_label.setFont(new Font("Serif", Font.BOLD, 21));
		guide_label.setForeground(Color.black);
		guide_label.setAlignment(Label.CENTER);
		guide_label.setBounds(0, 30, MAP_WIDTH, 50);
		
		p1Btn.setBounds(165, 130, 75, 30);
		p1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("i want p1");
			}
		});
		
		p2Btn.setBounds(260, 130, 75, 30);
		p2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("i want p2");
			}
		});
		
		// 추가
		lv1Btn.setBounds(115, 130, 75, 30);
		lv1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("select lv1");
			}
		});
		lv2Btn.setBounds(215, 130, 75, 30);
		lv2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("select lv2");
			}
		});
		lv3Btn.setBounds(315, 130, 75, 30);
		lv3Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("select lv3");
			}
		});
	}
	
	public GamePanel(Set<String> clientRequest) {
		
		this.clientRequest = clientRequest;
		
		setLayout(null);
        setFocusable(true);
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        setLabelAndButton();
        
        pInfo = new PlayerInfo();
        
        pInfo.move = "stop";
        pInfo.charging = false;
	}
	
	public void addLabelAndButton() {
		
		guide_label.setText(gameInfo.msg);

		p1Btn.setBackground(null);
		p2Btn.setBackground(null);
		
		add(guide_label);
		add(p1Btn);
		add(p2Btn);
	}
	
	public void removeLabelAndButton() {
		remove(p1Btn);
		remove(p2Btn);
	}

	// 추가
	public void addSelectLevelButton() {
		guide_label.setText("PLEASE SELECT LEVEL");
		add(guide_label);
		add(lv1Btn);
		add(lv2Btn);
		add(lv3Btn);
		
	}
	public void removeSelectLevelButton() {
		remove(guide_label);
		remove(lv1Btn);
		remove(lv2Btn);
		remove(lv3Btn);
		requestFocus();
	}
	
	public void gameStart() {
		
		checkRepaint = true;
		repaintThread = new RepaintThread();
		repaintThread.start();
		
		backgroundMove = true;
		backgroundMoveThread = new BackGroundMoveThread();
		backgroundMoveThread.start();
	}
	
	public void gameStop() {
		checkRepaint = false;
		backgroundMove = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(backgroundImg , 0 , backgroundLocate , null); // 추가
		g.drawImage(backgroundImg , 0 , backgroundLocate-MAP_HEIGHT , null); // 추가
		
		g.drawImage(plane1, gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1], null);
		g.drawImage(plane2, gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1], null);
		
		switch (gameInfo.p1_gauge_lv) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.yellow);
			g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+37, PLANE_WIDTH, 10);
			g.setColor(Color.BLUE);
			break;
		case 2:
			g.setColor(Color.BLUE);
			g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+37, PLANE_WIDTH, 10);
			g.setColor(Color.BLACK);	
			break;
		}
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+37, gameInfo.p1_gauge, 10);
		
		switch (gameInfo.p2_gauge_lv) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.yellow);
			g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+37, PLANE_WIDTH, 10);
			g.setColor(Color.BLUE);
			break;
		case 2:
			g.setColor(Color.BLUE);
			g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+37, PLANE_WIDTH, 10);
			g.setColor(Color.BLACK);	
			break;
		}
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+37, gameInfo.p2_gauge, 10);

		g.setColor(Color.RED);
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+47 , gameInfo.p1_HP , 10);
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+47 , gameInfo.p2_HP , 10);
		
		for(int i=0; i<gameInfo.wall.length; i++) {
			g.drawImage(wallImg, gameInfo.wall[i][0], gameInfo.wall[i][1], null); // 추가			
		}
		
		for(int[] bullet : gameInfo.bulletSet) {
			
			if(bullet[4]==0)
				continue;
			
			if(bullet[2]==1) {
				if(bullet[3]<0)
					g.drawImage(p1Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				else
					g.drawImage(p1WaveLV1, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}else if(bullet[2]==2) {
				if(bullet[3]<0)
					g.drawImage(p2Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				else
					g.drawImage(p2WaveLV1, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}
		}
		
		for(int[] boom : gameInfo.boom) {
			if(boom[2]>0) {
				g.drawImage(boomImg, boom[0]-26, boom[1] , null);
			}
		}
		
	}
	
	class Plane1Adapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) {
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
	
	class RepaintThread extends Thread {
		@Override
		public void run() {
			while(checkRepaint) {
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				
				repaint();
				
			}
		}
	}
	
	class BackGroundMoveThread extends Thread {
		@Override
		public void run() {
			while(backgroundMove) {
				try {
					Thread.sleep(2);
				} catch (Exception e) {}

				backgroundLocate++;
				
				if(backgroundLocate>=MAP_HEIGHT)
					backgroundLocate = 0;	
			}
		}
	}
	
}
