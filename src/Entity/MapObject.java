package Entity;

import Main.GamePanel;
import TileMap.TileMap;
import TileMap.Tile;

import java.awt.Rectangle;
/**
 * Ważna klasa, zawiera podstawowe parametry wspólne dla każdego obiektu w grze
 *
 */
public abstract class MapObject {
	
	/**
	 * Określa czy obiekt powinien być zlikwidowany 
	 */
	protected boolean dead;
	/**
	 * Referencja do mapy gry
	 */
	protected TileMap tileMap;
	/**
	 * Wielkość jednego klocka na planszy
	 */
	protected int tileSize;
	/**
	 * położenie mapy gry na osi x
	 */
	protected double xmap;
	/**
	 * położenie mapy gry na osi y
	 */
	protected double ymap;
	
	/**
	 * położenie obiektu względem osi x
	 */
	protected double x;
	/**
	 * położenie obiektu względem osi y
	 */
	protected double y;
	/**
	 * składowa prędkości obiektu względem osi x
	 */
	protected double dx;
	/**
	 * składowa prędkości obiektu względem osi y
	 */
	protected double dy;
	
	/**
	 * Szerokość obiektu
	 */
	protected int width;
	/**
	 * Wysokość obiektu
	 */
	protected int height;
	
	/**
	 * Szerokość collison boxa obiektu
	 */
	protected int cwidth;
	/**
	 * Wysokość collison boxa obiektu
	 */
	protected int cheight;
	
	// collision
	/**
	 * Obecny wiersz obiektu na mapie
	 */
	protected int currRow;
	/**
	 * Obecna kolumna obiektu na mapie
	 */
	protected int currCol;
	/**
	 * Punkt docelowy obiektu na osi x
	 */
	protected double xdest;
	/**
	 * Punkt docelowy obiektu na osi y
	 */
	protected double ydest;
	/**
	 * Punkt tymczasowy obiektu na osi x
	 */
	protected double xtemp;
	/**
	 * Punkt tymczasowy obiektu na osi y
	 */
	protected double ytemp;
	/**
	 * Lewy-górny klocek - kolizja
	 */
	protected boolean topLeft;
	/**
	 * Prawy-górny klocek - kolizja
	 */
	protected boolean topRight;
	/**
	 * Lewy-dolny klocek - kolizja
	 */
	protected boolean bottomLeft;
	/**
	 * Prawy-dolny klocek - kolizja
	 */
	protected boolean bottomRight;
	
	// animation
	/**
	 * Animacje związane z obiektem
	 */
	protected Animation animation;
	/**
	 * Akcja, która jest teraz wykonywana przez obiekt
	 */
	protected int currentAction;
	/**
	 * Akcja, która była poprzednio wykonywana przez obiekt
	 */
	protected int previousAction;
	/**
	 * Czy obiekt się patrzy w prawo
	 */
	protected boolean facingRight;
	
	// movement
	/**
	 * Określa czy obiekt się porusza w lewo
	 */
	protected boolean left;
	/**
	 * Określa czy obiekt się porusza w prawo
	 */
	protected boolean right;
	/**
	 * Określa czy obiekt się porusza do góry
	 */
	protected boolean up;
	/**
	 * Określa czy obiekt się porusza do dołu
	 */
	protected boolean down;
	/**
	 * Określa czy obiekt skacze
	 */
	protected boolean jumping;
	/**
	 * Określa czy obiekt spada
	 */
	protected boolean falling;
	
	// movement attributes
	/**
	 * Określa prędkość poruszania się obiektu
	 */
	protected double moveSpeed;
	/**
	 * Określa maksymalną prędkość poruszania się obiektu
	 */
	protected double maxSpeed;
	/**
	 * Określa prędkość hamowania obiektu
	 */
	protected double stopSpeed;
	/**
	 * Określa prędkość spadania obiektu
	 */
	protected double fallSpeed;
	/**
	 * Określa maksymalną prędkość spadania obiektu
	 */
	protected double maxFallSpeed;
	/**
	 * Określa prędkość początkową skoku obiektu
	 */
	protected double jumpStart;
	/**
	 * Określa prędkość hamującą skok obiektu
	 */
	protected double stopJumpSpeed;
	
