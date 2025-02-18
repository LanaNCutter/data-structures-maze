package mazePD;

import java.util.ArrayList;
import java.util.HashSet;

import mazePD.Maze.Content;
import mazePD.Maze.Direction;
import mazeUI.MazePanel;

/**
 * @author Lana Cutter
 * A droid whose sole purpose is to travel a maze like an experimental rat 
 */
public class Droid implements DroidInterface {

	/** Attributes ***************************************************************/
	
	/** Various statuses the droid can have. 
	 * IDLE - the droid doesn't have a maze
	 * READY - the droid has entered a maze
	 * EXPLORING - the droid is currently navigating a maze
	 * FINISHED - the droid has finished exploring a maze
	 */
	public enum Status {IDLE, READY, EXPLORING, FINISHED};
	
	/** The status the droid is currently in. */
	private Status status;
	
	/** Whether the maze has been traveled or not. This determines whether or not to reinitialize the 
	 * path and related variables.  
	 */
	private boolean hasBeenTraveled;
	
	/**The name of the droid. */
	private String name; 	
	
	/** A stack of movements from the maze's beginning to the droid's current location. 
	 * Entries are pushed on when the droid moves forward and popped off when it backtracks.*/
	private LinkedStack<Coordinates> path; 			
	
	/**A set of cells which the droid has visited on its journey.*/
	private HashSet<Coordinates> visitedCells;
	
	/**The maze which the droid is exploring.*/
	private Maze maze;
	
	/** The coordinates that the droid has scanned in navigation */
	private HashSet<Coordinates> scannedCells;
	
	/** The panel to update when the droid moves */
	private MazePanel panel;

	/** Constructors *************************************************************/
	
	/**
	 * The default constructor.
	 */
	public Droid() {
		this.initializeMaze();
		this.setStatus(Status.IDLE);
		this.hasBeenTraveled = false;
		this.panel = null;
	}
	
	/**
	 * The non-default constructor. Creates a droid given the droid's name and the maze which the droid is to navigate.
	 * @param name - the droid's name
	 */
	public Droid(String name) {
		this();
		this.setName(name);
	}
	
	/** Getters and Setters ******************************************************/
	
	private void setMaze(Maze maze) {
		this.maze = maze;
		this.initializeMaze(); /** Reset everything pertaining to the maze. */
	}
	
	public Maze getMaze() {
		return this.maze;
	}
	
	private void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setPanel(MazePanel panel) {
		this.panel = panel;
	}
	
	public MazePanel getPanel() {
		return this.panel;
	}
	
	public HashSet<Coordinates> getScannedCells() {
		return this.scannedCells;
	}
	
	/** Methods ******************************************************************/
	
	/**
	 * Initialize the path, visited cells, and scanned cells to nothing in preparation for navigating a maze
	 */
	private void initializeMaze() {
		this.path = new LinkedStack<Coordinates>();
		this.visitedCells = new HashSet<Coordinates>();
		this.scannedCells = new HashSet<Coordinates>();
	}
	
	/**
	 * Causes the droid to enter the maze. This happens on initializing the droid.
	 */
	public void enterMaze(Maze maze) {
		setMaze(maze);
		maze.enterMaze(this); /** Does work on the maze end of the problem. */
		Coordinates location = maze.getCurrentCoordinates(this); /** The droid starts at the beginning of the maze. */
		path.push(location); /** Pushes the start location onto the stack. */
		visitedCells.add(location); /** Adds the start location to the set of visited cells. */
		scannedCells.add(location); /** The droid has scanned the current location */
		this.setStatus(Status.READY); /** The droid is ready to start exploring the maze */
		
		/** Note: Do not call functions pertaining to updating the UI here because the panel is not 
		 * initialized yet. 
		 */
	}
	
