package gamecore.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.tuples.KeyValuePair;

/**
 * A dictionary class.
 * Maps keys to values.
 * Neither duplicate nor null keys are allowed, but null and duplicate values are acceptable.
 * @author Dawn Nye
 * @param <K> The key type.
 * @param <V> The value type.
 */
public class Dictionary<K,V> implements Iterable<KeyValuePair<K,V>>
{
	/**
	 * Creates an empty dictionary.
	 */
	public Dictionary()
	{
		Entries = new HashTable<KeyValuePair<K,V>>();
		return;
	}
	
	/**
	 * Creates a dictionary.
	 * @param seed The initial values to place in the dictionary. Null entries, null keys, and duplicate keys will be excised from it before populating the dictionary.
	 * @throws NullPointerException Thrown if {@code seed} is null or any key element of {@code seed} is null.
	 */
	public Dictionary(Iterable<? extends KeyValuePair<K,V>> seed)
	{
		Entries = new HashTable<KeyValuePair<K,V>>();
		AddAll(seed);
		
		return;
	}
	
	/**
	 * Puts a key-value pair into the dictionary.
	 * If {@code key} is already in the dictionary, then its mapping is overwritten with this new mapping.
	 * @param key The key to add.
	 * @param value The value to map key to.
	 * @return Returns the old value associated with {@code key} if any exists or {@code value} otherwise.
	 * @throws NullPointerException Thrown if {@code key} is null.
	 */
	public V Put(K key, V value)
	{
		if(key == null)
			throw new NullPointerException();
		
		KeyValuePair<K,V> ret = TryGet(key);
		
		if(ret == null)
			ret = new KeyValuePair<K,V>(key,value);
		else
			Remove(key);
		
		Entries.add(new KeyValuePair<K,V>(key,value));
		return ret.Item2;
	}
	
	/**
	 * Adds a key-value pair to the dictionary if the key does not already exist.
	 * @param key The key to add.
	 * @param value The value to map {@code key} to.
	 * @return Returns true if the key-value pair could be added and false otherwise.
	 * @throws NullPointerException Thrown if {@code key} is null.
	 */
	public boolean Add(K key, V value)
	{
		if(key == null)
			throw new NullPointerException();
		
		if(ContainsKey(key))
			return false;
		
		return Entries.add(new KeyValuePair<K,V>(key,value));
	}
	
	/**
	 * Adds a group of keys and values to the dictionary.
	 * @param c The key-value pairs to put into the dictionary. Null entries, null keys, and duplicate keys will be excised from it before populating the dictionary.
	 * @return Returns true if at least one item was added to the dictionary.
	 * @throws NullPointerException Thrown if {@code c} is null or any key in {@code c} is null.
	 */
	public boolean AddAll(Iterable<? extends KeyValuePair<K,V>> c)
	{
		LinkedList<KeyValuePair<K,V>> l = new LinkedList<KeyValuePair<K,V>>();
		
		// It's slightly more efficient to work with three smaller data sets to rip out the problem keys first, so we do so
		for(KeyValuePair<K,V> p : c)
			if(p != null && p.Item1 != null && !l.contains(p) && !ContainsKey(p.Item1))
				l.add(p);
		
		return Entries.addAll(l);
	}
	
	/**
	 * Gets the value associated with {@code key}.
	 * @param key The key to search for.
	 * @return Returns the value associated with {@code key}.
	 */
	public V Get(K key)
	{
		KeyValuePair<K,V> ret = Entries.get(new KeyValuePair<K,V>(key,null));
		
		if(ret == null)
			throw new NoSuchElementException();
		
		return ret.Item2;
	}
	
	/**
	 * Gets the key-value pair associated with {@code key}.
	 * @param key The key to search for.
	 * @return Returns the key-value pair associated with {@code key} or null if it does not exist.
	 */
	public KeyValuePair<K,V> TryGet(K key)
	{return Entries.get(new KeyValuePair<K,V>(key,null));}
	
	/**
	 * Removes the dictionary entry with key {@code key} if it exists.
	 * @param key The key to remove.
	 * @return Returns true if the key was removed and false otherwise.
	 */
	public boolean Remove(K key)
	{return Entries.remove(new KeyValuePair<K,V>(key,null));}
	
