package gamecore;

/**
 * Specifies how to update game components.
 * @author Dawn Nye
 */
public interface IUpdatable
{
	/**
	 * Called before the first update to initialize the game component.
	 */
	public void Initialize();
	
	/**
	 * Determines if this game component is initialized.
	 * @return Returns true if this game component is initialized and false otherwise.
	 */
	public boolean Initialized();
	
	/**
	 * Advances a game component by some time {@code delta}.
	 * @param delta The amount of time in milliseconds that has passed since the last update.
	 */
	public void Update(long delta);
	
	/**
	 * Called when the game component is removed from the game.
	 */
	public void Dispose();
	
	/**
	 * Determines if this game component has been disposed.
	 * @return Returns true if this game component has been disposed and false otherwise.
	 */
	public boolean Disposed();
}
