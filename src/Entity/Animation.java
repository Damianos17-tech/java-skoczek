package Entity;

import java.awt.image.BufferedImage;
/**
 * 
 * Odpowiada za wszystkie animację obiektów widocznych na planszy
 *
 */
public class Animation {
	/**
	 * Obrazki użyte w animacji
	 */
	private BufferedImage[] frames;
	/**
	 * Obecnie wyświetlana zawartość
	 */
	private int currentFrame;
	/**
	 * Czas rozpoczęcia animacji
	 */
	private long startTime;
	/**
	 * Opóznienie między kolejnymi klatkami
	 */
	private long delay;
	
	private boolean playedOnce;
	
	public Animation() {
		playedOnce = false;
	}
	/**
	 * Ustawia obrazki animacji
	 * @param frames
	 */
	public void setFrames(BufferedImage[] frames) {
		this.frames = frames;
		currentFrame = 0;
		startTime = System.nanoTime();
		playedOnce = false;
	}
	/**
	 * Ustawia opóznienie animacji
	 * @param d
	 */
	public void setDelay(long d) {
		delay = d;
	}
	/**
	 * Ustawia bieżący rysunek animacji
	 * @param i
	 */
	public void setFrame(int i) {
		currentFrame = i;
	}
	/**
	 * Aktualizuje stan animacji, jeżeli minął odpowiedni czas zmienia klatkę
	 */
	public void update() {
		
		if(delay == -1) return;
		
		long elapsed = (System.nanoTime() - startTime) / 1000000;
		if(elapsed > delay) {
			currentFrame++;
			startTime = System.nanoTime();
		}
		if(currentFrame == frames.length) {
			currentFrame = 0;
			playedOnce = true;
		}
	}
	
	/**
	 * zwraca bieżący numer obrazka animacji
	 * @return
	 */
	public int getFrame() {
		return currentFrame;
	}
	/**
	 * zwraca bieżący obrazek animacji
	 * @return
	 */
	public BufferedImage getImage() {
		return frames[currentFrame];
	}
	/**
	 * Określa czy animacja została już raz włączona
	 * @return
	 */
	public boolean hasPlayedOnce() {
		return playedOnce;
	}
	
	
}
