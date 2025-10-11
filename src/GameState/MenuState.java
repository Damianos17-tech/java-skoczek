package GameState;

import TileMap.Background;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;

import Audio.AudioPlayer;
import Entity.Player;

/**
 *  Klasa dziedzicząca po GameState. Zawiera szczegółowy opis zasad działania stanu "menu" gry
 */
public class MenuState extends GameState {
	
	/**
	 * Przechowuje obraz tła stanu gry "menu"
	 */
	private Background bg;
	/**
	 * Przechowuje muzykę tła stanu gry "menu"
	 */
	private AudioPlayer bgMusic;
	/**
	 * Obecny wybór z menu. Informuje, który napis podświelić
	 */
	private int currentChoice = 0;
	/**
	 * Opcje dostępne w menu jako napisy
	 */
	private String[] options = {
			/*
		"Start",
		"Scores",
		"Quit"
		*/
		"Graj",
		"Wyniki",
		"Wyjdz"
	};
	/**
	 * Kolor czcionki tytułowej
	 */
	private Color titleColor;
	/**
	 * Typ czcionki tytułowej
	 */
	private Font titleFont;
	/**
	 * Typ czcionki używanej w menu
	 */
	private Font font;
	
	/**
	 * Konstruktor stanu gry "menu". Ustawia czcionkę, parametry tła
	 */
	public MenuState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		try {
			
			bg = new Background("/Backgrounds/crashmenu1.jpg", 1);
			bg.setVector(-0.1, 0);
			
			titleColor = Color.RED;
			titleFont = new Font(
					"Calibri",
					Font.BOLD,
					28);
			
			font = new Font("Cambria", Font.BOLD, 16);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		//bgMusic = new AudioPlayer("/Music/crash menu.mp3");
		//bgMusic.play();
	}
	/**
	 * Konstruktor stanu gry "menu". Ustawia czcionkę, parametry tła
	 */
	public MenuState(GameStateManager gsm, AudioPlayer bgM) {
		
		this.gsm = gsm;
		
		try {
			
			bg = new Background("/Backgrounds/crashmenu1.jpg", 1);
			bg.setVector(-0.1, 0);
			
			//titleColor = new Color(128, 0, 0);
			titleColor = Color.RED;
			titleFont = new Font(
					"Calibri",
					Font.BOLD,
					28);
			
			font = new Font("Cambria", Font.BOLD, 16);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		bgMusic = bgM;
		//bgMusic.play();
	}
	/**
	 * Funckja nie robi nic, oprócz tego, że istnieje
	 */
	public void init() {}
	/**
	 * Funckja nie robi nic, oprócz tego, że istnieje
	 */
	public void update() {
		
	}
	/**
	 * Rysuje menu gry
	 */
	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("CRASH THE HOPPER", 50, 70);
		
		// draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(Color.BLUE);
			}
			else {
				g.setColor(Color.GREEN);
			}
			g.drawString(options[i], 140, 120 + i * 20);
		}
		
	}
	/**
	 * Obsługuje zdarzenie, które ma nastąpić po wybranu danej opcji
	 */
	private void select() {
		if(currentChoice == 0) {
			bgMusic.stop();
			gsm.setState(GameStateManager.LEVEL2STATE);
		}
		if(currentChoice == 1) {
			gsm.setScoresState(gsm.SCORESSTATE, bgMusic);
		}
		if(currentChoice == 2) {
			System.exit(0);
		}
	}
	/**
	 * Obsługuje zdarzenie, które ma nastąpić po naciśnięciu danego klawisza
	 */
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER){
			select();
		}
		if(k == KeyEvent.VK_UP) {
			currentChoice--;
			if(currentChoice == -1) {
				currentChoice = options.length - 1;
			}
		}
		if(k == KeyEvent.VK_DOWN) {
			currentChoice++;
			if(currentChoice == options.length) {
				currentChoice = 0;
			}
		}
	}
	/**
	 * Obsługuje zdarzenie, które ma nastąpić po puszczeniu danego klawisza
	 */
	public void keyReleased(int k) {}

	/**
	 * Nie robi nic, istnieje ze względu na polimorfizm
	 */
	public void createConnection() {
		// TODO Auto-generated method stub
		
	}
	
}










