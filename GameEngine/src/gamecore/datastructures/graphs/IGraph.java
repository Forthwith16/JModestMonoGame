package gamecore.datastructures.graphs;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.HashTable;
import gamecore.datastructures.graphs.exceptions.NoSuchEdgeException;
import gamecore.datastructures.graphs.exceptions.NoSuchVertexException;
import gamecore.datastructures.queues.IQueue;
import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.vectors.Vector2i;

/**
 * The bare bones that all graphs have in common.
 * @author Dawn Nye
 * @param <V> The data type stored in a vertex. Each of these values must be distinct.
 * @param <E> The data type stored in an edge.
 */
public interface IGraph<V,E>
{
	/**
	 * Adds a new vertex to the graph.
	 * @param vertex The vertex data to add.
	 * @return Returns the ID of the vertex added or -1 if no vertex was added.
	 */
	public int AddVertex(V vertex);
	
	/**
	 * Sets the data of the vertex with ID {@code vertex} in the graph to {@code data}.
	 * @param vertex The vertex ID of the vertex whose data we wish to change.
	 * @param data The data to put into the vertex.
	 * @return Returns the old data stored in the vertex with ID {@code vertex}.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public V SetVertex(int vertex, V data);
	
	/**
	 * Obtains the vertex data specified by {@code vertex}.
	 * @param vertex The vertex data to obtain.
	 * @return Gets the data in the vertex matching the vertex ID {@code vertex} stored in this graph.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public V GetVertex(int vertex);
	
	/**
	 * Remove the vertex with ID {@code vertex}.
	 * This also removes any edges for which the vertex is an endpoint.
	 * @param vertex The ID of the vertex to remove.
	 * @return Returns true if a vertex was removed and false otherwise.
	 */
	public boolean RemoveVertex(int vertex);
	
	/**
	 * Determines if a vertex with ID {@code vertex} is in the graph.
	 * @param vertex The vertex ID to check for.
	 * @return Returns true if a vertex with the ID {@code vertex} belongs to the graph and false otherwise.
	 */
	public boolean ContainsVertex(int vertex);
	
	/**
	 * Adds the edge ({@code src},{@code dst}) to the graph with data {@code data}.
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @param data The data to put in the edge.
	 * @return Returns true if the edge was added and false otherwise.
	 */
	public boolean AddEdge(int src, int dst, E data);
	
	/**
	 * Adds the edge ({@code src},{@code dst}) to the graph with data {@code data}.
	 * If the edge already belongs to the graph, it replaces the edge's data with {@code data} and returns the old data.
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @param data The data to put in the edge.
	 * @return Returns {@code data} if the edge did not already exist in the graph. If the edge did exist in the graph prior to this call, then the old data stored in that edge is returned.
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if the graph is missing a vertex with the ID {@code src} or {@code dst}.
	 */
	public E PutEdge(int src, int dst, E data);
	
	/**
	 * Obtains the edge data in edge ({@code src},{@code dst}).
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @param data The data to put in the edge.
	 * @return Returns the old edge data belonging to the edge ({@code src},{@code dst}).
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if the graph is missing a vertex with the ID {@code src} or {@code dst}.
	 * @throws NoSuchEdgeException Thrown if the graph does not contain the specified edge.
	 */
	public E SetEdge(int src, int dst, E data);
	
	/**
	 * Obtains the edge data in edge ({@code src},{@code dst}).
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @return Returns the edge data belonging to the edge ({@code src},{@code dst}) stored in this graph.
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if the graph is missing a vertex with the ID {@code src} or {@code dst}.
	 * @throws NoSuchEdgeException Thrown if the graph does not contain the specified edge.
	 */
	public E GetEdge(int src, int dst);
	
	/**
	 * Removes the edge ({@code src},{@code dst}).
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @return Returns true if the edge was removed and false otherwise.
	 */
	public boolean RemoveEdge(int src, int dst);
	
	/**
	 * Determines if the edge ({@code src},{@code dst}) is present in the graph.
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @return Returns true if the edge belongs to the graph and false otherwise.
	 */
	public boolean ContainsEdge(int src, int dst);
	
