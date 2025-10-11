package Main;

import javax.swing.JFrame;

public class Game {
	/**
	 * Funkcja główna gry. Tworzy najpierw obiekt typu JFrame. W nim będą dodawane
	 * i rysowane różne obiekty związane z grafiką
	 */
	public static void main(String[] args) {

		System.out.println("Java version: " + System.getProperty("java.version"));
		//System.out.println(System.getProperty("user.dir"));


		JFrame window = new JFrame("Crash Jumper");

		window.setContentPane(new GamePanel(window));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.pack();
		//window.setVisible(true);
		
	}
	
}
