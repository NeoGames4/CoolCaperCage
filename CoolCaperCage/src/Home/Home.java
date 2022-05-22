package Home;

import java.awt.Image;
import java.util.prefs.Preferences;

import Default.Launcher;

public class Home {
	
	public static long highscore = 0;
	public static long coins = 0;
	public static int character = 0;
	public static String characters = "0";
	
	public static boolean testMode = false;
	
	public static Image icon = Launcher.getImage("icon.png");
	
	public static void save() {
		Preferences preferences = Preferences.userNodeForPackage(Home.class);
		preferences.putLong("highscore", highscore);
		preferences.putLong("coins", coins);
		preferences.put("characters", characters);
	}
	
	public static void load() {
		Preferences preferences = Preferences.userNodeForPackage(Home.class);
		highscore = preferences.getLong("highscore", highscore);
		coins = preferences.getLong("coins", coins);
		characters = preferences.get("characters", characters);
	}

}
