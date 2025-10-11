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
 *  Klasa dziedzicząca po GameState. Zawiera szczegółowy opis zasad działania 3 stanu gry
 */
public class Level3State extends GameState {
	
	/**
	 * Stan gry posiada swoją własną mapę / planszę / poziom
	 */
	private TileMap tileMap;
	/**
	 *  Stan gry posiada swoje własne tło
	 */
	private Background bg;
	
	private Player player;
	private ArrayList<Enemy> enemies;
	private ArrayList<Apple> apples;
	private ArrayList<CrateTNT> cratesTNT;
	private ArrayList<Explosion> explosions;
	
	private HUD hud;
	
	private AudioPlayer bgMusic;
	
	public Level3State(GameStateManager gsm) {
		
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
		tileMap.loadMap("/Maps/mapa3000.map");
		//tileMap.loadMap(gsm.getClient()); //*networking
		tileMap.setPosition(0, -200);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/rainforest2.jpg", 0.1);
		
		player = new Player(tileMap);
		//player.setPosition(1150, 150);
		player.setAchievments(gsm.getLives(), gsm.getPoints());
		player.setPosition(100, 100);
		
		populateEnemies();
		
		explosions = new ArrayList<Explosion>();
		hud = new HUD(player);
		
		bgMusic = new AudioPlayer("/Music/jungle bash.wav");
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

		if(player.getx() > 1893 && player.gety() > 305) {
			bgMusic.stop();
			gsm.updateAchievments(player.getHealth(), player.getNumApples());
			gsm.setState(GameStateManager.LEVEL2STATE);
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
		
		if(player.gameOver()) {
			gsm.sendScore(player.getNumApples());
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
				new Point(30, 100),
				new Point(200, 100),
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
				new Point(300, 50),
				new Point(200, 200),
				new Point(200, 250),
				new Point(200, 300),
				new Point(190, 350),
				new Point(520, 320),
				new Point(550, 290),
				new Point(580, 270),
				new Point(610, 260),
				new Point(700, 350),
				new Point(880, 350),
				new Point(900, 350),
				new Point(940, 350),
				new Point(970, 350),
				new Point(1030, 350),
				new Point(1050, 350),
				new Point(1893, 300),
				
		};
		for(int i=0; i<pointsA.length; i++) {
			a = new Apple(tileMap);
			a.setPosition(pointsA[i].x, pointsA[i].y);
			apples.add(a);
		}
		
		cratesTNT = new ArrayList<CrateTNT>();
		CrateTNT cTNT;
		Point[] pointsTNT = new Point[] {
				new Point(60, 80),
				new Point(60, 260),
				new Point(1300, 260),
				new Point(1400, 260),
				new Point(1500, 260),
				new Point(1600, 260),
				new Point(1700, 260),
	
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

	
}







