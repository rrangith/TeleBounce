/*
 *TeleTimer
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Teletimer class, the user enters the amount of time they want to keep track of in seconds
 */
public class TeleTimer implements Runnable {
	/**Class variables**/
	private long startTime; //time when timer was started
	private long endTime; //used to keep track of current time
	private double elapsedTime; //time that has passed since timer was started
	private double timeToPass; //value entered by user that timer must go through
	private int timeRemaining; //the amount of time remaining on timer
	private String timeString; //used to display time remaining as a string

	TeleTimer(double timeToPass){ //constructor
		this.timeToPass = timeToPass; //sets the time needed to pass
	}

	/** run
	 * void method which continuously keeps track of time in thread
	 */
	public void run(){
		startTime = System.currentTimeMillis(); //gets the starting time
		do{
			endTime = System.currentTimeMillis(); //gets current time
		}while(((endTime - startTime)/1000) < this.timeToPass); //loops while there is still time left
	}

	/** getElapsedTime
	 * method to return the amount of time that has passed
	 * @return double, the time that has passed
	 */
	public double getElapsedTime(){
		elapsedTime = (endTime-startTime)/1000; //calculates elapsed time
		return elapsedTime; //returns
	}

	/** getTimeRemaining
	 * method to return the amount of time that is remaining
	 * @return int, the time that is remaining
	 */
	public int getTimeRemaining(){
		timeRemaining = (int)(timeToPass - getElapsedTime()); //calculates the amount of time remaining
		return timeRemaining; //returns time remaining
	}

	/** getTimeString
	 * method to return the amount of time that is remaining as a string
	 * @return String, the time that is remaining, number of minutes and number of seconds
	 */
	public String getTimeString(){
		int time = getTimeRemaining(); //gets the time remaining
		int minutes = time/60; //gets the amount of minutes
		int seconds = time%60; //gets the amount of seconds
		if (seconds != 0 && seconds >= 10){ //checks if more than 10 seconds is left
			timeString = minutes + ":" + seconds; //sets the value of the string
		}else if (seconds != 0){ //checks if there is less than 10 seconds left, but the timer is not done
			timeString = minutes + ":0" + seconds; //sets value of string
		}else{ //if timer is done
			timeString = minutes + ":00"; //sets value of string
		}
		return timeString; //returns string
	}

}
