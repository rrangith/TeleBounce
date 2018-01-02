/*
 *Enemy
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *subclass of entity, the object which attacks the user
 */

/**Imports**/
import java.awt.Rectangle;


public class Enemy extends Entity {
	
	private double speed;	//declare moving speed of an enemy
	
	/***Constructor***/
	public Enemy(float x, float y, double speed, String imageName) {
		super(x, y);	//pass position to superclass
		this.speed = speed;		//record speed
		loadImage(imageName);	//save image
		setImageDimensions(50, 50);		//set image size
	}
	
	/** getTranslatedBounds
	 * determine the next location of the enemy based on character x and y position, then use it to return the bounds
	 * of the image's next location
	 * used for checking wall collision
	 * @param xCharPos, float: character's x position
	 * @param yCharPos, float: character's y position
	 * @return Rectangle: entity/image's bound of next location
	 */
	public Rectangle getTranslatedBounds(float xCharPos, float yCharPos) {
		if (x < xCharPos) {	//if character is to the left this enemy
			dx = (float)speed;
		} else if (x > xCharPos) {	//if character is to the right of this enemy
			dx = (float)-speed;
		}
		if (y < yCharPos) {	//if character is above this enemy
			dy = (float) speed;
		} else if (y > yCharPos) {	//if character is below this enemy
			dy = (float) -speed;
		}
		return new Rectangle((int)(x+dx), (int)(y+dy), width, height);
	}

	/** moveAround
	 * void method which gets the position of the enemy relative to the character and moves it around a rock accordingly
	 */
	public void moveAround() {
		if (dx < 0 && dy < 0) { //if character is north west
			y-=1.8*dy;
		} else if (dx < 0 && dy > 0) { //if character is south west
			x-=1.8*dx;
		} else if (dx > 0 && dy < 0) { //if character is north east
			x-=1.8*dx;
		} else if (dx > 0 && dy > 0) { //if character is south east
			y-=1.8*dy;
		}
	}
}
