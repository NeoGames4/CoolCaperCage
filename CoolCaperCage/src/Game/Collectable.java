package Game;

import java.awt.Image;

import Default.Launcher;

public class Collectable {
	
	public static final float MAX_XV = 932f, MAX_YV = 932f;
	
	public static final int BOOST = 0, REVERT = 1;
	
	public static final Image IMAGE_BOOST = Launcher.getImage("boost.png"),
			IMAGE_REVERT = Launcher.getImage("revert.png");
	
	public int type;
	
	public Image image;
	public int size = 36;
	
	public Collectable(int type) {
		this.type = type;
		image = type == BOOST ? IMAGE_BOOST : IMAGE_REVERT;
	}

}
