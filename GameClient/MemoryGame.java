/**
 * A viewer for the client frame.
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 24 
 */
public class MemoryGame {
	/**
	 * The main method for the viewer class
	 * that launches a new client frame.
	 * @param args The CLI arguments
	 */
	public static void main(String[] args) {
		if(args.length == 2 && args[0].equalsIgnoreCase("-server")) {
			new ClientFrame(args[1]);
		} else {
			System.out.println("Usage is: MemoryGame -server [IPAddress] or MemoryGame -help");
		}
	}
}