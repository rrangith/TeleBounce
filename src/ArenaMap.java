/*
 *ArenaMap
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Map class for Multiplayer Arena
 */

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;


public class ArenaMap {
	/***Declare Map Info***/
	//tile size
	public static int width = 50;
	public static int height = 50;
	//all ImageIcons for tile painting
	ImageIcon ground = new ImageIcon(GUILauncher.class.getClassLoader().getResource("ground.jpg"));
	ImageIcon stoneGround = new ImageIcon(GUILauncher.class.getClassLoader().getResource("stone.jpg"));
	ImageIcon wall = new ImageIcon(GUILauncher.class.getClassLoader().getResource("wall.jpg"));
	//wall bounds
	ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
	//the default map array
	private int[][] map = {
			{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
			{2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2},
			{2,1,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,2},
			{2,1,0,2,2,2,2,2,2,2,2,0,1,0,0,1,0,2,2,2,2,2,2,2,2,0,1,2},
			{2,1,0,2,2,2,2,2,2,2,2,0,1,0,0,1,0,2,2,2,2,2,2,2,2,0,1,2},
			{2,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,2},
			{2,1,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,1,2},
			{2,1,0,0,0,0,1,2,2,1,0,0,0,0,0,0,0,0,1,2,2,1,0,0,0,0,1,2},
			{2,1,1,1,1,1,1,2,2,1,0,0,0,0,0,0,0,0,1,2,2,1,1,1,1,1,1,2},
			{2,1,0,0,0,0,1,2,2,1,0,0,0,2,2,0,0,0,1,2,2,1,0,0,0,0,1,2},
			{2,1,0,2,2,0,1,2,2,1,0,0,0,2,2,0,0,0,1,2,2,1,0,2,2,0,1,2},
			{2,1,0,2,2,0,1,0,0,1,0,0,0,0,0,0,0,0,1,0,0,1,0,2,2,0,1,2},
			{2,1,0,2,2,0,1,0,0,1,0,0,0,0,0,0,0,0,1,0,0,1,0,2,2,0,1,2},
			{2,1,0,0,0,0,1,2,2,1,0,0,0,0,0,0,0,0,1,2,2,1,0,0,0,0,1,2},
			{2,1,1,1,1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,2,2,1,1,1,1,1,1,2},
			{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}};
	
	/***Constructor***/
	ArenaMap() {
		//loop through all elements/tiles in the map array
		for (int j = 0; j < map.length; j++) {
			for (int i = 0; i < map[1].length;i++) {
				if (map[j][i] == 2) {	//if this element is a wall
					walls.add(new Rectangle(i*width, j*height, width, height));	//get this wall's bound info and add it to the list
				}
			}
		}
	}
	
	/** paint
	 * void method that paints all tiles and info on screen/frame, based on the map array
	 * @param g Graphics2D: painting tool
	 */
	public void paint(Graphics2D g) {
		for (int j = 0; j < map.length; j++) {
			for (int i = 0; i < map[1].length;i++) {
				g.drawImage(ground.getImage(), i*width, j*height, width, height, null);		//draw ground regardless of the element
				if (map[j][i] == 1) {		//if this is a stone ground tile
					g.drawImage(stoneGround.getImage(), i*width, j*height, width, height, null);	//draw stone ground on this position
				}else if (map[j][i] == 2) {		//if this is a wall tile
					g.drawImage(wall.getImage(), i*width, j*height, width, height, null);	//draw wall on this position
				}
			}
		}
	}
	
	/** blocked
	 * check if the passed bound(Rectangle) intersects with any of the wall tile's bound
	 * @param bound, Rectangle: the bound to be checked
	 * @return boolean: if this bound collides with any of the wall tile's bound
	 */
	public boolean blocked(Rectangle bound) {
		for (Rectangle wall: walls) {	//loop through the wall tile list
			if (wall.intersects(bound)) {	//if this wall tile intersects the given bound
				return true;	//return true
			}
		}
		return false;	//if none of them intersected the bound, return false
	}
}
