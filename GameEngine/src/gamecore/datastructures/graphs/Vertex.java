package gamecore.datastructures.graphs;

/**
 * The bare bones of a vertex of a graph.
 * @author Dawn Nye
 * @param <V> The data type to store in the vertex.
 */
public class Vertex<V>
{
	/**
	 * Creates an empty vertex with the provided id.
	 * @param id The vertex ID.
	 * @throws IllegalArgumentException Thrown if {@code id} is negative.
	 */
	public Vertex(int id)
	{
		if(id < 0)
			throw new IllegalArgumentException();
		
		ID = id;
		Data = null;
		
		return;
	}
	
	/**
	 * Creates a new vertex with the given data.
	 * @param id The vertex ID.
	 * @param data The data to store in this vertex.
	 * @throws IllegalArgumentException Thrown if {@code id} is negative.
	 */
	public Vertex(int id, V data)
	{
		if(id < 0)
			throw new IllegalArgumentException();
		
		ID = id;
		Data = data;
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj instanceof Vertex)
			return ID == ((Vertex)obj).ID; // Vertices are equal iff their IDs are equal by default
		
		return false;
	}
	
	@Override public int hashCode()
	{return ID;} // Vertices are equal iff their IDs are equal by default, and we pick a hashcode function to match
	
	@Override public String toString()
	{return ID + ": " + Data;}
	
	/**
	 * The vertex ID.
	 */
	public int ID;
	
	/**
	 * The data associated with this vertex.
	 */
	public V Data;
}