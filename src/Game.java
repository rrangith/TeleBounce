/*
 *Game
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *The game, which is launched from gui
 */

/**Imports**/
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends Canvas implements KeyListener, Runnable {
	/***Declare Variables***/
	//frames and strategy
	private BufferStrategy strategy;	//buffered strategy used for accelerated rendering
	Frame frame = new Frame("Game");
	Frame GUIFrame;	
	//start-game variables
	private int currentLevel = 0;
	private String charName;
	private boolean selectLevel;
	private ArrayList<HighScore> scoreList; 
	Thread gameCounter = null;
	Thread timer = null;
	TeleTimer t;	
	private Map map;	
	//player related variables
	private Character player;
	private static int CHAR_SPAWN_X = 50;
	private static int CHAR_SPAWN_Y = 50;
	private float playerX;
	private float playerY;
	private float dx;
	private float dy;
	private int direction = 0;
	int currentScore = 0;
	private Punch punch = null;
	private ImageIcon healthImage = new ImageIcon(GUILauncher.class.getClassLoader().getResource("heart.png"));
	//keeping track of time
	private double telepadTime = 0.0;
	double elapsedTimeSecond = 0;		
	//movement keys
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	//enemy list
	private Enemy[] enemies;	
	//missile related variables
	private ArrayList<Laser> missiles;
	private static int MISSILE_DISTANCE = 400;	
	//cooldown related variables
	private static double MISSILE_COOLDOWN = 1;
	private static double PUNCH_COOLDOWN = 1.5;	
	private static double SPEED_TIME = 1.5;
	private double[] currentCoolDown = {-1,-1};	
	//item related variables
	ArrayList<Item> items = new ArrayList<Item>();
	private double speedDuration = SPEED_TIME;

	/***Constructor***/
	public Game(String characterName, int level, boolean selectLevel, ArrayList<HighScore> highScores, JFrame GUIFrame) {
		frame.setLayout(null); 
		setBounds(0,0,800,800); 
		frame.add(this, BorderLayout.CENTER);
		frame.setSize(800,800); //sets size of frame
		frame.setResizable(false); //makes frame so can't be resized

		this.GUIFrame = GUIFrame;	//sets the gui frame
		currentLevel = level; //sets the level, given from gui
		charName = characterName; //sets the character name, given from gui
		this.selectLevel = selectLevel;	//sets select level, given from gui
		scoreList = highScores; //sets the list of high scores, given from gui

		//check if user used the select-level panel
		if (selectLevel) {
			t = new TeleTimer(30);	//if so, makes TeleTimer for 30 seconds. The user has this much time to win the level
		} else {
			t = new TeleTimer(300); //makes TeleTimer for 300 seconds, 5 minutes. The user has this much time to win
		}
		timer = new Thread(t); //makes thread for timer
		timer.start(); //starts thread

		frame.addWindowListener(new WindowAdapter() { //adds window listener for the game's frame
			/* windowClosing
			 * void method which sets the game thread to null and disposes of the game frame
			 * @param WindowEvent, the event which occurred, in this case it is when the window is closing
			 */
			public void windowClosing(WindowEvent e) {
				endGame();	//end game
			}
		});

		//add a key listener that allow player response
		frame.addKeyListener(this);
		addKeyListener(this);

		//show the frame before creating the buffer strategy
		frame.setVisible(true);

		//create the strategy used for accelerated rendering
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		setScreen();	//set game screen
	}	

	/** getScoreList
	 * returns the list of high scores
	 * @return ArrayList, the list of high scores
	 */
	public ArrayList<HighScore> getScoreList() {
		return scoreList;
	}

	/** setScreen
	 * create all game objects and executes the game thread, used for refreshing after each level
	 */
	public void setScreen() {
		//create game objects
		map = new Map(currentLevel); 	//create the map
		player = new Character(CHAR_SPAWN_X, CHAR_SPAWN_Y, charName, 5);	//create the player
		items = map.getItems();		//create all items from the map
		enemies = new Enemy[4+currentLevel];	//create enemy list

		//start the game loop
		if (gameCounter == null) {
			gameCounter = new Thread(this);	//create the game thread
			System.out.println("Created Thread: " + gameCounter.getId());
			gameCounter.start(); //executes run() method
		}	
	}

	/** run
	 * executes the game thread and loops game while the thread is up
	 */
	public void run() {

		long oldTime = System.nanoTime();	//initialize oldTime

		//keep looking while the game is running
		while (gameCounter == Thread.currentThread()) {

			//obtain required player info
			playerX = player.getX();
			playerY = player.getY();
			missiles = player.getMissiles();

			addEnemies();	//spawn enemies if needed

			paintAll((Graphics2D)strategy.getDrawGraphics());	//paint all entities and displays

			checkEndCondition();	//check end game conditions

			//pause a bit to give the system rest
			try {
				Thread.sleep(4);
			} catch (Exception e) {};

			//calculate elapsed time in seconds, using time between now and oldTime
			long deltaTime = (System.nanoTime() - oldTime) / 1000000;
			elapsedTimeSecond = (double)deltaTime/1000;
			oldTime = System.nanoTime();

			//update all(punch and missile) cooldowns
			for (int i = 0; i < currentCoolDown.length; i++) { //loop through all cooldowns
				if (currentCoolDown[i] != -1) {		//if cooldown is activated
					currentCoolDown[i]+= elapsedTimeSecond;		//update cooldown based on elapsed time
				}
			}
			if (currentCoolDown[0] >= MISSILE_COOLDOWN) {	//if missile cooldown has reached max duration
				currentCoolDown[0] = -1;	//deactivate cooldown
			}
			if (currentCoolDown[1] >= PUNCH_COOLDOWN) {	//if punch cooldown has reached max duration
				currentCoolDown[1] = -1;	//deactivate cooldown
			}

			//update moving entities
			updatePlayer();
			updateEnemy();

		}
	}

	/** addEnemies
	 * void method which refreshes the enemy list by filling in empty spots with new enemies
	 */
	private void addEnemies() {
		for (int i = 0; i < enemies.length; i++) {	//loop through enemies list
			if (enemies[i] == null) {	//if there is an empty slot
				float tempX, tempY;		//temporary enemy positions
				//loop until enemy is outside 400 pixels range of character, and isn't in a wall
				do {
					//randomize x/y positions and enemy type variables
					tempX = (float)(Math.random()*800+1);
					tempY = (float)(Math.random()*800+1);
					int enemyType = (int)Math.round(Math.random());
					//add based on randomized enemy type
					if (enemyType == 0) {
						enemies[i] = new Enemy(tempX, tempY, 0.2, "enemy1.png");
					} else if (enemyType == 1) {
						enemies[i] = new RangedEnemy(tempX, tempY, 0.2);			
					}
				}while (tempX > (playerX-400) && tempX < (playerX+400) && tempY > (playerY-400) && tempY < (playerY+400) || map.blocked(enemies[i].getBounds()));
			}
		}	//end of for loop
	}

	/** paintAll
	 * void method that paints all entities and info on screen/frame
	 * @param g Graphics2D: painting tool
	 */
	private void paintAll(Graphics2D g) {
		//fill canvas
		g.setColor(Color.black);
		g.fillRect(0,0,2000,2000);
		g.translate(400-(int)playerX, 400-(int)playerY);	//move canvas

		//render game objects
		map.paint(g);	//paint map
		player.paint(g);	//paint character
		for (Enemy e: enemies) {	//paint all enemies
			if (e != null) {
				e.paint(g);
				if (e instanceof RangedEnemy) {		//if this is a ranged enemy
					((RangedEnemy) e).resetCoolDown(elapsedTimeSecond);	//refresh its missile cooldown
					//paint all its missiles
					for (Laser m: ((RangedEnemy)e).getMissiles()) {
						g.setColor(Color.RED);
						m.paint(g);
					}
				}
			}
		}		
		for (Item i: items) {	//paint all items
			i.paint(g);
		}		 
		if (punch != null) {	//paint punch image
			punch.paint(g);
			//update punch; delete it if max duration is reached
			if (punch.reachMaxDuration(elapsedTimeSecond)) {
				punch = null;
			}
		}

		for (Laser m: missiles) {	//paint all character missiles
			g.setColor(Color.WHITE);
			m.paint(g);
		}

		//display level 
		g.setFont(new Font("TimesRoman", Font.PLAIN, 24));	//set font
		g.setColor(Color.GREEN);	//set word colour
		g.drawString("Level "+Integer.toString(currentLevel), (int)playerX+300, (int)playerY-350);	//draw level

		//display telepad time in decimals
		if (telepadTime != 0) {
			g.setFont(new Font("TimesRoman", Font.BOLD, 14));	//set font
			g.setColor(Color.YELLOW);	//set word colour
			DecimalFormat df = new DecimalFormat("#.##");	//create decimal format of 2 decimal points
			g.drawString(df.format(telepadTime), (int)playerX+40, (int)playerY-5);	//display time according to decimal format
		}

		//display health point
		for (int i = 1; i <= player.getHealth(); i++) {		//draw player hearts, every 30 pixels
			g.drawImage(healthImage.getImage(), (int)playerX-410+30*i, (int)playerY-360, 15, 15, null);
		}

		//display punch cooldown and speed duration in a bar, which stack on each other
		int stack = 1;
		if (currentCoolDown[1] != -1) {		//if punch cooldown is activated, draw a green bar based on its duration
			g.setColor(Color.GREEN);
			g.fillRect((int)playerX, (int)playerY-10*stack, (int)(30-currentCoolDown[1]*20), 10);
			stack++;
		}
		if (speedDuration < SPEED_TIME) {	//if speed duration is activated, draw a blue bar based on its duration
			g.setColor(Color.CYAN);
			g.fillRect((int)playerX, (int)playerY-10*stack, (int)(speedDuration*30), 10);
		}


		//time remaining
		if (t.getTimeRemaining() != 0){ //checks if there is still time remaining
			g.setFont(new Font("TimesRoman", Font.BOLD, 24)); //sets font
			g.setColor(Color.WHITE); //set word colour
			g.drawString("Time: " + t.getTimeString(), (int)playerX + 275, (int)playerY + 375); //displays time remaining at bottom right of screen

			g.setFont(new Font("Comic Sans MS", Font.BOLD, 24)); //sets font
			g.setColor(Color.PINK); //set word colour
			g.drawString("Score: " + Integer.toString(currentScore), (int)playerX - 370, (int)playerY + 375); //displays score at bottom left of screen
		}

		//flip the buffer to see the rendering
		try {
			g.dispose();
			strategy.show();
		}catch(IllegalStateException e) {	//catch exception so the game keeps running

		}
	}

	/** checkEndCondition
	 * void method that checks if the player win or lose; if so, end game
	 */
	public void checkEndCondition() {
		//check lose condition
		if (player.getHealth() < 1) {	//if player health depletes
			JOptionPane.showMessageDialog(this, "You died.");	//display message
			endGame();	//end game
		}			
		if (t.getTimeRemaining() <= 0) {	//if the time runs out
			JOptionPane.showMessageDialog(this, "You ran out of time.");	//display message
			endGame();	//end game
		}

		//check win condition
		if (map.checkCountDown(player.getBounds())) {	//if player is on the telepad
			telepadTime+= elapsedTimeSecond;	//count down using elapsed time
			if (telepadTime >= 5) {		//if telepad time reaches 5 seconds
				if (currentLevel == 10 || selectLevel == true) {	//if this is the final(first condition) or only(second condition) level
					currentScore += (t.getTimeRemaining() * 20);	//add up score based on time
					String currentName = JOptionPane.showInputDialog(this, "You won! Enter your name");		//ask for user name
					scoreList.add(new HighScore(currentName, currentScore));	//create highscore info and added it to the score list
					Collections.sort(scoreList);	//sort the list based on highest points
					endGame();	//end game
				} else {	//if there are more levels
					currentLevel++;		//increase the level
					setScreen();	//refresh game objects and thread
				}
			}
		} else {
			telepadTime = 0;	//initialize reset telepad time if character isn't on the telepad
		}
	}

	/** endGame
	 * void method that terminates this game thread and frame
	 */
	public void endGame() {
		gameCounter = null; //sets thread to null
		GUIFrame.setVisible(true); //display main menu
		frame.dispose(); //disposes of frame
	}

	/** updatePlayer
	 * void method that update player movement and all related entities
	 */
	public void updatePlayer() {
		//initialize delta x and y
		dx = 0;
		dy = 0;
		//check the keyboard and record which way the player is trying to move, then
		//record delta positions and the directional value
		if (left) {		//left movement
			dx -= 0.8;
			direction = 2;
		}
		if (right) {	//right movement
			dx += 0.8;
			direction = 0;
		}
		if (up) {		//up movement
			dy -= 0.8;
			direction = 3;
		}
		if (down) {		//down movement
			dy += 0.8;
			direction = 1;
		}
		//item interactions
		for (int i = 0; i < items.size(); i++) {	//loop through all items in game
			Item tempItem = items.get(i);	//set a temporary item variable
			if (player.getBounds().intersects(tempItem.getBounds())) {	//if the player intersects the item
				if (tempItem.getType().equals("health") && player.getHealth() < 5) {	//if this item is a health potion
					player.heal(1);	//increase player health by 1
				} else if (tempItem.getType().equals("speed")) {	//if this is a speed potion
					speedDuration = SPEED_TIME-0.0001;	//activate and refresh speed duration
				}
				items.remove(i);	//remove the item regardless
			}
		}
		//if speed duration is activated, and its within the speed time
		if (speedDuration < SPEED_TIME) {
			//multiply player's delta positions
			dx = dx*1.5f;
			dy = dy*1.5f;
			speedDuration-=elapsedTimeSecond;	//update speed duration based on elapsed time
			if (speedDuration <= 0) {	//if speed duration reaches 0 or below
				speedDuration = SPEED_TIME;		//deactivate speed duration
			}
		}

		//if delta x and y aren't 0, meaning a movement is requested to be made, and if a wall isn't in the way
		if (((dx != 0) || (dy != 0)) && !map.blocked(player.getTranslatedBounds(dx, dy))) {
			player.move();	//update character x and y position
		}		
	}

	/** updateEnemy
	 * void method that update enemies and all related entities
	 */
	public void updateEnemy() {
		//loop through each enemy
		for (int i = 0; i < enemies.length; i++) {
			if (enemies[i] != null) {	//if this enemy exists
				if (!map.blocked(enemies[i].getTranslatedBounds(playerX, playerY))) {	//detects if enemies' are on a wall using map.blocked
					enemies[i].move();
				} else{ //if they are blocked
					enemies[i].moveAround(); //move around the rock
				}
				Rectangle tempBound = enemies[i].getBounds();	//declare a temporary bound for enemy
				if (enemies[i] instanceof RangedEnemy) {	//if this is a range enemy
					RangedEnemy tempRanged = (RangedEnemy)enemies[i];
					tempRanged.shoot(playerX, playerY);		//checks if player is in range then shoots missile
					ArrayList<Laser> tempMissiles = tempRanged.getMissiles();		//declare temporary ArrayList for its missiles					
					for (int j = 0; j < tempMissiles.size(); j++) {		//loop through each missile
						tempMissiles.get(j).move();		//update the missile movement
						if (tempMissiles.get(j).reachMaxDistance() || map.blocked(tempMissiles.get(j).getBounds())) {	//if the missile's lifetime ends, or if it hits a wall
							tempMissiles.remove(j);	//delete this missile
						}
						if (j < tempMissiles.size() && tempMissiles.get(j).getBounds().intersects(player.getBounds())) {	//if this missile isn't removed yet and hits the player
							tempMissiles.remove(j);	//delete this missile
							player.damaged(1);	//damage the player by 1
						}
					}
				}
				if (tempBound.intersects(player.getBounds())) {		//if this enemy intersects the p;ayer
					player.damaged(1);	//damage the player by 1
					//knock back the character to distance opposite to the enemy
					dx = (float) (player.getX()-tempBound.getX());
					dy = (float) (player.getY()-tempBound.getY());
					if (!map.blocked(player.getTranslatedBounds(dx, dy))) {		//if the knock back doesn't cause player to collide with a wall
						player.move();	//update player movement
					}
				}
				if (punch != null && punch.getBounds().intersects(tempBound)) {		//if player punch hits this enemy	
					enemies[i] = null;	//delete this enemy
					currentScore+=50;	//add score
				}
				for (int j = 0; j < missiles.size(); j++) {		//loop through character's missile list
					Laser tempMissile = missiles.get(j);	//declare temporary missile
					tempMissile.move();		//update missile movement
					if (tempMissile.reachMaxDistance() || map.blocked(tempMissile.getBounds())) {	//if the missile's lifetime ends, or if it hits a wall
						missiles.remove(j);		//delete this missile
					}
					if (j < missiles.size() && tempMissile.getBounds().intersects(tempBound)) {		//if this missile isn't removed yet and hits this enemy	
						missiles.remove(j);		//delete this missile
						enemies[i] = null;	//delete this enemy
						currentScore+=50;	//add score
					}
				}
			}	//end of first if statement
		}	//end of first for loop
	}

	/**
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		//check the keyboard and record which keys are pressed
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {	//check left movement
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {	//check right movement
			right = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {	//check down movement
			down = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {		//check up movement
			up = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_N && currentCoolDown[1] == -1) {	//check default punch key and cooldown
			punch = new Punch(playerX, playerY, "punch.png", direction);	//create a punch when pressed
			currentCoolDown[1]++;	//then activate punch cooldown
		}
		if (e.getKeyCode() == KeyEvent.VK_M && currentCoolDown[0] == -1) {	//check default shoot key and missile cooldown
			missiles.add(new Laser((int)playerX, (int)playerY, MISSILE_DISTANCE, direction));	//create a missile when pressed
			currentCoolDown[0]++;	//then activate missile cooldown
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		//check the keyboard and record which keys are released
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {	//check left movement
			left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {	//check right movement
			right = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {	//check down movement
			down = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {		//check up movement
			up = false;
		}
	}

}