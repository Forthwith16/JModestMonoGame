package gamecore.datastructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A dynamically expanding/contracting list of items backed by an array.
 * @author Dawn Nye
 * @param <T> The type of data stored in the list.
 */
public class ArrayList<T> implements List<T>
{
	/**
	 * Creates an empty array list.
	 */
	public ArrayList()
	{
		Items = (T[])new Object[16]; // Java is baaaaaaaaaaaaaaaaaaaaaaaaad
		return;
	}
	
	/**
	 * Creates an array list initially populated by {@code c}.
	 * @param c The initial population.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public ArrayList(Iterable<? extends T> c)
	{
		// Add is amortized constant time, so just blindly add stuff from c
		this();
		
		for(T t : c)
			add(t);
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof List)
		{
			List l = (List)obj;
			
			if(size() != l.size())
				return false;
			
			Iterator mine = iterator();
			Iterator yours = l.iterator();
			
			while(mine.hasNext())
			{
				Object mobj = mine.next();
				Object yobj = yours.next();
				
				if(!(mobj == null ? yobj == null : mobj.equals(yobj)))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Expands the backing array by a factor of 2.
	 */
	protected void Expand()
	{
		// Only expand when we absolutely must
		if(size() < Items.length)
			return;
		
		T[] temp = (T[])new Object[Items.length << 1];
		
		for(int i = 0;i < size();i++)
			temp[i] = Items[i];
		
		Items = temp;
		return;
	}
	
	/**
	 * Contracts the backing array by a factor of 2.
	 */
	protected void Contract()
	{
		// Contract only if at least 2/3 of the array is empty
		// This garuntees us amortized constant time adds (but not inserts, of course)
		// We also pick 16 as our minimum length
		if(size() > Items.length / 3 || Items.length <= 16)
			return;
		
		T[] temp = (T[])new Object[Items.length >> 1];
		
		for(int i = 0;i < size();i++)
			temp[i] = Items[i];
		
		Items = temp;
		return;
	}
	
	public boolean add(T e)
	{
		Items[Count++] = e;
		Expand();
		
		return true;
	}

	public void add(int index, T t)
	{
		Count++;
		Expand();
		
		for(int i = size() - 1;i > index;i--)
			Items[i] = Items[i - 1];
		
		Items[index] = t;
		return;
	}
	
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(add(t))
				ret = true;
		
