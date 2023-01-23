package gamecore.datastructures.grids;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.LinkedList;
import gamecore.datastructures.tuples.Pair;
import gamecore.datastructures.vectors.Vector2i;

/**
 * Creates a two dimensional grid of a fixed size.
 * It is indexed into via (int,int) pairs.
 * @author Dawn Nye
 * @param <T> The type of item to store in the grid.
 */
public class FixedSizeGrid<T> implements IGrid<Vector2i,T>
{
	/**
	 * Creates a grid of a fixed size {@code w} by {@code h}.
	 * @param w The width of the grid.
	 * @param h The height of the grid.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is not a positive number.
	 */
	public FixedSizeGrid(int w, int h)
	{
		if(w < 1 || h < 1)
			throw new IllegalArgumentException();
		
		Width = w;
		Height = h;
		Count = 0;
		
		grid = (Pair<T,Boolean>[][])new Pair[Width][Height];
		
		for(int i = 0;i < Width;i++)
			for(int j = 0;j < Height;j++)
				grid[i][j] = new Pair<T,Boolean>(null,false);
		
		return;
	}
	
	/**
	 * Creates a grid of fixed size {@code w} by {@code h}, initially populated by {@code c}.
	 * @param w The width of the grid.
	 * @param h The height of the grid.
	 * @param c The initial population of the grid. Duplicate locations are allowed.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is not a positive number.
	 * @throws IndexOutOfBoundsException Thrown if any of its locations are out of bounds.
	 * @throws NullPointerException Thrown if {@code c} is null or any of its locations are null or if any of its items are null and this grid does not allow null entries.
	 */
	public FixedSizeGrid(int w, int h, Iterable<Pair<? extends T,Vector2i>> c)
	{
		this(w,h);
		
		for(Pair<? extends T,Vector2i> e : c)
			Set(e.Item1,e.Item2);
		
		return;
	}
	
	/**
	 * Creates a copy of the provided grid.
	 * The elements of the grid itself are shallow copied but the grid's backing data is a deep copy.
	 * @param grid The grid to copy.
	 * @throws NullPointerException Thrown if {@code grid} is null.
	 */
	public FixedSizeGrid(FixedSizeGrid<? extends T> grid)
	{
		this(grid.Width,grid.Height,grid);
		return;
	}
	
	/**
	 * Creates a grid of a fixed size {@code w} by {@code h}.
	 * It is populated with the values in {@code grid} so that if {@code grid}(x) is occupied, then this grid has (a shallow copy of) the same element at position x as well if x is in bounds. 
	 * @param w The width of the grid.
	 * @param h The height of the grid.
	 * @param grid The grid to copy.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is not a positive number.
	 * @throws NullPointerException Thrown if {@code grid} is null.
	 */
	public FixedSizeGrid(int w, int h, FixedSizeGrid<? extends T> grid)
	{
		this(w,h);
		
		for(Vector2i p : grid.IndexSet(true))
			Set(grid.Get(p),p);
		
		return;
	}
	
	public T Get(Vector2i index)
	{return Get(index.X,index.Y);}
	
	/**
	 * Gets the item at ({@code x},{@code y}).
	 * @param x The x index of the item to return.
	 * @param y The y index of the item to return.
	 * @return Returns the item at ({@code x},{@code y}).
	 * @throws IndexOutOfBoundsException Thrown if ({@code x},{@code y}) is out of bounds.
	 * @throws NoSuchElementException Thrown if no element exists at ({@code x},{@code y}).
	 */
	public T Get(int x, int y)
	{
		if(!ContainsIndex(x,y))
			throw new IndexOutOfBoundsException();
		
		if(IsCellOccupied(x,y))
			return grid[x][y].Item1;
		
		throw new NoSuchElementException();
	}
	
	public T Set(T t, Vector2i index)
	{return Set(t,index.X,index.Y);}
	
