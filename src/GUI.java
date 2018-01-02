/*
 *GUI
 *@author Rahul Rangith and Justin Liao
 *@version Jan 21st, 2017 
 *Main gui for game
 */

/**Imports**/
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI {

	/**Class Variables**/
	boolean keepRunning = true; //variable to check if the gui is still open
	String characterName = "Eye.png"; //default character
	String[] characterList = {"Circle Bob", "Face", "Iye", "Illumi"}; //list of character names
	String[] characterFiles = {"circlepants.png", "Face.png", "Eye.png", "Illumi.png"}; //list of character files
	ArrayList<HighScore> highScores; //high score arraylist
	String[] levelList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}; //list of levels
	private Thread music;
	
	/**Gui components**/
	private JTabbedPane tab; //tabbed pane for tabs in gui
	JComboBox<String> levelBox = new JComboBox<String>(levelList); //combo box to select level
	JComboBox<String> characterSelectBox; //combo box to select character
	JFrame frame = new JFrame("TeleBounce"); //the frame
	
	//Buttons which are used to display names and scores
	private JButton nameFirst;
	private JButton scoreFirst;
	private JButton nameSecond;
	private JButton scoreSecond;
	private JButton nameThird;
	private JButton scoreThird;

	Game g; //variable for game, to be created later

	/** getKeepRunning
	 * gets class variable and returns as boolean true or false
	 * @return boolean, to see if program should keep running
	 */
	public boolean getKeepRunning(){
		return keepRunning;
	}

	/** getHighScores
	 * gets list of high scores, used to write to text file at the end
	 * @return ArrayList, the list of high scores
	 */
	public ArrayList<HighScore> getHighScores() {
		return highScores;
	}
	
	/***Constructor***/
	GUI(ArrayList<HighScore> highScoreList){
		frame.setSize(1000, 1000); //sets size
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //sets default close operation
		frame.setResizable(false); //make it so can not be resized
		frame.addWindowListener(new WindowClose()); //adds window listener to JFrame
		highScores = highScoreList; //sets the list of high scores from the list that was imported from text file
		setMenu(); //sets the entire gui
		frame.setVisible(true); //sets JFrame to visible
		AudioPlayer a = new AudioPlayer("music.wav"); //creates audio player
		music = new Thread(a); //makes thread for music
		music.start(); //starts the thread so that music loops while game runs
	}

	/** setMenu
	 * void method that creates tabbed pane and adds tabs to gui
	 */
	public void setMenu(){
		tab = new JTabbedPane(); //creates tabbed pane
		tab.addChangeListener(new TabChangeListener()); //adds change listener

		tab.add("Play Survival", setMainScreen()); //adds main tab
		tab.add("Play Selected Level", setLevelScreen()); //adds level selection tab
		tab.add("Select Character(Singleplayer)", setCharacterScreen()); //adds character selection tab  
		tab.add("Play MultiPlayer Arena", setMultiScreen()); //adds multiplayer tab
		tab.add("Instructions", setInstructionScreen()); //adds instructions tab
		tab.add("High Scores", setScoreScreen()); //adds high score tab

		frame.add(tab); //adds tabbed pane to gui

	}
	
	/* setMainScreen
	 * creates main screen for survival mode
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setMainScreen(){
		JPanel mainScreen = new JPanel(); //main panel to be returned

		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //background image
		background.setLayout(new FlowLayout()); //sets layout

		JPanel mainPanel = new JPanel(new BorderLayout()); //creates a new panel to add onto
		JLabel logo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("logo.gif"))); //logo for the game
		JButton playButton = new JButton("Play"); //play button
		playButton.addActionListener(new PlayButtonListener()); //adds listener
		
		mainPanel.add(playButton, BorderLayout.CENTER); //adds button to panel
		mainPanel.add(logo, BorderLayout.NORTH); //adds logo to panel

		JLabel enemyOne = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("enemy1.png"))); //picture of enemy

		background.add(mainPanel); //adds panel to background
		background.add(enemyOne); //adds picture to background
		mainScreen.add(background); //adds background to panel
		return mainScreen; //returns panel
	}
	
	/** setInstructionsScreen
	 * creates screen for instructions
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setInstructionScreen(){
		JPanel instructionScreen = new JPanel(); //creates panel to be returned
		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //adds background
		background.setLayout(new FlowLayout());
		
		JLabel instructions = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Instructions.png")));
		JLabel logo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("instructionslogo.png")));
		background.add(logo);
		background.add(instructions);
		
		instructionScreen.add(background); //adds background to panel
		return instructionScreen; //returns panel
	}
	
	/* setCharacterScreen
	 * creates character selection screen
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setCharacterScreen(){
		JPanel mainScreen = new JPanel(); //main panel to be returned

		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //adds background
		background.setLayout(new FlowLayout()); //sets layout
		JPanel characterScreen = new JPanel(new GridLayout(2, 2)); //makes panel to display character images

		JPanel firstPanel = new JPanel(new BorderLayout()); //panel for first character
		JLabel characterOne = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("circlepants.png"))); //image for first character
		JLabel logoOne = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("circlepantslogo.png"))); //logo for first character
		firstPanel.add(characterOne, BorderLayout.CENTER); //adds image
		firstPanel.add(logoOne, BorderLayout.SOUTH); //adds logo

		JPanel secondPanel = new JPanel(new BorderLayout()); //panel for second character
		JLabel characterTwo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Face.png"))); //image
		JLabel logoTwo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("facelogo.png"))); //logo
		secondPanel.add(characterTwo, BorderLayout.CENTER); //adds image
		secondPanel.add(logoTwo, BorderLayout.SOUTH); //adds logo

		JPanel thirdPanel = new JPanel(new BorderLayout()); //panel for third character
		JLabel characterThree = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Eye.png"))); //image
		JLabel logoThree = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("iyelogo.png"))); //logo
		thirdPanel.add(characterThree, BorderLayout.CENTER); //adds image
		thirdPanel.add(logoThree, BorderLayout.SOUTH); //adds logo

		JPanel fourthPanel = new JPanel(new BorderLayout()); //panel for fourth character
		JLabel characterFour = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Illumi.png"))); //image
		JLabel logoFour = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("illumilogo.png"))); //logo
		fourthPanel.add(characterFour, BorderLayout.CENTER); //adds image
		fourthPanel.add(logoFour, BorderLayout.SOUTH); //adds logo

		//adds all panels to the main character display panel
		characterScreen.add(firstPanel);
		characterScreen.add(secondPanel);
		characterScreen.add(thirdPanel);
		characterScreen.add(fourthPanel);

		characterSelectBox = new JComboBox<String>(characterList); //makes a combo box to select the character
		characterSelectBox.addActionListener(new CharacterComboListener()); //adds action listener

		background.add(characterSelectBox); //adds combo box to background
		background.add(characterScreen); //adds character screen to background
		mainScreen.add(background); //adds background

		return mainScreen; //returns main panel
	}
	
	/** setLevelScreen
	 * creates level selection screen
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setLevelScreen(){
		JPanel mainScreen = new JPanel(); //main panel to be returned

		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //makes background
		background.setLayout(new FlowLayout());//sets layout

		JPanel mainPanel = new JPanel(); //panel to hold all components
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); //sets layout

		JLabel instructions = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("logo2.png"))); //makes logo
		background.add(instructions); //adds logo to background

		JPanel levelScreen = new JPanel(new GridLayout(2, 5)); //panel to hold all level images

		JButton playButton = new JButton("Play"); //play button
		playButton.addActionListener(new PlayLevelButtonListener()); //adds action listener

		JPanel levelPanels[] = new JPanel[10]; //makes panels for all levels
		JLabel levelLabels[] = new JLabel[10]; //makes labels for all levels
		ImageIcon numberIcons[] = new ImageIcon[10]; //makes image icons for all levels
		String fileName; //temp variable for file name

		for (int i = 0; i < levelPanels.length; i++){ //loops 10 times
			levelPanels[i] = new JPanel(); //creates panel
			fileName = (i+1) + ".png"; //sets the value of the string
			numberIcons[i] = new ImageIcon(GUILauncher.class.getClassLoader().getResource(fileName)); //makes the image icon the string name
			levelLabels[i] = new JLabel(numberIcons[i]); //makes the label
			levelPanels[i].add(levelLabels[i]); //adds label to panel
			levelScreen.add(levelPanels[i]); //adds small panel to big panel
		}

		mainPanel.add(levelScreen);//adds group of level images
		background.add(mainPanel);//adds panel to background
		background.add(levelBox); //adds combo box to background
		background.add(playButton); //adds play button to background
		mainScreen.add(background); //adds background to main panel
		return mainScreen; //returns main panel
	}
	
	/** setScoreScreen
	 * creates screen to display high scores
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setScoreScreen(){
		JPanel mainScreen = new JPanel(); //main panel to be returned

		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //sets background
		background.setLayout(new FlowLayout()); //sets layout

		JPanel scoreScreen = new JPanel(); //makes panel to display scores
		scoreScreen.setLayout(new BoxLayout(scoreScreen, BoxLayout.Y_AXIS)); //sets layout

		JPanel firstPanel = new JPanel(new GridLayout()); //makes panel to display first score
		JLabel firstLabel = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("1st.png"))); //image of first
		nameFirst = new JButton (highScores.get(0).getName()); //name of highest score, imported from text file
		scoreFirst = new JButton(Integer.toString(highScores.get(0).getScore())); //highest score, imported from text file

		firstPanel.add(firstLabel); //adds label
		firstPanel.add(nameFirst); //adds name
		firstPanel.add(scoreFirst); //adds score

		JPanel secondPanel = new JPanel(new GridLayout(1, 3)); //makes panel to display second score
		JLabel secondLabel = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("2nd.png"))); //image of second
		nameSecond = new JButton (highScores.get(1).getName()); //name of score, imported from text file
		scoreSecond = new JButton(Integer.toString(highScores.get(1).getScore())); //2nd score, from text file

		secondPanel.add(secondLabel); //adds label
		secondPanel.add(nameSecond); //adds name
		secondPanel.add(scoreSecond); //adds score

		JPanel thirdPanel = new JPanel(new GridLayout()); //panel for third score
		JLabel thirdLabel = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("3rd.png"))); //image of third
		nameThird = new JButton (highScores.get(2).getName()); //imported from text file
		scoreThird = new JButton(Integer.toString(highScores.get(2).getScore())); //3rd score, from text file

		thirdPanel.add(thirdLabel); //adds label
		thirdPanel.add(nameThird); //adds name
		thirdPanel.add(scoreThird); //adds score

		scoreScreen.add(firstPanel); //adds first panel
		scoreScreen.add(secondPanel); //adds second panel
		scoreScreen.add(thirdPanel); //adds third panel

		background.add(scoreScreen); //adds score display to background
		mainScreen.add(background); //adds background to main panel
		return mainScreen; // return panel
	}

	/** setMultiScreen
	 * creates screen for multiplayer
	 * @return JPanel, panel to be added to tabbed pane
	 */
	private JPanel setMultiScreen(){
		JPanel mainScreen = new JPanel(); //makes panel to be returned

		JLabel background = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("background.jpg"))); //makes background
		background.setLayout(new FlowLayout()); //sets layout

		JPanel mainPanel = new JPanel(new BorderLayout()); //makes panel to add logo and button to
		JLabel logo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Multi.png"))); //makes logo
		JButton playButton = new JButton("Play"); //makes play button
		playButton.addActionListener(new MultiPlayButtonListener()); //adds listener
		mainPanel.add(playButton, BorderLayout.CENTER); //adds button to panel
		mainPanel.add(logo, BorderLayout.NORTH); //adds logo

		//makes labels for all characters
		JLabel characterOne = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("circlepants.png")));
		JLabel characterTwo = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Eye.png")));
		JLabel characterThree = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Illumi.png")));
		JLabel characterFour = new JLabel(new ImageIcon(GUILauncher.class.getClassLoader().getResource("Face.png")));

		background.add(mainPanel); //adds panel to background
		//adds pictures of all characters
		background.add(characterOne);
		background.add(characterTwo);
		background.add(characterThree);
		background.add(characterFour);
		
		mainScreen.add(background); //adds background to panel

		return mainScreen; //returns panel
	}

	/**Inner Classes**/
	public class WindowClose implements WindowListener { //inner class for detecting when the window is closed

		public void windowActivated(WindowEvent arg0) {}//unused

		public void windowClosed(WindowEvent arg0) {}//unused

		/** windowClosing
		 * void method which sets the class variable to false so GUILauncher knows game is done running
		 */
		public void windowClosing(WindowEvent event) {
			keepRunning = false;
		}

		public void windowDeactivated(WindowEvent arg0) {}//unused

		public void windowDeiconified(WindowEvent arg0) {}//unused

		public void windowIconified(WindowEvent arg0) {}//unused

		public void windowOpened(WindowEvent arg0) {}//unused
	}

	public class PlayButtonListener implements ActionListener{ //inner class for play button on main screen

		/* actionPerformed
		 * void method which sets the gui to invisible and creates the game
		 * @param ActionEvent, the event which occurred, in this case clicking the button
		 */
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false); //sets frame to invisible
			g = new Game(characterName, 1, false, highScores, frame); //creates game
		}

	}

	public class PlayLevelButtonListener implements ActionListener{ //inner class for level selection button

		/** actionPerformed
		 * void method which sets the gui to invisible and creates the game with only the level chosen
		 * @param ActionEvent, the event which occurred, in this case clicking the button
		 */
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false); //sets frame to invisible
			g = new Game(characterName, levelBox.getSelectedIndex()+1, true, highScores, frame); //creates game
		}

	}

	public class MultiPlayButtonListener implements ActionListener{ //inner class for multiplayer button

		/** actionPerformed
		 * void method which sets the gui to invisible and creates the multiplayer game
		 * @param ActionEvent, the event which occurred, in this case clicking the button
		 */
		public void actionPerformed(ActionEvent event) {
			frame.setVisible(false); //sets frame to invisible
			int playerOne; //temp variable for player one's character
			int playerTwo; //temp variable for player two's character
			playerOne = (int)(Math.random()*4); //generates a random number from 0-3
			do{
				playerTwo = (int)(Math.random()*4);//generates a random number from 0-3
			}while (playerTwo == playerOne); //loops until the numbers are different
			new Arena(characterFiles[playerOne], characterFiles[playerTwo], frame); //creates multiplayer game, with random characters
		}

	}

	public class CharacterComboListener implements ActionListener{ //inner class for character selection combo box

		/** actionPerformed
		 * void method which sets the character to the one the user has selected
		 * @param ActionEvent, the event which occurred, in this case choosing an option from the combo box
		 */
		public void actionPerformed(ActionEvent event) {
			int choice = characterSelectBox.getSelectedIndex(); //gets the index of the selected item
			characterName = characterFiles[choice]; //sets the string of the character name to the one chosen
		}

	}

	public class TabChangeListener implements ChangeListener{ //inner class for checking a specific tab change

		/** stateChanged
		 * void method which checks if the high scores tab was selected and updates it with new high scores
		 * @param ChangeEvent, the event which occurred, in this case changing tabs
		 */
		public void stateChanged(ChangeEvent event) {
			if (tab.getSelectedIndex() == 4 && g != null){ //checks if a game has already been run and checks if the high score tab was selected
				highScores = g.getScoreList(); //gets high score list from the game object
				//sets the names and scores of the top 3
				nameFirst.setText(highScores.get(0).getName());
				scoreFirst.setText(Integer.toString(highScores.get(0).getScore()));
				nameSecond.setText(highScores.get(1).getName());
				scoreSecond.setText(Integer.toString(highScores.get(1).getScore()));
				nameThird.setText(highScores.get(2).getName());
				scoreThird.setText(Integer.toString(highScores.get(2).getScore()));
			}

		}

	}	

}
