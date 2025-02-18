package mazeUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import mazePD.Droid;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreateDroidPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField; /** Droid name text field */

	/**
	 * Create the panel.
	 */
	public CreateDroidPanel(JFrame currentFrame) {
		setBackground(new Color(215, 236, 236));
		setLayout(null);
		
		/** Title of the screen */
		JLabel lblNewLabel = new JLabel("Create droid");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(239, 11, 121, 14);
		add(lblNewLabel);
		
		/** Droid name label */
		JLabel lblNewLabel_1 = new JLabel("Droid name: ");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(125, 135, 77, 14);
		add(lblNewLabel_1);
		
		/** Droid name text field */
		textField = new JTextField();
		textField.setBounds(216, 132, 161, 20);
		add(textField);
		textField.setColumns(10);
		
		/** Create droid button */
		JButton btnNewButton = new JButton("Create!");
		btnNewButton.setBackground(new Color(239, 239, 239));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Droid droid = new Droid(textField.getText()); /** Create the droid */
				
				/** Change to the maze panel and pass it the newly created droid and the current frame */
				currentFrame.getContentPane().removeAll();
				currentFrame.getContentPane().add(new MazePanel(currentFrame, droid));
				currentFrame.getContentPane().revalidate();
			}
		});
		btnNewButton.setBounds(255, 178, 89, 23);
		add(btnNewButton);
	}
}
