/*
 *RangedEnemy
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *subclass of Enemy, the object which attacks the user, can shoot lasers
 */

/**Imports**/
import java.util.ArrayList;

public class RangedEnemy extends Enemy {
	
	/***Declare and Initialize RangedEnemy Info***/
	private ArrayList<Laser> missiles = new ArrayList<Laser>();
	private static int MISSILE_DISTANCE = 300;
	private static double MISSILE_COOLDOWN = 2;
	private double currentCoolDown = -1;
	private int direction;
	
	/***Constructor***/
	public RangedEnemy(float x, float y, double speed) {
		super(x, y, speed, "enemy2.png");	//pass spawn location and ranged enemy image to superclass
	}
	
	/** shoot
	 * Determines if character is in missile range and if missile cooldown isn't up; if so, adds a missile to its
	 * missile list and activate missile cooldown
	 * @param xCharPos, float: character's x position, used for inMissileRange method
	 * @param yCharPos, float: character's y position, used for inMissileRange method
	 */
	public void shoot(float xCharPos, float yCharPos) {
		if (inMissileRange(xCharPos, yCharPos) && currentCoolDown == -1) {
			currentCoolDown++;	//activate cooldown
			missiles.add(new Laser((int)x, (int)y, MISSILE_DISTANCE, direction));	//create and add a missile (shoot)
		}
	}
	
	/** inMissileRange
	 * checks if character's position is close to this enemy's shooting range/distance
	 * @param xCharPos, float: character's x position
	 * @param yCharPos, float: character's y position
	 * @return boolean: if character is in missile range
	 */
	public boolean inMissileRange(float xCharPos, float yCharPos) {
		direction = -1;		//initialize direction
		if (Math.abs(x-xCharPos) <= MISSILE_DISTANCE) {		//if character is within missile distance on x axis
			if (y <= (yCharPos+20) && y >= (yCharPos-20) && x < xCharPos) {		//if character is on the right, within 40 pixels
				direction = 0;	//aim right
			} else if (y <= (yCharPos+20) && y >= (yCharPos-20) && x > xCharPos) {		//if character is on the left, within 40 pixels
				direction = 2;	//aim left
			}
		}
		if (Math.abs(y-yCharPos) <= MISSILE_DISTANCE) {		//if character is within missile distance on y axis
			if (x <= (xCharPos+20) && x >= (xCharPos-20) && y < yCharPos) {		//if character is below, within 40 pixels
				direction = 1;	//aim downwards
			} else if (x <= (xCharPos+20) && x >= (xCharPos-20) && y > yCharPos) {		//if character is above, within 40 pixels
				direction = 3;	//aim upwards
			}
		} 
		return direction != -1;		//check if direction is a valid value
	}
	
	/** resetCoolDown
	 * update missile cooldown based on elapsed time
	 * @param elapsedTime, double: elapsed time, in seconds
	 */
	public void resetCoolDown(double elapsedTime) {
		if (currentCoolDown != -1) {	//if cooldown is activated
			currentCoolDown+=elapsedTime;	//update cooldown based on elapsed time
		}
		if (currentCoolDown >= MISSILE_COOLDOWN) {	//if cooldown has reached max duration
			currentCoolDown = -1;	//deactivate cooldown
		}
	}
	
	/** getMissiles
	 * return the missile list that this enemy has fired
	 * @return missiles, ArrayList<Laser>: the list of missiles
	 */
	public ArrayList<Laser> getMissiles() {
		return missiles;
	}

}
