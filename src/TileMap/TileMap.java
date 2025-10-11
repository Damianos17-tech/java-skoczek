package TileMap;

import java.awt.*;
import java.awt.image.*;

import java.io.*;
import java.nio.channels.ClosedByInterruptException;

import javax.imageio.ImageIO;

import Main.GamePanel;
import Networking.Client;

/**
 * Klasa planszy/mapy, na której toczy się gra. 
 */
public class TileMap {
	
	/**
	 * Położenie mapy względem osi x
	 */
	private double x;
	/**
	 * Położenie mapy względem osi y
	 */
	private double y;
	
	/**
	 * Minimalne położenie mapy względem osi x
	 */
	private int xmin;
	/**
	 * Minimalne położenie mapy względem osi y
	 */
	private int ymin;
	/**
	 * Maksymalne położenie mapy względem osi x
	 */
	private int xmax;
	/**
	 * Maksymalne położenie mapy względem osi y
	 */
	private int ymax;
	
	/**
	 * Parametr do wyśrodkowywania mapy, jak dotąd nie odgrywa znaczacej roli w tym programie
	 */
	private double tween;
	
	/**
	 * Mapa jako dwuwymiarowa tablica liczb. Każde miejsce w tablicy odpowiada typowi klocka
	 * który zostanie narysowany na ekranie
	 */
	private int[][] map;
	/**
	 * Określa rozmiar jednego bloczka
	 */
	private int tileSize;
	/**
	 * Liczba wierszy mapy liczona w klockach
	 */
	private int numRows;
	/**
	 * Liczba kolumn mapy liczona w klockach
	 */
	private int numCols;
	/**
	 * Szerokość mapy
	 */
	private int width;
	/**
	 * Wysokość mapy
	 */
	private int height;
	
	/**
	 * Obraz zawierający reprezentację graficzną zestawu klocków 
	 */
	private BufferedImage tileset;
	/**
	 * Liczba klocków na mapie liczona po długości osi x
	 */
	private int numTilesAcross;
	/**
	 * Tablica całych obiektów typu Tile
	 */
	private Tile[][] tiles;
	
	/**
	 * kraniec wiersza, brany pod uwagę podczas rysowania
	 */
	private int rowOffset;
	/**
	 * kraniec kolumny, brany pod uwagę podczas rysowania
	 */
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;
	
	/**
	 * Konstruktor mapy, ustawia podany rozmiar klocków.
	 * Oblicza ile wierszy i kolumn złożonych z klocków będzie rysowanych w pętli gry.
	 * Nie chcemy rysować całej planszy. Obciążałoby to niepotrzebnie zasoby komputera.
	 * Ustawia początkową pozycję mapy
	 */
	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		//tween = 0.07;
		
		setPosition(0, 0);
	}
	
	/**
	 * Ładuje grafikę każdego klocka, wysinając podobrazy z obrazu z zestawem klocków
	 */
	public void loadTiles(String s) {
		
		try {

			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			//ile klocków wszerz
			numTilesAcross = tileset.getWidth() / tileSize;
			//2, bo tworzymy nową tablicę dwóch wierszy klocków (tyle jest w obrazku)
			tiles = new Tile[2][numTilesAcross];
			
			BufferedImage subimage;
			//wczytujemy 2 wiersze na raz
			for(int col = 0; col < numTilesAcross; col++) {	
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				if(col == 0)
					tiles[0][col] = new Tile(subimage, Tile.NORMAL);
				else
					tiles[0][col] = new Tile(subimage, Tile.BLOCKED);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Ładuje mapę, kiedy nie jest używane programowanie sieciowe
	 * @param s
	 */
	public void loadMap(String s) {
		
		try {
			
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			//dwie pierwsze linie z pliku konfiguracyjnego
			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			
			//cięcie pliku konfiguracyjnego na stringi oddzielone białymi znakami
			// następnie konwersja na liczby, które oznaczają poszczególne klocki
			String delims = "\\s+";
			for(int row = 0; row < numRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Pobiera wielkość klocka
	 */
	public int getTileSize() { return tileSize; }
	public double getx() { return x; }
	public double gety() { return y; }
	/**
	 * Pobiera szerokość mapy
	 */
	public int getWidth() { return width; }
	/**
	 * Pobiera wysokość mapy
	 */
	public int getHeight() { return height; }
	
	/**
	 * Zwraca typ klocka, tzn. czy stanowi on blokadę dla innych obiektów mapy
	 * @param row
	 * @param col
	 * @return
	 */
	public int getType(int row, int col) {
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc %  numTilesAcross;
		return tiles[r][c].getType();
	}
	
	public void setTween(double d) { tween = d; }
	
	/**
	 * Ustawia pozycję mapy na panelu gry
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		
		fixBounds();
		
		//oblicza która kloumna i wiersz mają być krawędziami rysowanej planszy
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
	}
	
	/**
	 * Poprawia, ustala, dopasowuje ustawienie granic mapy na panelu gry
	 */
	private void fixBounds() {
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
		
	}
	

	/**
	 * Rysuje mapę gry, nie w całości, ale w zakresie widoczności
	 */
	public void draw(Graphics2D g) {
		
		for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			
			if(row >= numRows) 
				break;
				
			for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
				
				if(col >= numCols)
					break;
				
				if(map[row][col] == 0)
					continue;
				
				//rc to liczba oznaczająca jakiś kafelek
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;

				tiles[r][c].draw(g, (int)x + col * tileSize, (int)y + row * tileSize);	
			}
		}
		
	}
	
	
	///////////////////////////////////////////////////

		
	public void destroyTile(int r, int c) {
		map[r][c] = 0;
	}
	
	
///////////////////////////////////////////////////
	/**
	 * Ładuje mapę gry, poprzez komunikację z serwerem
	 * @param client
	 */
	public void loadMap(Client client) {
		
		Thread t10 = new Thread(() -> {
			
		while(!client.ifMapLoaded()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		numRows = client.getNumRows();
		numCols = client.getNumCols();
		map = new int[numRows][numCols];
		
		width = numCols * tileSize;
		height = numRows * tileSize;
		
		xmin = GamePanel.WIDTH - width;
		xmax = 0;
		ymin = GamePanel.HEIGHT - height;
		ymax = 0;
		
		for(int j=0; j<numRows; j++)
			for(int i=0; i<numCols; i++)
				map[j][i] = client.getMapElement(j, i);
		///
		System.out.println("Mapa w grze");
		});
		
		t10.start();
	}	

	
}



















