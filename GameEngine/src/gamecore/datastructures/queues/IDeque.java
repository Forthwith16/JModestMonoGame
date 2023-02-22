package gamecore.datastructures.queues;

import java.util.NoSuchElementException;

/**
 * A double-ended queue.
 * Allows addition and removal from either the front or the back of the queue.
 * This, of course, allows it to function as either a queue or a stack.
 * @author Dawn Nye
 * @param <T> The type to store in the deque.
 */
public interface IDeque<T> extends IStack<T>, IQueue<T>
{
	/**
	 * Adds {@code t} to the front of the deque.
	 * @param t The element to add.
	 * @return Returns true if the element was added and false otherwise.
	 */
	public boolean AddFront(T t);
	
	/**
	 * Adds every element of {@code c} to the front of the deque.
	 * @param c The elements to add.
	 * @return Returns true if at least one element was added and false otherwise.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean AddAllFront(Iterable<? extends T> c);
	
	/**
	 * Obtains the front of the deque without removing it.
	 * @throws NoSuchElementException Thrown if the deque is empty.
	 */
	public T Front();
	
	/**
	 * Obtains and removes the front of the deque.
	 * @throws NoSuchElementException Thrown if the deque is empty.
	 */
	public T PollFront();
	
	/**
	 * Adds {@code t} to the back of the deque.
	 * @param t The element to add.
	 * @return Returns true if the element was added and false otherwise.
	 */
	public boolean AddBack(T t);
	
	/**
	 * Adds every element of {@code c} to the back of the deque.
	 * @param c The elements to add.
	 * @return Returns true if at least one element was added and false otherwise.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean AddAllBack(Iterable<? extends T> c);
	
	/**
	 * Obtains the back of the deque without removing it.
	 * @throws NoSuchElementException Thrown if the deque is empty.
	 */
	public T Back();
	
	/**
	 * Obtains and removes the back of the deque.
	 * @throws NoSuchElementException Thrown if the deque is empty.
	 */
	public T PollBack();
	
	/**
	 * Clears the deque.
	 */
	public void Clear();
	
	/**
	 * The number of items in the deque.
	 */
	public int Count();
	
	/**
	 * Determines if the deque is empty.
	 * @return Returns true if the deque is empty and false otherwise.
	 */
	public boolean IsEmpty();
}