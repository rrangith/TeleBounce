/*
 *Laser
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *subclass of entity, the object which attacks the enemy
 */

/**Imports**/
import java.awt.Graphics2D;


public class Laser extends Weapon {
	/***Declare and Initialize Missile Info***/
	private int distanceRemain;
	private int speed = 1;
	
	/***Constructor***/
	Laser(int xCharPos, int yCharPos, int distance, int dir) {
		super(xCharPos, yCharPos);	//pass character location
		distanceRemain = distance;	//record missile distance
		this.direction = dir;	//record shooting direction
		findImageDirection();	//use method to modify where the painted object is facing
	}
	
	@Override
	/** findImagedirectionection
	 * void method which changes image's width, height, and x/y positions based on its directional value
	 */
	public void findImageDirection() {
		if (direction == 0) {
			dx = speed;
			setX(x+50);
			setY(y+23);
			setImageDimensions(25, 5);
		} else if (direction == 1) {
			setX(x+23);
			setY(y+50);
			dy = speed;  
			setImageDimensions(5, 25);
		} else if (direction == 2) {
			setX(x-23);
			setY(y+23);
			dx = -speed; 
			setImageDimensions(25, 5);
		} else {
			setX(x+23);
			setY(y-23);
			dy = -speed;  
			setImageDimensions(5, 25);
		}
	}
	
	@Override
	/** paint
	 * void method which paints a round rectangle instead
	 */
	public void paint(Graphics2D g) {
		g.fillRoundRect((int)x, (int)y, width, height, width, height);
	}

	/** reachMaxDistance
	 * updates distance using passed value and checks if this Missile class is ending (based on its distance traveled)
	 * @return boolean: if distanceRemain is equal to 0, meaning this missile should be ending
	 */
	public boolean reachMaxDistance() {
		distanceRemain--;
		return distanceRemain == 0;
	}
}
