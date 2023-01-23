package sample;

import sample.SampleGame;

/**
 * The bootstrap class that starts a game.  
 * @author Dawn Nye
 */
public class Bootstrap
{
	/**
	 * Loads a game and any related settings and mods.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) throws Exception
	{
		new Thread(new SampleGame()).start();
		return;
	}
}
