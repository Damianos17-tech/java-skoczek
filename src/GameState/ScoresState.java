package GameState;

import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

import Audio.AudioPlayer;

public class ScoresState extends GameState {
	
	/**
	 * Obiekt służący do obsługi tła
	 */
	private Background bg;
	/**
	 * Obiekt służący do obsługi muzyki w tle
	 */
	private AudioPlayer bgMusic;
	/**
	 * Obiekt służący do obsługi czcionki
	 */
	private Font font;
	
	/**
	 * Konstruktor stanu gry "wyniki". Nawiązuje pierwszy raz połączenie z serwerem jeżeli w menu
	 * zostanie wybrana opcja wyniki
	 */
	public ScoresState(GameStateManager gsm, AudioPlayer bgM) {
		
		this.gsm = gsm;
		init();
		
		try {
			
			bg = new Background("/Backgrounds/crashmenu1.jpg", 1);
			font = new Font("Cambria", Font.BOLD, 16);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		bgMusic = bgM;
	}
	
	/**
	 * Funkcja nie robi nic, oprócz tego, że jest
	 */
	public void init() {}
	
	/**
	 * Funkcja nie robi nic, oprócz tego, że jest
	 */
	public void update() {
	}
	
	/**
	 * Rysuje stan gry "wyniki"
	 */
	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);
		
		// draw title
		g.setColor(Color.YELLOW);
		g.setFont(font);
		g.drawString("Najlepsze wyniki:", 100, 20);
		g.setColor(Color.RED);
		g.drawString("Funkcja niedostępna ", 100, 60);
		g.drawString("w wersji Serverless!", 100, 80);

//		for(int i=0; i<gsm.game.getScores().size(); i++) {
//			g.drawString(i+1+". "+ gsm.client.getScores().get(i), 100, (i+2)*20);
//		}
		
	}
	
	/**
	 * Jeżeli obejrzeliśmy wyniki i chcemy wrócić do menu, należy kliknąć enter
	 */
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER){
			gsm.setMenuState(gsm.MENUSTATE, bgMusic);
		}
		
	}
	/**
	 * Funkcja nie robi nic, oprócz tego, że jest
	 */
	public void keyReleased(int k) {}


}










