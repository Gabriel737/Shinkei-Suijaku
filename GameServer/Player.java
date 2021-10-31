
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class to represent a player and handle communication.
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 30
 */
public class Player {
	
	private int score;
	private int playerId;
	private DataInputStream input;
	private DataOutputStream output;
	
	/**
	 * Constructs a new player with the specified id and IO streams.
	 * @param id The ID of the player
	 * @param in The Input Stream of the socket
	 * @param out The Output Stream of the socket
	 */
	public Player(int id, InputStream in, OutputStream out) {
		score = 0;
		playerId = id;
		input = new DataInputStream(in);
		output = new DataOutputStream(out);
	}
	
	/**
	 * Blocks until a command is received and then returns it
	 * In the event of an exception, -1 will be returned
	 * @return The command or -1 to signal a problem.
	 */
	public int nextCommand() {
		try {
			return input.readInt();
		} catch(IOException e) {
			System.out.println(e.getMessage());
			return -1;
		}
	}
	
	/**
	 * Writes all the arguments to the output stream
	 * and flushes them immediately.
	 * @param args The arguments to be written
	 */
	public void write(int ... args) {
		try {
			for(int i : args) {
				output.writeInt(i);
				output.flush();
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Increments the players score by one
	 */
	public void incrementScore() {
		score++;
	}
	
	/**
	 * @return The players score
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * @return The Players ID
	 */
	public int getPlayerId() {
		return playerId;
	}
}
