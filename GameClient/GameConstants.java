/**
 * An interface for having common command and 
 * state identifiers throughout the game
 * Assignment 10 - CPSC 1181 Section 1
 * @author Gabriel Henderson
 * @version March 24 
 */
public interface GameConstants {
	// GAME SETTINGS
	int NUM_PLAYERS = 2;
	int NUM_CARDS = 16;
	int REVEAL_TIME = 2000;
	int PORT_NUMBER = 1181;
	
	// COMMANDS ID's
	int CMD_SHOW = 0;	
	int CMD_HIDE = 1;
	int CMD_TRY = 2;
	int CMD_PLAYING = 3;
	int CMD_SCORE = 4;
	int CMD_WON = 5;
	int CMD_WAIT = 6;
	int CMD_EXIT = 7;
	int CMD_PLAYER = 8;
	int CMD_WRONG = 9;
	
	// STATE ID's
	int STATE_NOT_CONNECTED = 101;
	int STATE_WON = 102;
	int STATE_LOST = 103;
	int STATE_OKAY = 104;
	int STATE_WAITING = 105;
}
