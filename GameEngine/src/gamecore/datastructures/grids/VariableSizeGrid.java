package gamecore.datastructures.grids;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.LinkedList;
import gamecore.datastructures.maps.Dictionary;
import gamecore.datastructures.vectors.Vector2i;

/**
 * A variable size grid.
 * This will allow the user to store items at any coordinates.
 * It is backed with a dictionary.
 * @author Dawn Nye
 * @param <T> The type of item to store within the grid.
 */
public class VariableSizeGrid<T> implements IGrid<Vector2i,T>
{
	/**
	 * Creates an empty variable size grid.
	 */
	public VariableSizeGrid()
	{
		Grid = new Dictionary<Vector2i,T>();
		return;
	}
	
	public T Get(Vector2i index)
	{
		if(index == null)
			throw new NullPointerException();
		
		return Grid.Get(index);
	}
	
	/**
	 * Gets the item at ({@code x},{@code y}).
	 * @param x The x index of the item to return.
	 * @param y The y index of the item to return.
	 * @return Returns the item at ({@code x},{@code y}).
	 * @throws NoSuchElementException Thrown if no element exists at ({@code x},{@code y}).
	 */
	public T Get(int x, int y)
	{return Get(new Vector2i(x,y));}
	
	public T Set(T t, Vector2i index)
	{return Grid.Put(index,t);}
	
	/**
	 * Sets the item at ({@code x},{@code y}) to {@code t}.
	 * @param t The item to place at ({@code x},{@code y}).
	 * @param x The x index to place {@code t} in.
	 * @param y The y index to place {@code t} in.
	 * @return Returns the value placed into the grid so that this can be used like an assignment operator like a civilized language.
	 * @throws NullPointerException Thrown if {@code t} is null and the implementing class does not permit null entries.
	 */
	public T Set(T t, int x, int y)
	{return Set(t,new Vector2i(x,y));}
	
	public boolean Remove(Vector2i index)
	{
		if(index == null)
			throw new NullPointerException();
		
		return Grid.RemoveByKey(index);
	}
	
	/**
	 * Removes the item (if any) at {@code index}.
	 * @param x The x coordinate of the index to obliterate.
	 * @param y The y coordinate of the index to obliterate.
	 * @return Returns true if this grid was modified as a result of this call.
	 */
	public boolean Remove(int x, int y)
	{return Remove(new Vector2i(x,y));}
	
	public boolean IsCellOccupied(Vector2i index)
	{
		if(index == null)
			throw new NullPointerException();
		
		return Grid.ContainsKey(index);
	}
	
	/**
	 * Determines if the cell at ({@code x},{@code y}) is occupied.
	 * @param x The x index of the cell to check for occupation.
	 * @param y The y index of the cell to check for occupation.
	 * @return Returns true if the cell is occupied and false otherwise.
	 */
	public boolean IsCellOccupied(int x, int y)
	{return IsCellOccupied(new Vector2i(x,y));}
	
	public boolean IsCellEmpty(Vector2i index)
	{
		if(index == null)
			throw new NullPointerException();
		
		return !Grid.ContainsKey(index);
	}
	
	/**
	 * Determines if the cell at ({@code x},{@code y}) is vacant.
	 * @param x The x index of the cell to check for vacancy.
	 * @param y The y index of the cell to check for vacancy.
	 * @return Returns true if the cell is vacant and false otherwise.
	 */
	public boolean IsCellEmpty(int x, int y)
	{return !IsCellOccupied(x,y);}
	
	public Iterable<T> Items()
	{return Grid.Values();}
	
	public Iterable<Vector2i> IndexSet()
	{
		return new Iterable<Vector2i>()
		{
			public Iterator<Vector2i> iterator()
			{
				return new Iterator<Vector2i>()
				{
					public boolean hasNext()
					{return true;}
					
					public Vector2i next()
					{
						Vector2i ret = next;
						
						// We know that we need to increase our distance if we're pointing upward
						if(next.equals(new Vector2i(0,dist)))
							next = new Vector2i(1,dist++); // We start rotated 1 clockwise
						else // We rotate clockwise one
							if(next.X > 0)
								next = new Vector2i(next.X + (next.Y > 0 ? 1 : -1),next.Y - 1);
							else
								next = new Vector2i(next.X + (next.Y < 0 ? -1 : 1),next.Y + 1);
						
						return ret;
					}
					
					protected int dist = 0;
					protected Vector2i next = Vector2i.ZERO;
				};
			}
		};
	}
	
