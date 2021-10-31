import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * A class to handle server logic and session
 * creation between multiple clients.
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 29
 */
public class Server 
	extends JFrame implements GameConstants {
	
	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 400;
	public static final int JOIN_DELAY = 3000;
	
	private JTextArea logArea;
	private ServerSocket serverSocket;
	
	/**
	 * Initializes a new server that will open
	 * a server socket on the designated port
	 */
	public Server() {
		try {
	        serverSocket = new ServerSocket(PORT_NUMBER);
	        initializeFrame();
			begin();
        } catch (IOException e) {
	        JOptionPane.showMessageDialog(null, "Error - Cannot start server\n" + e.getMessage());
        } finally {
			try {
	            serverSocket.close();
            } catch (IOException e) {
	            e.printStackTrace(System.err);
            }
		}
	}
	
	/**
	 * Signals the server to begin accepting connections
	 * between clients. A new session with NUM_PLAYERS
	 * will be created on its own thread and started
	 */
	public void begin() {
		log("Server is now online");
		for(int sessionId = 1; true; sessionId++) {
			ArrayList<Socket> playerSockets = new ArrayList<Socket>();
			// Accept new socket connections until the desired
			// number of players has been reached then start.
			while(playerSockets.size() < NUM_PLAYERS) {
				try {
					playerSockets.add(serverSocket.accept());
					log("Player has connected");
				} catch (IOException e) {
					log(e.getMessage());
				}	
				// Check for players who may have disconnected while
				// in the waiting state and remove them from the session.
				for(int i = 0; i < playerSockets.size(); i++) {
					if(playerSockets.get(i).isClosed()) {
						playerSockets.remove(i);
						log("Player " + i + " has been removed from session " + sessionId);
					}
				}
			}
			
			try {
				Thread.sleep(JOIN_DELAY);
			} catch(InterruptedException e) {
				log(e.getMessage());
			}
			
			new SessionThread(playerSockets).start();
			log("Session " + sessionId + " has been started");
		}	
	}
	
	/**
	 * Sends a message with time stamp to the log area
	 * @param message The message to be logged
	 */
	public void log(String message) {
		logArea.append(new Date() + ": " + message + "\n");
	}
	
	/**
	 * Initializes the frame properties
	 */
	public void initializeFrame() {
		setTitle("Memory Game Server");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		logArea = new JTextArea(50, 50);
		logArea.setEditable(false);
		add(logArea);
		setResizable(false);
		setVisible(true);
	}
}