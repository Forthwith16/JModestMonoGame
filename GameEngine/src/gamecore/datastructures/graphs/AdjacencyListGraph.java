package gamecore.datastructures.graphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.LINQ.LINQ;
import gamecore.datastructures.LinkedList;
import gamecore.datastructures.graphs.exceptions.NoSuchEdgeException;
import gamecore.datastructures.graphs.exceptions.NoSuchVertexException;
import gamecore.datastructures.queues.Stack;
import gamecore.datastructures.vectors.Vector2i;

/**
 * An adjacency list backed graph.
 * It is backed with an ArrayList of LinkedLists.
 * @author Dawn Nye
 * @param <V> The data type to store in vertices.
 * @param <E> The data type to store in edges.
 */
public class AdjacencyListGraph<V,E> implements IGraph<V,E>
{
	/**
	 * Creates an empty graph.
	 * @param dir If true, this will be a directed graph. If false, this will be an undirected graph.
	 */
	public AdjacencyListGraph(boolean dir)
	{
		Vertices = new ArrayList<LVertex<V,E>>();
		Directed = dir;
		
		AvailableIDs = new Stack<Integer>();
		AvailableIDs.Push(0); // We need to keep the next available new ID at the bottom of the stack at all times
		
		VCount = 0;
		ECount = 0;
		
		return;
	}
	
	public int AddVertex(V vertex)
	{
		int ID = AvailableIDs.Pop();
		VCount++;
		
		// If we have no available ID to consume, push the next available one onto the stack
		if(AvailableIDs.IsEmpty())
		{
			Vertices.add(new LVertex<V,E>(ID,vertex));
			AvailableIDs.Push(VCount);
		}
		else
			Vertices.set(ID,new LVertex<V,E>(ID,vertex));
		
		return ID;
	}
	
	public V SetVertex(int vertex, V data)
	{
		VerifyVertex(vertex);
		
		V ret = Vertices.get(vertex).Data;
		Vertices.get(vertex).Data = data;
		
		return ret;
	}
	
	public V GetVertex(int vertex)
	{
		VerifyVertex(vertex);
		return Vertices.get(vertex).Data;
	}
	
	public boolean RemoveVertex(int vertex)
	{
		if(!ContainsVertex(vertex))
			return false;
		
		AvailableIDs.Push(vertex);
		
		// We decrease the edge count by the number of outgoing edges and the number of incoming edges
		ECount -= Vertices.get(vertex).OutEdges.size();
		
		for(Edge<V,E> e : Vertices.get(vertex).OutEdges)
			Vertices.get(e.Destination.ID).InEdges.remove(e);
		
		// We actually get to do 'less work' if we're an undirected graph since each incoming edge is also an outgoing edge
		if(IsDirected())
		{
			ECount -= Vertices.get(vertex).InEdges.size();
			
			for(Edge<V,E> e : Vertices.get(vertex).InEdges)
				Vertices.get(e.Source.ID).OutEdges.remove(e);
		}
		
		Vertices.set(vertex,null);
		VCount--;
		
		return true;
	}
	
	public boolean ContainsVertex(int vertex)
	{return VerifyVertexValid(vertex);}
	
	public boolean AddEdge(int src, int dst, E data)
	{
		if(!ContainsVertex(src) || !ContainsVertex(dst) || ContainsEdge(src,dst))
			return false;
		
		SecretPutEdge(src,dst,data);
		return true;
	}
	
	public E PutEdge(int src, int dst, E data)
	{
		VerifyVertex(src);
		VerifyVertex(dst);
		
		for(Edge<V,E> e : Vertices.get(src).OutEdges)
			if(e.Destination.ID == dst)
			{
				E ret = e.Data;
				e.Data = data;
				
				return ret;
			}
		
		SecretPutEdge(src,dst,data);
		return data;
	}
	
	/**
	 * Puts an edge into the graph without error checking.
	 * @param src The ID of the source vertex.
	 * @param dst The ID of the destination vertex.
	 * @param data The data to put in the edge.
	 */
	protected void SecretPutEdge(int src, int dst, E data)
	{
		Edge<V,E> e;
		
		Vertices.get(src).OutEdges.add(e = new Edge<V,E>(Vertices.get(src),Vertices.get(dst),data,IsDirected()));
		Vertices.get(dst).InEdges.add(e); // We add the same edge to both vertices in an undirected graph so that changing one changes both
		
		if(IsUndirected())
		{
			Vertices.get(src).InEdges.add(e = new Edge<V,E>(Vertices.get(dst),Vertices.get(src),data,IsDirected()));
			Vertices.get(dst).OutEdges.add(e);
		}
		
		ECount++;
		return;
	}
	
	public E SetEdge(int src, int dst, E data)
	{
		Edge<V,E> e = FetchEdge(src,dst);
		
		E ret = e.Data;
		e.Data = data;
		
		// The edges are the same object in an undirected graph in either direction, so changing one will change both
		if(IsUndirected())
			FetchEdge(dst,src).Data = data;
		
		return ret;
	}
	
	public E GetEdge(int src, int dst)
	{return FetchEdge(src,dst).Data;}
	