		return ret;
	}
	
	public boolean addAll(int index, Collection<? extends T> c)
	{
		int delta = c.size(); // Just in case this takes a while to compute
		
		Count += delta;
		Expand();
		
		for(int i = 0;i < delta;i++)
			Items[size() - 1 - i] = Items[size() - 1 - i - delta];
		
		int i = index;
		
		for(T t : c)
			Items[i++] = t;
		
		return i != index;
	}
	
	public T set(int index, T element)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		T ret = Items[index];
		Items[index] = element;
		
		return ret;
	}
	
	public T get(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		return Items[index];
	}
	
	public boolean remove(Object o)
	{
		for(int i = 0;i < size();i++)
			if(o == null ? Items[i] == null : o.equals(Items[i]))
			{
				while(i++ < size())
					Items[i - 1] = Items[i];
				
				Count--;
				return true;
			}
		
		return false;
	}
	
	public T remove(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		T ret = Items[index];
		
		for(int i = index;i < size() - 1;i++)
			Items[i] = Items[i + 1];
		
		// It's fine to leave garbage data behind, but just in case it needs to be garbage collected, we'll null it out
		Items[--Count] = null;
		
		Contract();
		return ret;
	}
	
	public boolean removeAll(Collection<?> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		int shift = 0;
		
		for(int i = 0;i < size();i++)
		{
			if(c.contains(Items[i]))
			{
				shift++;
				Items[i] = null;
			}
			else if(shift > 0)
			{
				Items[i - shift] = Items[i];
				Items[i] = null;
			}
		}
		
		Count -= shift;
		Contract();
		
		return shift > 0;
	}
	
	public boolean retainAll(Collection<?> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		int shift = 0;
		
		for(int i = 0;i < size();i++)
		{
			if(!c.contains(Items[i]))
			{
				shift++;
				Items[i] = null;
			}
			else if(shift > 0)
			{
				Items[i - shift] = Items[i];
				Items[i] = null;
			}
		}
		
		Count -= shift;
		Contract();
		
		return shift > 0;
	}
	
	public int indexOf(Object o)
	{
		for(int i = 0;i < size();i++)
			if(o == null ? Items[i] == null : o.equals(Items[i]))
				return i;
		
		return -1;
	}
	
	public int lastIndexOf(Object o)
	{
		for(int i = size() - 1;i > -1;i--)
			if(o == null ? Items[i] == null : o.equals(Items[i]))
				return i;
		
		return -1;
	}
	
	public boolean contains(Object o)
	{
		for(int i = 0;i < size();i++)
			if(o == null ? Items[i] == null : o.equals(Items[i]))
				return true;
		
		return false;
	}
	
	public boolean containsAll(Collection<?> c)
	{
		for(Object t : c)
			if(!contains(t))
				return false;
		
		return true;
	}
	
	public void clear()
	{
		Items = (T[])new Object[10];
		Count = 0;
		
		return;
	}
	
	public Iterator<T> iterator()
	{return new ArrayListIterator();}
	
	public ListIterator<T> listIterator()
	{return new ArrayListIterator();}
	
	public ListIterator<T> listIterator(int index)
	{
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		return new ArrayListIterator(index);
	}
	
	public List<T> subList(int from, int to)
	{return new UniversalSublist(this,from,to);}
	
	public boolean isEmpty()
	{return size() == 0;}
	
	public int size()
	{return Count;}
	
	public Object[] toArray()
	{
		Object[] ret = new Object[size()];
		int i = 0;
		
		for(T t : this)
			ret[i++] = t;
		
		return ret;
	}
	
	public <E> E[] toArray(E[] a)
	{
		E[] ret = a.length >= size() ? a : (E[])Array.newInstance(a.getClass().getComponentType(),size());
		int i = 0;
		
		for(T t : this)
			ret[i++] = (E)t;
		
		if(ret.length > size())
			ret[size()] = null;
		
		return ret;
	}
	
	@Override public String toString()
	{
		if(isEmpty())
			return "{}";
		
		String ret = "{";
		
		for(T t : this)
			ret += t + ", ";
		
		return ret.substring(0,ret.length() - 2) + "}";
	}
	
	@Override public int hashCode()
	{
		int hash = 1;
		
		for(T t : this)
			hash = 31 * hash + (t == null ? 0 : t.hashCode());
		
		return hash;
	}
	
	/**
	 * The items in the array. 
	 */
	protected T[] Items;
	
	/**
	 * The number of items in the list.
	 */
	protected int Count;
	
	/**
	 * Iterates array lists.
	 * @author Dawn Nye
	 */
	protected class ArrayListIterator implements ListIterator<T>
	{
		/**
		 * Creates an array list iterator.
		 */
		public ArrayListIterator()
		{
			Index = -1;
			CanRemove = false;
			
			return;
		}
		
		/**
		 * Creates an array list iterator.
		 * @param index The index to start at.
		 */
		public ArrayListIterator(int index)
		{
			Index = index;
			CanRemove = false;
			
			return;
		}
		
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			CanRemove = true;
			WasNext = true;
			
			return Items[++Index];
		}
		
		public T previous()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			CanRemove = true;
			WasNext = false;
			
			return Items[Index--];
		}
		
		public boolean hasNext()
		{return Index < size() - 1;}
		
		public boolean hasPrevious()
		{return Index > -1;}
		
		public int nextIndex()
		{return Index + 1;}
		
		public int previousIndex()
		{return Index;}
		
		public void add(T e)
		{
			Count++;
			Expand();
			
			int index = size() - 1;
			
			while(--index > Index)
				Items[index + 1] = Items[index];
			
			Items[++Index] = e;
			CanRemove = false;
			
			return;
		}
		
		public void set(T e)
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			Items[WasNext ? Index : Index + 1] = e;
			return;
		}
		
		public void remove()
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			CanRemove = false;
			
			for(int i = WasNext ? Index : Index + 1;i < size();i++)
				Items[i] = Items[i + 1];
			
			Items[--Count] = null;
			return;
		}
		
		/**
		 * The next index.
		 */
		protected int Index;
		
		/**
		 * If true, then we can remove.
		 */
		private boolean CanRemove;
		
		/**
		 * If true, then the last call to prev/next was a next.
		 * If false, then the last call to prev/next was a prev.
		 */
		private boolean WasNext;
	}
}