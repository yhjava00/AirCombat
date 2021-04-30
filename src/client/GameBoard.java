package client;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import panel.GamePanel;

public class GameBoard extends JFrame{
	
	public GameBoard(GamePanel gamePanel) {
		
		add(gamePanel);
		
		setResizable(false);
		pack();
		
	    Dimension frameSize = getSize();
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation((screenSize.width - frameSize.width) / 3, (screenSize.height - frameSize.height) / 3);
	    
        setTitle("Air Combat");
        setVisible(true);
	}
	
}