	// constructor
	/**
	 * Konstrukor obiektu na mapie
	 * @param tm
	 */
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize(); 
	}
	
	/**
	 * Sprawdza czy obiekt koliduje z innym
	 * @param o
	 * @return
	 */
	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}
	
	/**
	 * Zwraca collision boxa i jego położenie 
	 * @return
	 */
	public Rectangle getRectangle() {
		return new Rectangle(
				(int)x - cwidth/2,
				(int)y - cheight/2,
				cwidth,
				cheight
		);
	}
	
	/**
	 * Oblicza 4 punkty kwadratu środkiem którego jest obiekt i czy nie są one zablokowane
	 * @param x
	 * @param y
	 */
	public void calculateCorners(double x, double y) {
		
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;
		
		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		
		topLeft = tl == Tile.BLOCKED;
		topRight = tr == Tile.BLOCKED;
		bottomLeft = bl == Tile.BLOCKED;
		bottomRight = br == Tile.BLOCKED;	
		
	}
	/**
	 * Sprawdz kolizję obiektu związane z mapą 
	 */
	public void checkTileMapCollision() {
		
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
	
		calculateCorners(x, ydest);	///tutaj się zatrzymuje gra, gdy gracz wyjdzie za granice
		
		if(dy < 0) {
			if(topLeft || topRight) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		
		if(dy > 0) {
			if(bottomLeft || bottomRight) {
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		
		calculateCorners(xdest, y);
		if(dx < 0) {
			if(topLeft || bottomLeft) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}
		if(dx > 0) {
			if(topRight || bottomRight) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}
		
		if(!falling) {
			calculateCorners(x, ydest + 1);
			if(!bottomLeft && !bottomRight) {
				falling = true;
			}
			
		}	
	}
	
	public int getx() { return (int)x; }
	public int gety() { return (int)y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getCWidth() { return cwidth; }
	public int getCHeight() { return cheight; }
	
	/**
	 * Ustawia pozycję obiektu
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Ustawia prędkość obiektu
	 * @param dx
	 * @param dy
	 */
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Ustawia pozycję obiektu na mapie
	 */
	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
	}
	/**
	 * Ustawia kierunek poruszania się obiektu w lewo
	 */
	public void setLeft(boolean b) { left = b; }
	/**
	 * Ustawia kierunek poruszania się obiektu w prawo
	 */
	public void setRight(boolean b) { right = b; }
	/**
	 * Ustawia kierunek poruszania się obiektu do góry
	 */
	public void setUp(boolean b) { up = b; }
	/**
	 * Ustawia kierunek poruszania się obiektu do dołu
	 */
	public void setDown(boolean b) { down = b; }
	/**
	 * Ustawia poruszanie się obiektu na skok
	 */
	public void setJumping(boolean b) { jumping = b; }
	
	/**
	 * Sprawdza czy obiekt nie wykracza poza ekran
	 */
	public boolean notOnScreen() {
		return x + xmap + width < 0 ||
				x + xmap - width > GamePanel.WIDTH ||
				y + ymap + height < 0 ||
				y + ymap - height > GamePanel.HEIGHT;
	}
	/**
	 * Sprawdza czy obiekt nie spadł w przepaść
	 */
	public boolean intoAbyss() {
		return (y + height ) > tileMap.getHeight();
	}
	/**
	 * Sprawdza czy obiekt nie żyje
	 */
	public boolean isDead() {
		return dead;
	}
	/**
	 * Rysuje obiekt w odpowiednim miejscu na planszy gry
	 * @param g
	 */
	public void draw(java.awt.Graphics2D g) {
		
		//System.out.println("y = "+y+", ymap = "+ymap);
		if(facingRight) {
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - width / 2),
				(int)(y + ymap - height / 2),
				null
			);
		}
		else {
			g.drawImage(
				animation.getImage(),
				(int)(x + xmap - animation.getImage().getWidth() / 2 + animation.getImage().getWidth()),
				(int)(y + ymap - animation.getImage().getHeight() / 2),
				-animation.getImage().getWidth(),
				animation.getImage().getHeight(),
				null
			);
		}
	}
	
	
	///
	/**
	 * Sprawdza czy obiekt nie został dotknięty przez inny obiekt z lewej strony
	 */
	public boolean intersectsLeft(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getLeftBound(); 
		return r1.intersects(r2);
	}
	/**
	 * Sprawdza czy obiekt nie został dotknięty przez inny obiekt z prawej strony
	 */
	public boolean intersectsRight(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRightBound(); 
		return r1.intersects(r2);
	}
	/**
	 * Sprawdza czy obiekt nie został dotknięty przez inny obiekt od góry
	 */
	public boolean intersectsTop(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getTopBound(); 
		return r1.intersects(r2);
	}
	/**
	 * Sprawdza czy obiekt nie został dotknięty przez inny obiekt od dołu
	 */
	public boolean intersectsBottom(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getBottomBound(); 
		return r1.intersects(r2);
	}
	/**
	 * Zwraca lewą granicę collision boxa
	 * @return
	 */
	public Rectangle getLeftBound() {
		return new Rectangle(
				(int)x - cwidth/2+1,
				(int)y - cheight/2 + 2,
				1,
				cheight-4
		);
	}
	/**
	 * Zwraca prawą granicę collision boxa
	 * @return
	 */
	public Rectangle getRightBound() {
		return new Rectangle(
				(int)x + cwidth/2 - 1,
				(int)y - cheight/2 + 2,
				1,
				cheight - 4
		);
	}
	/**
	 * Zwraca górną granicę collision boxa
	 * @return
	 */
	public Rectangle getTopBound() {
		return new Rectangle(
				(int)x - cwidth/2+1,
				(int)y - cheight/2-1,
				cwidth-2,
				1
		);
	}
	/**
	 * Zwraca dolną granicę collision boxa
	 * @return
	 */
	public Rectangle getBottomBound() {
		return new Rectangle(
				(int)x - cwidth/2+1,
				(int)y + cheight/2 - 1,
				cwidth-2,
				1
		);
	}
	
	public double getdx() {
		return dx;
	}
	
	public double getdy() {
		return dy;
	}
	/**
	 * Zmienia kierunek poruszania się obiektu
	 * @return
	 */
	public void changeDirection() {
		right = !right;
		left = !left;
		dx = -dx;

	}
}
















