package panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import client.WaitingBoard;

public class WaitingPanel extends JPanel {

	WaitingBoard board;

	static JLabel codeLabel = new JLabel("null");
	
	public WaitingPanel(WaitingBoard board) {
		this.board = board;
		
        setLayout(null);
        setBounds(0, 0, 400, 400);
        
        codeLabel.setBounds(125, 100, 150, 30);
        codeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(codeLabel);
	}
	
	public static void setCode(String code) {
		codeLabel.setText(code);
	}
	
}
