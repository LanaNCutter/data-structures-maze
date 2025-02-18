package mazeUI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mazePD.Coordinates;
import mazePD.Droid;
import mazePD.Maze.Content;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;

public class GridPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	Droid droid; /** The thingy trapped in the maze */

	/**
	 * Create the panel.
	 */
	public GridPanel(JFrame currentFrame, Droid droid) {
		setLayout(new GridLayout(1, 0, 0, 0));
		this.droid = droid;
	}
	
	/** 
	 * When the maze is first created, initialize the maze display with boring gray unexplored cells.
	 * @param dimensions the dimensions of the maze regulating how many squares there are
	 */
	public void initializeGrid(int dimensions) {
		this.removeAll(); /** Remove all previous squares in case it is already displaying something */
		this.setLayout(new GridLayout(dimensions, dimensions, 1, 1)); /** Have dimensions * dimensions boxes with 1 pixel of space in between */
		
		/** Initialize the rows */
		for(int y = 0; y < dimensions; y++) {
			
			/** Initialize the columns */
			for(int x = 0; x < dimensions; x++) {

				/** Create an image icon of an unexplored cell and adhere it to a label */
		        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/resources/unexplored_cell.jpg"));
		        JLabel imageLabel = new JLabel(imageIcon);
		        add(imageLabel);
			}
		}
		
		/** When all of the squares are on the panel, display them */
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Displays a level on the grid 
	 * @param level - the level to display
	 */
    public void updateGrid(int level) {
        SwingUtilities.invokeLater(() -> {
        	this.removeAll(); /** First remove the level that was previously displayed */
        	
    		ArrayList<Coordinates> path = droid.getPath(); /** This is an array list of the coordinates on the droid's path. */
    		HashSet<Coordinates> scannedCells = droid.getScannedCells(); /** This is an iterable of the coordinates that the droid has scanned. */
    		String[] mazeArray = droid.getMaze().toStringLevel(level); /** The mazeArray is an array representing a single level of the maze. */
    	
			/** For each row in the level, display the row. */
			for(int y = 0; y < droid.getMaze().getMazeDim(); y++) { 
				
				/** Convert the row into an array without brackets so the contents of the array can be analyzed. */
				String contents = mazeArray[y].replaceAll("\\[",""); /** Take out all of the '[' */
				contents = contents.replaceAll("\\]", ""); /** Take out all of the ']' */
				String[] lineArray = contents.split(""); /** Create an array with each cell's content marker as an element */
				
				/** Display each cell in the row */
				for(int x = 0; x < lineArray.length; x++) { 
					String imageFilePath = "/resources/bad_cell.jpg"; /** The cell will be a guilty red if it didn't match any of the following if statements */
				
					Coordinates currentCoordinate = new Coordinates(x, y, level); /** The current coordinates of the cell */
					Content currentContent = droid.getMaze().getContentForCoordinates(currentCoordinate); /** The current content of the current cell */
					
					boolean isScanned = false; /** Assume that the cell is not scanned */
					
					/** Check if the cell is scanned. */
					for(Coordinates c : scannedCells) { 
										
						/** If the current cell is scanned, it is not a boring gray. */
						if(droid.coordinatesAreEqual(currentCoordinate, c)) {
							
							boolean isOnPath = false; /** Assume that the cell is not on the droid's path. */
							
							/** Check if the cell is on the path. */
							for(Coordinates l : path) { 
								/** If the cell is on the path, make it have an 'O' cell. */
								if(droid.coordinatesAreEqual(currentCoordinate, l)) {
									
									/** Check if the cell is empty, portal, beginning, or end. */
									/** Empty cells have white blocks. */
									if(currentContent == Content.EMPTY) { 
										imageFilePath = "/resources/empty_path_cell.jpg";
									} 
									/** If the cell is a portal, it has an orangey block, unless it is the beginning, in which case it is purple. */
									else if (currentContent == Content.PORTAL_DN || currentContent == Content.PORTAL_UP) {
										if (droid.coordinatesAreEqual(currentCoordinate, droid.getMaze().getMazeStartCoord())) {
											imageFilePath = "/resources/beginning_path_cell.jpg";
										} else {
											imageFilePath = "/resources/portal_path_cell.jpg";
										} 
									} 
									/** If the cell is the end, it is green. */
									else if (currentContent == Content.END) {
										imageFilePath = "/resources/end_path_cell.jpg";
									}
									
									isOnPath = true; /** The cell is found to be on the path, so mark it as such so later codes don't override it. */
									break; /** If the cell has matched a cell on the droid's path, a match is found and don't keep checking the cells. */
								}
							}
							
							/** If the cell is not on the path, give it a non-O cell. */
							if(!isOnPath) {
								
								/** Check if the cell is empty, portal, end, or block. */
								/** Empty cells are white */
								if(currentContent == Content.EMPTY) {
									imageFilePath = "/resources/empty_cell.jpg";
								} 
								/** Portals are orangey, except the beginning cell, which is purple */
								else if (currentContent == Content.PORTAL_DN || currentContent == Content.PORTAL_UP) {
									if (droid.coordinatesAreEqual(currentCoordinate, droid.getMaze().getMazeStartCoord())) {
										imageFilePath = "/resources/beginning_cell.jpg";
									} else {
										imageFilePath = "/resources/portal_cell.jpg";
									}
								} 
								/** End cells are green */
								else if (currentContent == Content.END) {
									imageFilePath = "/resources/end_cell.jpg";
								} 
								/** Everything else is a block, which is black. */
								else {
									imageFilePath = "/resources/block_cell.jpg";
								} 
							}
							
							isScanned = true; /** The cell is marked as scanned so it is not overriden later */
							break; /** If the cell has matched a scanned cell, a match is found and don't keep checking the cells. */
						} 
					}
					
					/** If the cell is not scanned, it is still that boring gray block. */
					if(!isScanned) {
						imageFilePath = "/resources/unexplored_cell.jpg";
					}
					
					/** Create a label with the image of whatever the cell was chosen to have in the
					 * previous if statements.
					 */
					ImageIcon imageIcon = new ImageIcon(getClass().getResource(imageFilePath));
			        JLabel imageLabel = new JLabel(imageIcon);
			        add(imageLabel);
				}
			}

			/** When all the cells are once more added, redisplay the screen. */
			this.revalidate();
            this.repaint();
        });
    }
}
