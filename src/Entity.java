/*
 *Entity
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *superclass of all objects to be spawned into the game
 */

/**Imports**/
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public abstract class Entity {
	/***Entity Info***/
	protected float x;
	protected float y;
	protected float dx;
	protected float dy;
	protected int width;
	protected int height;
	private Image image;
	
	/***Constructor***/
	Entity(float x, float y) {
		//record entity position
		this.x = x;
		this.y = y;
	}
	
	/** loadImage
	 * void method which saves image in variables using the given file location (String) 
	 * @param imageName, String: file location
	 */
	public void loadImage(String imageName) {
		ImageIcon ii = new ImageIcon(GUILauncher.class.getClassLoader().getResource(imageName));
		image = ii.getImage();
	}
	
	/** setImageDimensions
	 * void method which saves the width and height info of image
	 * @param w, int: width of image
	 * @param h, int: height of image
	 */
	public void setImageDimensions(int w, int h) {
		width = w;
		height = h;
	}
	
	/** paint
	 * void method which paints this entity's image based on its x and y positions
	 * @param g, Graphics2D: object for painting
	 */
	public void paint(Graphics2D g) {
		g.drawImage(image, (int)x, (int)y, width, height, null);
	}
	
	/** getImage
	 * returns the image of this entity, saved previously (for Arena painting)
	 * @return image, Image: image of this entity
	 */
	public Image getImage() {
		return image;
	}
	
	/** setX
	 * void method which sets x to the passed x value 
	 * @param x, int: the replacing x value
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/** setY
	 * void method which sets y to the passed y value
	 * @param y, int: the replacing y value
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/** getX
	 * returns the x position of this entity
	 * @return x, float: the x position
	 */
	public float getX() {
		return x;
	}
	
	/** getY
	 * returns the y position of this entity
	 * @return y, float: the y position
	 */
	public float getY() {
		return y;
	}
	
	/** move
	 * void method which moves this entity by adding delta x and y to the x and y positions
	 */
	public void move() {
		x += dx;
		y += dy;		
	}
	
	/** getBounds
	 * returns a Rectangle, the bounds of this entity, based on the x/y positions and its dimensions
	 * @return Rectangle: a rectangle representing the image's location and size
	 */
	public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}
