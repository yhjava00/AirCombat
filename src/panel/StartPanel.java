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
	private ImageIcon makeroom_img = new ImageIcon(makeroom_img_path); //
	private ImageIcon joinroom_img = new ImageIcon(joinroom_img_path); //

	private WaitingBoard board;
	
	private JButton makeRoom = new JButton(makeroom_img);
	private JLabel label = new JLabel("Please enter the code");
	private JTextField code = new JTextField();
	private JButton joinRoom = new JButton(joinroom_img);
	
	
	public StartPanel(WaitingBoard board) {
		this.board = board;
		
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
        add(makeRoom);
        add(label);
        add(code);
        add(joinRoom);
	}
	
}
