/*
 *Punch
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *subclass of entity, the object which attacks the enemy
 */

/**Imports**/
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;


public class Punch extends Weapon{
	/***Declare Punch Info***/
	private double punchDuration = 0.1;
	private boolean hitConfirm = false;
	private AffineTransform identity;
	private AffineTransform trans;

	/***Constructor***/
	Punch(float xCharPos, float yCharPos, String imageName, int dir) {
		super(xCharPos, yCharPos); //pass character location to superclass
		loadImage(imageName);	//save image

		direction = dir;	//record the image's direction
		findImageDirection();	//use method to modify where the image is facing
	}
	
	@Override
	/** findImagedirectionection
	 * void method which changes image's width, height, and x/y positions based on its directional value
	 */
	public void findImageDirection() {
		//if facing up
		width = getImage().getHeight(null);
		height = getImage().getWidth(null);
		if (direction == 0) {	//if facing right
			setX(x+width);
			width = getImage().getWidth(null);
			height = getImage().getHeight(null);
		} else if (direction == 1) {	//if facing down
			setX(x+width);
			setY(y+width);
		} else if (direction == 2) {	//if facing left
			setY(y+width);
			width = getImage().getWidth(null);
			height = getImage().getHeight(null);
		}
		
		//use AffineTransform to rotate the image, based directional value
		identity = new AffineTransform();
		trans = new AffineTransform();
		trans.setTransform(identity);
		trans.translate((int)x, (int)y);
		trans.rotate(Math.toRadians(90*direction));
	}
	
	@Override
	/** paint
	 * void method which paints image based on AffineTransform
	 */
	public void paint(Graphics2D g) {		
		g.drawImage(getImage(), trans, null); 
	}
	
	/** reachMaxDuration
	 * updates punchDuration using passed value and checks if this Punch class is ending (based on its punch duration)
	 * @param elapsedTime, double: used to update punchDuration
	 * @return boolean: if punchDuration is below are equal to 0, meaning this punch should be ending
	 */
	public boolean reachMaxDuration(double elapsedTime) {
		punchDuration-=elapsedTime;
		return punchDuration <= 0;
	}
	
	/** setHitConfirm
	 * sets hitConfirm to the passed boolean value (used for Arena)
	 * @param hitConfirm, boolean: the replacing boolean value
	 */
	public void setHitConfirm(boolean hitConfirm) {
		this.hitConfirm = hitConfirm;
	}
	
	/** isHitConfirm
	 * returns hitConfirm, a boolean type variable (used for Arena)
	 * @return hitConfirm, boolean: checks if this Punch has intersected another player already
	 */
	public boolean isHitConfirm() {
		return hitConfirm;
	}
	
	@Override
	/** getBounds
	 * returns a Rectangle, the bounds of this entity, based on the x/y positions, its image dimensions, and its directional value
	 * @return Rectangle: a rectangle representing the image's location and size
	 */
	public Rectangle getBounds() {
		if (direction == 1) {	//if facing down
			return new Rectangle((int)x-width, (int)y, width, height);
		} else if (direction == 2) {	//if facing left
			return new Rectangle((int)x-width, (int)y-height, width, height);
		} else if (direction == 3) {	//if facing up
			return new Rectangle((int)x, (int)y-height, width, height);
		} 
		return new Rectangle((int)x, (int)y, width, height);	//if facing right
	}
}