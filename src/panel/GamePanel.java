package panel;

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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import info.GameInfo;
import info.PlayerInfo;

public class GamePanel extends JPanel{

	private final int MAP_HEIGHT = 700;
	private final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 31;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;

	private final int ITEM_HEIGHT = 30;
	private final int ITEM_WIDTH = 30;
	
	public Label guide_label = new Label();
	
	private Image plane1 = new ImageIcon(GamePanel.class.getResource("../image/p1.png")).getImage();
	private Image plane1L = new ImageIcon(GamePanel.class.getResource("../image/p1L.png")).getImage();
	private Image plane1R = new ImageIcon(GamePanel.class.getResource("../image/p1R.png")).getImage();
	private Image plane2 = new ImageIcon(GamePanel.class.getResource("../image/p2.png")).getImage();
	private Image plane2L = new ImageIcon(GamePanel.class.getResource("../image/p2L.png")).getImage();
	private Image plane2R = new ImageIcon(GamePanel.class.getResource("../image/p2R.png")).getImage();
	private Image p1Wave = new ImageIcon(GamePanel.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GamePanel.class.getResource("../image/p2Wave.png")).getImage();
	private Image p1WaveLV1 = new ImageIcon(GamePanel.class.getResource("../image/p1WaveLV1.png")).getImage();
	private Image p2WaveLV1 = new ImageIcon(GamePanel.class.getResource("../image/p2WaveLV1.png")).getImage();
	private Image heartImg = new ImageIcon(GamePanel.class.getResource("../image/heart.png")).getImage();
	private Image damageImg = new ImageIcon(GamePanel.class.getResource("../image/damage.png")).getImage();
	private Image superImg = new ImageIcon(GamePanel.class.getResource("../image/super.png")).getImage();
	private Image boomImg = new ImageIcon(GamePanel.class.getResource("../image/boom.png")).getImage();
	private Image wallImg = new ImageIcon(GamePanel.class.getResource("../image/wall.png")).getImage();
	private Image backgroundImg = new ImageIcon(GamePanel.class.getResource("../image/background.png")).getImage();
	private Image player01BtnPath = new ImageIcon(GamePanel.class.getResource("../image/player01Btn.png")).getImage();
	private Image player02BtnPath = new ImageIcon(GamePanel.class.getResource("../image/player02Btn.png")).getImage();
	private Image level1Path = new ImageIcon(GamePanel.class.getResource("../image/level1.png")).getImage();
	private Image level2Path = new ImageIcon(GamePanel.class.getResource("../image/level2.png")).getImage(); 
	private Image level3Path = new ImageIcon(GamePanel.class.getResource("../image/level3.png")).getImage();
	private Image selectedP1Path = new ImageIcon(GamePanel.class.getResource("../image/player01BtnSelect.png")).getImage();
	private Image selectedP2Path = new ImageIcon(GamePanel.class.getResource("../image/player02BtnSelect.png")).getImage();
	
	public ImageIcon Player01Btn = new ImageIcon(player01BtnPath); 
	public ImageIcon Player02Btn = new ImageIcon(player02BtnPath);
	private ImageIcon Level1Btn = new ImageIcon(level1Path);
	private ImageIcon Level2Btn = new ImageIcon(level2Path);
	private ImageIcon Level3Btn = new ImageIcon(level3Path);
	public ImageIcon SelectedP1 = new ImageIcon(selectedP1Path);
	public ImageIcon SelectedP2 = new ImageIcon(selectedP2Path);
	
	public JButton p1Btn = new JButton(Player01Btn);
	public JButton p2Btn = new JButton(Player02Btn);
	public JButton lv1Btn = new JButton(Level1Btn);
	public JButton lv2Btn = new JButton(Level2Btn);
	public JButton lv3Btn = new JButton(Level3Btn);
	
	private boolean inGame;
	
	private int backgroundLocate = 0;
	
	private Clip bgm;
	
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
		guide_label.setBounds(0, 70, MAP_WIDTH, 50);
		
