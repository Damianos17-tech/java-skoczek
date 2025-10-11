package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.PasswordAuthentication;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import GameState.GameStateManager;

public class GamePanel extends JPanel 
	implements Runnable, KeyListener{
	
	/**
	 * jeden z wymiarów okna, w którym wyświetlana będzie gra. Określa szerokość okna.
	 */
	public static final int WIDTH = 320;
	/**
	 * jeden z wymiarów okna, w którym wyświetlana będzie gra. Określa wysokość okna.
	 */
	public static final int HEIGHT = 240;
	/**
	 * SCALE - skala, całkowita wielokrotność wymiarów okna
	 */
	public static final int SCALE = 2;
	
	/**
	 * Wątek główny gry. 
	 */
	private Thread thread;
	/**
	 * Zmienna mówiąca o tym, czy pętla gry jest nadal wykonywana 
	 */
	private boolean running;
	/**
	 * FPS - Frames per second. Ta zmienna to częstotliwość odświeżania grafki. Innymi słowy
	 * to liczba narysowań zawartości na ekranie gry w ciągu jednej sekundy 
	 */
	private int FPS = 60;
	/**
	 * Czas w milisekundach, który przypada na jednorazowe wyświetlenie zawartości graficznej
	 */
	private long targetTime = 1000 / FPS;
	
	/**
	 * Obrazek panelu gry, który dziedziczy po JPanel
	 */
	private BufferedImage image;
	/**
	 * Obiekt klasy języka java, który obsługuje rysowanie komponentów na ekranie
	 */
	private Graphics2D g;
	
	/**
	 * Obiekt klasy GameStateManager, który zarządza stanami gry
	 */
	private GameStateManager gsm;
	
	/**
	 * Zmienna określająca czy gra jest zatrzymana czy nie
	 */
	private boolean pause;
	/**
	 * Konstruktor obiektu GamePanel, który dziedziczy po klasie JPanel.
	 * Na nim będzie rysowana grafika. Można go dodać do obiektu klasy JFrame.
	 * Również w obiekcie klasy JPanel mogą być umieszczane komponenty graficzne.
	 */
	public GamePanel(JFrame window) {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
		this.window = window; 
	}
	
	/**
	 * Pobudza wątek gry do działania, włącza go, dodaje słuchaczy (tutaj słuchacza klawiszy
	 * z klawiatury)
	 */
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	/**
	 * Inicjalizuje panel gry i kontekst graficzny, oraz przypisuje pamięć wołając kontruktor dla
	 * gsm - obiektu klasy GameStateManager. Program rozpocznie działanie głównej pętli gry.
	 */
	private void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		running = true;
		
		gsm = new GameStateManager(this);
		
	}
	
	/**
	 * Metoda obowiązkowa podczas używania interfejsu runnable. Wewnątrz tej funkcji 
	 * zdefiniowana jest pętla gry. Metoda run() na początku woła funkcję init(). Następnie
	 * po sprawdzeniu flagi running (czyli tego czy pętla gry ma się kręcić) ustawia czas
	 * startu. Oczywiście po wywołaniu init() zawsze za pierwszym razem warunek zostanie 
	 * spełniony. Zaraz po zapamiętaniu czasu startu zostaje zaktualizowany stan gry i 
	 * narysowane potrzebne elementy graficzne na ekranie. Gdy to się wykona, sprawdzany
	 * jest czas, jaki te czynności zajęły. Zazwyczaj czas przeznaczony na aktualizację 
	 * stanu gry (targetTime) jest dłuższy niż tego wymaga program. Zatem wątek gry, który 
	 * wykonał swoją pracę czeka - zostaje uśpiony do następnego punktu w czasie, który 
	 * zależy od wartości zmiennej FPS.
	 */
	public void run() {
		
		init();
		
		long start;
		long elapsed;
		long wait;
		
		//pętla gry
		while(running) {
			
			start = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			elapsed = System.nanoTime() - start;
			
			wait = targetTime - elapsed / 1000000;
			if(wait < 0) wait = 3;
			
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * Woła funkcję update() bieżącego stanu gry. Funckja prywatna wywoływana z funkcji run().
	 */
	private void update() {
		if(!pause) {
			gsm.update();
		}
		
	}
	
	/**
	 * Woła funkcję draw() bieżącego stanu gry. Funckja prywatna wywoływana z funkcji run().
	 */
	private void draw() {
		gsm.draw(g);
	}
	
	/**
	 * ??????????????????????????
	 */
	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g2.dispose();
	}
	
	public void keyTyped(KeyEvent key) {}
	
	/**
	 * Słuchacz klawiszy z klawiatury. Woła funkcję o tej samej nazwie dla bieżącego stanu
	 * gry. Klasa GameStateManager nie wykorzystuje interfejsu runnable ani nie wykorzystuje
	 * słuchaczy. Informacje związane ze zdarzeniami otrzymuje z klasy GamePanel.
	 */
	public void keyPressed(KeyEvent key) {
		gsm.keyPressed(key.getKeyCode());
		if(key.getKeyCode() == KeyEvent.VK_P ) {
			pause = !pause;
		}
	}
	
	/**
	 * Słuchacz klawiszy z klawiatury. Woła funkcję o tej samej nazwie dla bieżącego stanu
	 * gry. Klasa GameStateManager nie wykorzystuje interfejsu runnable ani nie wykorzystuje
	 * słuchaczy. Informacje związane ze zdarzeniami otrzymuje z klasy GamePanel.
	 */
	public void keyReleased(KeyEvent key) {
		gsm.keyReleased(key.getKeyCode());
	}
	
	private JFrame window;
	
	/**
	 * Funkcja opózniająca wołanie setVisible(true) w celu umożliwienia pokazania wszystkich 
	 * komponentów graficznych. Jej istnienie wynika z logiki programu
	 */
	public void setVisible() {
		window.setVisible(true);
	}
	
}
