	/**
	 * Fetches the raw edge ({@code src},{@code dst}) in the graph.
	 * @param src The source vertex ID of the edge. In an undirected graph, this and {@code dst} may be interchanged.
	 * @param dst The destination vertex of the edge. In an undirected graph, this and {@code src} may be interchanged.
	 * @return Returns the raw edge requested.
	 * @throws IllegalArgumentException Thrown if {@code src} or {@code dst} is negative.
	 * @throws NoSuchVertexException Thrown if the graph is missing a vertex with the ID {@code src} or {@code dst}.
	 * @throws NoSuchEdgeException Thrown if the graph does not contain the specified edge.
	 */
	protected Edge<V,E> FetchEdge(int src, int dst)
	{
		VerifyVertex(src);
		VerifyVertex(dst);
		
		for(Edge<V,E> e : Vertices.get(src).OutEdges)
			if(e.Destination.ID == dst)
				return e;
		
		throw new NoSuchEdgeException(src,dst);
	}
	
	public boolean RemoveEdge(int src, int dst)
	{
		if(!ContainsVertex(src) || !ContainsVertex(dst))
			return false;
		
		boolean ret = Vertices.get(src).OutEdges.remove(new Edge<V,E>(src,dst,IsDirected()));
		
		// If we removed the outgoing edge, we need to remove an ingoing edge too
		if(ret)
		{
			Vertices.get(dst).InEdges.remove(new Edge<V,E>(src,dst,IsDirected()));
			
			// We store undirected graphs with their edge in both adjacency lists
			// If we succeeded in finding one edge in an undirected graph, we need to remove the other
			if(IsUndirected())
			{
				Vertices.get(dst).OutEdges.remove(new Edge<V,E>(dst,src,IsDirected()));
				Vertices.get(src).InEdges.remove(new Edge<V,E>(dst,src,IsDirected()));
			}
			
			ECount--;
		}
		
		return ret;
	}
	
	public boolean ContainsEdge(int src, int dst)
	{return ContainsVertex(src) && ContainsVertex(dst) && Vertices.get(src).OutEdges.contains(new Edge<V,E>(src,dst,IsDirected()));}
	
	public Iterable<Integer> Neighbors(int vertex)
	{return LINQ.Select(OutboundEdges(vertex),e -> e.Y);}
	
	public Iterable<Vector2i> OutboundEdges(int vertex)
	{
		VerifyVertex(vertex);
		return LINQ.ReadOnly(LINQ.Select(Vertices.get(vertex).OutEdges,e -> new Vector2i(e.Source.ID,e.Destination.ID)));
	}
	
	public Iterable<Vector2i> InboundEdges(int vertex)
	{
		VerifyVertex(vertex);
		return LINQ.ReadOnly(LINQ.Select(Vertices.get(vertex).InEdges,e -> new Vector2i(e.Source.ID,e.Destination.ID)));
	}
	
	/**
	 * Returns an iterable list of all currently utilized vertex IDs in the graph.
	 */
	public Iterable<Integer> VertexIDs()
	{return LINQ.Select(LINQ.Where(Vertices,v -> v != null),v -> v.ID);}
	
	/**
	 * Returns an iterable list of all edges in the graph.
	 */
	public Iterable<V> Vertices()
	{return LINQ.Select(LINQ.Where(Vertices,v -> v != null),v -> v.Data);}
	
