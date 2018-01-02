/*
 *HighScore
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Class to make a high score object which keeps track of name and high score
 */

public class HighScore implements Comparable<HighScore>{
	private String name; //String to hold name of user
	private int score; //int to hold the score of user

	/***Constructor***/
	public HighScore(String name, int score){
		this.name = name; //sets name
		this.score = score; //sets score
	}

	/** getName
	 * gets the name of the user
	 * @return String, the name of user
	 */
	public String getName() {
		return name;
	}

	/** getScore
	 * gets the score of the user
	 * @return int, the score of user
	 */
	public int getScore() {
		return score;
	}

	/** compareTo
	 * compares HighScore objects by their score
	 * @param the score to be compared
	 * @return int, to check which one has a higher score
	 */
	public int compareTo(HighScore score) {
		if (score.getScore() == this.score){ //checks if equal
			return 0;
		}else if (score.getScore() < this.score){ //checks if the entered score is greater
			return -1;
		}
		return 1; //if above cases are both false
	}


}
