package gamecore.datastructures.graphs;

import gamecore.LINQ.LINQ;
import gamecore.datastructures.graphs.exceptions.NoSuchEdgeException;
import gamecore.datastructures.graphs.exceptions.NoSuchVertexException;
import gamecore.datastructures.grids.VariableSizeGrid;
import gamecore.datastructures.maps.Dictionary;
import gamecore.datastructures.queues.Stack;
import gamecore.datastructures.vectors.Vector2i;

public class AdjacencyMatrixGraph<V,E> implements IGraph<V,E>
{
	/**
	 * Creates an empty graph.
	 * @param dir If true, this will be a directed graph. If false, this will be an undirected graph.
	 */
	public AdjacencyMatrixGraph(boolean dir)
	{
		Vertices = new Dictionary<Integer,Vertex<V>>();
		Edges = new VariableSizeGrid<Edge<V,E>>();
		Directed = dir;
		
		AvailableIDs = new Stack<Integer>();
		AvailableIDs.Push(0); // We need to keep the next available new ID at the bottom of the stack at all times
		
		ECount = 0;
		SCount = 0;
		
		return;
	}
	
	public int AddVertex(V vertex)
	{
		int ID = AvailableIDs.Pop();
		
		// If we have no available ID to consume, push the next available one onto the stack
		if(AvailableIDs.IsEmpty())
		{
			Vertices.Add(ID,new Vertex<V>(ID,vertex));
			AvailableIDs.Push(VertexCount());
		}
		else
			Vertices.Put(ID,new Vertex<V>(ID,vertex));
		
		return ID;
	}
	
	public V SetVertex(int vertex, V data)
	{
		VerifyVertex(vertex);
		
		V ret = Vertices.Get(vertex).Data;
		Vertices.Get(vertex).Data = data;
		
		return ret;
	}
	
	public V GetVertex(int vertex)
	{
		VerifyVertex(vertex);
		return Vertices.Get(vertex).Data;
	}
	
	public boolean RemoveVertex(int vertex)
	{
		if(!ContainsVertex(vertex))
			return false;
		
		AvailableIDs.Push(vertex);
		
		// We need to remove all of the edges by iterating over all the possible edges
		for(Integer ID : VertexIDs())
		{
			if(Edges.Remove(new Vector2i(vertex,ID)))
				ECount--;
			
			// We don't double count edges in undirected graphs
			if(Edges.Remove(new Vector2i(ID,vertex)) && IsDirected())
				ECount--;
		}
		
		return Vertices.RemoveByKey(vertex);
	}
	
	public boolean ContainsVertex(int vertex)
	{return VerifyVertexValid(vertex);}
	
	public boolean AddEdge(int src, int dst, E data)
	{
		if(!ContainsVertex(src) || !ContainsVertex(dst) || ContainsEdge(src,dst))
			return false;
		
		Edges.Set(new Edge<V,E>(Vertices.Get(src),Vertices.Get(dst),data,IsDirected()),src,dst);
		
		if(src == dst)
			SCount++;
		
		// We have to special case self-loops in the matrix version of the graph or else the dictionary will throw a fit
		// This edge is not the same as the edge in the opposite direction even though its undirected since it may become directed later (and has a different src and dst)
		if(IsUndirected() && src != dst)
			Edges.Set(new Edge<V,E>(Vertices.Get(dst),Vertices.Get(src),data,IsDirected()),dst,src);
		
		ECount++;
		return true;
	}
	
	public E PutEdge(int src, int dst, E data)
	{
		VerifyVertex(src);
		VerifyVertex(dst);
		
		E ret;
		
		if(ContainsEdge(src,dst))
		{
			Edge<V,E> e = Edges.Get(src,dst);
			
			ret = e.Data;
			e.Data = data;
			
			// We have to special case self-loops in the matrix version of the graph or else the dictionary will throw a fit
			// This edge is not the same as the edge in the opposite direction even though its undirected since it may become directed later (and has a different src and dst)
			if(IsUndirected() && src != dst)
				Edges.Get(dst,src).Data = data;
		}
		else
		{
			Edges.Set(new Edge<V,E>(Vertices.Get(src),Vertices.Get(dst),data,IsDirected()),src,dst);
			
			if(src == dst)
				SCount++;
			
			// We have to special case self-loops in the matrix version of the graph or else the dictionary will throw a fit
			// This edge is not the same as the edge in the opposite direction even though its undirected since it may become directed later (and has a different src and dst)
			if(IsUndirected() && src != dst)
				Edges.Set(new Edge<V,E>(Vertices.Get(dst),Vertices.Get(src),data,IsDirected()),dst,src);
			
			ret = data;
			ECount++;
		}
		
		return ret;
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
		
		if(ContainsEdge(src,dst))
			return Edges.Get(src,dst);
		
		throw new NoSuchEdgeException(src,dst);
	}
	
