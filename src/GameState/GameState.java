package GameState;

import Entity.Player;

/**
 * 
 * Abstrakcyjna klasa stanu gry. Stany gry mogą być różne, stąd wykorzystanie polimorfizmu.
 *
 */
public abstract class GameState {
	
	protected GameStateManager gsm;
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(java.awt.Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);

}
