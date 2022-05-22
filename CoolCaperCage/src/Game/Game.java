package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Default.Launcher;
import Home.Characters;
import Home.Home;

public class Game extends JFrame {
	
	public final Stage stage;
	
	public final Player player = new Player();
	ArrayList<Platform> platforms = new ArrayList<>();
	
	public long score = 0;
	
	public long revert = 0;
	
	public final int fps = 32;
	public float factor = 1;
	
	public float cameraResetSteps = 0;
	
	private boolean start = false,
			began = false,
			pause = false;
	
	public Game(int width, int height) {
		setBounds(0, 0, width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Launcher.getImage("icon.png"));
		setVisible(true);
		stage = new Stage(width, height);
		add(stage);
		addKeyListener(new Controls());
		setFocusTraversalKeysEnabled(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Home.save();
				System.exit(0);
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				factor = getWidth()/1200f;
				stage.setSize(getWidth(), getHeight());
			}
		});
		platforms.add(new Platform(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, 10));
		
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			if(!pause) {
				float playerXa = 2548f/fps;
				float playerXf = 2148f/fps;
				// Player horizontal movements
				if((revert <= 0 && Controls.right) || (revert > 0 && Controls.left)) player.accelerate(playerXa);
				if((revert <= 0 && Controls.left) || (revert > 0 && Controls.right)) player.accelerate(-playerXa);
				if((revert <= 0 && ((!Controls.left && player.vx < 0) || (!Controls.right && player.vx > 0))) || (revert > 0 && ((!Controls.right && player.vx < 0) || (!Controls.left && player.vx > 0)))) player.applyFriction(playerXf);
				
				// Player vertical movements
				float playerYa = 932f;
				float gravity = 1024f;
				player.applyGravity(gravity/fps);
				if(((revert <= 0 && Controls.down) || (revert > 0 && Controls.up)) && player.loaded) {
					player.loaded = false;
					player.vy = -2156f;
				}
				
				if(start) {
					// Platforms
					for(Platform p : platforms) {
						if(player.y >= p.y && player.y + (player.vy/fps) < p.y) {
							if((player.x + (player.size * factor)/2 > p.x - (p.width * factor)/2 && player.x - (player.size * factor)/2 < p.x + (p.width * factor)/2)) {
								player.vy = player.vy > -2000f ? playerYa : 2f * playerYa;
								player.y = p.y - player.vy/fps;
								if(p.y > 0) p.accelerate(-p.boost);
								if(!began && p.y > 0) began = true;
							}
						}
						p.applyFriction(1512f/fps);
						if(Math.abs(p.x + Math.copySign(p.width/2, p.x) + p.vx/fps) > getWidth()/2-10) p.vx = -p.vx;
						// (Collectables)
						if(p.collectable != null) {
							if(player.y <= p.y + p.collectableHeight + (p.collectable.size * factor) && player.y + (player.size * factor) >= p.y + p.collectableHeight) {
								if((player.x + (player.size * factor)/2 > p.x - (p.collectable.size * factor)/2 && player.x - (player.size * factor)/2 < p.x + (p.collectable.size * factor)/2)) {
									switch(p.collectable.type) {
										case Collectable.BOOST:
											player.loaded = true;
											break;
										case Collectable.REVERT:
											revert = revert <= 0 ? (long) (fps * 10f) : 0;
											break;
									}
									p.collectable = null;
								}
							}
						}
						p.move(p.vx/fps, p.vy/fps);
					}
					
					player.move(player.vx/fps, player.vy/fps);
					
					// Reverse
					if(revert > 0) revert--;
					
					// Wraparound
					if(player.x + player.size/2 < -getWidth()/2) player.x = getWidth()/2 + player.size/2;
					if(player.x - player.size/2 > getWidth()/2) player.x = -getWidth()/2 - player.size/2;
					
					// Camera movements
					if(began) stage.cy += Math.min(100 + score/200, 210)/fps;
					if(player.y - stage.cy + player.size + 30 > stage.getHeight()) stage.cy += player.y - stage.cy - stage.getHeight() + player.size + 30;
					
					// Score
					if(began) score = Math.max((long) player.y, score);
					
					// Death
					if(getHeight() + stage.cy - player.y > getHeight() + 1024) {
						player.vx = player.vy = player.x = 0;
						player.y = stage.getHeight();
						stage.setCamera(0);
						Home.highscore = Math.max(score, Home.highscore);
						platforms.clear();
						platforms.add(new Platform(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, 10));
						revert = 0;
						Home.coins += score/100 * (Characters.characters[Home.character].equals(Characters.TAYLOR) ? 2 : 1);
						Home.save();
						player.loaded = began = start = false;
					} else {
						if(getHeight() + stage.cy - player.y > getHeight()) {
							for(Platform p : platforms) p.accelerate(792f);
						}
						managePlatforms(playerYa, gravity);
						// Pause
						if(Controls.escape && began) {
							Controls.escape = false;
							pause = true;
						} else if(Controls.escape) Controls.escape = false;
					}
				} else {
					// Start or unlock
					if(Controls.up) {
						int index = Home.character;
						Characters character = Characters.characters[index];
						if(Home.characters.contains(index + "") || character.worth <= 0) {
							score = 0;
							player.y = player.vy = 0;
							pause = false;
							start = true;
						} else {
							Home.load();
							if(Home.coins >= character.worth) {
								Home.coins -= character.worth;
								Home.characters += index;
								Home.save();
							}
						}
					} else {
						int imageWidth = player.image == null ? 92 : player.image.getWidth(null);
						int offset = (int) (imageWidth * (stage.getWidth()/5/(double) player.size) + stage.getWidth()/30d)/2;
						if(player.x - offset > stage.getWidth()/2) { // Character selection
							player.x = -stage.getWidth()/2 - offset; 
							Home.character++;
							if(Home.character > Characters.characters.length-1) Home.character = 0;
							player.image = Characters.characters[Home.character].image;
						} else if(player.x + offset < -stage.getWidth()/2) {
							player.x = stage.getWidth()/2 + offset;
							Home.character--;
							if(Home.character < 0) Home.character = Characters.characters.length-1;
							player.image = Characters.characters[Home.character].image;
						}
					}
					player.move(player.vx/fps, player.vy/fps);
					if(player.y < 0) {
						player.y = 0;
						player.vy = 0;
					}
				}
			} else {
				if(Controls.escape) {
					Controls.escape = false;
					pause = false;
				}
			}
			
			repaint();
		}, 100, (int) ((1d/((double) fps)) * 1000d), TimeUnit.MILLISECONDS);
	}
	
	public void managePlatforms(float playerYa, float gravity) {
		platforms.removeIf(p -> stage.getHeight() + stage.cy - p.y > stage.getHeight() + (p.collectable != null ? p.collectable.size + p.collectableHeight : 0) + 10);
		float maxD = 0.5f * gravity * (float) Math.pow(playerYa/gravity, 2) * 0.97f;
		Platform heighest = platforms.get(0);
		for(Platform p : platforms) {
			if(p.y > heighest.y) heighest = p;
		} if(heighest.y - stage.cy < stage.getHeight()) {
			int width = (int) (64 + Math.random() * 128d),
					x = (int) ((Math.random() < 0.5 ? 1 : -1) * Math.random() * (stage.getWidth()/2d - width/2 - 2)),
					y = (int) (heighest.y + maxD/Math.min(2, 1d/((score+1)/7500d) + 1.2) + Math.random() * (maxD - maxD/Math.min(2, 1d/((score+1)/7500d) + 1.2)) * 0.95);
			Collectable collectable = Math.random() < 0.15 ? new Collectable(Math.random() < 0.68 ? Collectable.BOOST : Collectable.REVERT) : null;
			float boost = Math.random() < Math.min(score/42000d, 0.6) ? Math.random() < Math.min(score/64000d, 0.2) ? 3096f : 512f : 156f;
			platforms.add(new Platform(x, y, width, boost > 500 ? boost > 1000 ? 5 : 7 : 10, boost, (Math.random() < 0.5 ? 1 : -1) * Math.random() < 0.2 ? (float) (Math.random() * 122d) + 22f : 0f).setCollectable(collectable));
		}
	}
	
	public class Stage extends JPanel {
		
		public float cy = 0;
		
		public Stage(int width, int height) {
			this.setBounds(0, 0, width, height);
			setCamera(0);
			this.repaint();
		}
		
		public void setCamera(float y) {
			cy = y-getHeight()/4.5f;
		}
		
		public Color getColor() {
			return getColor((int) cy);
		}
		
		public Color getColor(float x) {
			int r = (int) Math.abs(Math.sin((x-600)/5000)*220d),
					g = (int) Math.abs(Math.sin((x-600)/5000+20)*220d),
					b = (int) Math.abs(Math.sin((x-600)/5000+40)*183d);
			return new Color(r+30, g+30, b+72);
		}
		
		public Color modify(Color color, int amount) {
			int r = Math.max(Math.min(color.getRed()+amount, 255), 0),
					g = Math.max(Math.min(color.getGreen()+amount, 255), 0),
					b = Math.max(Math.min(color.getBlue()+amount, 255), 0);
			return new Color(r, g, b);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			// Background
			Color decent = !pause ? revert <= 0 ? getColor() : Color.WHITE : Color.GRAY;
			g2.setColor(revert <= 0 ? modify(decent, 40) : Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setColor(revert <= 0 ? modify(decent, 35) : Color.BLACK);
			g2.setStroke(new BasicStroke(12));
			int offset = (int) (cy/5) % 92;
			for(int i = offset; i<getHeight() * 2; i += 46) g2.drawLine(0, i - getHeight(), getWidth(), i + getWidth() - getHeight());
			g2.setColor(revert <= 0 ? modify(decent, 40) : Color.BLACK);
			g2.setStroke(new BasicStroke(24));
			for(int i = offset; i<getHeight() * 2; i += 92) g2.drawLine(0, i + getWidth() - getHeight(), getWidth(), i - getHeight());
			
			// Platforms
			g2.setStroke(new BasicStroke(2));
			for(Platform p : platforms) {
				g2.setColor(modify(decent, 10));
				g2.drawLine(0, (int) (getHeight() + cy - p.y), getWidth(), (int) (getHeight() + cy - p.y));
				g2.setColor(modify(decent, -10));
				int platformWidth = (int) (p.width * factor);
				if(revert <= 0) g2.fillRect(getWidth()/2 - platformWidth/2 + (int) p.x, (int) (getHeight() + cy - p.y), platformWidth, p.height);
				g2.setColor(modify(decent, p.boost > 500 ? -50 : -30));
				g2.drawRect(getWidth()/2 - platformWidth/2 + (int) p.x, (int) (getHeight() + cy - p.y), platformWidth, p.height);
				if(p.collectable != null) {
					int cSize = (int) (p.collectable.size * factor);
					g2.drawImage(p.collectable.image.getScaledInstance(cSize, cSize, Image.SCALE_FAST), getWidth()/2 - cSize/2 + (int) p.x, (int) (getHeight() + cy - p.y - cSize - p.collectableHeight), null);
				}
			}
			
			// Player
			g2.setColor(decent);
			int playerSize = start ? (int) (player.size * factor) : getWidth()/5;
			int playerX = getWidth()/2 - playerSize/2 + (int) player.x,
					playerY = (int) (getHeight() + cy - player.y - playerSize);
			if(revert <= 0) g2.fillRoundRect(playerX, playerY, playerSize, playerSize, playerSize/3, playerSize/3);
			g2.setColor(modify(decent, -20));
			g2.setStroke(new BasicStroke(playerSize/12));
			g2.drawRoundRect(playerX, playerY, playerSize, playerSize, playerSize/3, playerSize/3);
			g2.setColor(Color.WHITE);
			int eyeX = playerX + playerSize/2,
					eyeY = playerY + playerSize/4;
			g2.fillRoundRect(eyeX - playerSize/4 - 2, eyeY, playerSize/4, (int) (playerSize/2.5), playerSize/6, playerSize/6);
			g2.fillRoundRect(eyeX + 2, eyeY, playerSize/4, (int) (playerSize/2.5), playerSize/6, playerSize/6);
			if(!Characters.characters[Home.character].equals(Characters.AVATAR)) {
				g2.setColor(modify(decent, -172));
				int pupilXOffset = (int) Math.max(Math.min(player.vx/100 * factor, playerSize/6), -playerSize/6),
						pupilY = eyeY + playerSize/5 - playerSize/8 - (int) Math.max(Math.min(player.vy/100f * factor, playerSize/5 - playerSize/8), -playerSize/5 + playerSize/8);
				g2.fillOval(eyeX - playerSize/4 - 2 + playerSize/8 - playerSize/12 + pupilXOffset, pupilY, playerSize/6, playerSize/4);
				g2.fillOval(eyeX + 2 + playerSize/8 - playerSize/12 + pupilXOffset, pupilY, playerSize/6, playerSize/4);
			}
			if(player.image != null) {
				int imageSize = (int) (player.image.getWidth(null) * (playerSize/(double) player.size) + playerSize/6d);
				g2.drawImage(player.image.getScaledInstance(imageSize, imageSize, Image.SCALE_FAST), getWidth()/2 - imageSize/2 + (int) player.x, (int) (getHeight() + cy - player.y - playerSize - (imageSize - playerSize)/2), null);
			}
			
			// Overlay
			// Revert
			if(revert > 0) {
				g2.setColor(Color.WHITE);
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 15));
				String str = "-" + (Math.ceil((revert/(float) fps)*100f) / 100f) + " s";
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, g2.getFont().getSize());
			}
			// Score
			g2.setColor(new Color(100, 100, 100, 100));
			g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 15));
			String scoreText = score + "";
			while(scoreText.length() < 6) scoreText = "0" + scoreText;
			int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
			g2.fillRect(getWidth() - scoreWidth - 4, 0, scoreWidth + 8, g2.getFont().getSize() + 4);
			g2.setColor(Color.WHITE);
			g2.drawString(scoreText, getWidth() - scoreWidth - 4, g2.getFont().getSize());
			// Loaded
			if(player.loaded) {
				g2.setColor(new Color(100, 100, 100, 100));
				g2.fillRect(0, 0, Math.max(g2.getFontMetrics().stringWidth("Press 'S'"), Collectable.IMAGE_BOOST.getWidth(null)) + 8, Collectable.IMAGE_BOOST.getHeight(null) + g2.getFont().getSize() + 4);
				g2.drawImage(Collectable.IMAGE_BOOST, Math.abs(Collectable.IMAGE_BOOST.getWidth(null)/2 - g2.getFontMetrics().stringWidth("Press 'S'")/2)/2 + 4, 0, null);
				g2.setColor(Color.WHITE);
				g2.drawString("Press 'S'", 4, g2.getFont().getSize() + Collectable.IMAGE_BOOST.getHeight(null));
			}
			// Pause
			if(pause) {
				g2.setColor(Color.WHITE);
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 15));
				String str = "Press 'ESCAPE' to continue";
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, (int) (getHeight() * 1/3));
			}
			// Start
			if(!start) {
				float c = -(float) Math.pow(Integer.parseInt((System.currentTimeMillis() + "").substring(7))%2000/1000f - 1, 2) + 1;
				Color foreground = new Color(255, 210 + (int) (c * 45), 130 + (int) (c * 125));
				g2.setColor(decent);
				g2.fillRect(0, getHeight() + (int) cy, getWidth(), (int) -cy);
				g2.setColor(Color.WHITE);
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 19));
				String str = "Move left or right to select a character";
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight() + (int) cy + g2.getFont().getSize() * 2);
				Characters character = Characters.characters[Home.character];
				str = "\"" + character.name + "\"" + (character.equals(Characters.TAYLOR) ? " (2x Coins)" : "") + (!Home.characters.contains(Home.character + "") && character.worth > 0 ? " (" + character.worth + " C)" : "");
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight() + (int) cy + g2.getFont().getSize() * 5);
				if(!Home.characters.contains(Home.character + "") && character.worth > Home.coins) {
					g2.setColor(new Color(255, 100, 110));
					str = "You don't have enough coins :,(";
					g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight() + (int) (cy + g2.getFont().getSize() * 6.5));
				}
				g2.setColor(new Color(240, 240, 240));
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 13));
				str = "© Mika Thein";
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight()  - g2.getFont().getSize() - 12);
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 12));
				str = "Up / Start ('W', 'arrow up', 'space') - Down / Use ('S', 'arrow down') - Left ('A', 'arrow left') - Right ('D', 'arrow right')";
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight()  - g2.getFont().getSize() * 3 - 12);
				
				if(score > 0) {
					g2.setColor(Color.GRAY);
					g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 104));
					g2.drawString(scoreText, getWidth()/2 - g2.getFontMetrics().stringWidth(scoreText)/2 - 1, getHeight()/2 - g2.getFont().getSize()/2 - 140 + 2);
					g2.setColor(new Color(255, 210, 130));
					g2.drawString(scoreText, getWidth()/2 - g2.getFontMetrics().stringWidth(scoreText)/2, getHeight()/2 - g2.getFont().getSize()/2 - 140);
				} else {
					g2.drawImage(Home.icon.getScaledInstance(getWidth()/2, (int) (getWidth()/2d * (((double) Home.icon.getHeight(null))/Home.icon.getWidth(null))), Image.SCALE_FAST), getWidth()/4, 20, null);
				}
				
				str = Home.characters.contains(Home.character + "") || character.worth <= 0 ? "Press 'UP' to start" : "Press 'UP' to unlock";
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 27));
				g2.setColor(Color.GRAY);
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2 - 1, getHeight()/2 - g2.getFont().getSize()/2 + 2);
				g2.setColor(foreground);
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight()/2 - g2.getFont().getSize()/2);
				
				String highscoreText = Home.highscore + "";
				while(highscoreText.length() < 6) highscoreText = "0" + highscoreText;
				str = "Highscore: " + highscoreText;
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 19));
				g2.setColor(Color.GRAY);
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2 - 1, getHeight()/2 - g2.getFont().getSize()/2 - 72 + 2);
				g2.setColor(new Color(255, 210, 130));
				g2.drawString(str, getWidth()/2 - g2.getFontMetrics().stringWidth(str)/2, getHeight()/2 - g2.getFont().getSize()/2 - 72);
				
				g2.setColor(new Color(100, 100, 100, 100));
				g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 15));
				int coinsWidth = g2.getFontMetrics().stringWidth(Home.coins + " C");
				g2.fillRect(0, 0, coinsWidth + 8, g2.getFont().getSize() + 4);
				g2.setColor(Color.WHITE);
				g2.drawString(Home.coins + " C", 4, g2.getFont().getSize());
			}
		}
		
	}

}
