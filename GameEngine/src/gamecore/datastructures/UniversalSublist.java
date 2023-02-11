package gamecore.datastructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A universal sublist so that we don't have to implement another of Java's bad ideas.
 * If there are savings (time/space) to be made in the idea of having sublists, the actual data structure (and often even th user) can explicitly implement them itself.
 * Otherwise this is too niche an application that requires too much reworking of the core data structures to bother.
 * On another note, the behavior of a sublist becomes undefined if the original list makes a modification left (inclusive) of the endpoint of the sublist.
 * @author Dawn Nye
 */
public final class UniversalSublist<T> implements List<T>
{
	/**
	 * Creates a sublist from {@code l}.
	 * @param l The list to sublist.
	 * @param from The leftmost (inclusive) index.
	 * @param to The rightmost (exclusive) index.
	 */
	public UniversalSublist(List<T> l, int from, int to)
	{
		TheList = l;
		
		Left = from;
		Right = to;
		
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
	
	public boolean add(T e)
	{
		add(size(),e);
		return true;
	}
	
	public void add(int index, T t)
	{
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		TheList.add(Left + index,t);
		Right++;
		
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
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		boolean ret = false;
		
		for(T t : c)
			add(index++,t);
		
		return true;
	}
	
	public T set(int index, T t)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		return TheList.set(Left + index,t);
	}
	
	public T get(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		return TheList.get(Left + index);
	}
	
	public boolean remove(Object o)
	{
		int index = indexOf(o);
		
		if(index > -1)
			remove(index);
		else
			return false;
		
		return true;
	}
	
	public T remove(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		Right--;
		return TheList.remove(Left + index);
	}
	
	public boolean removeAll(Collection<?> c)
	{
		boolean ret = false;
		
		for(int i = Left;i < Right;i++)
			if(c.contains(TheList.get(i)))
			{
				ret = true;
				remove(i - Left);
			}
		
		return ret;
	}
	
	public boolean retainAll(Collection<?> c)
	{
		boolean ret = false;
		
		for(int i = Left;i < Right;i++)
			if(!c.contains(TheList.get(i)))
			{
				ret = true;
				remove(i - Left);
			}
		
		return ret;
	}
	
	public int indexOf(Object o)
	{
		for(int i = Left;i < Right;i++)
			if(o == null ? TheList.get(i) == null : o.equals(TheList.get(i)))
				return i - Left;
		
		return -1;
	}
	
	public int lastIndexOf(Object o)
	{
		for(int i = Right - 1;i >= Left;i--)
			if(o == null ? TheList.get(i) == null : o.equals(TheList.get(i)))
				return i - Left;
		
		return -1;
	}
	
	public boolean contains(Object o)
	{
		for(int i = Left;i < Right;i++)
			if(o == null ? TheList.get(i) == null : o.equals(TheList.get(i)))
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
		ListIterator<T> iter = TheList.listIterator(Left);
		
		for(int i = 0;i < size();i++)
		{
			iter.next();
			iter.remove();
		}
		
		Right = Left;
		return;
	}
	
	public Iterator<T> iterator()
	{return new UniversalIterator();}
	
	public ListIterator<T> listIterator()
	{return new UniversalIterator();}
	
	public ListIterator<T> listIterator(int index)
	{
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		ListIterator<T> ret = new UniversalIterator();
		
		// We have to iterate over the list anyway, so just do it here
		for(int i = 0;i < index;i++)
			ret.next();
		
		return ret;
	}
	
	public List<T> subList(int from, int to)
	{
		if(from < 0 || to > size() ||from > to)
			throw new IndexOutOfBoundsException();
		
		return new UniversalSublist<T>(this,from,to);
	}
	
	public boolean isEmpty()
	{return size() == 0;}
	
	public int size()
	{return Right - Left;}
	
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
	 * The one true list.
	 */
	private final List<T> TheList;
	
	/**
	 * The leftmost (inclusive) index of this sublist.
	 */
	private final int Left;
	
	/**
	 * The rightmost (exclusive) index of this sublist.
	 */
	private int Right;
	
	/**
	 * Iterates over any list.
	 * @author Dawn Nye
	 */
	private final class UniversalIterator implements ListIterator<T>
	{
		/**
		 * Creates a new universal iterator.
		 */
		public UniversalIterator()
		{
			Iter = TheList.listIterator();
			
			for(int i = 0;i < Left;i++)
				Iter.next();
			
			CanRemove = false;
			return;
		}

		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			CanRemove = true;
			return Iter.next();
		}

		public T previous()
		{
			if(!hasPrevious())
				throw new NoSuchElementException();
			
			CanRemove = true;
			return Iter.previous();
		}

		public int nextIndex()
		{return Iter.nextIndex() - Left;}

		public int previousIndex()
		{return Iter.previousIndex() - Left;}

		public boolean hasNext()
		{return Iter.nextIndex() < Right;}

		public boolean hasPrevious()
		{return Iter.previousIndex() >= Left;}

		public void add(T t)
		{
			Iter.add(t);
			Right++;
			
			CanRemove = false;
			return;
		}

		public void set(T t)
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			Iter.set(t);
			return;
		}

		public void remove()
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			Iter.remove();
			Right--;
			
			CanRemove = false;
			return;
		}
		
		/**
		 * The iterator to reference.
		 */
		private ListIterator<T> Iter;
		
		/**
		 * If true, then we can add or remove.
		 */
		private boolean CanRemove;
	}
}
