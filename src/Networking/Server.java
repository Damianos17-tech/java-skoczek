package Networking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.scene.layout.ConstraintsBase;

/**
 * 
 * Klasa obsługująca stronę serwera w połączeniu klient serwer
 *
 */
public class Server implements Runnable {

	/**
	 * Gniazdko służące do połączenia z klientem
	 */
	private ServerSocket serverSocket;
	/**
	 * Gniazdko służące do połączenia z klientem
	 */
	private Socket socket;
	/**
	 * Strumień wejściowy
	 */
	private InputStream is;
	/**
	 * Strumień wyjściowy
	 */
	private OutputStream os;
	/**
	 * Strumień do odczytywania danych
	 */
	private BufferedReader br;
	/**
	 * Strumień do wysyłania/pisania danych
	 */
	private PrintWriter pw;
	private Thread thread;
	/**
	 * Wiadomość, która przyszła od klienta
	 */
	private String fromClient;
	/**
	 * Presyłana mapa do gry
	 */
	private String[][] mapS ;
	/**
	 * Liczba wierszy mapy
	 */
	private int numRows;
	/**
	 * Liczba kolumn mapy
	 */
	private int numCols;
	/**
	 * Ścieżka gdzie znajduje się mapa
	 */
	private String mapSource;
	/**
	 * Obecny stan gry oznaczony numerem
	 */
	private int currentLevel;
	/**
	 * Ogólna liczba stanów w grze
	 */
	private static final int NUMLEVELS = 6;
	/**
	 * Opóznienie czasu do wysłania kolejnego element do klienta
	 */
	private static final int mapSendingSleepTime = 7;
	/**
	 * Imię podane w trakcie danej gry
	 */
	private String sessionName;
	/**
	 * Wynik uzyskany w trakcie danej gry
	 */
	private int sessionScore;
	/**
	 * Lista najlepszych wyników
	 */
	private ArrayList<String> scores;
	/**
	 * Ścieżka dostępu do pliku z listą wyników
	 */
	private String path;
	/**
	 * Maksymalna liczba wyników, która będzie brana pod uwagę
	 */
	private final int ranksNum = 10;
	/**
	 * Konstruktor serwera
	 */
	public Server(int port) {
		try {
			System.out.println("Nr portu serwera: " + port);
			System.out.println("Oczekiwanie na połączenie..");
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			is = socket.getInputStream();
			os = socket.getOutputStream();
			br = new BufferedReader(new InputStreamReader(is));
			pw = new PrintWriter(os, true);
			
			path = "Scores.txt";
			scores = new ArrayList<String>();
			loadStats();
			//
			thread = new Thread(this);
			thread.start();
			//
			System.out.println("Uzyskano połączenie: " + port);
			
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void run() {
		
		while(true) {
			//pw.println();
			try {
				fromClient = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//if(fromClient != null )
				//System.out.print("fromClient = " + fromClient);
			switch( fromClient ) {
				case "getNumRows":
					sendNumRows();
					break;
				case "getNumCols":
					sendNumCols();
					break;
				case "getNumLevels":
					sendNumLevels();
					break;
				case "getMap":
					sendMap();
				try {Thread.sleep(50);} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
					break;
				case "updateLevel1":
					updateLevel(1);
					break;
				case "updateLevel2":
					updateLevel(2);
					break;
				case "updateLevel3":
					updateLevel(3);
					break;
				case "saveScore":
					saveScore();
					break;
				case "saveName":
					saveName();
					break;
				case "getRankList":
					sendRankList();
					break;
				default:
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Wysyła liczbę wierszy na mapie
	 */
	public void sendNumRows() {
		pw.println(numRows);
		fromClient = null;
	}
	/**
	 * Wysyła liczbę kolumn na mapie
	 */
	public void sendNumCols() {
		pw.println(numCols);
		fromClient = null;
	}
	/**
	 * Wysyła liczbę stanów gry do klienta
	 */
	public void sendNumLevels(){
		pw.println(NUMLEVELS);
		fromClient = null;
	}
	/**
	 * Wysyła mapę gry do klienta
	 */
	public void sendMap() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		fromClient = null;
		Thread tsm = new Thread(() -> {
		
			for(int j=0; j<numRows; j++)
				for(int i=0; i<numCols; i++)
				{
					fromClient = null;
					pw.println(mapS[j][i]);
					do {
						
						//System.out.println("j = " + j +", i = " + i);
						try {
							Thread.sleep(mapSendingSleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					while(fromClient == null || (!fromClient.equals("next")));
				}
			pw.println("Map end");
			});
		tsm.start();
				
	}
	
	/**
	 * Odczytuje mapę z pliku na dysku
	 */
	public void loadMap() {
		
		try {

			//InputStream inTemp = getClass().getResourceAsStream(mapSource);
			//BufferedReader brTemp = new BufferedReader(new InputStreamReader(inTemp));
			
			FileReader fr = new FileReader(mapSource);
			BufferedReader brTemp = new BufferedReader(fr);
			
			//dwie pierwsze linie z pliku konfiguracyjnego
			numCols = Integer.parseInt(brTemp.readLine());
			numRows = Integer.parseInt(brTemp.readLine());
			mapS = new String[numRows][numCols];

			//cięcie pliku konfiguracyjnego na stringi oddzielone białymi znakami
			// następnie konwersja na liczby, które oznaczają poszczególne klocki
			String delims = "\\s+";
			for(int row = 0; row < numRows; row++) {
				String line = brTemp.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0; col < numCols; col++) {
					mapS[row][col] = tokens[col];
				}
			}
			brTemp.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Map loaded to server "+numRows + ", "+numCols);
	}
	/**
	 * Ustawia informację jaki jest obecny stan gry
	 */
	public void updateLevel(int l) {
		
		currentLevel = l;
		System.out.println(System.getProperty("user.dir"));
		switch(currentLevel) {
		case 0:
			break;
		case 1:
			mapSource = "Resources/Maps/mapa1000.map";
			loadMap();
			break;
		case 2:
			mapSource = "Resources/Maps/mapa4000.map";
			loadMap();
			break;
		case 3:
			mapSource = "Resources/Maps/mapa3000.map";
			loadMap();
			break;
		}
		fromClient = null;
		pw.println("U");
	}
	/**
	 * Oczekuje na wynik od klienta i zapisuje go w pamięci
	 */
	private void saveScore() {
		Thread t0 = new Thread(() -> {
			
			fromClient = null;
			pw.println("readyToSave");
			
			while(fromClient == null || fromClient.equals("")) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("scoreSERVER = "+fromClient);
			sessionScore = Integer.parseInt(fromClient);
			fromClient = null;
			});
			t0.start();
			
	}
	
	/**
	 * Oczekuje na imię gracza od klienta i zapisuje je w pamięci
	 *
	 */
	public void saveName() {
		System.out.println("funckja saveName");
		Thread t0 = new Thread(() -> {
			
			fromClient = null;
			pw.println("readyToSaveName");
			
			while(fromClient == null || fromClient.equals("")) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("nameSERVER = "+fromClient);
			sessionName = fromClient;
			fromClient = null;
			String sessionRank = sessionName + " " + sessionScore;
			scores.add(sessionRank);
			saveStatsToFile();
			System.out.println("Game Over");
			});
			t0.start();
			
			
	}
	/**
	 * Odczytuje listę wyników z pliku
	 */
	private void loadStats() {
		String line = new String("");
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(path)
					);
			
			 while (line != null) {
				 line = br.readLine();
				 if(line != null && i < ranksNum) {
					 scores.add(line);
					 i++;
				 }	 
			 }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sortScores();
		printStats();
	}
	
	/**
	 * Sortuje listę wyników od nalpszego do najgorszego
	 */
	private void sortScores() {
		Collections.sort(scores, new Comparator<String>() {

	        public int compare(String score1, String score2)
	        {
	        	String[] points1s = score1.split("\\s+");
	        	String[] points2s = score2.split("\\s+");
	        	int points1 = Integer.parseInt(points1s[1]);
	        	int points2 = Integer.parseInt(points2s[1]);
	            if(points1 == points2)
	                return 0;
	            else if(points1 < points2)
	                return 1;
	            else
	                return -1;
	        }
	    });
	}
	/**
	 * Zapisuje do pliku listę najlepszych wyników
	 */
	private void saveStatsToFile() {
		PrintWriter writer;
		sortScores();
		try {
			writer = new PrintWriter(path, "UTF-8");
			for(int i=0; i<scores.size(); i++) {
				writer.println(scores.get(i));
			}
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void printStats() {
		
		for(int i=0; i<scores.size(); i++) {
			System.out.println(scores.get(i));
		}
	}
	
	/**
	 * Dodaje wynik do listy wyników na odpowiednie miejsce, usuwa ich nadmiar jeżeli trzeba
	 * @param score
	 */
	public void addScore(String score) {
		scores.add(score);
		sortScores();
		if(scores.size() > ranksNum) {
			scores.remove(scores.size()-1);
		}
	}
	
	/**
	 * Wysyła listę wyników do klienta
	 */
	public void sendRankList() {
		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			fromClient = null;
			Thread tsm = new Thread(() -> {
			
				for(int i=0; i<scores.size(); i++)
				{
					fromClient = null;
					pw.println(scores.get(i));
					do {
						try {
							Thread.sleep(mapSendingSleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					while(fromClient == null || (!fromClient.equals("nextStat")));
				}
				pw.println("RankList end");
				});
			tsm.start();
	}
	/**
	 * Niezależność serwera - funkcja main do jego inicjalizacji
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Server server = new Server("s");
		//server.saveStatsToFile();
		//server.loadStats();
		//server.sortScores();
		//server.printStats();
		
		new Server(12019);
		
	}
	
}
