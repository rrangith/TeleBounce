/*
 *Weapon
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *abstract, super class for all weapons, sub class of entity
 */

abstract class Weapon extends Entity {
	
	protected int direction; //direction value: 0 is rightt, 1 is down, 2 is left, and 3 is up
	
	/***Constructor***/
	Weapon(float x, float y) {
		super(x, y); //gives x,y coordinates to super class
	}
	
	abstract void findImageDirection(); //abstract method to be overwritten in sub classes
	
}
