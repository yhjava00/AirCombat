package panel;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.WaitingBoard;

public class WaitingPanel extends JPanel {
	
	private Image gameout_img_path = new ImageIcon(WaitingPanel.class.getResource("../image/game_out.png")).getImage(); //
	private ImageIcon gameout_img = new ImageIcon(gameout_img_path); //

	private WaitingBoard board;

	public static JLabel codeLabel = new JLabel("null");
	private JButton gameOutButton = new JButton(gameout_img);
	
	public WaitingPanel(WaitingBoard board) {
		this.board = board;
		
        setLayout(null);
        setBounds(0, 0, 400, 400);
        
        codeLabel.setBounds(125, 100, 150, 30);
        codeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        gameOutButton.setBounds(125, 150, 150, 30);
        gameOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				board.gameOut();
			}
		});
        
        add(codeLabel);
        add(gameOutButton);
	}
	
	public static void setCode(String code) {
		codeLabel.setText(code);
	}
	
}