		p1Btn.setBounds(165, 150, 75, 30);
		p1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("i want p1");
			}
		});
		
		p2Btn.setBounds(260, 150, 75, 30);
		p2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("i want p2");
			}
		});
		
		// 추가
		lv1Btn.setBounds(115, 150, 75, 30);
		lv1Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("select lv1");
			}
		});
		lv2Btn.setBounds(215, 150, 75, 30);
		lv2Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientRequest.add("select lv2");
			}
		});
		lv3Btn.setBounds(315, 150, 75, 30);
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
	
	public void addLabelAndButton(String msg) {
				
		if(msg.equals("")) {			
			guide_label.setText(gameInfo.msg);
		}
		else
			guide_label.setText(msg);
			
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
	public void addSelectLevelButton(int p) {
		if(p==1)
			guide_label.setText("PLEASE  SELECT  LEVEL");
		else
			guide_label.setText("PLEASE  WAIT  PLAYER01");
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
		
		inGame = true;
		
		try {
			bgm = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(
					GamePanel.class.getResourceAsStream("../audio/bgm.wav"));
			bgm.open(inputStream);
			bgm.start();
			bgm.loop(-1);
		} catch (Exception e) {}
		
		repaintThread = new RepaintThread();
		repaintThread.start();
		
		backgroundMoveThread = new BackGroundMoveThread();
		backgroundMoveThread.start();
		
	}
	
	public void gameStop() {
		
		if(bgm!=null)
			bgm.stop();
		
		inGame = false;
	}
	
	private void makeAudio(String name) {

		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(
			          GamePanel.class.getResourceAsStream("../audio/" + name));
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {}
		
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(backgroundImg, 0, backgroundLocate, null); // 추가
		g.drawImage(backgroundImg, 0, backgroundLocate-MAP_HEIGHT, null); // 추가
		
		if(gameInfo.p1Move.equals("left"))
			g.drawImage(plane1L, gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1], null);
		else if(gameInfo.p1Move.equals("right"))
			g.drawImage(plane1R, gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1], null);
		else
			g.drawImage(plane1, gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1], null);

		if(gameInfo.p2Move.equals("left"))
			g.drawImage(plane2L, gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1], null);
		else if(gameInfo.p2Move.equals("right"))
			g.drawImage(plane2R, gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1], null);
		else
			g.drawImage(plane2, gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1], null);
		
		switch (gameInfo.p1_gauge_lv) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.yellow);
			g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+PLANE_HEIGHT, PLANE_WIDTH, 10);
			g.setColor(Color.BLUE);
			break;
		case 2:
			g.setColor(Color.BLUE);
			g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+PLANE_HEIGHT, PLANE_WIDTH, 10);
			g.setColor(Color.BLACK);	
			break;
		}
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+PLANE_HEIGHT, gameInfo.p1_gauge, 10);
		
		switch (gameInfo.p2_gauge_lv) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.yellow);
			g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+PLANE_HEIGHT, PLANE_WIDTH, 10);
			g.setColor(Color.BLUE);
			break;
		case 2:
			g.setColor(Color.BLUE);
			g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+PLANE_HEIGHT, PLANE_WIDTH, 10);
			g.setColor(Color.BLACK);	
			break;
		}
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+PLANE_HEIGHT, gameInfo.p2_gauge, 10);
		
		if(gameInfo.p1Super>0)
			g.setColor(Color.MAGENTA);
		else
			g.setColor(Color.RED);
		g.fillRect(gameInfo.p1[0]-(PLANE_WIDTH/2), gameInfo.p1[1]+PLANE_HEIGHT+10 , gameInfo.p1_HP , 10);
		
		if(gameInfo.p2Super>0)
			g.setColor(Color.MAGENTA);
		else
			g.setColor(Color.RED);
		g.fillRect(gameInfo.p2[0]-(PLANE_WIDTH/2), gameInfo.p2[1]+PLANE_HEIGHT+10 , gameInfo.p2_HP , 10);
		
		for(int i=0; i<gameInfo.wall.length; i++) {
			g.drawImage(wallImg, gameInfo.wall[i][0], gameInfo.wall[i][1], null); // 추가			
		}
		
		for(int[] bullet : gameInfo.bulletSet) {
			
			if(bullet[4]==0)
				continue;
			
			if(bullet[2]==1) {
				
				if(bullet[3]<0) {
					if(bullet[1]>=MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20 - 10)
						makeAudio("gun1.wav");
					g.drawImage(p1Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				}
				else {
					if(bullet[1]>=MAP_HEIGHT - PLANE_HEIGHT - BULLET_HEIGHT - 20 - 10)
						makeAudio("gun2.wav");
					g.drawImage(p1WaveLV1, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				}
			}else if(bullet[2]==2) {
				
				if(bullet[3]<0) {
					if(bullet[1]<=PLANE_HEIGHT + 10)
						makeAudio("gun1.wav");
					g.drawImage(p2Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				}
				else {
					if(bullet[1]<=PLANE_HEIGHT + 10)
						makeAudio("gun2.wav");					
					g.drawImage(p2WaveLV1, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
				}
			}
		}
		
		for(int[] boom : gameInfo.boom) {
			if(boom[2]>0) {
				g.drawImage(boomImg, boom[0]-20, boom[1] , null);
			}
		}
		
		
		for(int[] item : gameInfo.itemBox) {
			
			if(item[4]==0)
				continue;
			
			if(item[2]==0) 				
				g.drawImage(damageImg, item[0], item[1], null);
			else if(item[2]==1)
				g.drawImage(heartImg, item[0], item[1], null);
			else if(item[2]==2) 
				g.drawImage(superImg, item[0], item[1], null);
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
			case KeyEvent.VK_T:
				pInfo.move = "test";
            }
		}
	}
	
	class RepaintThread extends Thread {
		@Override
		public void run() {
			while(inGame) {
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
			while(inGame) {
				
				int speed = getSpeed();
				
				try {
					Thread.sleep(speed);
				} catch (Exception e) {}

				backgroundLocate++;
				
				if(backgroundLocate>=MAP_HEIGHT)
					backgroundLocate = 0;	
			}
		}
	}
	
	private int getSpeed() {

		switch ((gameInfo.p1_HP+gameInfo.p2_HP)/10) {
		case 10:
			return 10;
		case 9:
			return 7;
		case 8:
			return 5;
		case 7:
			return 4;
		case 6:
			return 3;
		case 5:
		case 4:
		case 3:
			return 2;
		default:
			return 1;
		}
		
	}
	
}