	/**
	 * Sets the item at ({@code x},{@code y}) to {@code t}.
	 * @param t The item to place at ({@code x},{@code y}).
	 * @param x The x index to place {@code t} in.
	 * @param y The y index to place {@code t} in.
	 * @return Returns the value placed into the grid so that this can be used like an assignment operator like a civilized language.
	 * @throws IndexOutOfBoundsException Thrown if ({@code x},{@code y}) is out of bounds.
	 * @throws NullPointerException Thrown if {@code t} is null and the implementing class does not permit null entries.
	 */
	public T Set(T t, int x, int y)
	{
		if(!ContainsIndex(x,y))
			throw new IndexOutOfBoundsException();
		
		if(IsCellEmpty(x,y))
			Count++;
		
		grid[x][y] = new Pair<T,Boolean>(t,true);
		return t;
	}
	
	public boolean Remove(Vector2i index)
	{return Remove(index.X,index.Y);}
	
	/**
	 * Removes the item (if any) at {@code index}.
	 * @param x The x coordinate of the index to obliterate.
	 * @param y The y coordinate of the index to obliterate.
	 * @return Returns true if this grid was modified as a result of this call.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is out of bounds.
	 */
	public boolean Remove(int x, int y)
	{
		if(!ContainsIndex(x,y))
			throw new IndexOutOfBoundsException();
		
		if(IsCellOccupied(x,y))
		{
			grid[x][y] = new Pair<T,Boolean>(null,false);
			Count--;
			
			return true;
		}
		
		return false;
	}
	
	public boolean IsCellOccupied(Vector2i index)
	{return IsCellOccupied(index.X,index.Y);}
	
	/**
	 * Determines if the cell at ({@code x},{@code y}) is occupied.
	 * @param x The x index of the cell to check for occupation.
	 * @param y The y index of the cell to check for occupation.
	 * @return Returns true if the cell is occupied and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown in ({@code x},{@code y}) is out of bounds.
	 */
	public boolean IsCellOccupied(int x, int y)
	{return grid[x][y].Item2;}
	
	public boolean IsCellEmpty(Vector2i index)
	{return IsCellEmpty(index.X,index.Y);}
	
	/**
	 * Determines if the cell at ({@code x},{@code y}) is vacant.
	 * @param x The x index of the cell to check for vacancy.
	 * @param y The y index of the cell to check for vacancy.
	 * @return Returns true if the cell is vacant and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown in ({@code x},{@code y}) is out of bounds.
	 */
	public boolean IsCellEmpty(int x, int y)
	{return !IsCellOccupied(x,y);}
	
	public Iterable<T> Items()
	{return new IterableItems(true);}
	
	public Iterable<Vector2i> IndexSet()
	{return IndexSet(false);}
	
	public Iterable<Vector2i> IndexSet(boolean nonempty)
	{return new IterableIndicies(nonempty);}
	
	public Iterable<T> Neighbors(Vector2i index)
	{return Neighbors(index.X,index.Y);}
	
