package Networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Klasa obsługująca stronę klienta w połączeniu klient serwer
 */
public class Client implements Runnable{
	
	/**
	 * Gniazdko służące do połączenia z serwerem
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
	/**
	 * Wiadomość, która przyszła od serwera
	 */
	private String fromServer;
	private Thread thread;
	/**
	 * Liczba wierszy mapy
	 */
	private int numRows;
	/**
	 * Liczba kolumn mapy
	 */
	private int numCols;
	/**
	 * Otrzymana z serwera mapa do gry
	 */
	private int[][] mapC;
	/**
	 * Określa czy mapa została załadowana/pobrana pomyślnie
	 */
	private boolean mapLoaded;
	/**
	 * Opóznienie czasu oczekania na kolejny element do odebrania 
	 */
	private static final int sleepTime = 0;
	/**
	 * Ogólna liczba stanów w grze pobrana z serwera
	 */
	private int numLevels;
	/**
	 * Imię podane w trakcie danej gry
	 */
	private String guestName;
	/**
	 * Lista najlepszych wyników
	 */
	private ArrayList<String> rankList;
	/**
	 * Określa czy lista najlepszych wyników została odebrana pomyślnie
	 */
	private boolean rankListReceived;
	/**
	 * Konstruktor klienta
	 */
	public Client(int port) {
		try {
			mapLoaded = false;
			socket = new Socket("localhost", port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			br = new BufferedReader( new InputStreamReader(is));
			pw = new PrintWriter(os, true);
			
			thread = new Thread(this);
			thread.start();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Zwraca się do serwera o pobranie parametrów mapy
	 */
	public void getMap( int level ) {
		mapLoaded = false;
		Thread thread1 = new Thread(() -> {
			switch(level) {
			case 1:
				pw.println("updateLevel1");
				break;
			case 2:
				pw.println("updateLevel2");
				break;
			case 3:
				pw.println("updateLevel3");
				break;
			}
			
		//pw.println("updateLevel1");
		
		while(fromServer == null || !fromServer.equals("U")) {
			System.out.println("Oczekiwanie na mapę... fromServ= <" + fromServer + ">" );
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
		pw.println("getNumLevels");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		numLevels = Integer.parseInt(fromServer);
		System.out.println("Liczba poziomów to: "+numLevels);
		
		pw.println("getNumRows");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		numRows = Integer.parseInt(fromServer);
		//-------------
		
		pw.println("getNumCols");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		numCols = Integer.parseInt(fromServer);
		
		System.out.println("numRows = " + numRows);
		System.out.println("numCols = " + numCols);
		///ŁADOWANIE MAPY------------------------------------
		fromServer = null;
		mapC = new int[numRows][numCols];
		pw.println("getMap");
		
		for(int j=0; j<numRows; j++)
			for(int i=0; i<numCols; i++) {
				
				while(fromServer == null || fromServer.equals("")) {
					try {
						//fromServer = br.readLine();
						//System.out.println(" fS=<" + fromServer+">");
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				mapC[j][i] = Integer.parseInt(fromServer);
			
				System.out.println("mapC["+j+"]"+"["+i+"] = " + mapC[j][i]);
				fromServer = null;
				pw.println("next");
			}
		
		try {Thread.sleep(150);} catch(InterruptedException e) {e.printStackTrace();}
			
		if(fromServer != null && fromServer.equals("Map end") ) {
			mapLoaded = true;
			System.out.println("Mapa załadowana");
		}
		else 
			System.out.println("Błąd w ładowaniu mapy(to nigdy nie nastąpi)");
		
		});
	
		thread1.start();
		}
		
	public void run() {
		
		while(true) {
			//pw.println();
			try {
				fromServer = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Zwraca element mapy
	 */
	public int getMapElement(int r, int c) {
		return mapC[r][c];
	}
	/**
	 * Zwraca liczbę wierszy na mapie
	 */
	public int getNumRows(){
		return numRows;
	}
	/**
	 * Zwraca liczbę kolumn na mapie
	 */
	public int getNumCols(){
		return numCols;
	}
	/**
	 * Określa czy mapa została wczytana w całości
	 */
	public boolean ifMapLoaded() {
		return mapLoaded;
	}
	/**
	 * Zwraca liczbę stanów gry
	 */
	public int getNumLevels() {
		return numLevels;
	}
	/**
	 * Pobiera liczbę stanów gry z serwera
	 */
	public void setNumLevels() {
		
		Thread t0 = new Thread(() -> {
			
		pw.println("getNumLevels");
		
		while(fromServer == null || !fromServer.equals("")) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		numLevels = Integer.parseInt(fromServer);
		fromServer = null;
		System.out.println("Liczba poziomów to: "+numLevels);
		});
		t0.start();
	}
	/**
	 * Wysyła wynik gry na serwer
	 */
	public void sendScore(int score) {

		Thread t0 = new Thread(() -> {
			//System.out.println("scoreClient = "+score);
			pw.println("saveScore");
			while(fromServer == null || !fromServer.equals("readyToSave")) {
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			fromServer = null;
			pw.println(score);
			sendName(guestName);
			});
			t0.start();

	}
	/**
	 * Wysyła imię z gry na serwer
	 */
	public void sendName(String guestName) {
		Thread t0 = new Thread(() -> {
			
			pw.println("saveName");
			while(fromServer == null || !fromServer.equals("readyToSaveName")) {
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			fromServer = null;
			pw.println(guestName);
			});
			t0.start();
	}
	/**
	 * Zapisuje imię podane w trakcie gry
	 * @param s
	 */
	public void setGuestName(String s) {
		guestName = s;
	}
	/**
	 * Pobiera z serwera listę najlepszych wyników
	 */
	public void getRankList() {
		rankListReceived = false;
		rankList = new ArrayList<String>();
		fromServer = null;
		pw.println("getRankList");
		
		Thread threadRL = new Thread(() -> {
			while(true) {
				while(fromServer == null || fromServer.equals("")) {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(fromServer.equals("RankList end")) {
					rankListReceived = true;
					break;
				}
				
				rankList.add(fromServer);
				fromServer = null;
				pw.println("nextStat");	
			}
			fromServer = null;
		});
	
		threadRL.start();
	}
	/**
	 * Określa czy lista najlepszych wyników została odebrana pomyślnie
	 */
	public boolean rankListReceived() {
		return rankListReceived;
	}
	/**
	 * Zwraca listę najlepszych wyników
	 */
	public ArrayList<String> getScores(){
		return rankList;
	}
	
	/**
	public static void main(String[] args) {
		
		Client client = new Client(3003);
		client.getMap(1);
	}
	**/
	
	
}