	/**
	 * Determines if the dictionary contains the key {@code k}.
	 * @param k The key to search for.
	 * @return Returns true if the dictionary contains the key and false otherwise.
	 */
	public boolean ContainsKey(K k)
	{
		if(k == null)
			return false;
		
		return Entries.contains(new KeyValuePair<K,V>(k,null));
	}
	
	/**
	 * Determines if the dictionary contains every key in {@code keys}.
	 * @param keys The keys to search for.
	 * @return Returns true if the dictionary contains every key and false otherwise.
	 * @throws NullPointerException Thrown if {@code keys} is null.
	 */
	public boolean ContainsAllKeys(Iterable<? extends K> keys)
	{
		for(K k : keys)
			if(!ContainsKey(k))
				return false;
		
		return true;
	}
	
	/**
	 * Determines if the dictionary contains any key in {@code keys}.
	 * @param keys The keys to search for.
	 * @return Returns true if the dictionary contains any key and false otherwise.
	 * @throws NullPointerException Thrown if {@code keys} is null.
	 */
	public boolean ContainsAnyKey(Iterable<? extends K> keys)
	{
		for(K k : keys)
			if(ContainsKey(k))
				return true;
		
		return false;
	}
	
	/**
	 * Clears the dictionary of all entries.
	 */
	public void Clear()
	{
		Entries.clear();
		return;
	}
	
	/**
	 * Determines the number of entries in the dictionary.
	 * @return Returns the number of entries in the dictionary.
	 */
	public int Count()
	{return Entries.size();}
	
	/**
	 * Determines if the dictionary is empty.
	 * @return
	 */
	public boolean IsEmpty()
	{return Count() == 0;}
	
	public Iterator<KeyValuePair<K,V>> iterator()
	{return Entries.iterator();}
	
	/**
	 * Obtains the keys in the dictionary.
	 * @return Returns the keys in the dictionary. They are garunteed to appear in the same order as their values as obtained from Values.
	 */
	public Iterable<K> Keys()
	{return new KeyIterable();}
	
	/**
	 * Obtains the values in the dictionary.
	 * @return Returns the values in the dictionary. They are garunteed to appear in the same order as their keys as obtained from Keys.
	 */
	public Iterable<V> Values()
	{return new ValueIterable();}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(KeyValuePair<K,V> p : this)
			ret += p.Item1 + " -> " + p.Item2 + "\n";
		
		return ret.substring(0,ret.length() - 1);
	}
	
	/**
	 * The backing data structure for the dictionary.
	 */
	protected HashTable<KeyValuePair<K,V>> Entries;
	
	/**
	 * Used to set up a key iterator.
	 * @author Dawn Nye
	 */
	protected class KeyIterable implements Iterable<K>
	{
		public KeyIterable()
		{return;}
		
		public Iterator<K> iterator()
		{return new KeyIterator();}
	}
	
	/**
	 * Iterates over the keys of a dictionary.
	 * @author Dawn Nye
	 */
	protected class KeyIterator implements Iterator<K>
	{
		public KeyIterator()
		{
			Iter = iterator();
			return;
		}
		
		public boolean hasNext()
		{return Iter.hasNext();}
		
		public K next()
		{return Iter.next().Item1;}
		
		protected Iterator<KeyValuePair<K,V>> Iter;
	}
	
	/**
	 * Used to set up a value iterator.
	 * @author Dawn Nye
	 */
	protected class ValueIterable implements Iterable<V>
	{
		public ValueIterable()
		{return;}
		
		public Iterator<V> iterator()
		{return new ValueIterator();}
	}
	
	/**
	 * Iterates over the values of a dictionary.
	 * @author Dawn Nye
	 */
	protected class ValueIterator implements Iterator<V>
	{
		public ValueIterator()
		{
			Iter = iterator();
			return;
		}
		
		public boolean hasNext()
		{return Iter.hasNext();}
		
		public V next()
		{return Iter.next().Item2;}
		
		protected Iterator<KeyValuePair<K,V>> Iter;
	}
}