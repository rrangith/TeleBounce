/*
 *GUILauncher
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Contains the main method, loads scores from text file, starts gui, then writes updated scores to text file
 */

/**Imports**/
import java.io.*;
import java.util.ArrayList;

public class GUILauncher {
	/***Main Method***/
	public static void main (String[] args) throws IOException{ //main method
		ArrayList<HighScore> highScoreList = new ArrayList<HighScore>(); //creates an ArrayList to store high scores
		HighScore score; //temp variable to store a score
		
		//File scoreFile = new File("scores.txt"); //makes file which contains score
		//FileReader fr = new FileReader(scoreFile); //file reader
		BufferedReader br = new BufferedReader(new InputStreamReader(GUILauncher.class.getClassLoader().getResourceAsStream("scores.txt"))); //buffered reader to read from text file
		
		String names[] = new String[3]; //temp array to hold names
		Integer scores[] = new Integer[3]; //temp array to hold scores
		String line; //temp string to hold line from text file
		int count = 0; //temp variable to go to each index of array
		
		try{ //try-catch
			while((line = br.readLine())!= null){ //reads while there are still lines in the text file
				names[count] = line; //gets the name
				scores[count] = Integer.parseInt(br.readLine()); //gets the score
				count++; //adds one to count
			}
			br.close(); //closes buffered reader
		}catch(FileNotFoundException ex){ //catches file not found exception
			ex.printStackTrace(); //prints location of error
		}
		
		for (int i = 0; i < 3; i++){ //loops 3 times
			score = new HighScore(names[i], scores[i]); //makes a score
			highScoreList.add(score); //adds to score list
		}
		
		GUI gui = new GUI(highScoreList); //makes gui, puts in high score list
		
		while (gui.getKeepRunning()){ //runs while loop to prevent main method from continuing, exits when the gui is closed
		}
		
		highScoreList = gui.getHighScores(); //gets updated high score list
		PrintWriter output = new PrintWriter("scores.txt"); //makes a print writer to write to score text file
		
		for (int i = 0; i < 3; i++){ //loops 3 times
			output.println(highScoreList.get(i).getName()); //writes name
			output.println(highScoreList.get(i).getScore()); //writes score
		}
		output.close(); //closes print writer
	}

}
