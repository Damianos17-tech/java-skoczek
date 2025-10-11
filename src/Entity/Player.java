package Entity;

import TileMap.*;
import javafx.scene.layout.ConstraintsBase;
import Audio.AudioPlayer;
import Main.GamePanel;

import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.ietf.jgss.GSSManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Klasa obsługująca cały zestaw parametru gracza 
 * @author DAMIAN-HP
 *
 */
public class Player extends MapObject {
	/**
	 * Określa czy gracz jest na samo-poruszającym się obiekcie
	 */
	private boolean mobile;
	/**
	 * Określa czy gracz je jabłko w tym momencie
	 */
	private boolean eating;
	/**
	 * Czas jedzenia
	 */
	private long eatTimer;
	/**
	 * Określa czy gracz przegrał
	 */
	boolean gameover;
	/**
	 * Liczba podniesionych jabłek
	 */
	private int pickedApples;
	/**
	 * Liczba punktów życia
	 */
	private int health;
	/**
	 * Maksymalna liczba punktów życia
	 */
	private int maxHealth;
	private int fire;
	private int maxFire;
	//private boolean dead;
	/**
	 * Określa migotanie gracza po utracie życia i ustawia chwilową nieśmiertelność
	 */
	private boolean flinching;
	/**
	 * Czas migotania gracza
	 */
	private long flinchTimer;
	
	// fireball
	/**
	 * Określa czy gracz rzuca jabłkiem
	 */
	private boolean firing;
	/**
	 * Koszt rzutu jabłkiem
	 */
	private int fireCost;
	/**
	 * Obrażenia rzutu jabłkiem
	 */
	private int fireBallDamage;
	/**
	 * Lista obiektów rzuconych jabłek 
	 */
	private ArrayList<FireBall> fireBalls;
	
	// scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;
	
	/**
	 * Określa szybowanie gracza w powietrzu, funkcja nie wykorzystana jak dotąd
	 */
	private boolean gliding;
	
	/**
	 * Obrazki animacji gracza
	 */
	private ArrayList<BufferedImage[]> sprites;
	
	/**
	 * Kolejność klatek animacji gracza
	 */
	private final int[] numFrames = {
			5, 8, 3, 3, 4, 2, 5
		};
	
	/**
	 * Stan początkowy gracza
	 */
	private static final int IDLE = 0;
	/**
	 * Stan gracza, gdy idzie
	 */
	private static final int WALKING = 1;
	/**
	 * Stan gracza, gdy skacze
	 */
	private static final int JUMPING = 2;
	/**
	 * Stan gracza, gdy spada
	 */
	private static final int FALLING = 3;
	/**
	 * Stan gracza, gdy szybuje
	 */
	private static final int GLIDING = 4;
	/**
	 * Stan gracza, gdy rzuca jabłkiem
	 */
	private static final int FIREBALL = 5;
	/**
	 * Stan gracza, gdy drapie
	 */
	private static final int SCRATCHING = 6;
	/**
	 * Tablica hashująca z dzwiękami efektów gry gracza
	 */
	private HashMap<String, AudioPlayer> sfx;
	
