import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A class for buttons that represent cards
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 24 
 */
public class CardButton extends JButton  {
	
	public static final String IMAGE_PATH = "images/";
	public static final String IMAGE_TYPE = ".jpg";
	
	private int position;
	
	/**
	 * Initializes a new Card Button with a specified position.
	 * @param p The position from 0 to NUM_CARDS
	 */
	public CardButton(int p) {
		position = p;
		setPreferredSize(new Dimension(150, 150));
	}
	
	/**
	 * Display the image with the specified id.
	 * @param id The image id
	 */
	public void showImage(int id) {
		setIcon(new ImageIcon(IMAGE_PATH + id + IMAGE_TYPE));
	}
	
	/**
	 * Hide the image on the button.
	 */
	public void hideImage() {
		setIcon(null);
	}
	
	/**
	 * Returns the position of the button.
	 * @return The position
	 */
	public int getPosition() {
		return position;
	}
}