	/**
	 * Returns an iterable list of all neighbors of the vertex with ID {@code vertex}.
	 * The neighbors will be given as their IDs.
	 * @param vertex The ID of the vertex whose neighbors we want to obtain.
	 * @return Returns an iterable list of neighbors of the vertex with ID {@code vertex}.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public Iterable<Integer> Neighbors(int vertex);
	
	/**
	 * Returns the iterable list of all edges leaving the vertex with ID {@code vertex}.
	 * The format of the returned value is (SourceID,DestinationID).
	 * @param vertex The ID of the vertex whose outbound edges we want to obtain.
	 * @return Returns an iterable list of edges leaving the vertex with ID {@code vertex}.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public Iterable<Vector2i> OutboundEdges(int vertex);
	
	/**
	 * Returns the iterable list of all edges entering the vertex with ID {@code vertex}.
	 * The format of the returned value is (SourceID,DestinationID).
	 * @param vertex The ID of the vertex whose inbound edges we want.
	 * @return Returns an iterable list of inbound edges entering the vertex with ID {@code vertex}.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public Iterable<Vector2i> InboundEdges(int vertex);
	
	/**
	 * Gets all of the vertex IDs of vertices reachable from {@code vertex}.
	 * @param vertex The vertex to obtain all reachable vertex IDs of.
	 * @return Returns an iterable list of all reachable vertex IDs from {@code vertex}.
	 */
	public default Iterable<Integer> ComponentVertexIDs(int vertex)
	{
		return new Iterable<Integer>()
		{
			public Iterator<Integer> iterator()
			{
				return new Iterator<Integer>()
				{
					@Override public boolean hasNext()
					{
						if(Q == null)
						{
							Visited = new HashTable<Integer>();
							
							Q = new Queue<Integer>();
							Q.Add(vertex);
							
							return true;
						}
						
						while(!Q.IsEmpty() && Visited.Contains(Q.Front()))
							Q.Dequeue();
						
						return !Q.IsEmpty();
					}
					
					@Override public Integer next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						Integer ret = Q.Dequeue();
						Visited.add(ret);
						
						for(Integer i : Neighbors(ret))
							if(!Visited.Contains(i))
								Q.Add(i);
						
						return ret;
					}
					
					private IQueue<Integer> Q;
					private HashTable<Integer> Visited;
				};
			}
		};
	}
	
	/**
	 * Returns an iterable list of all currently utilized vertex IDs in the graph.
	 */
	public Iterable<Integer> VertexIDs();
	
	/**
	 * Returns an iterable list of all vertex data in the graph.
	 */
	public Iterable<V> Vertices();
	
	/**
	 * Returns an iterable list of all edges in the graph.
	 * The format of the returned value is (SourceID,DestinationID).
	 */
	public Iterable<Vector2i> Edges();
	
	/**
	 * Removes all vertices and edges from the graph.
	 */
	public void Clear();
	
	/**
	 * Determines the number of vertices in this graph.
	 */
	public int VertexCount();
	
	/**
	 * Determines the number of edges in this graph.
	 */
	public int EdgeCount();
	
	/**
	 * Determines the out-degree of the vertex with ID {@code vertex}.
	 * @param vertex The ID of the vertex to count the outgoing edges of.
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public int OutDegree(int vertex);
	
	/**
	 * Determines the in-degree of the vertex with ID {@code vertex}.
	 * @param vertex The ID of the vertex to count the incoming edges of.
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	public int InDegree(int vertex);
	
	/**
	 * Determines if this is a directed graph.
	 * @return Returns true if the graph is directed and false otherwise.
	 */
	public boolean IsDirected();
	
	/**
	 * Makes this graph into a directed graph.
	 */
	public void MakeDirected();
	
	/**
	 * Makes this graph into an undirected graph.
	 * @param fill If true, then unpaired directed edges will be retained as undirected edges. If false, unpaired directed edges will be removed.
	 */
	public void MakeUndirected(boolean fill);
	
	/**
	 * Determines if this is an undirected graph.
	 * @return Returns true if the graph is undirected and false otherwise.
	 */
	public default boolean IsUndirected()
	{return !IsDirected();}
}