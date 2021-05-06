package panel;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.WaitingBoard;

public class StartPanel extends JPanel {

	private Image makeroom_img_path = new ImageIcon(StartPanel.class.getResource("../image/make_room.png")).getImage(); //
	private Image joinroom_img_path = new ImageIcon(StartPanel.class.getResource("../image/join_room.png")).getImage(); //
	private Image soundOn_img_path = new ImageIcon(StartPanel.class.getResource("../image/SoundOn.png")).getImage(); //
	private Image soundOff_img_path = new ImageIcon(StartPanel.class.getResource("../image/SoundOff.png")).getImage(); //
	
	private ImageIcon makeroom_img = new ImageIcon(makeroom_img_path); //
	private ImageIcon joinroom_img = new ImageIcon(joinroom_img_path); //
	private ImageIcon soundOn_img = new ImageIcon(soundOn_img_path); //
	private ImageIcon soundOff_img = new ImageIcon(soundOff_img_path); //

	private JButton makeRoom = new JButton(makeroom_img);
	private JLabel label = new JLabel("Please enter the code");
	private JTextField code = new JTextField();
	private JButton joinRoom = new JButton(joinroom_img);
	
	private JButton soundOn = new JButton(soundOn_img);
	private JButton soundOff = new JButton(soundOff_img);
	
	public StartPanel(WaitingBoard board) {
        setLayout(null);
        setBounds(0, 0, 400, 400);
        
        makeRoom.setBounds(125, 100, 150, 30);
        makeRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GamePanel.makeAudio("click.wav");
				board.makeRoom();
			}
		});
        
        label.setBounds(125, 150, 150, 30);
        label.setHorizontalAlignment(JLabel.CENTER);
        
        code.setBounds(125, 200, 150, 30);
        
        joinRoom.setBounds(125, 250, 150, 30);
        joinRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GamePanel.makeAudio("click.wav");
				board.joinRoom(code.getText());
			}
		});
        
        soundOn.setBounds(0, 0, 30, 30);
        soundOn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				GamePanel.mute = true;
				
				add(soundOff);
				remove(soundOn);
			}
		});
        
        soundOff.setBounds(0, 0, 30, 30);
        soundOff.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		
        		GamePanel.mute = false;
        		
        		GamePanel.makeAudio("click.wav");
        		
                add(soundOn);
        		remove(soundOff);
        	}
        });
        
        add(makeRoom);
        add(label);
        add(code);
        add(joinRoom);
        add(soundOn);
	}
	
}
