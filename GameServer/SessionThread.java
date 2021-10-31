import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A class to manage a session and handle
 * logic and communication between its players.
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 30
 */
public class SessionThread 
	extends Thread implements GameConstants {
	
	private ArrayList<Player> players;
	private ArrayList<Integer> cards;
	private int totalScore;
	
	/**
	 * Initializes a new session with the designated
	 * sockets that correspond to individual players.
	 * @param playerSockets The sockets of the players
	 */
	public SessionThread(ArrayList<Socket> playerSockets) {
		players = new ArrayList<Player>();
		cards = new ArrayList<Integer>();
		totalScore = 0;
		initializeCardIds();
		
		for(int i = 0; i < playerSockets.size(); i++) {
			try {
				players.add(new Player(i + 1, playerSockets.get(i).getInputStream(),
					playerSockets.get(i).getOutputStream()));
			} catch(IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}
	
	/**
	 * Starts the session and effectively begins the game. The
	 * player with the lowest ID will receive the first turn.
	 */
	public void run() {
		initializePlayerIds();
		while(totalScore < NUM_CARDS / 2) {
			for(int i = 0; totalScore < NUM_CARDS / 2 && i < NUM_PLAYERS; i++) {
				// Rotate between all the players in ascending order
				// and notify each client at the beginning of the 
				// turn that this player is currently playing.
				Player currentPlayer = players.get(i);
				writeAll(CMD_PLAYING, i + 1);
				
				int commandOne = currentPlayer.nextCommand();
				if(commandOne == CMD_TRY) {
					int firstChoice = currentPlayer.nextCommand();
					writeAll(CMD_SHOW, firstChoice, cards.get(firstChoice));
					
					int commandTwo = currentPlayer.nextCommand();
					
					if(commandTwo == CMD_EXIT) {
						// The player has exited before selecting 
						// a second card so exit the session and
						// select a winner that is not them.
						endSession(currentPlayer);
						return;
					}
					
					int secondChoice = currentPlayer.nextCommand();
					boolean isMatch = (cards.get(firstChoice) == cards.get(secondChoice));
					writeAll(CMD_SHOW, secondChoice, cards.get(secondChoice));
					writeAll(CMD_WAIT);

					if(isMatch) {
						writeAll(CMD_SCORE, i + 1);
						currentPlayer.incrementScore();
						totalScore++;
					} else {
						writeAll(CMD_WRONG);
					}
					
					try {
	                    Thread.sleep(REVEAL_TIME);
	                } catch (InterruptedException e) {
	                    e.printStackTrace(System.err);
	                }
					
					if(!isMatch) {
						// If the selections are not a match then hide the cards
						// again. If they were a pair then this block of code 
						// will not be executed and they will remain revealed
						// for the remainder of the session.
						writeAll(CMD_HIDE, firstChoice);
						writeAll(CMD_HIDE, secondChoice);
					}
				} else {
					// Something has went wrong - either the player has sent a
					// CMD_EXIT or a -1 which signals that a socket has failed
					// so end the session and select a winner that is not them.
					endSession(currentPlayer);
					return;
				}
			}
		}
		// Everything has went according to plan so end
		// the session normally and select a winner.
		endSession(null);
	}
	
	/**
	 * Ends the session with a specified exiting player.
	 * The player with the highest score AND lowest ID that 
	 * is not the exiting player will be designated the winner
	 * An exiting player of NULL which corresponds to a successful
	 * session will select the winner normally.
	 * @param exitingPlayer The exiting player if one exists
	 */
	public void endSession(Player exitingPlayer) {
		int highestScore = -1;
		int winnerPlayerId = -1;
		
		for(Player p : players){
			if(p != exitingPlayer && p.getScore() > highestScore) {
				winnerPlayerId = p.getPlayerId();
				highestScore = p.getScore();
			}
		}

		for(Player p : players) {
			if(p != exitingPlayer) {
				p.write(CMD_WON, winnerPlayerId);
				p.write(CMD_EXIT);
			}
		}
	}
	
	/**
	 * Writes the arguments to all players in the session.
	 * @param args The messages to be written
	 */
	public void writeAll(int ... args) {
		for(Player p : players) {
			for(int i : args) {
				p.write(i);
			}
		}
	}
	
	/**
	 * Initializes the cards to 8 randomized pairs
	 * out of a possible 16 combinations. 
	 */
	public void initializeCardIds() {
		// Randomly select eight unique numbers and 
		// insert them twice followed by a shuffle
		// to generate the card pairs for this session.
		int randomNumber = 0;
		Random random = new Random();
		while(cards.size() < NUM_CARDS) {
			do {
				randomNumber = random.nextInt(NUM_CARDS);
			} while(cards.contains(randomNumber));
			
			cards.add(randomNumber); 
			cards.add(randomNumber); 
		}
		Collections.shuffle(cards);
	}
	
	/**
	 * Notifies the players of their Player ID which is
	 * designated based on the order in which they joined.
	 * For example, the first player that has joined will
	 * receive an ID of 1, and so on.
	 */
	public void initializePlayerIds() {
		for(int i = 0; i < players.size(); i++) {
			players.get(i).write(CMD_PLAYER, i + 1);
		}
	}
}
