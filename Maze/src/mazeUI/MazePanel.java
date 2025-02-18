package mazeUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import mazePD.Droid;
import mazePD.Droid.Status;
import mazePD.Maze;
import mazePD.Maze.MazeMode;

import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class MazePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private GridPanel panel; /** Displays the maze */
	private JTextField textField; /** Dimensions text field */
	private JTextField textField_1; /** Levels text field */
	private JTextField textField_2; /** Location text field */
	private JTextField textField_3; /** Path length text field */
	
	private JLabel lblNewLabel_1; /** Droid name and status label */
	private JLabel lblNewLabel_3_2; /** Level label */
	
	private Droid droid; /** The cute little robot running around in the maze */
	
	private JButton btnNewButton_1; /** Run maze button */
	
	private DefaultComboBoxModel<Integer> comboBoxModel; /** Holds the level integers */
	private JComboBox comboBox; /** For the levels */
	
	/**
	 * Create the panel.
	 */
	public MazePanel(JFrame currentFrame, Droid droid) {
		MazePanel mazePanel = this; /** This will be passed into other things later. */
		this.droid = droid; /** This allows droid to be used in other functions without passing. */
		
		/** Set background and layout */
		setBackground(new Color(215, 236, 236));
		setLayout(null);
		
		/** Run maze title label */
		JLabel lblNewLabel = new JLabel("Run maze");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(265, 5, 70, 17);
		add(lblNewLabel);
		
		/** The panel that displays the maze */
		panel = new GridPanel(currentFrame, droid);
		panel.setBounds(200, 40, 320, 320);
		panel.setBackground(getBackground());
		add(panel);
		
		/** The droid name and status label */
		lblNewLabel_1 = new JLabel(droid.getName() + ": " + droid.getStatus().toString());
		lblNewLabel_1.setBounds(10, 8, 200, 14);
		add(lblNewLabel_1);
		
		/** Dimensions label */
		JLabel lblNewLabel_2 = new JLabel("Dimensions:");
		lblNewLabel_2.setBounds(10, 40, 91, 14);
		add(lblNewLabel_2);
		
		/** Levels label */
		JLabel lblNewLabel_3 = new JLabel("Levels:");
		lblNewLabel_3.setBounds(10, 65, 91, 14);
		add(lblNewLabel_3);
		
		/** Dimensions text field */
		textField = new JTextField("10");
		textField.setBounds(111, 37, 49, 20);
		add(textField);
		textField.setColumns(10);
		
		/** Levels text field */
		textField_1 = new JTextField("3");
		textField_1.setColumns(10);
		textField_1.setBounds(111, 62, 49, 20);
		add(textField_1);
		
		/** Create maze button */
		JButton btnNewButton = new JButton("Create maze!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** The droid enters the maze */
				droid.enterMaze(new Maze(new Integer(textField.getText()), new Integer(textField_1.getText()), MazeMode.NORMAL));
				
				/** The droid can start navigating the maze */
				btnNewButton_1.setEnabled(true);

				/** Cause the squares of the grid the appear */
				panel.initializeGrid(droid.getMaze().getMazeDim());
				
				/** Add the panel to the droid so it can update it */
				droid.setPanel(mazePanel);
				
				/** Update the droid's status, location, and path on the screen */
				updateStatus();
				updateMove();
				
				/** Cause the combo box to allow selection of as many levels that the maze is */
				comboBoxModel.removeAllElements(); /** First remove what was there previously so there is not a mess */
				for(int i = 0; i < droid.getMaze().getMazeDepth(); i++) {
					comboBoxModel.addElement(i+1); /** Number them in a non-computer-human literate way, starting from 1 */
				}
			}
		});
		btnNewButton.setBounds(25, 100, 114, 48);
		add(btnNewButton);
		
		/** Location label */
		JLabel lblNewLabel_2_1 = new JLabel("Location:");
		lblNewLabel_2_1.setBounds(10, 170, 91, 14);
		add(lblNewLabel_2_1);
		
		/** Location text field */
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(111, 167, 49, 20);
		textField_2.setHorizontalAlignment(JTextField.CENTER);
		add(textField_2);
		
		/** Path length label */
		JLabel lblNewLabel_3_1 = new JLabel("Path Length:");
		lblNewLabel_3_1.setBounds(10, 195, 91, 14);
		add(lblNewLabel_3_1);
		
		/** Path length text field */
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(111, 192, 49, 20);
		textField_3.setHorizontalAlignment(JTextField.CENTER);
		add(textField_3);
		
		/** Level label */
		lblNewLabel_3_2 = new JLabel("Level:");
		lblNewLabel_3_2.setBounds(10, 220, 91, 14);
		add(lblNewLabel_3_2);
		
		/** Create a level combo box of integers */
		comboBoxModel = new DefaultComboBoxModel<Integer>();
		comboBox = new JComboBox(comboBoxModel);
		comboBox.setBounds(111, 216, 49, 22);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** When the combo box value changes, update the grid to the selected level */
				if(comboBox.getSelectedItem() != null)
					panel.updateGrid((Integer)comboBox.getSelectedItem()-1);
			}
		});
		add(comboBox);
		
		/** Run maze button */
		btnNewButton_1 = new JButton("Run maze!");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** Don't allow the user to change the level while the droid is exploring the maze.
				 * This could be problematic because the droid causes the level to change while it is
				 * running.
				 */
				comboBox.setEnabled(false); 
				
				/** The droid navigates the maze in a new thread so it will be simultaneous to updating 
				 * the UI
				 */
				new Thread(new Runnable() {
					public void run() {
						droid.navigateMaze();
					}
				}).start();
			}
		});
		btnNewButton_1.setBounds(25, 252, 114, 48);
		btnNewButton_1.setEnabled(false);
		add(btnNewButton_1);
	}
	
	/**
	 * Get the maze display.
	 * @return the grid panel that displays the maze
	 */
	public GridPanel getGridPanel() {
		return this.panel;
	}
	
	/**
	 * Updates the droid's status on the screen
	 */
	public void updateStatus() {
		 SwingUtilities.invokeLater(() -> { /** It is called among the droid's functions */
			 lblNewLabel_1.setText(droid.getName() + ": " + droid.getStatus().toString());
			 if(droid.getStatus() == Status.FINISHED) {
				 comboBox.setEnabled(true); /** Allow the user to see other levels if the droid is finished navigating */
			 }
	         this.repaint();
	     });
	}
	
	/**
	 * Update the level on the screen. Called when the droid starts a new level. 
	 */
	public void updateLevel() {
		SwingUtilities.invokeLater(() -> {
			comboBox.setSelectedItem(droid.getMaze().getCurrentCoordinates(droid).getZ()+1);
		});
	}
	
	/** 
	 * When the droid moves, update the current coordinates and the path length.
	 */
	public void updateMove() {
		SwingUtilities.invokeLater(() -> {
			textField_2.setText(droid.getMaze().getCurrentCoordinates(droid).toString());
			textField_3.setText(((Integer)droid.getPath().size()).toString());
		});
	}	
}
