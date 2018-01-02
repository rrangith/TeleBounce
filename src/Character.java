/*
 *Character
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *subclass of entity, the object which the user controls
 */

/**Imports**/
import java.awt.Rectangle;
import java.util.ArrayList;

public class Character extends Entity {
	
	/***Declare Character Info***/
	private ArrayList<Laser> missiles = new ArrayList<Laser>();
	private int health;
	
	/***Constructor***/
	Character(float x, float y, String imageName, int health) {	
		super(x, y);	//pass position to superclass
		this.health = health;	//record total health of character
		loadImage(imageName);	//save image
		setImageDimensions(50, 50);		//set image size
	}
	
	
	/** getMissiles
	 * return character's ArrayList of existing missiles
	 * @return missiles, ArrayList: the list of fired missiles
	 */
	public ArrayList<Laser> getMissiles() {
		return missiles;
	}
	
	/** getHealth
	 * return character's health variable
	 * @return health, int: character's current number of health
	 */
	public int getHealth() {
		return health;
	}
	
	/** damaged
	 * void method which subtracts character's health by the given int value, used for depleting character
	 * health points
	 * @param damage, int: damage given to character's health
	 */
	public void damaged(int damage) {
		health-=damage;
	}
	
	/** heal
	 * void method which adds character's health by the given int value, used when obtaining a health potion
	 * @param heal, int: recovered value for character's health
	 */
	public void heal(int heal) {
		health+=heal;
	}
	
	/** getTranslatedBounds
	 * records character delta x and y positions, then use them to return the bounds of the image's next location 
	 * used for checking  wall collision
	 * @param dx, float: delta x of character
	 * @param dy, float: delta y of character
	 * @return Rectangle: entity/image's bound of next location
	 */
	public Rectangle getTranslatedBounds(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
		return new Rectangle((int)(x+dx), (int)(y+dy), width, height);
	}
	
}
