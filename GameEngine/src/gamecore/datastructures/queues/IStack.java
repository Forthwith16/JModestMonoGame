package gamecore.datastructures.queues;

import java.util.NoSuchElementException;

import gamecore.datastructures.ICollection;

/**
 * A stack datastructure.
 * @author Dawn Nye
 * @param <T> The type to store in the stack.
 */
public interface IStack<T> extends ICollection<T>
{
	/**
	 * Pushes {@code t} onto the top of the stack.
	 * @param t The element to push onto the stack.
	 * @return Returns true if the element was pushed onto the stack and false otherwise.
	 */
	public boolean Push(T t);
	
	/**
	 * Pushes all of the elements of {@code c} onto the top of the stack.
	 * The last element of {@code c} is the first to pop off the stack.
	 * The first element of {@code c} is the last element to pop off the stack.
	 * @param c The elements to add to the stack.
	 * @return Returns true if at least one item was added to the stack and false otherwise.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean PushAll(Iterable<? extends T> c);
	
	/**
	 * Pops the top element off the stack.
	 * @throws NoSuchElementException Thrown if the stack is empty.
	 */
	public T Pop();
	
	/**
	 * Obtains the top element of the stack without removing it.
	 * @throws NoSuchElementException Thrown if the stack is empty.
	 */
	public T Peek();
	
	/**
	 * Clears the stack.
	 */
	public void Clear();
	
	/**
	 * The number of items in the stack.
	 */
	public int Count();
	
	/**
	 * Determines if the stack is empty.
	 * @return Returns true if the stack is empty and false otherwise.
	 */
	public boolean IsEmpty();
}
