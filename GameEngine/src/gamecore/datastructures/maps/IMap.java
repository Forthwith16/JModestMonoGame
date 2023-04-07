package gamecore.datastructures.maps;

import java.util.NoSuchElementException;

import gamecore.datastructures.tuples.KeyValuePair;

/**
 * Describes the bare basics of what it means to be a map.
 * This is, in essence, a partial function between type {@code K} and type {@code V}.
 * For example, a map from Integers to Doubles could have entries for 1-5 and 7 mapping to f(x) = x + 0.5.
 * @author Dawn Nye
 * @param <K> The key or input values of the map.
 * @param <V> The value or output values of the map.
 */
public interface IMap<K,V> extends Iterable<KeyValuePair<K,V>>
{
	/**
	 * Adds a key-value pair to the map if the key does not already exist.
	 * @param key The key to add.
	 * @param value The value to map {@code key} to.
	 * @return Returns true if the key-value pair could be added and false otherwise.
	 * @throws NullPointerException Thrown if {@code key} is null and the map does not permit null keys.
	 */
	public boolean Add(K key, V value);
	
	/**
	 * Puts a key-value pair into the map.
	 * If {@code key} is already in the map, then its mapping is overwritten with this new mapping.
	 * @param key The key to add.
	 * @param value The value to map key to.
	 * @return Returns the old value associated with {@code key} if any exists or {@code value} otherwise.
	 * @throws NullPointerException Thrown if {@code key} is null and the map does not permit null keys.
	 */
	public V Put(K key, V value);
	
	/**
	 * Removes the map entry with key {@code key} if it exists.
	 * @param key The key to remove.
	 * @return Returns true if the key was removed and false otherwise.
	 */
	public boolean RemoveByKey(K key);
	
	/**
	 * Gets the value associated with {@code key}.
	 * @param key The key to search for.
	 * @return Returns the value associated with {@code key}.
	 * @throws NoSuchElementException Thrown if the key is not in the map.
	 */
	public V Get(K key);
	
	/**
	 * Gets the key-value pair associated with {@code key}.
	 * @param key The key to search for.
	 * @return Returns the key-value pair associated with {@code key} or null if it does not exist.
	 */
	public KeyValuePair<K,V> TryGet(K key);
	
	/**
	 * Determines if the map contains the key {@code k}.
	 * @param k The key to search for.
	 * @return Returns true if the map contains the key and false otherwise.
	 */
	public boolean ContainsKey(K key);
	
	/**
	 * Clears the map of all entries.
	 */
	public void Clear();
	
	/**
	 * Determines the number of entries in the map.
	 */
	public int Count();
	
	/**
	 * Determines if the map is empty.
	 */
	public default boolean IsEmpty()
	{return Count() == 0;}
	
	/**
	 * Obtains the keys in the map.
	 * @return Returns the keys in the map. They are guaranteed to appear in the same order as their values as obtained from {@code Values}.
	 */
	public Iterable<K> Keys();
	
	/**
	 * Obtains the values in the map.
	 * @return Returns the values in the map. They are guaranteed to appear in the same order as their keys as obtained from {@code Keys}.
	 */
	public Iterable<V> Values();
}
