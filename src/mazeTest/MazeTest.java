package mazeTest;


import mazePD.*;
import mazePD.Maze.MazeMode;

/**
 * A test in which the droid navigates the maze
 * @author Lana Cutter
 */
public class MazeTest {

	/**
	 * Runs the tests on the maze and the droid
	 * @param args
	 */
	public static void main (String[] args) {
		/** The dimensions of the maze (the x- and y- index) */
		int mazeDimensions = 10;
		
		/** How many levels the maze has (the z-index) */
		int mazeLevels = 5;

		Maze maze = new Maze(mazeDimensions, mazeLevels, MazeMode.NORMAL); /** Create a maze */
		Droid droid = new Droid("Robothopper"); /** Create a droid */
		droid.enterMaze(maze); /** Set the droid in the maze */
	
		droid.navigateMaze(); /** The droid navigates the maze */
		
		/** Print out the results of the navigation */
		System.out.println("Maze Test\n");
		printMaze(maze); /** The maze from the maze's perspective */
		System.out.println();
		droid.printDroidMaze(); /** The solved maze from the droid's perspective */
	}
	
	/**
	 * Prints out the maze from the maze's perspective. 
	 * This is in this class since the requirements of this problem say that the Maze class cannot be changed.
	 * @param maze - the maze to print
	 */
	public static void printMaze(Maze maze) {
		System.out.println("Maze from Maze's Perspective - " + maze.toString() + "\n");
		
		/** Print each level (z-index) */
		for(int z = 0; z < maze.getMazeDepth(); z++) {
			System.out.println("Level - " + z);
			String[] mazeArray = maze.toStringLevel(z); /** The mazeArray is an array representing a single level of the maze. */
			
			/**Print out each line of the maze. */
			for(int y = 0; y < maze.getMazeDim(); y++) {
				System.out.println(y + " " + mazeArray[y]); /** The maze array has each line as a single entry, so it is one-dimensional. */
			}
			
			System.out.println(); /** Print out a newline to make extra space between levels. */
		}
	}
}
