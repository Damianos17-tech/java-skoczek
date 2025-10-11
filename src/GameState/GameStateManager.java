package GameState;

import Audio.AudioPlayer;
import Main.Game;
import Main.GamePanel;
//import Networking.Client;

/**
 * Klasa zarządzająca stanami gry
 */
public class GameStateManager {
	public Game game;
	/**
	 * Tablica stanów gry, dostępnych w aplikacji
	 */
	private GameState[] gameStates;
	/**
	 * Liczba przyporządkowana odpowiedniemu stanowiw grze
	 */
	private int currentState;
	/**
	 * globalna liczba punktów, które zdobył gracz
	 */
	private int score;
	/**
	 * globalna liczba żyć, które posiada gracz
	 */
	private int chances;
	
	private boolean gameTerminated;
	
	public static int NUMGAMESTATES = 6;
	public static final int MAXLIVESNUM = 5;
	/**
	 * Początkowy stan gry oznaczony liczbą 0. Menu gry i lista opcji. Pierwszy widok
	 * w aplikacji. Kolejne liczby będą oznaczały poziomy w grze / nr planszy.
	 */
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int LEVEL2STATE = 2;
	public static final int LEVEL3STATE = 3;
	public static final int SCORESSTATE = 4;
	public static final int NAMESTATE = 5;
	
	/**
	 * Domyślny numer portu
	 */
	public static final int portNum = 12019;
	
	/**
	 * konstruktor klasy GameStateManager. Tworzona jest tablica stanów występujących w grze.
	 * Ustawiany jest stan gry, który wystąpi po uruchomieniu aplikacji
	 */
	public GameStateManager() {

		score = 0;
		chances = MAXLIVESNUM;
		
		gameStates = new GameState[NUMGAMESTATES];
		
		currentState = NAMESTATE;
		loadState(currentState);
		
	}
	
	public GameStateManager(GamePanel gP) {

		score = 0;
		chances = MAXLIVESNUM;
		
		gameStates = new GameState[NUMGAMESTATES];
		
		currentState = NAMESTATE;
		gameStates[currentState] = new NameState(this, gP);
		
	}
	
	/**
	 * Funkcja ładuje dany stan gry (przełącza na podany jako liczba całkowita).
	 * Poszczególne stany opisane są przez klasy dziedziczące po klasie GameState
	 */
	private void loadState(int state) {
		if(state == MENUSTATE)
			gameStates[state] = new MenuState(this);
		else if(state == LEVEL2STATE)
			gameStates[state] = new Level2State(this);
		else if(state == LEVEL3STATE)
			gameStates[state] = new Level3State(this);
		else if(state == NAMESTATE)
			gameStates[state] = new NameState(this);
		else if(state == SCORESSTATE)
			;
			//gameStates[state] = new Level3State(this);
	}
	
	/**
	 * Skoro można załadować stan, można też go usunąć z tablicy stanów. 
	 * Stworzona na wypadek, gdy będzie ładowany ponownie ten sam stan, aby móc go 
	 * zresetować
	 */
	private void unloadState(int state) {
		gameStates[state] = null;
	}
	
	/**
	 * Ustawia bieżący stan gry (wybiera go z tablicy istniejących stanów)
	 */
	public void setState(int state) {
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}
	
	/**
	 * Ustawia bieżący stan gry, gdy dodatkowo chcemy utrzymać biężący dzwięk
	 */
	public void setMenuState(int state, AudioPlayer bgM) {
		
		unloadState(currentState);
		currentState = state;
		gameStates[state] = new MenuState(this, bgM);
	}
	
	/**
	 * Ustawia bieżący stan gry, gdy dodatkowo chcemy utrzymać biężący dzwięk
	 */
	public void setScoresState(int state, AudioPlayer bgM) {
		
		unloadState(currentState);
		currentState = state;
		gameStates[state] = new ScoresState(this, bgM);
	}
	/**
	 * Woła metodę update() dla bieżącego stanu gry. Klasa GameStateManager
	 * jest zarządcą z definicji
	 */
	public void update() {
		//System.out.println("currSt = "+currentState);
		try {
			if(gameStates[currentState] != null) {
				gameStates[currentState].update();
				}
	
			} catch(Exception e) {}
	}
	
	/**
	 * Woła metodę draw() dla bieżącego stanu gry. Klasa GameStateManager
	 * jest zarządcą z definicji
	 */
	public void draw(java.awt.Graphics2D g) {
		if(gameStates[currentState] != null) {
			gameStates[currentState].draw(g);
		}
		else {
			g.setColor(java.awt.Color.BLACK);
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
	}
	
	/**
	 * Woła metodę keyPressed() dla bieżącego stanu gry. Klasa GameStateManager
	 * jest zarządcą z definicji
	 */
	public void keyPressed(int k) {
		if(gameStates[currentState] == null)
			return;
		gameStates[currentState].keyPressed(k);
	}
	
	/**
	 * Woła metodę keyReleased() dla bieżącego stanu gry. Klasa GameStateManager
	 * jest zarządcą z definicji
	 */
	public void keyReleased(int k) {
		if(gameStates[currentState] == null)
			return;
		gameStates[currentState].keyReleased(k);
	}
	
	//IV etap
	
	public Game getGame() {
		return game;
	}
	
	public int getLives() {
		return chances;
	}
	
	/**
	 * Zwraca liczbę punktów zdobytych przez gracza w czasie bieżącej sesji
	 * @return
	 */
	public int getPoints() {
		return score;
	}
	
	/**
	 * Nadpisuje wyniki gracza: życia i punkty
	 */
	public void updateAchievments(int lives, int points) {
		chances = lives;
		score = points;
	}
	
	/**
	 * Wysyła wynik na serwer
	 * @param score
	 */
	public void sendScore(int score) {
		if(!gameTerminated) {
			gameTerminated = true;
		}
	}

}









