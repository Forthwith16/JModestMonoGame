package gamecore.datastructures.queues;

import java.util.NoSuchElementException;

import gamecore.datastructures.ICollection;

/**
 * A queue datastructure.
 * @author Dawn Nye
 * @param <T> The type of data stored in the queue.
 */
public interface IQueue<T> extends ICollection<T>
{
	/**
	 * Adds {@code t} to the queue.
	 * @param t The item to add to the queue.
	 * @return Returns true if this queue was changed as a result of this call and false otherwise.
	 */
	public boolean Enqueue(T t);
	
	/**
	 * Adds every element of {@code c} to the queue.
	 * @param c The elements to add to the queue.
	 * @return Returns true if this queue was changed as a result of this call and false otherwise.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean EnqueueAll(Iterable<? extends T> c);
	
	/**
	 * Gets and removes the front item from the queue.
	 * @return Returns the front item of the queue.
	 * @throws NoSuchElementException Thrown if the queue is empty.
	 */
	public T Dequeue();
	
	/**
	 * Gets the front of the queue without removing it.
	 * @return Returns the front item of the queue.
	 * @throws NoSuchElementException Thrown if the queue is empty.
	 */
	public T Front();
	
	/**
	 * Clears the queue.
	 */
	public void Clear();
	
	/**
	 * The number of items in the queue.
	 */
	public int Count();
	
	/**
	 * Determines if the queue is empty.
	 * @return Returns true if the queue is empty and false otherwise.
	 */
	public boolean IsEmpty();
}
