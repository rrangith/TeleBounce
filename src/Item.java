/*
 *Item
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *class for all potions
 */

public class Item extends Entity{
	
	String type;	//declare type of item
	
	/***Constructor***/
	Item(float x, float y, String type) {
		super(x, y);	//pass item position to superclass
		this.type = type;	//record this item's type
		if (type.equals("health")) {	//if this is a health potion
			loadImage("HealthPot.png");	//save health potion image
		} else {	//otherwise
			loadImage("SpeedPot.png");		//save speed potion image
		}
		setImageDimensions(50, 50);		//set image size
	}	
	
	/** getType
	 * return the recorded item type
	 * @return type, String: this Item's type
	 */
	public String getType() {
		return type;
	}
	
}
