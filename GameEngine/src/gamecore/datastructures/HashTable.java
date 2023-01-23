package gamecore.datastructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Stores values in a hash table.
 * Collisions are resolved with chaining.
 * Duplicate values are allowed, but null entries are prohibited.
 * @author Dawn Nye
 * @param <T> The type of values to store in the table.
 */
public class HashTable<T> implements Collection<T>
{
	/**
	 * Creates an empty hash table.
	 */
	public HashTable()
	{
		this(null,16,0.75,false);
		return;
	}
	
	/**
	 * Creates a hash table.
	 * @param seed The initial population of the hash table.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	public HashTable(Iterable<? extends T> seed)
	{
		this(seed,16,0.75,true);
		return;
	}
	
	/**
	 * Creates an empty hash table.
	 * @param c The initial capacity.
	 */
	public HashTable(int c)
	{
		this(null,c,0.75,false);
		return;
	}
	
	/**
	 * Creates a hash table.
	 * @param seed The initial population of the hash table.
	 * @param c The initial capacity.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	public HashTable(Iterable<? extends T> seed, int c)
	{
		this(seed,c,0.75,true);
		return;
	}
	
	/**
	 * Creates an empty hash table.
	 * @param c The initial capacity.
	 * @param l The load factor. This value must be at least 0.55.
	 */
	public HashTable(int c, double l)
	{
		this(null,c,l,false);
		return;
	}
	
	/**
	 * Creates a hash table.
	 * @param seed The initial population of the hash table.
	 * @param c The initial capacity.
	 * @param l The load factor. This value must be at least 0.55.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	public HashTable(Iterable<? extends T> seed, int c, double l)
	{
		this(seed,c,l,true);
		return;
	}
	
	/**
	 * Creates a hash table.
	 * @param seed The initial population of the hash table.
	 * @param c The initial capacity.
	 * @param l The load factor. This value must be at least 0.55. 
	 * @param not_null Determines if there is supposed to be a non null value for {@code seed}.
	 * @throws NullPointerException Thrown if {@code seed} is null and {@code null_check} is false.
	 */
	protected HashTable(Iterable<? extends T> seed, int c, double l, boolean not_null)
	{
		Table = (LinkedList<T>[])new LinkedList[InitialCapacity = c];
		
		Count = 0;
		LoadFactor = Math.max(0.55,l);
		
		return;
	}
	
	/**
	 * Doubles or halves the hash table's capacity if necessary and rehashes every entry.
	 * If no change is needed, nothing is done.
	 */
	protected void Rehash()
	{
		boolean expand = false;
		
		if(Load() >= LoadFactor)
			expand = true;
		else if(Capacity() <= InitialCapacity || Load() > 0.25)
			return;
		
		LinkedList<T>[] temp = (LinkedList<T>[])new LinkedList[expand ? (Table.length << 1) : (Table.length >> 1)];
		
		for(T t : this)
			SecretPut(temp,t);
		
		Table = temp;
		return;
	}
	
	/**
	 * Places an item into a hash table.
	 * @param l The hash table.
	 * @param t The item to place.
	 * @throws NullPointerException Thrown if either {@code l} or {@code t} are null.
	 */
	protected void SecretPut(LinkedList<T>[] l, T t)
	{
		int hash = t.hashCode() % l.length;
		
		while(hash < 0)
			hash += l.length;
		
		if(l[hash] == null)
			l[hash] = new LinkedList<T>();
		
		l[hash].AddLast(t);
		return;
	}
	
	public boolean add(T t)
	{
		SecretPut(Table,t);
		Count++;
		
		Rehash();
		return true;
	}
	
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(add(t))
				ret = true;
		
