package Game;

import java.awt.Image;

public class Platform {
	
	public static final float MAX_XV = 512f, MAX_YV = 16762f;
	
	public float x = 0;
	public float y = 0;
	
	public float vx = 0;
	public float vy = 0;
	
	public Image image;
	public int width;
	public int height;
	
	public Collectable collectable = null;
	public int collectableHeight = 7;
	
	public float boost = 156f;
	
	public Platform(float x, float y, int width, int height, float boost, float vx) {
		this(x, y, width, height, boost);
		this.vx = vx;
	}
	
	public Platform(float x, float y, int width, int height, float boost) {
		this(x, y, width, height);
		this.boost = boost;
	}
	
	public Platform(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Platform setCollectable(Collectable collectable) {
		this.collectable = collectable;
		return this;
	}
	
	public void accelerate(float verticalAcceleration) {
		vy += verticalAcceleration;
		if(vy < -MAX_YV) vy = -MAX_YV;
		else if(vy > MAX_YV) vy = MAX_YV;
	}
	
	public void applyFriction(float f) {
		if(Math.abs(vy) <= f) vy = 0;
		else if(vy > 0) vy -= f;
		else if(vy < 0) vy += f;
	}
	
	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

}
