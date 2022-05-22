package Game;

import java.awt.Image;

public class Player {
	
	public static final float MAX_XV = 5024f, MAX_YV = 5024f;
	
	public float x = 0;
	public float y = 0;
	
	public float vx = 0;
	public float vy = 0;
	
	public boolean loaded = false;
	
	public Image image;
	public int size = 46;
	
	public void accelerate(float horizontalAcceleration) {
		vx += horizontalAcceleration;
		if(vx < -MAX_XV) vx = -MAX_XV;
		else if(vx > MAX_XV) vx = MAX_XV;
	}
	
	public void applyFriction(float f) {
		if(Math.abs(vx) <= f) vx = 0;
		else if(vx > 0) vx -= f;
		else if(vx < 0) vx += f;
	}
	
	public void applyGravity(float g) {
		vy -= g;
		if(vy < -MAX_YV) vy = -MAX_YV;
		else if(vy > MAX_YV) vy = MAX_YV;
	}
	
	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

}
