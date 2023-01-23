package gamecore.observe;

/**
 * Represents something that is observable.
 * @author Dawn Nye
 * @param <T> The type of observations sent out.
 */
public interface IObservable<T>
{
	/**
	 * Causes {@code eye} to begin observing this.
	 * Observers are allowed to subscribe multiple times if desired.
	 * Observers are garunteed to be notified in the order of subscription.
	 * @param eye The observer.
	 * @throws NullPointerException Thrown if {@code eye} is null.
	 */
	public void Subscribe(IObserver<T> eye);
	
	/**
	 * Causes {@code eye} to stop observing this.
	 * Only removes at most the first/oldest instance of {@code eye} if subscribed multiple times.
	 * @param eye THe observer.
	 * @throws NullPointerException Thrown if {@code eye} is null.
	 */
	public void Unsubscribe(IObserver<T> eye);
}
