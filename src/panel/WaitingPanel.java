package panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import client.GameInfo;
import client.WaitingBoard;

public class WaitingPanel extends JPanel {

	WaitingBoard board;
	GameInfo info;

	JLabel codeLabel = new JLabel("null");
	
	public WaitingPanel(WaitingBoard board, GameInfo info) {
		this.board = board;
		this.info = info;
		
        setLayout(null);
        setBounds(0, 0, 400, 400);
        
        codeLabel.setBounds(125, 100, 150, 30);
        codeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(codeLabel);
	}
	
	public void setCode(String code) {
		codeLabel.setText(code);
	}
	
}
