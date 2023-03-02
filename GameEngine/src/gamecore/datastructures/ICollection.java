package gamecore.datastructures;

/**
 * Describes the bare basics of what it means to be a collection of things.
 * This includes the ability to add and remove from the collection.
 * We can also query the collection to determine its size and contents.
 * @author Dawn Nye
 * @param <T> The type stored in the collection.
 */
public interface ICollection<T> extends Iterable<T>
{
	/**
	 * Adds {@code t} to the collection.
	 * @param t The item to add.
	 * @return Returns true if the item was added and false otherwise.
	 * @throws NullPointerException Thrown if {@code t} is null, this collection does not permit null entries, and the null value is not simply rejected.
	 * @throws UnsupportedOperationException Thrown if the addition of elements is not permitted by this collection.
	 */
	public boolean Add(T t);
	
	/**
	 * Removes {@code t} from the collection.
	 * @param t The item to remove.
	 * @return Returns true if the item was removed and false otherwise.
	 * @throws UnsupportedOperationException Thrown if the removal of elements is not permitted by this collection.
	 */
	public boolean Remove(T t);
	
	/**
	 * Determines if {@code t} is in the collection.
	 * @param t The item to search for.
	 * @return Returns true if the item is in the collection and false otherwise.
	 */
	public boolean Contains(T t);
	
	/**
	 * Empties the collection.
	 * @throws UnsupportedOperationException Thrown if removing elemnts from the collection is not permitted.
	 */
	public void Clear();
	
	/**
	 * Determines if this collection is empty.
	 * @return Returns true if the collection is empty and false otherwise.
	 */
	public default boolean IsEmpty()
	{return Count() == 0;}
	
	/**
	 * Determines the size of this collection.
	 * @return Returns the integer number of elements belonging to this collection.
	 */
	public int Count();
}
