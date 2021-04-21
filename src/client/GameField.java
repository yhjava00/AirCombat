package client;

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

import info.ClientInfo;
import info.ServerInfo;

public class GameField extends JPanel implements ActionListener{
	
	private static final int MAP_HEIGHT = 700;
	private static final int MAP_WIDTH = 500;
	
	private final int PLANE_HEIGHT = 37;
	private final int PLANE_WIDTH = 50;
	
	private final int BULLET_HEIGHT = 22;
	private final int BULLET_WIDTH = 25;
	
	private final int DELAY = 1;

	private Label end_label = new Label();
	private Button restart = new Button("restart");

	private Image plane1 = new ImageIcon(GameField.class.getResource("../image/plane1.png")).getImage();
	private Image plane2 = new ImageIcon(GameField.class.getResource("../image/plane2.png")).getImage();
	private Image p1Wave = new ImageIcon(GameField.class.getResource("../image/p1Wave.png")).getImage();
	private Image p2Wave = new ImageIcon(GameField.class.getResource("../image/p2Wave.png")).getImage();
	
	public ServerInfo sInfo;
	public ClientInfo cInfo;
	
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
				cInfo.request = "ready";
			}
		});
		
	}
	
	protected GameField() {
		
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(MAP_WIDTH, MAP_HEIGHT));
        
        addKeyListener(new Plane1Adapter());
        
        setLabelAndButton();
        
        timer = new Timer(DELAY, this);
        
	}
	
	protected void fieldStart() {
        timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(plane1, sInfo.p1[0]-(PLANE_WIDTH/2), sInfo.p1[1], null);
		g.drawImage(plane2, sInfo.p2[0]-(PLANE_WIDTH/2), sInfo.p2[1], null);
		
		g.setColor(Color.yellow);
		g.fillRect(sInfo.p1[0]-(PLANE_WIDTH/2), sInfo.p1[1]+37, sInfo.p1_gauge, 10);
		g.fillRect(sInfo.p2[0]-(PLANE_WIDTH/2), sInfo.p2[1]+37, sInfo.p2_gauge, 10);

		g.setColor(Color.RED);
		g.fillRect(sInfo.p1[0]-(PLANE_WIDTH/2), sInfo.p1[1]+47 , sInfo.p1_HP , 10);
		g.fillRect(sInfo.p2[0]-(PLANE_WIDTH/2), sInfo.p2[1]+47 , sInfo.p2_HP , 10);

		for(int[] bullet : sInfo.bulletSet) {
			if(bullet[2]==1) {
				g.drawImage(p1Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}else if(bullet[2]==2) {
				g.drawImage(p2Wave, bullet[0]-(BULLET_WIDTH/2), bullet[1], null);
			}
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(cInfo.state.equals("end")) {
			end_label.setText(sInfo.end_msg);
			add(end_label);
			add(restart);
			cInfo.state = "";
		}
		repaint();
	}
	
	class Plane1Adapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent ke) { //키 입력
			switch (ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				cInfo.move = "left";
				break;
			case KeyEvent.VK_RIGHT:
				cInfo.move = "right";	
				break;
			case KeyEvent.VK_SPACE:
				cInfo.charging = true;
			break;           
			}
		}
		
		@Override
		public void keyReleased(KeyEvent ke) {
			switch(ke.getKeyCode()) {
			case KeyEvent.VK_LEFT:	
				cInfo.move = "stop";			
				break;
			case KeyEvent.VK_RIGHT:
				cInfo.move = "stop";
				break;
			case KeyEvent.VK_SPACE:
				cInfo.charging = false;
				break;
            }
		}
	}
}
