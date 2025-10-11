package GameState;

import Main.GamePanel;
import Networking.Client;
import TileMap.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Audio.AudioPlayer;
import Entity.Apple;
import Entity.CrateTNT;
import Entity.Enemy;
import Entity.Explosion;
import Entity.HUD;
import Entity.Player;
import Entity.Slugger;

/**
 *  Klasa dziedzicząca po GameState. Zawiera szczegółowy opis zasad działania 2 stanu gry
 */
public class Level2State extends GameState {
	
	/**
	 * Stan gry posiada swoją własną mapę / planszę / poziom
	 */
	private TileMap tileMap;
	/**
	 *  Stan gry posiada swoje własne tło
	 */
	private Background bg;
	/**
	 * Obiekt gracza powinien znalezć się w definicji poziomu
	 */
	private Player player;
	/**
	 * Przeciwnicy z danego poziomu
	 */
	private ArrayList<Enemy> enemies;
	/**
	 * Jabłka na danym poziomie
	 */
	private ArrayList<Apple> apples;
	/**
	 * Skrzynki TNT na danym poziomie
	 */
	private ArrayList<CrateTNT> cratesTNT;
	/**
	 * Ekplozje
	 */
	private ArrayList<Explosion> explosions;
	
	/**
	 * Wyświetla status gracza w trakcie gry na ekranie 
	 */
	private HUD hud;
	/**
	 * Muzyka tła charakterystyczna dla danego poziomu
	 */
	private AudioPlayer bgMusic;

	/**
	 * Konstruktor poziomu
	 * @param gsm
	 */
	public Level2State(GameStateManager gsm) {
		
		this.gsm = gsm;
		init();
	}
	
	
	/**
	 * Inicjalizuje poziom 1 gry. Woła konstruktor mapy, gdzie argumentem jest długość
	 * boku klocka w pikselach.
	 * Ładuje obraz zródłowy z którego będą wycianane podobrazy.
	 * Ustawia początkową pozycję mapy oraz początkowe tło.
	 * Tworzy mapę na podstawie pliku konfiguracyjnego 
	 */	
	public void init() {
		
		tileMap = new TileMap(33);
		tileMap.loadTiles("/Tilesets/playstation2exp4.gif");
		tileMap.loadMap("/Maps/mapa4000.map");
		//tileMap.loadMap(gsm.getClient());
		tileMap.setPosition(0, -200);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/kosmosPanorama2.jpg", 0.1);
		//bg.setVector(-4, 0);
		
		player = new Player(tileMap);
		player.setAchievments(gsm.getLives(), gsm.getPoints());
		//player.setPosition(1150, 150);
		player.setPosition(100, 100);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		hud = new HUD(player);
		
		bgMusic = new AudioPlayer("/Music/space bash.wav");
		bgMusic.play();
	}
	