	public Iterable<Vector2i> IndexSet(boolean nonempty)
	{return nonempty ? Grid.Keys() : IndexSet();}
	
	public Iterable<T> Neighbors(Vector2i index)
	{return Neighbors(index.X,index.Y);}
	
	/**
	 * Returns an enumerable set of neighbors of ({@code x},{@code y}).
	 * @param x The x coordinate of the index whose neighbors we want to obtain.
	 * @param y The y coordinate of the index whose neighbors we want to obtain.
	 * @return Returns an enumerable set of neighbors of ({@code x},{@code y}).
	 */
	public Iterable<T> Neighbors(int x, int y)
	{
		LinkedList<T> ret = new LinkedList<T>();
		
		if(IsCellOccupied(x - 1,y))
			ret.add(Get(x - 1,y));
		
		if(IsCellOccupied(x,y - 1))
			ret.add(Get(x,y - 1));
		
		if(IsCellOccupied(x + 1,y))
			ret.add(Get(x + 1,y));
		
		if(IsCellOccupied(x,y + 1))
			ret.add(Get(x,y + 1));
		
		return ret;
	}
	
	public Iterable<Vector2i> NeighborIndexSet(Vector2i index)
	{return NeighborIndexSet(index.X,index.Y);}
	
	/**
	 * Returns an enumerable set of neightbors of ({@code x},{@code y}).
	 * @param x The x coordinate of the index to obtain the neighbors of.
	 * @param y The y coordinate of the index to obtain the neighbors of.
	 * @return Returns an enumerable set containing all indicies adjacent to ({@code x},{@code y}).
	 */
	public Iterable<Vector2i> NeighborIndexSet(int x, int y)
	{return NeighborIndexSet(x,y,false);}
	
	public Iterable<Vector2i> NeighborIndexSet(Vector2i index, boolean nonempty)
	{return NeighborIndexSet(index.X,index.Y,nonempty);}
	
	/**
	 * Returns an enumerable set of neightbors of ({@code x},{@code y}).
	 * @param x The x coordinate of the index to obtain the neighbors of.
	 * @param y The y coordinate of the index to obtain the neighbors of.
	 * @param nonempty If true, returns only nonempty indices.
	 * @return Returns an enumerable set containing all indicies adjacent to ({@code x},{@code y}).
	 */
	public Iterable<Vector2i> NeighborIndexSet(int x, int y, boolean nonempty)
	{
		LinkedList<Vector2i> ret = new LinkedList<Vector2i>();
		
		if(!nonempty || IsCellOccupied(x - 1,y))
			ret.add(new Vector2i(x - 1,y));
		
		if(!nonempty || IsCellOccupied(x,y - 1))
			ret.add(new Vector2i(x,y - 1));
		
		if(!nonempty || IsCellOccupied(x + 1,y))
			ret.add(new Vector2i(x + 1,y));
		
		if(!nonempty || IsCellOccupied(x,y + 1))
			ret.add(new Vector2i(x,y + 1));
		
		return ret;
	}
	
	public boolean ContainsIndex(Vector2i index)
	{return true;}
	
	/**
	 * Determines if the given index lies on this grid.
	 * @param x The x index to check.
	 * @param y The y index to check.
	 * @return Returns true if the index is in bounds and false otherwise.
	 */
	public boolean ContainsIndex(int x, int y)
	{return true;}
	
	public boolean Clear()
	{
		Grid.Clear();
		return true;
	}
	
	public int Count()
	{return Grid.Count();}
	
	public int Size()
	{return -1;}
	
	@Override public String toString()
	{return Grid.toString();}
	
	/**
	 * The actual grid backing the class.
	 */
	protected Dictionary<Vector2i,T> Grid;
}