	/**
	 * Causes the droid to use the portal and advance to the next level of the maze. 
	 */
	private void usePortal() {
		Coordinates location = maze.usePortal(this, Direction.DN); /** The droid moves down, but the z-index goes up. */
		path.push(location); /** Pushes the droid's new location, in the next level, onto the stack. */
		visitedCells.add(location); /** Adds the droid's new location to the set of visited cells. */
		scannedCells.add(location); /** Add the droid's location to the scanned cells */
		this.panel.updateLevel(); /** Update the level in the UI to start displaying the level the droid
									* is currently on */
	}
	
	/**
	 * The droid navigates through the maze and finds a path from the beginning, through the portals, to the end.
	 */
	public void navigateMaze() {
		
		panel.updateLevel(); /** Update the panel's level to begin with in case the user changed the level
								* before the droid started. */
		
		/** If the current maze has been traveled before, reset the information gathered when exploring
		 * and reenter the maze.
		 */
		if(hasBeenTraveled) {
			this.initializeMaze();
			this.enterMaze(maze);
		}
		
		this.setStatus(Status.EXPLORING); /** The droid is now exploring. */
		this.panel.updateStatus(); /** Update the status in the UI. */
		
		boolean pathFound = true; /** Assume that there is a way through the maze to begin with. */
		/**Keep moving in the maze unless:
		 	*  the stack is empty (no path found)
		 	*  the droid has reached the end
		 	*  it is proven that there is no path through. */
		while(!path.isEmpty() && maze.scanCurLoc(this) != Content.END && pathFound) { 
			
			boolean canProgress = false; /** Assume that the droid can't move forward until a square is shown to be available and unvisited. */
			boolean portalFound = false; /** Assume that the droid is not on a portal. */
			Content[] surroundingCellContents = maze.scanAdjLoc(this); /** An array with elements at 0, 90, 180, and 270 degrees.*/
			int directionIndex = -1; /** This references the previous array. */
			Content c = null; /** Holds a single Content of a cell. */
	
			/** Iterates through the surrounding cells to see if the droid can move into any of them. */
			this.scanCells(); /** Add the surrounding cells to the scanned cells */
			for(int i = 0; i < surroundingCellContents.length; i++) {
				c = surroundingCellContents[i]; /** c is now the content of the current adjacent cell at index i in the array. */
				
				
				/** Checks the content of the adjacent cell to see if the droid can progress and sets settings based on that content. */
				if(c == Content.END) { /** If an adjacent cell is the end, */
					canProgress = true; /** the droid can move there, */
					directionIndex = i; /** the direction is set to be towards that cell, */
					break; /** and since the droid can progress, break out of the cell checking loop. */
				} 
				else if(c == Content.PORTAL_DN) { /** If an adjacent cell is a portal down to the next level, */
					canProgress = true; /** the droid can move there, */
					directionIndex = i; /** the direction is set to be towards that cell, */
					portalFound = true; /** a portal has been found, */
					break; /** and since the droid can progress, break out of the cell checking loop. */
				} 
				else if(c != Content.BLOCK  && c != Content.NA && !isVisited(i)) { /** If an adjacent cell can be moved into 
																		(it is not a block or a wall) and it is not visited, */
					canProgress = true; /** the droid can move there, */
					directionIndex = i; /** the direction is set to be towards that cell, */
					break; /** and since the droid can progress, break out of the cell checking loop. */
				}
			}
			
			/** Does actions (move forward, move backward, or no path found) based on the settings set while checking 
			 * the adjacent cells. */
			if(canProgress) { /** Move forward if it is possible to do so. */
				this.move(directionIndex); /** Move in the direction of the first possible cell to move into. */
				if(portalFound) { /** If the droid has moved onto a portal, */
					this.usePortal(); /** go through the portal. */
				}
			} else if (path.peek()!= null) { /** If it is possible to backtrack (there are cells to backtrack to), */
				this.backtrack(); /** move back. */
			} else { /** If it is not possible to move forward and the droid can't backtrack, then there is no possible path. */
				System.out.println("No path found!");
				pathFound = false; /** Set pathFound to break out of the while loop. */
			}
			
			/** If the end is found, do these actions: */
			if(maze.scanCurLoc(this) == Content.END) {
				this.setStatus(Status.FINISHED); /** Set the status to finished */
				this.panel.updateStatus(); /** Update the status in the UI */
				
				this.hasBeenTraveled = true; /** Set the maze as having been traveled through */
				this.scannedCells.add(this.maze.getCurrentCoordinates(this)); /** Add the end cell to the scanned cells */
				this.visitedCells.add(this.maze.getCurrentCoordinates(this)); /** Add the end cell to the visited cells */
			}
			
			/** Update the display in the UI */
		   try {
	            Thread.sleep(200); /** Slow it down slow enough so that the user can see the droid explore */
	            
	            panel.getGridPanel().updateGrid(maze.getCurrentCoordinates(this).getZ()); /** Display the current level */
	            panel.updateMove(); /** Update the path length and current location */
	            
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	/** 
	 * Converts the stack/ droid path to an array so it can be iterated over.
	 * @return the path as an array
	 */
	public ArrayList<Coordinates> getPath() {
		return path.toArray();
	}
	
	/**
	 * Checks if the cell adjacent to the droid in a specific direction has been visited.
	 * @param directionIndex an index corresponding to a direction (0, 90, 180, or 270 degrees) out of the droid's cell.
	 * @return a boolean, true if the cell has been visited, false if it has not.
	 */
	private Boolean isVisited(int directionIndex) { 
		
		Boolean isVisited = false; /** Assume that the cell is not visited until proven so. */
		Direction direction = calcDirection(directionIndex); /** Convert the direction index into a direction. */
		Direction reverseDirection = calcReverseDirection(directionIndex); /** Find the reverse direction. */
		
		Coordinates cell = maze.move(this, direction); /** Move the droid into the cell to be checked to get its coordinates. */
		maze.move(this, reverseDirection); /** We have the coordinates, so move the droid back to the original location. */
		
		/** Iterate through the visited cells to see if any of them match the coordinates of the cell we are trying to check. */
		for (Coordinates c : this.visitedCells) {
			isVisited = coordinatesAreEqual(c, cell); /** Check if the coordinates of the cell match the current visited cell. */
			if(isVisited) {
				break; /** Break out of the loop if we find a single visited cell. */
			}
		}
		
		return isVisited;
	}
	
	/**
	 * Add the surrounding cells to the scanned cells
	 */
	private void scanCells() {
		Coordinates currentPosition = this.maze.getCurrentCoordinates(this); /** The droid's current position */
		
		int x = currentPosition.getX(); /** x-coordinate */
		int y = currentPosition.getY(); /** y-coordinate */
		int z = currentPosition.getZ(); /** z-coordinate */

		/** Add each of the four surrounding cells only if they are valid cells to display,
		 * since that is where they will be used.
		 */
		Coordinates cell1 = new Coordinates(x+1, y, z);
		if(this.maze.getContentForCoordinates(cell1) != null && this.maze.getContentForCoordinates(cell1) != Content.NA) {
			scannedCells.add(cell1);
		}

		Coordinates cell2 = new Coordinates(x-1, y, z);
		if(this.maze.getContentForCoordinates(cell2) != null && this.maze.getContentForCoordinates(cell2) != Content.NA) {
			scannedCells.add(cell2);
		}

		Coordinates cell3 = new Coordinates(x, y+1, z);
		if(this.maze.getContentForCoordinates(cell3) != null && this.maze.getContentForCoordinates(cell3) != Content.NA) {
			scannedCells.add(cell3);
		}

		Coordinates cell4 = new Coordinates(x, y-1, z);	
		if(this.maze.getContentForCoordinates(cell4) != null && this.maze.getContentForCoordinates(cell4) != Content.NA) {
			scannedCells.add(cell4);
		}
	}
	
	/**
	 * Calculate the direction from the direction index. 
	 * @param directionIndex - the index of an array corresponding to a direction
	 * @return the Direction corresponding to the direction index
	 */
	private Direction calcDirection(int directionIndex) {
		Direction direction = Direction.D00; /** Assume that the direction is 0, corresponding to Content[0] */
		
		/** If it is not 0, find the corresponding direction */
		if (directionIndex == 1) direction = Direction.D90;
		else if (directionIndex == 2) direction = Direction.D180;
		else if (directionIndex == 3) direction = Direction.D270;
		
		return direction;
	}
	
	/**
	 * Calculate the reverse direction from the direction index. 
	 * @param directionIndex - the index of an array corresponding to a direction
	 * @return the Direction corresponding to the reverse of the direction index
	 */
	private Direction calcReverseDirection(int directionIndex) {
		Direction reverseDirection = Direction.D180; /** Assume that the reverse direction is opposite of index 0 */
		
		/** If it is not 0, find the reverse direction from the index */
		if (directionIndex == 1) reverseDirection = Direction.D270;
		else if (directionIndex == 2) reverseDirection = Direction.D00;
		else if (directionIndex == 3) reverseDirection = Direction.D90;
		
		return reverseDirection;
	}
	
	/**
	 * Calculate the direction reverse to the given direction.  
	 * @param directionIndex - the index of an array corresponding to a direction
	 * @return the Direction corresponding to the reverse of the given direction
	 */
	private Direction calcReverseDirection(Direction direction) {
		Direction reverseDirection = Direction.D180;
		
		/** If the direction is not 0, find the reverse direction. */
		if (direction == Direction.D90) reverseDirection = Direction.D270;
		else if (direction == Direction.D180) reverseDirection = Direction.D00;
		else if (direction == Direction.D270) reverseDirection = Direction.D90;
		
		return reverseDirection;
	}
	
	/**
	 * Moves the droid in the direction of the index specified.
	 * @param directionIndex - the index of the direction to travel in.
	 */
	private void move(int directionIndex) {
		Direction direction = calcDirection(directionIndex); /** Find the direction to travel in given the direction index. */
	
		Coordinates newPosition = maze.move(this, direction); /** Travel in the direction found. */
		path.push(newPosition); /** Add the new position to the stack/ path. */
		this.visitedCells.add(newPosition); /** Mark the current cell as visited. */
	}
	
	/**
	 * Causes the droid to backtrack and move to the second last location on the path.
	 */
	private void backtrack() {
		
		Coordinates goalLocation = path.peekSecondLast(); /** Since the first location is the current location, the goal location
		 													is the second-last location on the stack. */
		boolean canBacktrack = false; /** The droid can't backtrack until it finds the direction of the cell to backtrack to. */
		int directionIndex = -1; /** This is the index corresponding to the direction of the goal location. */
		Content[] surroundingCellContents = maze.scanAdjLoc(this); /** An array of the contents of the surrounding cells. */
		Content content = null; /** Holds each value as the program iterates through the surrounding cell contents. */
		
		/** Check the surrounding cells to see which one is the correct one to backtrack to. */
		for(int i = 0; i < surroundingCellContents.length; i++) { /** Iterate through the surrounding cells. */
			content = surroundingCellContents[i]; /** Check the content of each cell. */

			/** Check if the surrounding cell can be moved into, and if it can, check if it matches the goal location.
			 * Check if the cell is enterable first because the move function called in moveEqualsCoordinate will give
			 * erroneous results if it is not. */
			if(content != Content.BLOCK  && content != Content.NA && moveEqualsCoordinate(i, goalLocation)) {
				canBacktrack = true; /** If the cell checked can be moved into and if it equals the goal location, */
				directionIndex = i; /** the direction index to travel in is kept, */
				break; /** and since the target is found, there is no need to keep checking the other cells, so break. */
			} 
		}
		
		/** If the droid can backtrack, then move in the correct direction and manage the stack. */
		if(canBacktrack) {
			Direction direction = calcDirection(directionIndex); /** Calculate the direction from the direction index. */
			maze.move(this, direction); /** Move the droid back in the calculated direction. */
			path.pop(); /** Pop the previous location off of the stack. */
		} 
	}
	
	/**
	 * Check if a move in the direction of the specified direction index will result in the droid in the position of 
	 * the specified coordinate. 
	 * @param directionIndex - the index of the direction to move to
	 * @param coordinate - the coordinate to check if the coordinate in the specified direction equals
	 * @return a boolean, true if the cell hypothetically moved to and the coordinate are the same, false otherwise
	 */
	private Boolean moveEqualsCoordinate(int directionIndex, Coordinates coordinate) {

		Boolean moveEqualsCoordinate = false; /** Assume that the move does not equal the coordinate until proven so. */
		Direction direction = calcDirection(directionIndex); /** Calculate the direction to hypothetically move in from the direction index. */
		Direction reverseDirection = calcReverseDirection(directionIndex); /** Calculate the reverse direction from the direction index. */

		Coordinates cell = maze.move(this, direction); /** Moves the droid in the specified direction to find the coordinates */
		maze.move(this, reverseDirection); /** The coordinates are found, so move the droid back */
		moveEqualsCoordinate = coordinatesAreEqual(coordinate, cell); /** The move equals the coordinate if the found coordinate matches the coordinate given */

		return moveEqualsCoordinate;
	}

	/**
	 * Checks if the two given coordinates are equal (that is, have equal x, y, and z attributes).
	 * @param a - the first coordinate
	 * @param b - the second coordinate to compare with the first
	 * @return a boolean, true if they are equal, false if they are not
	 */
	public Boolean coordinatesAreEqual(Coordinates a, Coordinates b) {
		return a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
	}
	
	/**
	 * Print out the maze from the droid's perspective. 
	 */
	public void printDroidMaze() {

		ArrayList<Coordinates> path = this.getPath(); /** This is an array list of the coordinates on the droid's path. */
		
		System.out.println("Maze from " + this.getName() + "'s Persepective - " + maze.toString() + "\n");
		
		/** For each level of the maze, print the level. */
		for(int z = 0; z < maze.getMazeDepth(); z++) {
			System.out.println("Level - " + z);
			String[] mazeArray = maze.toStringLevel(z); /** The mazeArray is an array representing a single level of the maze. */
	
			/** For each row in the level, print the row. */
			for(int y = 0; y < maze.getMazeDim(); y++) { 
				
				/** Convert the row into an array without brackets so the contents of the array can be edited. */
				String contents = mazeArray[y].replaceAll("\\[",""); /** Take out all of the '[' */
				contents = contents.replaceAll("\\]", ""); /** Take out all of the ']' */
				String[] lineArray = contents.split(""); /** Create an array with each cell's content marker as an element */
				
				/** Print each cell in the row */
				for(int x = 0; x < lineArray.length; x++) { 
					String currentCell = lineArray[x]; /** The content of the current cell we are trying to edit and print */
					Coordinates currentCoordinate = new Coordinates(x, y, z); /** The current coordinates of the cell */
			
					/** Check if the current cell is on the droid's path */
					for(Coordinates l : path) { 
						/** If the current cell matches a cell on the droid's path and is not a portal or the end, change its contents to 'X' */
						if(this.coordinatesAreEqual(currentCoordinate, l) && !currentCell.equals("E") && !currentCell.equals("P")) {
							currentCell = "X";
							break; /** If the cell has matched a cell on the droid's path, a match is found and don't keep checking the cells. */
						}
					}
					
					/** If the droid is currently in the current cell, change its contents to 'D' */
					if(this.coordinatesAreEqual(maze.getCurrentCoordinates(this), currentCoordinate) && !currentCell.equals("E") && !currentCell.equals("P")) 
						currentCell = "D";
					
					System.out.print("[" + currentCell + "]"); /** Print out the current cell */
				}
				System.out.println(); /** Add a newline to start the next line on the next line */
			}
			System.out.println(); /** Add a newline so there will be a space between levels of the array */
		}
	}
}
