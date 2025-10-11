package TileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.nio.Buffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.MapObject;


 /**
 * reprezentuje fragment planszy gry. W tym przypadku bloczek po którym może poruszać się gracz
 */

public class Tile {
	
	/**
	 * przechowuje rysunek klocka
	 */
	private BufferedImage image;
	private int type;
	
	/**
	 * NORMAL oznacza, że jest to element graficzny planszy, który nie jest
	 * przeszkodą do przejścia.
	 */
	public static final int NORMAL = 0;
	/**
	 * BLOCKED oznacza blok, którego gracz nie może przejść (musi go ominąć/przeskoczyć)
	 */
	public static final int BLOCKED = 1;
	
	/**
	 * konstruktor klocka. Argumentami są jakiś obraz i typ klocka
	 */
	public Tile(BufferedImage image, int type) {
		/**
		 * Teraz klocek będzie wyglądał dokładnie jak podany w argumencie obrazek
		 */
		this.image = image;
		this.type = type;
		
		//III etap

	}
	/**
	 * zwraca obrazek klocka
	 */
	public BufferedImage getImage() { return image; }
	/**
	 * zwraca typ klocka
	 */
	public int getType() { return type; }
	
	/**
	 * Ta funkcja nic nie robi oprócz tego, że jest
	 */
	public void update() {}
	
	/**
	 * Rysuje kafelki planszy
	 * @param g
	 * @param x
	 * @param y
	 */
	public void draw(Graphics2D g, int x, int y) {
		g.drawImage(image, x, y, null);
	}
	
	
}
