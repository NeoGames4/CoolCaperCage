package Default;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.imageio.ImageIO;

import Game.Game;

public class Launcher {
	
	public static void main(String[] args) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Home.Home.load();
		Game game = new Game((int) (d.width/2), (int) (d.height/1.2));
	}
	
	public static Image getImage(String path) {
		try {
			Image image = ImageIO.read(Launcher.class.getResource(path.startsWith(".") ? path.substring(1) : path));
			if(image != null) return image;
			else throw new Exception();
		} catch(Exception e) {
			e.printStackTrace();
			try { return ImageIO.read(new File(path));
		} catch(Exception exception) { 
			exception.printStackTrace();
			return null;
		} }
	}

}