		return ret;
	}
	
	/**
	 * Gets the entry {@code key} if it exists in this hash table.
	 * This is useful for when entries hold more data than are necessary to be equal, such as for a map of keys to values.
	 * @param key The entry to look for.
	 * @return Returns the entry {@code key} or null if no such entry exists.
	 * @throws NullPointerException Thrown if {@code key} is null.
	 */
	public T get(T key)
	{
		int hash = key.hashCode() % Capacity();
		
		while(hash < 0)
			hash += Capacity();
		
		if(Table[hash] == null)
			return null;
		
		Iterator<T> Iter = Table[hash].iterator();
		
		while(Iter.hasNext())
		{
			T temp = Iter.next();
			
			if(key.equals(temp))
				return temp;
		}
		
		return null;
	}
	
	public boolean remove(Object o)
	{
		int hash = o.hashCode() % Capacity();
		
		while(hash < 0)
			hash += Capacity();
		
		if(Table[hash] == null)
			return false;
		
		boolean ret = Table[hash].remove(o);
		
		if(ret)
		{
			Count--;
			
			if(Table[hash].isEmpty())
				Table[hash] = null;
		}
		
		Rehash();
		return ret;
	}
	
	public boolean removeAll(Collection<?> c)
	{
		boolean ret = false;
		
		for(Object obj : c)
			while(remove(obj))
				ret = true;
		
		return ret;
	}
	
	public boolean retainAll(Collection<?> c)
	{
		boolean ret = false;
		
		for(int i = 0;i < Table.length;i++)
			if(Table[i] != null)
			{
				Iterator<T> iter = Table[i].iterator();
				
				while(iter.hasNext())
					if(!c.contains(iter.next()))
						iter.remove();
				
				if(Table[i].isEmpty())
					Table[i] = null;
			}
		
		return ret;
	}
	
	public boolean contains(Object o)
	{
		for(int i = 0;i < Table.length;i++)
			if(Table[i] != null)
				if(Table[i].contains(o))
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
		Table = (LinkedList<T>[])new LinkedList[InitialCapacity];
		Count = 0;
		
		return;
	}
	
	public int size()
	{return Count;}
	
	public boolean isEmpty()
	{return size() == 0;}
	
	public Iterator<T> iterator()
	{return new HashTableEntryIterator();}
	
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
		String ret = "";
		
		for(int i = 0;i < Table.length;i++)
			ret += i + " -> " + (Table[i] == null ? "<no entries>" : Table[i].toString()) + "\n";
		
		return ret.substring(0,ret.length() - 1);
	}
	
	/**
	 * Determines the current load of the hash table.
	 * @return Returns the value of Count / Capacity.
	 */
	protected double Load()
	{return size() / (double)Capacity();}
	
	/**
	 * Determines the maximum capacity of the hash table.
	 * @return Return the capacity of the hash table.
	 */
	protected int Capacity()
	{return Table.length;}
	
	/**
	 * The backing data structure for the hash table.
	 */
	protected LinkedList<T>[] Table;
	
	/**
	 * The number of values in the hash table.
	 */
	protected int Count;
	
	/**
	 * The initial capacity of the hash table.
	 */
	protected int InitialCapacity;
	
	/**
	 * The maximum Count/Capacity ratio allowed before a rehash is required.
	 */
	protected double LoadFactor;
	
	/**
	 * Iterates over a hash table's entries.
	 * @author Dawn Nye
	 */
	protected class HashTableEntryIterator implements Iterator<T>
	{
		/**
		 * Creates a new hash table entry iterator.
		 */
		public HashTableEntryIterator()
		{
			TableIndex = -1;
			Iter = null;
			
			Index = -1;
			return;
		}
		
		public boolean hasNext()
		{return Index + 1 < size();}

		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			while(Iter == null || !Iter.hasNext())
				if(Table[++TableIndex] != null)
					Iter = Table[TableIndex].iterator();
			
			Index++;
			return Iter.next();
		}
		
		/**
		 * The current index of the iterator.
		 */
		protected int Index;
		
		/**
		 * The current table index.
		 */
		protected int TableIndex;
		
		/**
		 * The current iterator.
		 */
		protected Iterator<T> Iter;
	}
}
