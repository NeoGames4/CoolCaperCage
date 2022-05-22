package Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Controls implements KeyListener {
	
	public static boolean left = false, right = false, up = false, down = false, space = false, escape = false;
	
	@Override
    public void keyTyped(KeyEvent e) {
		if(Home.Home.testMode) {
			if(e.getKeyChar() == '+') {
				Home.Home.coins += 100;
				Home.Home.save();
			} else if(e.getKeyChar() == '-') {
				Home.Home.coins = 0;
				Home.Home.characters = "";
				Home.Home.highscore = 0;
				Home.Home.save();
			}
		}
	}

	@Override
    public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				left = true;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				right = true;
				break;
			case KeyEvent.VK_SPACE:
				space = true;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				up = true;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				down = true;
				break;
			case KeyEvent.VK_ESCAPE:
				escape = true;
				break;
		}
	}

	@Override
    public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				left = false;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				right = false;
				break;
			case KeyEvent.VK_SPACE:
				space = false;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				up = false;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				down = false;
				break;
		}
	}

}