	public boolean RemoveEdge(int src, int dst)
	{
		if(!ContainsVertex(src) || !ContainsVertex(dst))
			return false;
		
		boolean ret = Edges.Remove(src,dst);
		
		// If we removed the outgoing edge, we need to remove an ingoing edge too
		if(ret)
		{
			if(src == dst)
				SCount--;
			
			if(src != dst && IsUndirected())
				Edges.Remove(dst,src);
			
			ECount--;
		}
		
		return ret;
	}
	
	public boolean ContainsEdge(int src, int dst)
	{return Edges.IsCellOccupied(src,dst);}
	
	public Iterable<Integer> Neighbors(int vertex)
	{
		VerifyVertex(vertex);
		return LINQ.Where(VertexIDs(),ID -> ContainsEdge(vertex,ID));
	}
	
	public Iterable<Vector2i> OutboundEdges(int vertex)
	{
		VerifyVertex(vertex);
		return LINQ.Select(LINQ.Where(VertexIDs(),ID -> ContainsEdge(vertex,ID)),ID -> new Vector2i(vertex,ID));
	}
	
	public Iterable<Vector2i> InboundEdges(int vertex)
	{
		VerifyVertex(vertex);
		return LINQ.Select(LINQ.Where(VertexIDs(),ID -> ContainsEdge(ID,vertex)),ID -> new Vector2i(ID,vertex));
	}
	
	public Iterable<Integer> VertexIDs()
	{return Vertices.Keys();}
	
	public Iterable<V> Vertices()
	{return LINQ.Select(Vertices.Values(),v -> v.Data);}
	
	public Iterable<Vector2i> Edges()
	{
		if(IsDirected())
			return Edges.IndexSet(true);
		
		return LINQ.Where(Edges.IndexSet(true),v -> v.X <= v.Y); // We can't not special case self-loops here because the dictionary will throw a fit if we try to add them twice
	}
	
	public void Clear()
	{
		Vertices.Clear();
		Edges.Clear();
		
		AvailableIDs = new Stack<Integer>();
		AvailableIDs.Push(0); // We need to keep the next available new ID at the bottom of the stack at all times
		
		ECount = 0;
		SCount = 0;
		
		return;
	}
	
	public int VertexCount()
	{return Vertices.Count();}
	
	public int EdgeCount()
	{return ECount;}
	
	public int OutDegree(int vertex)
	{
		int ret = 0;
		
		for(Integer ID : VertexIDs())
			if(ContainsEdge(vertex,ID))
				ret++;
		
		return ret;
	}
	
	public int InDegree(int vertex)
	{
		int ret = 0;
		
		for(Integer ID : VertexIDs())
			if(ContainsEdge(ID,vertex))
				ret++;
		
		return ret;
	}
	
	public boolean IsDirected()
	{return Directed;}
	
	public void MakeDirected()
	{
		if(IsDirected())
			return;
		
		ECount = (ECount << 1) - SCount;
		Directed = true;
		
		return;
	}
	
	public void MakeUndirected(boolean fill)
	{
		if(IsUndirected())
			return;
		
		for(Vector2i v : LINQ.FixSequence(Edges()))
			if(!ContainsEdge(v.Y,v.X))
				if(fill)
					AddEdge(v.Y,v.X,GetEdge(v.X,v.Y));
				else
					RemoveEdge(v.X,v.Y);
			else // We set the data to be whatever we find first since we have no better option than to clobber one piece of data (we could pair it up, but meh)
				SetEdge(v.Y,v.X,GetEdge(v.X,v.Y));
		
		ECount = (ECount + SCount) >> 1;
		Directed = false;
		
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
		
		if(!Vertices.ContainsKey(vertex))
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
		
		if(!Vertices.ContainsKey(vertex))
			throw new NoSuchVertexException(vertex);
		
		return;
	}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(Integer src : VertexIDs())
		{
			ret += Vertices.Get(src) + "\n-----\nOutbound Edges: ";
			
			for(Integer dst : VertexIDs())
				if(ContainsEdge(src,dst))
					ret += Edges.Get(src,dst) + " ";
			
			ret = ret.substring(0,ret.length() - 1) + "\nInbound Edges: ";
			
			for(Integer dst : VertexIDs())
				if(ContainsEdge(dst,src))
					ret += Edges.Get(dst,src) + " ";
			
			ret += "\n";
		}
		
		return ret;
	}
	
	/**
	 * The set of vertices in the graph.
	 */
	protected Dictionary<Integer,Vertex<V>> Vertices;
	
	/**
	 * The edge set in the graph.
	 */
	protected VariableSizeGrid<Edge<V,E>> Edges;
	
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
	 * The number of self-loops in the graph.
	 */
	protected int SCount;
	
	/**
	 * Marks that this is a directed graph if true and an undirected graph if false.
	 */
	protected boolean Directed;
}