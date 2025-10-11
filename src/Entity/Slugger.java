package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.w3c.dom.ls.LSException;

import TileMap.TileMap;

public class Slugger extends Enemy {

	private BufferedImage[] sprites;
	
	public Slugger(TileMap tm) {
		
		super(tm);
		moveSpeed = 0.5;
		maxSpeed = 0.9;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;
		
		width = 65;
		height = 30;
		cwidth = 60;
		cheight = 30;
		
		health = maxHealth = 1;
		damage = 1;
		
		BufferedImage spritesheet;
		try {
			spritesheet = ImageIO.read(
					getClass().getResourceAsStream("/Sprites/Enemies/BadDogs.gif")
					);
			sprites = new BufferedImage[5];
			for(int i=0; i<sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(
						i* width+10, 2, width, height);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(80);
		
		right = true;
		facingRight = true;

	}
	
	
	public void getNextPosition() {
		if(right) {
			dx -= moveSpeed;
			if(dx < -maxSpeed)
				dx = -maxSpeed;	//right i left w ifach powinno byc odwrotnie, bo 
								//zle wycztalem animacje
		}
		else if(left) {
			dx += moveSpeed;
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
			facingRight = false;
		}
		else if(left && dx == 0) {
			right = true;
			left = false;
			facingRight = true;
		}
		
		animation.update();
	}
	
	public void draw(Graphics2D g) {
		if(notOnScreen());
			//return;
		setMapPosition();
		super.draw(g);
	}
}
