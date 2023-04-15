package gamecore.datastructures.graphs;

/**
 * The bare bones of an edge of a graph.
 * @author Dawn Nye
 * @param <V> The data type stored in a vertex.
 * @param <E> The data type to store in an edge.
 */
public class Edge<V,E>
{
	/**
	 * Creates a new edge between {@code src} and {@code dst} with the data {@code data}.
	 * @param src The source vertex for the edge.
	 * @param dst The destination vertex for the edge.
	 * @param data The data stored in the edge.
	 * @param dir This value indicates if this is a directed edge.
	 */
	public Edge(Vertex<V> src, Vertex<V> dst, E data, boolean dir)
	{
		Source = src;
		Destination = dst;
		Data = data;
		Directed = dir;
		
		return;
	}
	
	/**
	 * Creates a dummy edge that will pass an equality test or a hashcode check but contains no data.
	 * @param src The source vertex ID.
	 * @param dst The destination vertex ID.
	 * @param dir This value indicates if this is a directed edge.
	 */
	public Edge(int src, int dst, boolean dir)
	{
		Source = new Vertex<V>(src);
		Destination = new Vertex<V>(dst);
		Data = null;
		Directed = dir;
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj instanceof Edge)
		{
			Edge e = (Edge)obj;
			
			if(Directed) // Directed edges are equal if they have the same source and destination
				return Source.equals(e.Source) && Destination.equals(e.Destination);
			else // Undirected edges are equal if their endpoints match
				return Source.equals(e.Source) && Destination.equals(e.Destination) || Source.equals(e.Destination) && Destination.equals(e.Source);
		}
		
		return false;
	}
	
	@Override public int hashCode()
	{return Destination.hashCode();} // Edges are hashed via their destination only, since edges should be sorted by their source first
	
	@Override public String toString()
	{return "(" + Source.ID + "," + Destination.ID + ")";}
	
	/**
	 * The source vertex of this edge.
	 */
	public final Vertex<V> Source;
	
	/**
	 * The destination vertex of this edge.
	 */
	public final Vertex<V> Destination;
	
	/**
	 * If true, this is a directed edge.
	 * If false, this is an undirected edge.
	 */
	public boolean Directed;
	
	/**
	 * The data stored in this edge.
	 */
	public E Data;
}