package Home;

import java.awt.Image;

import Default.Launcher;

public enum Characters {
	
	NORMAL(null, "Clark", 0),
	AGENT("agent.png", "Agent C", 750),
	VADER("vader.png", "Vader", 750),
	AVATAR("avatar.png", "Avatar Clark", 900),
	PRIDE("pride.png", "Pride Clark", 900),
	IRONMAN("iron_man.png", "Iron Man", 1200),
	TROY("troy.png", "Clark Bolton", 1500),
	TAYLOR("taylor.png", "Clark Swift", 2213);
	
	public static final Characters[] characters = {NORMAL, AGENT, VADER, AVATAR, PRIDE, IRONMAN, TROY, TAYLOR};
	
	public final Image image;
	public final String name;
	public final int worth;
	
	private Characters(String imageName, String name, int worth) {
		image = imageName != null ? Launcher.getImage(imageName) : null;
		this.name = name;
		this.worth = worth;
	}

}