	/**
	 * Returns an iterable list of all edges in the graph.
	 */
	public Iterable<Vector2i> Edges()
	{
		return new Iterable<Vector2i>()
		{
			public Iterator<Vector2i> iterator()
			{
				return new Iterator<Vector2i>()
				{
					public boolean hasNext()
					{
						if(Done)
							return false;
						
						if(Next != null)
							return true;
						
						while(Next == null)
						{
							while(Iter == null) // We don't need a !Iter.hasNext() check because we'll just fall through everything and assign Iter = null
							{
								if(++Index >= Vertices.size())
								{
									Done = true;
									return false;
								}
								
								if(Vertices.get(Index) != null)
								{
									Iter = Vertices.get(Index).OutEdges.iterator();
									SelfLooped = false;
								}
							}
							
							while(Iter.hasNext() && Next == null)
							{
								Next = Iter.next();
								
								// All directed edges are good edges
								if(IsUndirected())
								{
									// Since undirected graphs double store edges, we'll only process edges whose first vertex is at most (in case of self-loops) the vertex itself
									if(Next.Source.ID > Next.Destination.ID)
										Next = null;
									else if(Next.Source.ID == Next.Destination.ID)
										if(SelfLooped) // We will output at most one self-loop (that gets double stored in undirected graphs)
											Next = null;
										else
											SelfLooped = true;
								}
							}
							
							// If we didn't find something, then we must have a bad iterator
							if(Next == null)
								Iter = null;
						}
						
						return true;
					}
					
					public Vector2i next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						Edge<V,E> ret = Next;
						Next = null;
						
						return new Vector2i(ret.Source.ID,ret.Destination.ID);
					}
					
					protected int Index = -1;
					protected Edge<V,E> Next = null;
					protected boolean Done = false;
					protected Iterator<Edge<V,E>> Iter = null;
					protected boolean SelfLooped = false;
				};
			}
		};
	}
	
	public void Clear()
	{
		Vertices = new ArrayList<LVertex<V,E>>();
		
		AvailableIDs = new Stack<Integer>();
		AvailableIDs.Push(0); // We need to keep the next available new ID at the bottom of the stack at all times
		
		VCount = 0;
		ECount = 0;
		
		return;
	}
	
	public int VertexCount()
	{return VCount;}
	
	public int EdgeCount()
	{return ECount;}
	
	public int OutDegree(int vertex)
	{
		VerifyVertex(vertex);
		return Vertices.get(vertex).OutEdges.size();
	}
	
	public int InDegree(int vertex)
	{
		VerifyVertex(vertex);
		return Vertices.get(vertex).InEdges.size();
	}
	
	public boolean IsDirected()
	{return Directed;}
	
	public void MakeDirected()
	{
		Directed = true; // The undirected version has all of the edges we need already, so we don't have to do anything
		return;
	}
	
	public void MakeUndirected(boolean fill)
	{
		if(IsUndirected())
			return;
		
		for(Vector2i v : Edges())
			if(!ContainsEdge(v.Y,v.X))
				if(fill)
					AddEdge(v.Y,v.X,GetEdge(v.X,v.Y));
				else
					RemoveEdge(v.X,v.Y);
		
		Directed = true;
		return;
	}
	
	/**
	 * Error checks a vertex parameter.
	 * @param vertex The proposed vertex ID.
	 * @return Returns true if the vertex is valid and false otherwise.
	 */
	protected boolean VerifyVertexValid(int vertex)
	{
		if(vertex < 0)
			return false;
		
		if(vertex >= VCount || Vertices.get(vertex) == null)
			return false;
		
		return true;
	}
	
	/**
	 * Error checks a vertex parameter.
	 * @param vertex The proposed vertex ID.
	 * @throws IllegalArgumentException Thrown if {@code vertex} is negative.
	 * @throws NoSuchVertexException Thrown if no vertex with the ID {@code vertex} exists in the graph.
	 */
	protected void VerifyVertex(int vertex)
	{
		if(vertex < 0)
			throw new IllegalArgumentException();
		
		if(vertex >= VCount || Vertices.get(vertex) == null)
			throw new NoSuchVertexException(vertex);
		
		return;
	}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(int i = 0;i < Vertices.size();i++)
			if(Vertices.get(i) != null)
				ret += Vertices.get(i) + (i != Vertices.size() - 1 ? "\n-----\n" : "");
		
		return ret;
	}
	
	/**
	 * The vertices of the graph.
	 */
	protected ArrayList<LVertex<V,E>> Vertices;
	
	/**
	 * The number of vertices in the graph.
	 */
	protected int VCount;
	
	/**
	 * The set of available IDs for vertices.
	 * This will either contain ID gaps in the graph or the next available ID if it would otherwise be empty.
	 * ID gaps are always consumed before new IDs are utilized.
	 */
	protected Stack<Integer> AvailableIDs;
	
	/**
	 * The number of edges in the graph.
	 */
	protected int ECount;
	
	/**
	 * If true, this is a directed graph.
	 * If false, this is an undirected graph.
	 */
	protected boolean Directed;
	
	/**
	 * A vertex with a linked list reference to its outgoing edges.
	 * @author Dawn Nye
	 * @param <V> The data type to store in the vertex.
	 */
	protected static class LVertex<V,E> extends Vertex<V>
	{
		/**
		 * Creates an empty vertex with the provided id.
		 * @param id The vertex ID.
		 * @throws IllegalArgumentException Thrown if {@code id} is negative.
		 */
		public LVertex(int id)
		{
			super(id);
			
			OutEdges = new LinkedList<Edge<V,E>>();
			InEdges = new LinkedList<Edge<V,E>>();
			
			return;
		}
		
		/**
		 * Creates a new vertex with the given data.
		 * @param id The vertex ID.
		 * @param data The data to store in this vertex.
		 * @throws IllegalArgumentException Thrown if {@code id} is negative.
		 */
		public LVertex(int id, V data)
		{
			super(id,data);
			
			OutEdges = new LinkedList<Edge<V,E>>();
			InEdges = new LinkedList<Edge<V,E>>();
			
			return;
		}
		
		@Override public String toString()
		{
			String ret = super.toString() + "\nOutbound Edges: ";
			
			for(Edge<V,E> e : OutEdges)
				ret += e + " ";
			
			ret = ret.substring(0,ret.length() - 1) + "\nInbound Edges: ";
			
			for(Edge<V,E> e : InEdges)
				ret += e + " ";
			
			return ret.substring(0,ret.length() - 1);
		}
		
		/**
		 * The outgoing edges of this vertex.
		 */
		protected LinkedList<Edge<V,E>> OutEdges;
		
		/**
		 * The incoming edges of this vertex.
		 */
		protected LinkedList<Edge<V,E>> InEdges;
	}
}