	/**
	 * Returns an enumerable set of neighbors of ({@code x},{@code y}).
	 * @param x The x coordinate of the index whose neighbors we want to obtain.
	 * @param y The y coordinate of the index whose neighbors we want to obtain.
	 * @return Returns an enumerable set of neighbors of ({@code x},{@code y}).
	 * @throws IndexOutOfBoundsException Thrown if ({@code x},{@code y}) is out of bounds.
	 */
	public Iterable<T> Neighbors(int x, int y)
	{
		if(!ContainsIndex(x,y))
			throw new IndexOutOfBoundsException();
		
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
	 * @throws IndexOutOfBoundsException Thrown if ({@code x},{@code y}) is out of bounds.
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
	 * @throws IndexOutOfBoundsException Thrown if ({@code x},{@code y}) is out of bounds.
	 */
	public Iterable<Vector2i> NeighborIndexSet(int x, int y, boolean nonempty)
	{
		if(!ContainsIndex(x,y))
			throw new IndexOutOfBoundsException();
		
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
	{return ContainsIndex(index.X,index.Y);}
	
	/**
	 * Determines if the given index lise on this grid.
	 * @param x The x index to check.
	 * @param y The y index to check.
	 * @return Returns true if the index is in bounds and false otherwise.
	 */
	public boolean ContainsIndex(int x, int y)
	{return x >= 0 && x < Width && y >= 0 && y < Height;}
	
	public boolean Clear()
	{
		grid = (Pair<T,Boolean>[][])new Object[Width][Height];
		Count = 0;
		
		return true;
	}
	
	public int Count()
	{return Count;}
	
	public int Size()
	{return Width * Height;}
	
	/**
	 * Gets the width of this grid.
	 * @return Returns the width of this grid.
	 */
	public int Width()
	{return Width;}
	
	/**
	 * Gets the height of this grid.
	 * @return Returns the height of this grid.
	 */
	public int Height()
	{return Height;}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(int j = 0;j < Height;j++)
		{
			for(int i = 0;i < Width;i++)
				ret += IsCellOccupied(i,j) ? Get(i,j).toString() : "<empty>";
			
			if(j != Height - 1)
				ret += "\n";
		}
		
		return ret;
	}
	
	/**
	 * The width of the grid.
	 */
	protected int Width;
	
	/**
	 * The height of the grid.
	 */
	protected int Height;
	
	/**
	 * The grid of values.
	 * The first item is the value itself.
	 * The second item is true if there is a value stored at that location in the grid and false otherwise, which allows us to distinguish null entries from empty entries.
	 */
	protected Pair<T,Boolean>[][] grid;
	
	/**
	 * The number of items in the grid.
	 */
	protected int Count;
	
	/**
	 * Creates iterators over the items of the grid.
	 * @author Dawn Nye
	 */
	protected class IterableItems implements Iterable<T>
	{
		/**
		 * Creates a new ItemIterator generator.
		 * @param skip_empty If true, then the iterators generated will skip over empty cells.
		 */
		public IterableItems(boolean skip_empty)
		{
			SkipEmpty = skip_empty;
			return;
		}
		
		public Iterator<T> iterator()
		{return new ItemIterator(SkipEmpty);}
		
		/**
		 * If true, we skip empty entries.
		 */
		protected boolean SkipEmpty;
	}
	
	/**
	 * Iterates over the items of the grid.
	 * @author Dawn Nye
	 */
	protected class ItemIterator implements Iterator<T>
	{
		/**
		 * Creates a new item iterator.
		 */
		public ItemIterator(boolean skip_empty)
		{
			CurX = -1;
			CurY = 0;
			
			SkipEmpty = skip_empty;
			ReadyToRemove = false;
			
			Done = false;
			Precomputed = false;
			PrecomputedHasNext = false;
			
			return;
		}
		
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			Increment();
			return Get(CurX,CurY);
		}
		
		public boolean hasNext()
		{
			if(Done)
				return false;
			
			if(Precomputed)
				return PrecomputedHasNext;
			
			int temp_x = CurX;
			int temp_y = CurY;
			
			PrecomputedHasNext = Increment();
			PrecomputedNextX = CurX;
			PrecomputedNextY = CurY;
			Precomputed = true; // This is important to do after Increment
			
			CurX = temp_x;
			CurY = temp_y;
			
			return PrecomputedHasNext;
		}
		
		public void remove()
		{
			if(ReadyToRemove)
			{
				Remove(CurX,CurY);
				ReadyToRemove = false;
			}
			else
				throw new IllegalStateException();
			
			return;
		}
		
		/**
		 * Increments the current position by one.
		 * @return Returns true if the next position is on the grid and false if its out of bounds.
		 */
		protected boolean Increment()
		{
			if(Done)
				return false;
			
			if(Precomputed)
			{
				Precomputed = false;
				
				CurX = PrecomputedNextX;
				CurY = PrecomputedNextY;
				
				return PrecomputedHasNext;
			}
			
			do
			{
				CurX++;
				
				if(CurX == Width)
				{
					CurX = 0;
					CurY++;
					
					if(CurY == Height)
					{
						Done = true;
						return false;
					}
				}
			}
			while(SkipEmpty && IsCellEmpty(CurX,CurY));
			
			return true;
		}
		
		/**
		 * If true, then we're done.
		 */
		protected boolean Done;
		
		/**
		 * If true, then we've precomputed the next values.
		 */
		protected boolean Precomputed;
		
		/**
		 * If true, then we've determined that we have a next value.
		 */
		protected boolean PrecomputedHasNext;
		
		/**
		 * The precomputed next x value.
		 */
		protected int PrecomputedNextX;
		
		/**
		 * The precomputed next y value.
		 */
		protected int PrecomputedNextY;
		
		/**
		 * If true, we skip empty entries.
		 */
		protected boolean SkipEmpty;
		
		/**
		 * The current x position.
		 */
		protected int CurX;
		
		/**
		 * The current y position.
		 */
		protected int CurY;
		
		/**
		 * If true, then we are ready to remove an element.
		 */
		protected boolean ReadyToRemove;
	}
	
