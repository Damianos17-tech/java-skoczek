package TileMap;

import Main.GamePanel;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
 * Klasa dotycząca tła i jego działania.
 */
public class Background {
	
	/**
	 * Obiekt przechowujący obrazek tła
	 */
	private BufferedImage image;
	/**
	 * Położenie tła na osi poziomej x
	 */
	private double x;
	/**
	 * Położenie tła na osi pionowej y
	 */
	private double y;
	/**
	 * Składowa wektora prędkości w kierunku osi x
	 */
	private double dx;
	/**
	 * Składowa wektora prędkości w kierunku osi y
	 */
	private double dy;
	
	/**
	 * Parametr określający jak szybko względem mapy ma się przewijać tło gry. Np. 2 oznacza
	 * dwukrotną szybkość
	 */
	private double moveScale;
	
	/**
	 * Konstruktor klasy tło, wczytywany jest obraz z dysku i ustawiany parametr moveScale
	 */
	public Background(String s, double ms) {
		
		try {
			image = ImageIO.read( getClass().getResourceAsStream(s) );
			moveScale = 1;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Ustawia pozycję tła na zadane parametry
	 */
	public void setPosition(double x, double y) {
		
		this.x = (x * moveScale) % (GamePanel.WIDTH*2);
		this.y = (y * moveScale) % GamePanel.HEIGHT;

	}
	/**
	 * Ustawia wektor prędkości poruszania się tła
	 */
	
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Aktualizuje klasę opisującą zachowanie tła
	 */
	public void update() {
		x += dx;
		y += dy;
	}
	
	/**
	 * Rysuje tło gry z odpowiednim efektem wizualnym, tak aby było rysowane nawet
	 * poza granicami obrazka tła
	 * @param g
	 */
	public void draw(Graphics2D g) {
		
		g.drawImage(image, (int)x, (int)y, null);
		
		if(x < 0) {
			g.drawImage(image, (int)x + 2*GamePanel.WIDTH, (int)y, null);
			System.out.println("x < 0");
		}
			
		if(x > 0) {
			System.out.println("x < 0");
			g.drawImage(image, (int)x - 2*GamePanel.WIDTH, (int)y, null);
		}
			
		
		///dorobione
		if(y < 0) 
			g.drawImage(image, (int)x, (int)y + GamePanel.HEIGHT, null);
		if(y > 0)
			g.drawImage(image, (int)x, (int)y - GamePanel.HEIGHT, null);
	}
	
	/**
	 * Pobiera położenie tła na osi x
	 */
	public int getX() {
		return (int)x;
	}
	
	/**
	 * Pobiera położenie tła na osi y
	 */
	public int getY() {
		return (int)y;
	}
}







