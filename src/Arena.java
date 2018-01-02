/*
 *Arena
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Multiplayer mode, fight to the death
 */

/**Imports**/
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Arena implements KeyListener, Runnable {
	/***Declare Variables***/
	//frames and strategy
	private BufferStrategy strategy;
	Frame frame = new Frame("Arena");
	Frame GUIFrame;
	//start-game variables
	private String firstCharName;
	private String secondCharName;
	private ArenaMap map;
	Thread gameCounter = null;
	//player related variables
	private Character[] players = new Character[2];
	private float[] playerX = new float[2];
	private float[] playerY = new float[2];
	private float[] dx = new float[2];
	private float[] dy = new float[2];
	private int[] direction = {0,0};
	private Punch[] punches = {null, null};
	private ImageIcon healthImage = new ImageIcon(GUILauncher.class.getClassLoader().getResource("heart.png"));
	//keeping track of time
	long oldTime = 0;
	long deltaTime = 0;
	double elapsedTimeSecond = 0;
	//player two movement keys
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	//player one movement keys
	private boolean wKey;
	private boolean aKey;
	private boolean sKey;
	private boolean dKey;
	//missile related variables
	private ArrayList<ArrayList<Laser>> missiles = new ArrayList<ArrayList<Laser>>();
	private static int MISSILE_DISTANCE = 400;
	//cooldown related variables
	private static double MISSILE_COOLDOWN = 1;
	private static double PUNCH_COOLDOWN = 1.5;
	private double[][] currentCoolDown = {{-1, -1},{-1, -1}};

	/***Constructor***/
	Arena(String firstCharacterName, String secondCharacterName, JFrame GUIFrame) {

		frame.setLayout(null);
		frame.setBounds(0,0,1400,800);
		frame.setSize(1400,800);
		frame.setResizable(false);		

		this.GUIFrame = GUIFrame;	//sets the gui frame
		firstCharName = firstCharacterName;	//sets the first character name
		secondCharName = secondCharacterName;	//sets the second character name

		frame.addWindowListener(new WindowAdapter() { //adds window listener for the game's frame
			/* windowClosing
			 * void method which sets the game thread to null and disposes of the game frame
			 * @param WindowEvent, the event which occurred, in this case it is when the window is closing
			 */
			public void windowClosing(WindowEvent e) {
				gameCounter = null; //sets thread to null
				GUIFrame.setVisible(true); //display main menu
				frame.dispose(); //disposes of frame
			}
		});

		//add a key listener that allow player response
		frame.addKeyListener(this);

		//show the frame before creating the buffer strategy
		frame.setVisible(true);

		//create the strategy used for accelerated rendering
		frame.createBufferStrategy(2);
		strategy = frame.getBufferStrategy();

		//create game objects
		map = new ArenaMap();	//create the map
		players[0] = new Character(50, 400, firstCharName, 10);	//create player one
		players[1] = new Character(1300, 400, secondCharName, 10);	//create player two

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

		oldTime = System.nanoTime();	//initialize oldTime

		//keep looking while the game is running
		while (gameCounter == Thread.currentThread()) {

			//obtain required player info for each player
			for (int i = 0; i < players.length; i++) {
				//position
				playerX[i] = players[i].getX();
				playerY[i] = players[i].getY();
				//missile list
				missiles.add(players[i].getMissiles());
			}	

			paintAll((Graphics2D)strategy.getDrawGraphics());	//paint all entities and displays

			checkEndCondition();	//check end game conditions

			//pause a bit to give the system rest
			try {
				Thread.sleep(4);
			} catch (Exception e) {};

			//calculate elapsed time in seconds, using time between now and oldTime
			deltaTime = (System.nanoTime() - oldTime) / 1000000;
			elapsedTimeSecond = (double)deltaTime/1000;
			oldTime = System.nanoTime();

			for (int i = 0; i < players.length; i++) {	//update cooldown of each player
				for (int j = 0; j < currentCoolDown.length; j++) {	//update all(punch and missile) cooldowns
					if (currentCoolDown[i][j] != -1) {		//if cooldown is activated
						currentCoolDown[i][j]+= elapsedTimeSecond;		//update cooldown based on elapsed time
					}
				}
				if (currentCoolDown[i][0] >= MISSILE_COOLDOWN) {	//if missile cooldown has reached max duration
					currentCoolDown[i][0] = -1;	//deactivate cooldown
				}
				if (currentCoolDown[i][1] >= PUNCH_COOLDOWN) {	//if punch cooldown has reached max duration
					currentCoolDown[i][1] = -1;	//deactivate cooldown
				}
			}

			//update the players
			updatePlayers();

		}
	}

	/** paintAll
	 * void method that paints all entities and info on screen/frame
	 * @param g Graphics2D: painting tool
	 */
	private void paintAll(Graphics2D g) {		
		//paint map
		map.paint(g);
		for (Character c: players) {	//paint each player
			c.paint(g);
		}
		//render game objects for each player
		for (int i = 0; i < players.length; i++) {	//loop through each player
			if (punches[i] != null) {	//paint punch image
				punches[i].paint(g);
				//update punch; delete it if max duration is reached
				if (punches[i].reachMaxDuration(elapsedTimeSecond)) {
					punches[i] = null;
				}
			}
			//laser missiles
			for (Laser m: missiles.get(i)) {
				g.setColor(Color.WHITE);
				m.paint(g);
			}
			//display punch cooldown in a green bar
			if (currentCoolDown[i][1] != -1) {		//if punch cooldown is activated, draw a green bar based on its duration
				g.setColor(Color.GREEN);
				g.fillRect((int)playerX[i], (int)playerY[i]-10, (int)(30-currentCoolDown[i][1]*20), 10);
			}
		}

		//display player one image and health
		g.setFont(new Font("TimesRoman", Font.BOLD, 18));
		g.setColor(Color.CYAN);
		g.drawString("Player One", 65, 65);		//draw the player number
		g.drawImage(players[0].getImage(), 5, 25, 50, 50, null);		//draw the image
		for (int i = 1; i <= players[0].getHealth(); i++) {	//draw player hearts, every 15 pixels
			g.drawImage(healthImage.getImage(), 15*i, 80, 15, 15, null);
		}
		//display player two image and health
		g.drawString("Player Two", 1235, 65);	//draw the player number
		g.drawImage(players[1].getImage(), 1345, 25, 50, 50, null);	//draw the image
		for (int i = 1; i <= players[1].getHealth(); i++) {	//draw player hearts, every 15 pixels
			g.drawImage(healthImage.getImage(), 1385-15*i, 80, 15, 15, null);
		}

		//flip the buffer to see the rendering
		try {
			g.dispose();
			strategy.show();
		}catch(IllegalStateException e) {	//catch exception so the game keeps running

		}

	}

	/** checkEndCondition
	 * void method that checks if there is a winner between the two players; if so, end game
	 */
	public void checkEndCondition() {
		String winner = null;	//declare and initialize the winner to null
		//find the winner
		if (players[0].getHealth() < 1) {	//if player one's health depletes
			winner = "Player Two";	//set winner to player two
		}
		if (players[1].getHealth() < 1) {	//if player two's health depletes
			if (winner != null) {	//there is already a winner
				winner = "Both";	//both are the winner
			} else {	//if there isn't a winner yet
				winner = "Player One";	//set winner to player one
			}
		}
		if (winner != null) {	//if there is a winner
			JOptionPane.showMessageDialog(frame, winner+" won!");	//display the winner
			gameCounter = null; //sets thread to null
			GUIFrame.setVisible(true); //display main menu
			frame.dispose(); //disposes of frame
		}
	}

	/** updatePlayers
	 * void method that update players' movements and all related entities
	 */
	public void updatePlayers() {
		//initialize delta x and y for each player
		for (int i = 0; i < 2; i++) {
			dx[i] = 0;
			dy[i] = 0;
		}

		//check the keyboard and record which way the player is trying to move, then
		//record delta positions and the directional value

		//record player one movement
		if (aKey) {		//left movement
			dx[0] -= 0.8;
			direction[0] = 2;
		}
		if (dKey) {		//right movement
			dx[0] += 0.8;
			direction[0] = 0;
		}
		if (wKey) {		//up movement
			dy[0] -= 0.8;
			direction[0] = 3;
		}
		if (sKey) {		//down movement
			dy[0] += 0.8;
			direction[0] = 1;
		}
		//record player two movement
		if (left) {		//left movement
			dx[1] -= 0.8;
			direction[1] = 2;
		}
		if (right) {	//right movement
			dx[1] += 0.8;
			direction[1] = 0;
		}
		if (up) {		//up movement
			dy[1] -= 0.8;
			direction[1] = 3;
		}
		if (down) {		//down movement
			dy[1] += 0.8;
			direction[1] = 1;
		}

		//update each player
		for (int i = 0; i < players.length; i++) {	//loop through each player
			//if delta x and y aren't 0, meaning a movement is requested to be made, and if a wall isn't in the way
			if (((dx[i] != 0) || (dy[i] != 0)) && !map.blocked(players[i].getTranslatedBounds(dx[i], dy[i]))) {
				players[i].move();	//update this player's movement
			}			
			//find who the other player is
			Character otherPlayer;
			if (i == 0) {	//if this is player one
				otherPlayer = players[1];	//the other player is player two
			} else {	//otherwise
				otherPlayer = players[0];	//the other player is player one
			}
			//punch interaction
			//if punch hasn't hit the other player yet and hits the other player
			if (punches[i] != null && !punches[i].isHitConfirm() && punches[i].getBounds().intersects(otherPlayer.getBounds())) {
				otherPlayer.damaged(2);		//decrease the other player's health by 2
				punches[i].setHitConfirm(true);		//set this punch to already hit (hitConfirm)
			}
			//missile interaction
			ArrayList<Laser> tempMissileList = missiles.get(i);		//get this player's missile list
			for (int j = 0; j < tempMissileList.size(); j++) {		//loop through each missile
				Laser tempMissile = tempMissileList.get(j);			//declare temporary missile
				tempMissile.move();		//update missile movement
				if (tempMissile.reachMaxDistance() || map.blocked(tempMissile.getBounds())) {	//if the missile's lifetime ends, or if it hits a wall
					tempMissileList.remove(j);	//delete this missile
				}
				if (j < missiles.get(i).size() && tempMissile.getBounds().intersects(otherPlayer.getBounds())) {	//if this missile isn't removed yet and hits the other player
					tempMissileList.remove(j);	//delete this missile
					otherPlayer.damaged(1);		//decrease the other player's health by 1
				}
			}
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
		//player one
		if (e.getKeyCode() == KeyEvent.VK_A) {	//check left movement
			aKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {	//check right movement
			dKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {	//check down movement
			sKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {	//check up movement
			wKey = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_F && currentCoolDown[0][1] == -1) {	//check default punch key and cooldown
			punches[0] = new Punch(playerX[0], playerY[0], "punch.png", direction[0]);		//create a punch when pressed
			currentCoolDown[0][1]++;	//then activate this player's punch cooldown
		}
		if (e.getKeyCode() == KeyEvent.VK_G && currentCoolDown[0][0] == -1) {	//check default shoot key and missile cooldown
			missiles.get(0).add(new Laser((int)playerX[0], (int)playerY[0], MISSILE_DISTANCE, direction[0]));	//create a missile when pressed
			currentCoolDown[0][0]++;	//then activate this player's missile cooldown
		}
		//player two
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
		if (e.getKeyCode() == KeyEvent.VK_N && currentCoolDown[1][1] == -1) {	//check default punch key and cooldown
			punches[1] = new Punch(playerX[1], playerY[1], "punch.png", direction[1]);		//create a punch when pressed
			currentCoolDown[1][1]++;	//then activate this player's punch cooldown
		}
		if (e.getKeyCode() == KeyEvent.VK_M && currentCoolDown[1][0] == -1) {	//check default shoot key and missile cooldown
			missiles.get(1).add(new Laser((int)playerX[1], (int)playerY[1], MISSILE_DISTANCE, direction[1]));	//create a missile when pressed
			currentCoolDown[1][0]++;	//then activate this player's missile cooldown
		}
	}

	/**
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		//check the keyboard and record which keys are released
		//player one
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
		//player two
		if (e.getKeyCode() == KeyEvent.VK_A) {		//check left movement
			aKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {		//check right movement
			dKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {		//check down movement
			sKey = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_W) {		//check up movement
			wKey = false;
		}
	}

}
