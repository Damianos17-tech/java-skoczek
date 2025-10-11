package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HUD {
	
	/**
	 * Dostęp do obiektu gracza pozwala na posiadanie informacji na jego temat, które będą
	 * wyświetlane
	 */
	private Player player;
	/**
	 * obiekt obrazka klasy
	 */
	private BufferedImage image;
	private BufferedImage imageA;
	/**
	 * czcionka do wypisywania informacji na temat gracza
	 */
	private Font font;
	
	/**
	 * Konstruktor klasy HUD, wczytuje obrazki i ustawia czcionkę
	 * @param p
	 */
	public HUD(Player p) {
		player = p;
		
		try {
			BufferedImage img = ImageIO.read(
					getClass().getResourceAsStream("/HUD/CrashFace.gif")
					);
			
			image = img.getSubimage(40, 40, 50, 50);
			font = new Font("Arial", Font.PLAIN, 14);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedImage imgA = ImageIO.read(
					getClass().getResourceAsStream("/HUD/jablkaDoZbierania.gif")
					);
			
			imageA = imgA.getSubimage(0, 0, 15, 15);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Rysuje liczbę żyć i zebranych jabłek na ekranie w sposób nieprzerwany
	 * @param g
	 */
	public void draw(Graphics2D g) {
		g.drawImage(image, 0, 0, null);
		g.drawImage(imageA, 13, 32, null);
		g.setFont(font);
		g.setColor(Color.WHITE);
		g.drawString(
				"x "+player.getHealth(),
				35,
				25
				);
		g.drawString(
				"x "+player.getNumApples(),
				35,
				45
				);
	}
	
	
	
	
	
	
	
}
