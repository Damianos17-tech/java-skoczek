package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import TileMap.TileMap;

public class CrateTNT extends Enemy{
	
	private BufferedImage[] sprites;
	private boolean burdened;
	private long startTimeB;
	private long startTimeInV;
	private boolean invisible;
	
	public CrateTNT(TileMap tm) {
		
		super(tm);
		moveSpeed = 0.8;
		maxSpeed = 1.2;
		fallSpeed = 0;
		maxFallSpeed = 0.9;
		
		width = 33;
		height = 33;
		cwidth = 33;
		cheight = 33;
		
		health = maxHealth = 2;
		damage = 1;
		
		BufferedImage spritesheet;
		try {
			spritesheet = ImageIO.read(
					getClass().getResourceAsStream("/Tilesets/Crates3.gif")
					);
			sprites = new BufferedImage[5];
			for(int i=0; i<sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(
						(i+4)* width, 0, width, height);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(-1); //a bylo 1000
		
		right = true;
		facingRight = true;
		
		burdened = false;
		invisible = false;
	}
	
	public void getNextPosition() {
		if(left) {
			dx = -moveSpeed;
			if(dx < -maxSpeed)
				dx = -maxSpeed;	//right i left tutaj poprawione, dobrze
			
		}
		else if(right) {
			dx = moveSpeed;
			if(dx > maxSpeed)
				dx = maxSpeed;
		}
		if(falling) {
			dy += fallSpeed;
		}
	}
	
	public void update() {
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchingTimer) / 1000000;
			if(elapsed > 400)
				flinching = false;
		}
		
		if(right & dx == 0) {
			right = false;
			left = true;
		}
		else if(left && dx == 0) {
			right = true;
			left = false;
		}
		
		animation.update();
		
		if(burdened) {
			long elapsed = (System.nanoTime() - startTimeB) / 1000000;
			if(elapsed > 3000) {
				invisible = true;
				startTimeInV = System.nanoTime();
				burdened = false;
			}	
		}
		
		if(invisible) {
			long elapsed = (System.nanoTime() - startTimeInV) / 1000000;
			if(elapsed > 1000) {
				invisible = false;
				animation.setDelay(-1);
				animation.setFrame(0);
			}
		}
	}
	
	public void setBurdened(){
		if(burdened || invisible)
			return;
		burdened = true;
		startTimeB = System.nanoTime();
		animation.setDelay(1000);
	}
	
	public boolean isInvisible() {
		return invisible;
	}
	
	public void draw(Graphics2D g) {
		if(notOnScreen());
			//return;
		setMapPosition();
		super.draw(g);
	}
}
	
