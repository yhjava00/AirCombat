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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import client.GameInfo;

public class GamePanel extends JPanel implements ActionListener{
	
	private Label end_label = new Label();
	private Button restart = new Button("restart");

	private Image plane1 = new ImageIcon(GamePanel.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GamePanel.class.getResource("../image/plane2.png")).getImage();
	private Image p1Wave = new ImageIcon(GamePanel.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GamePanel.class.getResource("../image/p2Wave.png")).getImage();
	// 추가
	private Image wallImg = new ImageIcon(GamePanel.class.getResource("../image/wall.png")).getImage();
	private Image backgroundImg = new ImageIcon(GamePanel.class.getResource("../image/background.png")).getImage();
	
	private GameInfo info;
	
	private Timer timer;

	private void setLabelAndButton() {
		
		end_label.setBackground(Color.BLACK);
		end_label.setFont(new Font("Serif", Font.BOLD, 21));
		end_label.setForeground(Color.blue);
		end_label.setAlignment(Label.CENTER);
		end_label.setBounds(info.MAP_WIDTH/2-75, 50, 150, 75);
		
		restart.setBounds(info.MAP_WIDTH/2-37, 130, 75, 30);
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(end_label);
				remove(restart);
				info.cInfo.request[0] = "ready";
			}
		});
		
	}
	
	public GamePanel(GameInfo info) {
		
		this.info = info;
		
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(info.MAP_WIDTH, info.MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        setLabelAndButton();

		add(end_label);
		add(restart);
		
        timer = new Timer(info.DELAY, this);
        timer.start();
        
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(backgroundImg , 0 , 0 , null); // 추가
		
		g.drawImage(plane1, info.sInfo.p1[0]-(info.PLANE_WIDTH/2), info.sInfo.p1[1], null);
		g.drawImage(plane2, info.sInfo.p2[0]-(info.PLANE_WIDTH/2), info.sInfo.p2[1], null);
		
		g.setColor(Color.yellow);
		g.fillRect(info.sInfo.p1[0]-(info.PLANE_WIDTH/2), info.sInfo.p1[1]+37, info.sInfo.p1_gauge, 10);
		g.fillRect(info.sInfo.p2[0]-(info.PLANE_WIDTH/2), info.sInfo.p2[1]+37, info.sInfo.p2_gauge, 10);

		g.setColor(Color.RED);
		g.fillRect(info.sInfo.p1[0]-(info.PLANE_WIDTH/2), info.sInfo.p1[1]+47 , info.sInfo.p1_HP , 10);
		g.fillRect(info.sInfo.p2[0]-(info.PLANE_WIDTH/2), info.sInfo.p2[1]+47 , info.sInfo.p2_HP , 10);

		g.drawImage(wallImg, info.sInfo.wall[0][0], info.sInfo.wall[0][1], null); // 추가
		g.drawImage(wallImg, info.sInfo.wall[1][0], info.sInfo.wall[1][1], null); // 추가
		g.drawImage(wallImg, info.sInfo.wall[2][0], info.sInfo.wall[2][1], null); // 추가
		
		for(int[] bullet : info.sInfo.bulletSet) {
			if(bullet[2]==1) {
				g.drawImage(p1Wave, bullet[0]-(info.BULLET_WIDTH/2), bullet[1], null);
			}else if(bullet[2]==2) {
				g.drawImage(p2Wave, bullet[0]-(info.BULLET_WIDTH/2), bullet[1], null);
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(info.cInfo.state.equals("end")) {
			end_label.setText(info.sInfo.end_msg);
			add(end_label);
			add(restart);
			info.cInfo.state = "";
		}
		repaint();
	}
	
	class Plane1Adapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) { //키 입력
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				info.cInfo.move = "left";
				break;
			case KeyEvent.VK_RIGHT:
				info.cInfo.move = "right";	
				break;
			case KeyEvent.VK_SPACE:
				info.cInfo.charging = true;
				break;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				info.cInfo.move = "stop";			
				break;
			case KeyEvent.VK_RIGHT:
				info.cInfo.move = "stop";
				break;
			case KeyEvent.VK_SPACE:
				info.cInfo.charging = false;
				break;
            }
		}
	}
}
