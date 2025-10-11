package GameState;

import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Audio.AudioPlayer;
import Main.GamePanel;

public class NameState extends GameState {
	
	/**
	 * Obiekt służący do obsługi tła
	 */
	private Background bg;
	
	private Color titleColor;
	private Font titleFont;
	
	private AudioPlayer bgMusic;
	
	private JTextField textField;
	private JButton button;
	private JPanel panel;
	
	private boolean clicked;
	
	public NameState(GameStateManager gsm) {
		
		this.gsm = gsm;
		
		try {

			System.out.println(System.getProperty("user.dir"));
			bg = new Background("/Backgrounds/crashmenu1.jpg", 1);
			titleColor = Color.RED;
			titleFont = new Font("Calibri", Font.BOLD, 20);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		bgMusic = new AudioPlayer("/Music/crashmenu.wav");
		bgMusic.play();
	}
	
	public NameState(GameStateManager gsm, GamePanel gP) {
		
		this.gsm = gsm;
		try {

			bg = new Background("/Backgrounds/crashmenu1.jpg", 1);
			titleColor = Color.RED;
			titleFont = new Font("Calibri", Font.BOLD, 20);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		bgMusic = new AudioPlayer("/Music/crashmenu.wav");

		/**
		 *
		 *
		 *
		 *
		 *
		 *
		 */


		/**
		 *
		 *
		 *
		 *
		 *
		 */
		bgMusic.play();
		
		formatTextField();
		formatButton();
		gP.setLayout(null);
		gP.add(button);
		gP.add(textField);
		gP.setVisible();
		
		panel = gP;
		gsm.setMenuState(gsm.MENUSTATE, bgMusic);
	}
	
	public void init() {}
	
	public void update() {
		bgMusic.upadte();
		buttonUpdate();
	}
	
	public void draw(Graphics2D g) {
	
		// draw bg
		bg.draw(g);
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Wpisz imię:", 110, 70);
		
		textField.repaint();
		button.repaint();
	}
	
	public void keyPressed(int k) {
		if(k == KeyEvent.VK_ENTER) {
			gsm.setMenuState(gsm.MENUSTATE, bgMusic);
		}
	}
	public void keyReleased(int k) {}

	
	public void formatTextField() {
		textField = new JTextField(20);
		textField.setLocation(new Point(170, 200));
		textField.setSize(300, 35);
		textField.setFont(new Font("Calibri", Font.PLAIN, 22));
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setForeground(Color.RED);
		textField.setBackground(Color.YELLOW);
	}
	
	public void formatButton() {
		button = new JButton("OK");
		button.setLocation(new Point(280, 300));
		button.setSize(70, 40);
		button.setFont(new Font("Calibri", Font.PLAIN, 22));
		button.setHorizontalAlignment(JTextField.CENTER);
		button.setForeground(Color.RED);
		button.setBackground(Color.YELLOW);
	}
	
	public void buttonUpdate() {
	
		if(!button.getModel().isPressed() || clicked)
			return;
		clicked = true;
		String s = textField.getText();
		if(s.length() >= 12) {
			s = s.substring(0, 11);
		}
		if(s == null || s.length() == 0) {
			clicked = false;
			return;
		}
		//gsm.saveGuestName(s);
		panel.remove( textField );
		panel.remove( button );
		panel.revalidate();
		panel.repaint();
		gsm.setMenuState(gsm.MENUSTATE, bgMusic);
	}

}