	/**
	 * Aktualizując stan gry, aktualizujemy parametry mapy, gracza, obiektów.
	 *  Pozycja tła jest ustawiana na taką samą jak pozycja mapy, mapa jest centrowana względem
	 *  pozycji gracza. Sprawdzane są wszystkie oddziaływania obiektów, nadane jest im sterowanie
	 *  zgodnie z mechaniką gry, wołane są funkcje do aktualizacji parametrów obiektów na na tym
	 *  poziomie
	 */
	public void update() {
		player.update();
		bgMusic.upadte();
		
		//tileMap.update();
		//bg.setPosition(bg.getX()-moveBg, bg.getY());
		
		tileMap.setPosition(
				GamePanel.WIDTH/2-player.getx(),
				GamePanel.HEIGHT/2-player.gety()
				);
		
		//bg.setPosition(tileMap.getx(), tileMap.gety());
		bg.update();
		
		player.checkCratesTNT(cratesTNT);
		player.checkApples(apples);
		player.checkAttack(enemies);
		
		//System.out.println("x = "+player.getx()+", y = "+player.gety());
		if(player.getx() < 77 && player.gety() < 47) {
			bgMusic.stop();
			gsm.updateAchievments(player.getHealth(), player.getNumApples());
			gsm.setState(GameStateManager.LEVEL3STATE);
		}
		
		if(player.gameOver()) {
			gsm.sendScore(player.getNumApples());
		}
		
		for(int i=0; i<cratesTNT.size(); i++) {
			CrateTNT cTNT = cratesTNT.get(i);
			cTNT.update();
			for(int j=0; j<cratesTNT.size(); j++) {
				if(j == i)
					continue;
				CrateTNT cTNTj = cratesTNT.get(j);
				if(cTNT.intersects(cTNTj)) {
					cTNT.changeDirection();
					cTNTj.changeDirection();
					
				}
			}
			
		}
		
		for(int i=0; i<apples.size(); i++) {
			Apple a = apples.get(i);
			a.update();
			
			if(a.isPicked()) {
				apples.remove(i);
				i--;
			}
		}
		
		for(int i=0; i<enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.update();
			
			if(e.isDead()) {
				enemies.remove(i);
				i--;
				explosions.add(
						new Explosion(e.getx(), e.gety())
						);
			}
		}
		
	
		for(int i=0; i<explosions.size(); i++) {
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove()) {
				explosions.remove(i);
				i--;
			}
		}
		

	}
	
	/**
	 * Rysuje obecny stan gry (stan pierwszy), czyli jego tło i mapę na ekranie oraz wszystkie
	 * obiekty, które zawiera 
	 */
	public void draw(Graphics2D g) {
		
		bg.draw(g);
		tileMap.draw(g);		
		player.draw(g);
		
		for(int i=0; i<cratesTNT.size(); i++) {
			cratesTNT.get(i).draw(g);
		}
		
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		for(int i=0; i<apples.size(); i++) {
			apples.get(i).draw(g);
		}
		
		for(int i=0; i<explosions.size(); i++) {
			explosions.get(i).setMapPosition(
					(int)tileMap.getx(),
					(int)tileMap.gety()
					);
		}
		
		hud.draw(g);
	}
	
	/**
	 * Umieszcza przeciwników i przeszkody na planszy w podanych punktach
	 */
	private void populateEnemies() {
		
		enemies = new ArrayList<Enemy>();
		Slugger s;
		Point[] points = new Point[] {
				new Point(80, 30),
				new Point(180, 30),
				new Point(810, 200),
				new Point(880, 200),
				new Point(1000, 200),
				new Point(11680, 200),
				new Point(11800, 200)
		};
		for(int i=0; i<points.length; i++) {
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
		apples = new ArrayList<Apple>();
		Apple a;
		Point[] pointsA = new Point[] {
				new Point(50, 140),
				new Point(100, 50),
				new Point(150, 50),
				new Point(250, 50),
				new Point(80, 300),
				new Point(130, 300),
				new Point(180, 300),
		};
		for(int i=0; i<pointsA.length; i++) {
			a = new Apple(tileMap);
			a.setPosition(pointsA[i].x, pointsA[i].y);
			apples.add(a);
		}
		
		cratesTNT = new ArrayList<CrateTNT>();
		CrateTNT cTNT;
		Point[] pointsTNT = new Point[] {
				new Point(60, 260),
				new Point(260, 230),
				new Point(220, 280),
				new Point(240, 330),
		};
		for(int i=0; i<pointsTNT.length; i++) {
			cTNT = new CrateTNT(tileMap);
			cTNT.setPosition(pointsTNT[i].x, pointsTNT[i].y);
			cratesTNT.add(cTNT);
		}
	}
	
	/**
	 * Obsługuje zdarzenie, które ma nastąpić po naciśnięciu danego klawisza
	 */
	public void keyPressed(int k) {
		
		if(k == KeyEvent.VK_LEFT) player.setLeft(true);
		if(k == KeyEvent.VK_RIGHT) player.setRight(true);
		if(k == KeyEvent.VK_UP) player.setUp(true);
		if(k == KeyEvent.VK_DOWN) player.setDown(true);
		
		if(k == KeyEvent.VK_W) player.setJumping(true);
		if(k == KeyEvent.VK_E) player.setGliding(true);
		if(k == KeyEvent.VK_R) player.setScratching();
		if(k == KeyEvent.VK_F) player.setFiring();

	}
	
	/**
	 * Obsługuje zdarzenie, które ma nastąpić po puszczeniu danego klawisza
	 */
	public void keyReleased(int k) {

		if(k == KeyEvent.VK_LEFT) player.setLeft(false);
		if(k == KeyEvent.VK_RIGHT) player.setRight(false);
		if(k == KeyEvent.VK_UP) player.setUp(false);
		if(k == KeyEvent.VK_DOWN) player.setDown(false);
		
		if(k == KeyEvent.VK_W) player.setJumping(false);
		if(k == KeyEvent.VK_E) player.setGliding(false);
	}

/*
	public void unload() {
		tileMap = null;
		bg = null;
		bg = null;
		player = null;
		enemies = null;
		apples = null;
		cratesTNT = null;
		explosions = null;
		hud = null;
		bgMusic = null;
	}
	*/
}







