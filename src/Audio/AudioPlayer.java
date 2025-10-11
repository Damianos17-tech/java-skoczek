package Audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * Klasa obsługująca muzykę w grze
 *
 */
public class AudioPlayer {
	/**
	 * Klip jako podstawowy obiekt przechowujący strumień dzwiękowy
	 */
	private Clip clip;
	/**
	 * Konstruktor obiektu klasy AudioPlayer, wczytuje do pamięci plik muzyczny
	 * o podanej ścieżce
	 * @param s
	 */
	public AudioPlayer(String s) {
		try (InputStream is = getClass().getResourceAsStream(s)) {
			if (is == null) {
				System.out.println("❌ Nie znaleziono pliku dźwiękowego!");
				return;
			}

			// Zapis do pliku tymczasowego
			File tempFile = File.createTempFile("sound", ".wav");
			tempFile.deleteOnExit();
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
			}

			// Teraz odczyt z pliku, a nie ze strumienia JAR-a
			try (AudioInputStream ais = AudioSystem.getAudioInputStream(tempFile)) {
				clip = AudioSystem.getClip();
				clip.open(ais);
				clip.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Włącza granie strumienia dzwiękowego
	 */
	public void play() {
		stop();
		clip.setFramePosition(0);
		clip.start();

	}
	/**
	 * Zatrzymuje granie strumienia dzwiękowego
	 */
	public void stop() {
		if(clip == null)
			return;
		if(clip.isRunning())
			clip.stop();
	}
	/**
	 * Zamyka strumień dzwiękowy
	 */
	public void close() {
		stop();
		clip.close();
	}
	/**
	 * Aktualizuje strumień dzwiękowy, jeżeli utwór się skończył, zostanie włączony od początku
	 */
	public void upadte() {
		if(!clip.isRunning())
			play();
	}
	/**
	 * zeruje pamięć strumienia
	 */
	public void reset() {
		clip = null;
	}

}
