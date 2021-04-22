package panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.GameBoard;
import client.GameInfo;
import client.WaitingBoard;

public class StartPanel extends JPanel {

	WaitingBoard board;
	GameInfo info;
	
	JButton makeRoom = new JButton("MAKE ROOM");
	JLabel label = new JLabel("Please enter the code");
	JTextField code = new JTextField();
	JButton sendCode = new JButton("SEND CODE");
	
	
	public StartPanel(WaitingBoard board, GameInfo info) {
		this.board = board;
		this.info = info;
		
        setLayout(null);
        setBounds(0, 0, 400, 400);
        
        makeRoom.setBounds(125, 100, 150, 30);
        makeRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.makeRoom();
			}
		});
        
        label.setBounds(125, 150, 150, 30);
        label.setHorizontalAlignment(JLabel.CENTER);
        
        code.setBounds(125, 200, 150, 30);
        
        sendCode.setBounds(125, 250, 150, 30);
        sendCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.sendCode(code.getText());
				new GameBoard(info);
			}
		});
        add(makeRoom);
        add(label);
        add(code);
        add(sendCode);
	}
	
}
