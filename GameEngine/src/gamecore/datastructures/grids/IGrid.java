package gamecore.datastructures.grids;

import java.util.NoSuchElementException;

/**
 * Defines the abstract notion of a (discrete) grid, an indexing system (likely a periodic and 2D tessellated set of shapes) which denote cells.
 * Each cell may be referenced via an object of generic type {@code I}.
 * Often, {@code I} will be an integer pair of coordinates or a string name.
 * Excpliit implementations of this interface for, for example {@code I} = (Integer,Integer), may speed up computation.
 * @author Dawn Nye
 * @param <I> The index type.
 * @param <T> The item type.
 */
public interface IGrid<I,T>
{
	/**
	 * Gets the item at {@code index}.
	 * @param index The index to retrieve.
	 * @return Returns the item at {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NoSuchElementException Thrown if no element exists at ({@code x},{@code y}).
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public T Get(I index);
	
	/**
	 * Sets the item at {@code index} to {@code t}.
	 * @param t The item to place at {@code index}.
	 * @param index The index to place {@code t} in.
	 * @return Returns the value placed into the grid so that this can be used like an assignment operator like a civilized language.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NullPointerException Thrown {@code index} is null or if {@code t} is null and the implementing class does not permit null entries.
	 */
	public T Set(T t, I index);
	
	/**
	 * Removes the item (if any) at {@code index}.
	 * @param index The index to obliterate.
	 * @return Returns true if this grid was modified as a result of this call.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public boolean Remove(I index);
	
	/**
	 * Determines if the cell at {@code index} is occupied.
	 * @param index The cell to check for occupation.
	 * @return Returns true if the cell is occupied and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown in {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public boolean IsCellOccupied(I index);
	
	/**
	 * Determines if the cell at {@code index} is vacant.
	 * @param index The cell to check for vacancy.
	 * @return Returns true if the cell is vacant and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown in {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public boolean IsCellEmpty(I index);
	
	/**
	 * Returns an enumerable set of all items in the entire grid.
	 * @return Returns an enumeration of all the items in the entire grid.
	 */
	public Iterable<T> Items();
	
	/**
	 * Returns an enumerable set of indices for the entire grid. 
	 * @return Returns an enumerable set containing all indicies in the grid.
	 */
	public Iterable<I> IndexSet();
	
	/**
	 * Returns an enumerable set of indices for the entire grid.
	 * @param nonempty If true, returns only nonempty indices.
	 * @return Returns an enumerable set containing all indicies in the grid.
	 */
	public Iterable<I> IndexSet(boolean nonempty);
	
	/**
	 * Returns an enumerable set of neighbors of {@code index}.
	 * @param index The index whose neighbors we want to obtain.
	 * @return Returns an enumerable set of neighbors of {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public Iterable<T> Neighbors(I index);
	
	/**
	 * Returns an enumerable set of neightbors of {@code index}.
	 * @param index The index to obtain the neighbors of.
	 * @return Returns an enumerable set containing all indicies adjacent to {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public Iterable<I> NeighborIndexSet(I index);
	
	/**
	 * Returns an enumerable set of neightbors of {@code index}.
	 * @param index The index to obtain the neighbors of.
	 * @param nonempty If true, returns only nonempty indices.
	 * @return Returns an enumerable set containing all indicies adjacent to {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public Iterable<I> NeighborIndexSet(I index, boolean nonempty);
	
	/**
	 * Determines if the given index lise on this grid.
	 * @param index The index to check.
	 * @return Returns true if the index is in bounds and false otherwise.
	 * @throws NullPointerException Thrown if {@code index} is null.
	 */
	public boolean ContainsIndex(I index);
	
	/**
	 * Clears the grid (if able).
	 * @return Returns true if the grid was cleared and false otherwise.
	 */
	public boolean Clear();
	
	/**
	 * Determines the number of items stored in this grid.
	 * @return Returns the number of items stored in this grid.
	 */
	public int Count();
	
	/**
	 * Determines the number of cells in this grid.
	 * @return Returns the number of cells in this grid.
	 * If there are infinitely many cells, a negative value is returned.
	 */
	public int Size();
}