	/**
	 * Creates iterators over the indices of the grid.
	 * @author Dawn Nye
	 */
	protected class IterableIndicies implements Iterable<Vector2i>
	{
		/**
		 * Creates a new IndexIterator generator.
		 * @param skip_empty If true, then the iterators generated will skip over empty cells.
		 */
		public IterableIndicies(boolean skip_empty)
		{
			SkipEmpty = skip_empty;
			return;
		}
		
		public Iterator<Vector2i> iterator()
		{return new IndexIterator(SkipEmpty);}
		
		/**
		 * If true, we skip empty entries.
		 */
		protected boolean SkipEmpty;
	}
	
	/**
	 * Iterates over the items of the grid.
	 * @author Dawn Nye
	 */
	protected class IndexIterator implements Iterator<Vector2i>
	{
		/**
		 * Creates a new index iterator.
		 */
		public IndexIterator(boolean skip_empty)
		{
			CurX = -1;
			CurY = 0;
			
			SkipEmpty = skip_empty;
			ReadyToRemove = false;
			
			Done = false;
			Precomputed = false;
			PrecomputedHasNext = false;
			
			return;
		}
		
		public Vector2i next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			Increment();
			return new Vector2i(CurX,CurY);
		}
		
		public boolean hasNext()
		{
			if(Done)
				return false;
			
			if(Precomputed)
				return PrecomputedHasNext;
			
			int temp_x = CurX;
			int temp_y = CurY;
			
			PrecomputedHasNext = Increment();
			PrecomputedNextX = CurX;
			PrecomputedNextY = CurY;
			Precomputed = true; // This is important to do after Increment
			
			CurX = temp_x;
			CurY = temp_y;
			
			return PrecomputedHasNext;
		}
		
		public void remove()
		{
			if(ReadyToRemove)
			{
				Remove(CurX,CurY);
				ReadyToRemove = false;
			}
			else
				throw new IllegalStateException();
			
			return;
		}
		
		/**
		 * Increments the current position by one.
		 * @return Returns true if the next position is on the grid and false if its out of bounds.
		 */
		protected boolean Increment()
		{
			if(Done)
				return false;
			
			if(Precomputed)
			{
				Precomputed = false;
				
				CurX = PrecomputedNextX;
				CurY = PrecomputedNextY;
				
				return PrecomputedHasNext;
			}
			
			do
			{
				CurX++;
				
				if(CurX == Width)
				{
					CurX = 0;
					CurY++;
					
					if(CurY == Height)
					{
						Done = true;
						return false;
					}
				}
			}
			while(SkipEmpty && IsCellEmpty(CurX,CurY));
			
			return true;
		}
		
		/**
		 * If true, then we're done.
		 */
		protected boolean Done;
		
		/**
		 * If true, then we've precomputed the next values.
		 */
		protected boolean Precomputed;
		
		/**
		 * If true, then we've determined that we have a next value.
		 */
		protected boolean PrecomputedHasNext;
		
		/**
		 * The precomputed next x value.
		 */
		protected int PrecomputedNextX;
		
		/**
		 * The precomputed next y value.
		 */
		protected int PrecomputedNextY;
		
		/**
		 * If true, we skip empty entries.
		 */
		protected boolean SkipEmpty;
		
		/**
		 * The current x position.
		 */
		protected int CurX;
		
		/**
		 * The current y position.
		 */
		protected int CurY;
		
		/**
		 * If true, then we are ready to remove an element.
		 */
		protected boolean ReadyToRemove;
	}
}