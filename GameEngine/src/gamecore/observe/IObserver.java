package gamecore.observe;

/**
 * Represents something that observes something else.
 * @author Dawn Nye
 * @param <T> The type of observations made.
 */
public interface IObserver<T>
{
	/**
	 * Called when an observation is made.
	 * @param event The observation.
	 */
	public void OnNext(T event);
	
	/**
	 * Called when an error occurs.
	 * @param e The observed error.
	 */
	public void OnError(Exception e);
	
	/**
	 * Called when the observable has finished sending observations.
	 */
	public void OnCompleted();
}
