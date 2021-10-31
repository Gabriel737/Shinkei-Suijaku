import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A class for the client GUI.
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 30
 * @see ServerFrame
 */
public class ClientFrame 
	extends JFrame implements Runnable, GameConstants {
	
	public static final int WINDOW_LENGTH = 580;
	public static final int CARDS_PER_ROW = 4;

	private JPanel cardPanel;
	private JPanel messagePanel;
	private JLabel statusLabel;
	private JLabel scoreLabel;
	private JCheckBox soundCheckBox;
	private JButton statusButton;
	private ArrayList<CardButton> cards;
	
	private int score;
	private int state;
	private int playerId;
	private boolean isMyTurn;
	private String IPAddress;
	
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private SoundEngine sound;
	
	/** 
	 * Initializes a new client frame with blank cards.
	 * @param ip The IP address of the server
	 */
	public ClientFrame(String ip) {
		statusLabel = new JLabel();
		scoreLabel = new JLabel();
		soundCheckBox = new JCheckBox("Sound");
		soundCheckBox.setSelected(true);
		statusButton = new JButton("Exit Game");
		statusButton.addActionListener(new ExitListener());
		sound = SoundEngine.getInstance();
		IPAddress = ip;
		
		messagePanel = new JPanel();
		messagePanel.setLayout(new GridLayout(0, CARDS_PER_ROW));
		messagePanel.add(statusLabel);
		messagePanel.add(scoreLabel);
		messagePanel.add(soundCheckBox);
		messagePanel.add(statusButton);
		
		initializeFrame();
		initializeCards();
		
		add(cardPanel, BorderLayout.CENTER);
		add(messagePanel, BorderLayout.NORTH);
		
		resetGame();
		setVisible(true);
		(new Thread(this)).run();
	}
	
	/**
	 * Starts the main thread for the client that handles
	 * the communication between us and the server.
	 */
	public void run() {
		while(true) {
			try {
				int command = input.readInt();
				if(command == CMD_PLAYING) {
					isMyTurn = (input.readInt() == playerId);
					state = STATE_OKAY;
				} else if(command == CMD_SCORE) {
					if(input.readInt() == playerId) {
						score++;
					} 
					if(soundCheckBox.isSelected()) {
						sound.playDing();
					}
				}  else if(command == CMD_WRONG) {
					if(soundCheckBox.isSelected()) {
						sound.playBuzz();
					}
				}  else if(command == CMD_WON) {
					if(input.readInt() == playerId) {
						if(soundCheckBox.isSelected()) {
							sound.playClap();
						}
						JOptionPane.showMessageDialog(null, "Congratulations, you have won!");
					} else {
						if(soundCheckBox.isSelected()) {
							sound.playBoo();
						}
						JOptionPane.showMessageDialog(null, "Sorry, you lost!");
					}
				} else if(command == CMD_SHOW) {
					showCard(input.readInt(), input.readInt());
				} else if(command == CMD_HIDE) {
					hideCard(input.readInt()); 
				} else if(command == CMD_WAIT) {
					state = STATE_WAITING;
					isMyTurn = false;
				} else if(command == CMD_PLAYER) {
					playerId = input.readInt();
					setTitle("The Memory Game ---> Player " + playerId);
				} else if(command == CMD_EXIT) {
					// If the player would like to start a new game then reset the score,
					// reconnect the sockets and continue the main loop.
					if(JOptionPane.showConfirmDialog(null, "Would you like to play again?") == JOptionPane.YES_OPTION) {
						resetGame();
						continue;
					} else {
						socket.close();
						System.exit(0);
					}
				} 
			} catch(IOException e) {
				JOptionPane.showMessageDialog(null, "An error has occured - The program will now exit\n" + e.getMessage());
				System.exit(1);
			}
			statusLabel.setText(getStatus());
			scoreLabel.setText("Score: " + score);
			statusButton.setEnabled(isMyTurn);
			toggleCards();
			repaint();
		}
	}
	
	/**
	 * Enables the non revealed cards for pressing if it
	 * is the users turn and disables them otherwise.
	 */
	public void toggleCards() {
		for(CardButton c : cards) {
			if(isMyTurn) {
				c.setEnabled(true);
			} else {
				c.setEnabled(c.getIcon() != null);
			}
		}
	}
	
	/**
	 * Reveals the image and enables the button.
	 * @param position The card position
	 * @param imageId The image id
	 */
	public void showCard(int position, int imageId) {
		cards.get(position).setEnabled(true);
		cards.get(position).showImage(imageId);
	}
	
	/**
	 * Hides the image and disables the button if it isn't our turn.
	 * @param position The card position
	 */
	public void hideCard(int position) {
		if(!isMyTurn) {
			cards.get(position).setEnabled(false);
		}
		cards.get(position).hideImage();
	}
	
	/**
	 * Resets the frame and scores back to its default
	 * settings and makes a connection to the server.
	 */
	public void resetGame() {
		setTitle("The Memory Game");
		statusButton.setEnabled(true);
		scoreLabel.setText("Score: 0");
		statusLabel.setText("Waiting for players");
		state = STATE_NOT_CONNECTED;
		isMyTurn = false;
		playerId = -1;
		score = 0;
		
		for(CardButton c : cards) {
			c.setEnabled(false);
			c.setIcon(null);
		}
		
		try {
			socket = new Socket(IPAddress, PORT_NUMBER);
			input = new DataInputStream(socket.getInputStream());
	        output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Unable to connect to server\n" + e.getMessage());
	        System.exit(1);
        }
	}
	
	/**
	 * Initializes the card buttons and adds them to the frame.
	 */
	public void initializeCards() {
		cards = new ArrayList<CardButton>();
		cardPanel = new JPanel();
		cardPanel.setLayout(new GridLayout(CARDS_PER_ROW, CARDS_PER_ROW));
		cardPanel.setVisible(true);
		
		for(int i = 0; i < NUM_CARDS; i++) {
			CardButton card = new CardButton(i);
			card.addActionListener(new CardListener());
			cardPanel.add(card);
			cards.add(card);
		}
	}
	
	/**
	 * Initializes the frame properties
	 */
	public void initializeFrame() {
		setLocationRelativeTo(null);
		setSize(WINDOW_LENGTH, WINDOW_LENGTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());
		setResizable(false);
	}
	
	/**
	 * Returns the "status" of the game that is dependent
	 * on state and whose turn it currently is.
	 * @return A description of the status
	 */
	public String getStatus() {
		if(state == STATE_OKAY) {
			if(isMyTurn) {
				return "Your Turn";
			} else {
				return "Their Turn";
			}
		} else if(state == STATE_WAITING) {
			return "Waiting";
		} else if(state == STATE_WON) {
			return "You Won!";
		} else if(state == STATE_LOST) {
			return "You Lost!";
		} else {
			return "Waiting for connection!";
		}
	}
	
	/**
	 * A listener class for the card buttons that sends the 
	 * appropriate message to the server upon being clicked
	 * if and only if the button is not locked.
	 * Assignment 10 - CPSC 1181 Section 1
	 * @author Gabriel Henderson
	 * @version March 25
	 */
	private class CardListener implements ActionListener {
		/**
		 * Sends a try command to the server if is our turn
		 * @param e The Mouse Event
		 */
		public void actionPerformed(ActionEvent e) {
			CardButton c = (CardButton) e.getSource();
			if(c.getIcon() == null) {
				try {
					output.writeInt(CMD_TRY);
                    output.writeInt(c.getPosition());
                    output.flush();
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
			}
		}
	}

	/**
	 * A listener class for the exit button on the client frame
	 * that notifies the server and closes the socket.
	 * Assignment 10 - CPSC 1181 Section 1
	 * @author Gabriel Henderson
	 * @version March 30
	 */
	private class ExitListener implements ActionListener {
		/**
		 * Notifies the server and exits when the button is pressed.
		 * @param e The ActionEvent
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				output.writeInt(CMD_EXIT);
                output.flush();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            } finally {
				try {
	                socket.close();
                } catch (IOException ex) {
	                ex.printStackTrace(System.err);
                }
			}
			System.exit(0);
        }
	}
}