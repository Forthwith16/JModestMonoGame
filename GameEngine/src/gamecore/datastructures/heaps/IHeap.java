package gamecore.datastructures.heaps;

/**
 * A heap datastructure.
 * @author Dawn Nye
 * @param <T> The type of data stored in the heap.
 */
public interface IHeap<T> extends Iterable<T>
{
	/**
	 * Adds the given element to the heap.
	 * @param t The element to add.
	 * @return Returns true if this queue was changed as a result of this call and false otherwise.
	 */
	public boolean Add(T t);
	
	/**
	 * Adds every element of {@code c} to the heap in the order they appear.
	 * @param c The elements to add.
	 * @return Returns true if this queue was changed as a result of this call and false otherwise.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean AddAll(Iterable<? extends T> c);
	
	/**
	 * Gets and removes the top element of the heap.
	 * @return Returns the top element of the heap or null if no such element exists.
	 */
	public T RemoveTop();
	
	/**
	 * Gets the top element of the heap.
	 * @return Returns the top element of the heap or null if no such element exists.
	 */
	public T Top();
	
	/**
	 * Clears the heap.
	 */
	public void Clear();
	
	/**
	 * Determines the number of items in the heap.
	 * @return Returns the number of items in this heap.
	 */
	public int Count();
	
	/**
	 * Determines if the heap is empty.
	 * @return Returns true if the heap is empty and false otherwise.
	 */
	public boolean IsEmpty();
}
