package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import TileMap.TileMap;

/**
 * 
 * Jabłko, służy do zbierania i wpływa na punktację
 *
 */
public class Apple extends MapObject {
	/**
	 * określa czy jabłko zostało podniesione
	 */
	private boolean picked;
	private BufferedImage[] sprites;
	
	public Apple(TileMap tm) {
		super(tm);
		moveSpeed = 0;
		maxSpeed = 0;
		fallSpeed = 0;
		maxFallSpeed = 0;
		
		width = 15;
		height = 15;
		cwidth = 14;
		cheight = 14;
		
		picked = false;
		
		BufferedImage spritesheet;
		try {
			spritesheet = ImageIO.read(
					getClass().getResourceAsStream("/Sprites/Enemies/jablkaDoZbierania.gif")
					);
			sprites = new BufferedImage[2];
			for(int i=0; i<sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(
						i* width, 0, width, height);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(180);
		
		right = true;
		facingRight = true;
	}
	
	public void pick() {
		picked = true;
	}
	
	public boolean isPicked() {
		return picked;
	}
	
	public void update() {
		animation.update();
	}

	public void draw(Graphics2D g) {
		if(notOnScreen());
			//return;
		setMapPosition();
		super.draw(g);
	}
}