	/**
	 * Konstruktor gracza, ustawia parametry początkowe, wczytuje obrazki do animacji
	 * oraz dzwieki efektów
	 */
	public Player(TileMap tm) {
		
		super(tm);
		
		width = 23;
		height = 43;
		cwidth = 20;
		cheight = 42;

		moveSpeed = 0.7;
		maxSpeed = 2.5;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 5;
		fire = maxFire = 2500;
		
		fireCost = 0;
		fireBallDamage = 1;
		fireBalls = new ArrayList<FireBall>();
		
		scratchDamage = 8;
		scratchRange = 40;
		
		gameover = false;
		eating = false;
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Player/crashAnimation.gif"
				)
			);
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 7; i++) {
				BufferedImage[] bi;
				if(i != IDLE) {
					bi = new BufferedImage[numFrames[i]];
				}
				else {
					bi = new BufferedImage[numFrames[i]*2];
				}
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					switch(i) {
					case SCRATCHING:
						bi[j] = spritesheet.getSubimage(
								j * width * 2,
								i * height+10,
								width * 2,
								height
						);
						break;
					case IDLE:
						bi[j] = spritesheet.getSubimage(
								j * width+1,
								i * height+10,
								width,
								height
						);
						bi[j+numFrames[i]] = spritesheet.getSubimage(
								(numFrames[i]-j-1) * width+1,
								i * height+10,
								width,
								height
						); 
						break;
					case WALKING:
						int posMod[] = {0,33,77,111,140,175,215,247};
						int widthMod[] = {33,44,34,29,35,40,32,29};
						bi[j] = spritesheet.getSubimage(
								posMod[j],
								104,
								widthMod[j],
								height
						);
						break;
					case JUMPING:
						bi[j] = spritesheet.getSubimage(
								j * 26,
								155,
								width,
								height
						);
						break;
					case FALLING:
						bi[j] = spritesheet.getSubimage(
								(j+2) * 26,
								155,
								width,
								height
						);
						break;
					case FIREBALL:
						bi[j] = spritesheet.getSubimage(
								j * (width+10),
								i * height-15,
								width+5,
								height
						);
						break;
					default:
						bi[j] = spritesheet.getSubimage(
								j * width,
								i * height-10,
								width,
								height
						);
						break;
					}
					
				}
				
				sprites.add(bi);
				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(40);
		
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("jump", new AudioPlayer("/SFX/jump.wav"));
		sfx.put("scratch", new AudioPlayer("/SFX/scratch.wav"));
		sfx.put("throwApple", new AudioPlayer("/SFX/throwApple.wav"));
		sfx.put("whoah", new AudioPlayer("/SFX/whoah.wav"));
		sfx.put("falling", new AudioPlayer("/SFX/falling.wav"));
		sfx.put("pickApple", new AudioPlayer("/SFX/pickApple.wav"));
		
		
	}
	/**
	 * zwraca życie gracza
	 * @return
	 */
	public int getHealth() { return health; }
	/**
	 * zwraca maksymalną liczbę punktów życia gracza
	 * @return
	 */
	public int getMaxHealth() { return maxHealth; }
	public int getFire() { return fire; }
	public int getMaxFire() { return maxFire; }
	
	/**
	 * Ustawia stan gracza na rzucanie jabłkiem
	 */
	public void setFiring() { 
		firing = true;
	}
	/**
	 * Ustawia stan gracza na drapanie
	 */
	public void setScratching() {
		scratching = true;
	}
	/**
	 * Ustawia stan gracza na szybowanie
	 */
	public void setGliding(boolean b) { 
		gliding = b;
	}
	/**
	 * Sprawdza kolidowanie gracza i przeciwników na mapie
	 * @param enemies
	 */
	public void checkAttack(ArrayList<Enemy> enemies) {
		
		// loop through enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			
			// scratch attack
			if(scratching) {
				if(facingRight) {
					if(
						e.getx() > x &&
						e.getx() < x + scratchRange && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(scratchDamage);
					}
				}
				else {
					if(
						e.getx() < x &&
						e.getx() > x - scratchRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(scratchDamage);
					}
				}
			}
			
			// fireballs
			for(int j = 0; j < fireBalls.size(); j++) {
				if(fireBalls.get(j).intersects(e)) {
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;
				}
			}
			
			// check enemy collision
			if(intersects(e)) {
				hit(e.getDamage());
			}
			
			if( e.intoAbyss()) {
				sfx.get("falling").play();
				enemies.remove(i);
				i--;
				}
		}
		
	}
	
	/**
	 * Sprawdza zależność położenia gracza i jabłek
	 * @param apples
	 */
	public void checkApples(ArrayList<Apple> apples) {
		
		for(int i = 0; i < apples.size(); i++) {
			Apple a = apples.get(i);
			if(intersects(a)) {
				sfx.get("pickApple").play();
				a.pick();
				if(!eating) {
					eating = true;
					eatTimer = System.nanoTime();
				}
				else
					pickedApples++;
				
			}
			if( eating ) {
				long elapsed = (System.nanoTime() - eatTimer) / 1000000; 
				if(elapsed > 500) {
					eating= false;
					pickedApples++;
				}
				
			}
				
		}
	}
	/**
	 * Sprawdza zależność położenia gracza i skrzynek TNT
	 * @param cratesTNT
	 */
	public void checkCratesTNT(ArrayList<CrateTNT> cratesTNT) {
		for(int i = 0; i < cratesTNT.size(); i++) {
			CrateTNT cTNT = cratesTNT.get(i);
			if(cTNT.isInvisible()) {
				continue;
			}
				
			if(intersectsTop(cTNT) && (y-cheight/2) < (cTNT.gety() - cTNT.getCHeight() - 20)) {
				cTNT.setBurdened();
				falling = false;
				mobile = true;
				dy = cTNT.getdy();
				if(!right && !left) {
					dx = cTNT.getdx()*1.5;
				}
					
				//System.out.println("gora");
				
			}
			else if(intersectsLeft(cTNT)){
				setPosition(cTNT.getx() - cTNT.getCWidth()/2 - cwidth / 2+1, y);
				mobile = false;
				//System.out.println("Lewa");
			}
			else if(intersectsRight(cTNT)){
				setPosition(cTNT.getx() + cTNT.getCWidth()/2 + cwidth /2, y);
				mobile = false;
				//System.out.println("Prawa");
			}

			else if(intersectsBottom(cTNT)){
				//cTNT.setBurdened();
				setPosition(x, cTNT.gety() + cTNT.getCHeight()/2 + cheight / 2);
				dy = 0;
				mobile = false;
				//System.out.println("Dol");
			}
			else {
				mobile = false;
				//System.out.println("x = "+x);
			}
		}
	}
	/**
	 * Zadaje obrażenia graczowi
	 * @param damage
	 */
	public void hit(int damage) {
		if(flinching) return;
		sfx.get("whoah").play();
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public int getNumApples() {
		return pickedApples;
	}
	/**
	 * Ustawia parametry gracza takie same jak w poprzednim poziomie gry
	 * @param health
	 * @param points
	 */
	public void setAchievments(int health, int points) {
		this.health = health;
		this.pickedApples = points;
	}
	/**
	 * Sprawdza i ustawia następną pozycję gracza na podstawie biężącego stanu gry 
	 */
	private void getNextPosition() {
		
		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		else if(!mobile){

			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		// cannot move while attacking, except in air
		if(
		(currentAction == SCRATCHING || currentAction == FIREBALL) &&
		!(jumping || falling)) {
			dx = 0;
		}
		
		// jumping
		if(jumping && !falling) {
			sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
		}
		
		// falling
		if(falling) {
			
			if(dy > 0 && gliding) dy += fallSpeed * 0.1;
			else dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
		}
	}
	
	/**
	 * Aktualizuje parametry gracza, sprawdza czy jeszcze nie przegrał, woła odpowienie
	 * fukncje do obsługi postaci gracza i jego czynności
	 */
	public void update() {
		
		if(isDead()) {
			if(health <= 0) {
				gameover = true;
				return;
			}
			health--;
			dead = false;
			setPosition(100, 100);
		}
		
		if(intoAbyss()) {
			y+=3;
			sfx.get("falling").play();
			dead = true;
		}
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		// check attack has stopped
		if(currentAction == SCRATCHING) {
			if(animation.hasPlayedOnce()) scratching = false;
		}
		if(currentAction == FIREBALL) {
			if(animation.hasPlayedOnce()) firing = false;
		}
		
		// fireball attack
		fire += 1;
		if(fire > maxFire) fire = maxFire;
		if(firing && currentAction != FIREBALL) {
			if(fire > fireCost) {
				fire -= fireCost;
				sfx.get("throwApple").play();
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
		}
		
		// update fireballs
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove()) {
				fireBalls.remove(i);
				i--;
			}
		}
		
		// check done flinching
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 1000) {
				flinching = false;
			}
		}
		
		// set animation
		if(scratching) {
			if(currentAction != SCRATCHING) {
				sfx.get("scratch").play();
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				//width = 60;
			}
		}
		else if(firing) {
			if(currentAction != FIREBALL) {
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				//width = 30;
			}
		}
		else if(dy > 0) {
			if(gliding) {
				if(currentAction != GLIDING) {
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					//width = 30;
				}
			}
			else if(currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(180);
				//width = 30;
			}
			
		}
		else if(dy < 0) {
			if(currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(180);
				//width = 30;
			}
		}
		else if(left || right) {
			if(currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(80);
				//width = 30;
			}
		}
		else {
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(90);
				//width = 30;
			}
		}
		
		animation.update();
		
		// set direction
		if(currentAction != SCRATCHING && currentAction != FIREBALL) {
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
		
	}
	/**
	 * Sprawda czy gracz nie spadł w przepaść
	 */
	public boolean intoAbyss() {
		return (y + ymap + height ) > GamePanel.HEIGHT;
	}
	/**
	 * Zwraca czy gra została przegrana
	 * @return
	 */
	public boolean gameOver() {
		return gameover;
	}
	/**
	 * Rysuje postać gracza na planszy
	 */
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		// draw fireballs
		for(int i = 0; i < fireBalls.size(); i++) {
			fireBalls.get(i).draw(g);
		}
		
		// draw player
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		super.draw(g);
		
		if(gameover) {
			g.drawString("GAME OVER", 130, 110);
		}
			
		
	}
	
}

















