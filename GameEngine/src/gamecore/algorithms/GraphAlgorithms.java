package gamecore.algorithms;

import java.util.Comparator;

import gamecore.datastructures.HashTable;
import gamecore.datastructures.LinkedList;
import gamecore.datastructures.graphs.AdjacencyListGraph;
import gamecore.datastructures.graphs.IGraph;
import gamecore.datastructures.graphs.exceptions.CyclicGraphException;
import gamecore.datastructures.graphs.exceptions.NoSuchEdgeException;
import gamecore.datastructures.graphs.exceptions.NoSuchVertexException;
import gamecore.datastructures.graphs.exceptions.UnconnectedGraphException;
import gamecore.datastructures.maps.Dictionary;
import gamecore.datastructures.queues.PriorityQueue;
import gamecore.datastructures.queues.PriorityQueueTree;
import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.queues.Stack;
import gamecore.datastructures.tuples.Pair;
import gamecore.datastructures.vectors.Vector2i;

/**
 * A collection of graph algorithms.
 * @author Dawn Nye
 */
public final class GraphAlgorithms
{
	/**
	 * No one will make one of these.
	 */
	private GraphAlgorithms()
	{return;}
	
	/**
	 * Computes a BFS of {@code G} starting from the first unvisited vertex(s).
	 * The BFS will continue selecting new start vertices until each vertex has been visited exactly once.
	 * The resultant forest will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the BFS proceeded over the edge away from the starting vertex.
	 * <br><br>
	 * If a vertex is outside of a previously constructed tree but can reach the tree later, then it will belong to a disjoint tree.
	 * For example, in the graph A -> B -> C, if we start at B and then start at A, we will get the forest A and B -> C.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the BFS in.
	 * @return Returns a BFS forest as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> BFS(IGraph<V,E> G)
	{return BFS(G,null);}
	
	/**
	 * Computes a BFS of {@code G} starting from the first unvisited vertex(s).
	 * The BFS will continue selecting new start vertices until each vertex has been visited exactly once.
	 * The resultant forest will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the BFS proceeded over the edge away from the starting vertex.
	 * <br><br>
	 * If a vertex is outside of a previously constructed tree but can reach the tree later, then it will belong to a disjoint tree.
	 * For example, in the graph A -> B -> C, if we start at B and then start at A, we will get the forest A and B -> C.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the BFS in.
	 * @param f The function which allows us to visit vertices as we explore the graph. This value can be null, in which case it is ignored.
	 * @return Returns a BFS forest as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> BFS(IGraph<V,E> G, BFSVisitor<V,E> f)
	{
		if(G == null)
			throw new NullPointerException();
		
		IGraph<V,E> ret = new AdjacencyListGraph<V,E>(true);
		Dictionary<Integer,Integer> ID_translator = new Dictionary<Integer,Integer>();
		
		for(Integer start : G.VertexIDs())
		{
			if(ID_translator.ContainsKey(start))
				continue;
			
			Queue<Pair<Integer,Integer>> verts = new Queue<Pair<Integer,Integer>>();
			verts.Enqueue(new Pair<Integer,Integer>(-1,start));
			
			while(!verts.IsEmpty())
			{
				Pair<Integer,Integer> src = verts.Dequeue();
				int s_src;
				
				// We may accidentally put things into the queue multiple times, so let's be careful not to visit them multiple times
				if(ID_translator.ContainsKey(src.Item2))
					continue;
				
				ID_translator.Add(src.Item2,s_src = ret.AddVertex(G.GetVertex(src.Item2)));
				
				// If we came from somewhere, add the edge between us and them
				if(src.Item1 > -1)
					ret.AddEdge(ID_translator.Get(src.Item1),s_src,G.GetEdge(src.Item1,src.Item2));
				
				if(f != null)
					f.Visit(G,src.Item2,ret,s_src);
				
				// Find all children that haven't been visited yet and enqueue them
				for(Integer n : G.Neighbors(src.Item2))
					if(!ID_translator.ContainsKey(n))
						verts.Enqueue(new Pair<Integer,Integer>(src.Item2,n));
			}
		}
		
		return ret;
	}
	
	/**
	 * Computes a BFS of {@code G} starting from the vertex with ID {@code start}.
	 * The BFS will not visit any vertices unreachable from {@code start}.
	 * The resultant tree will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the BFS proceeded over the edge away from the starting vertex.
	 * Unvisited vertices will not be part of the returned graph.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the BFS in.
	 * @param start The vertex to start the BFS on.
	 * @return Returns a BFS tree as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> BFS(IGraph<V,E> G, int start)
	{return BFS(G,start,null);}
	
	/**
	 * Computes a BFS of {@code G} starting from the vertex with ID {@code start}.
	 * The BFS will not visit any vertices unreachable from {@code start}.
	 * The resultant tree will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the BFS proceeded over the edge away from the starting vertex.
	 * Unvisited vertices will not be part of the returned graph.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the BFS in.
	 * @param start The vertex to start the BFS on.
	 * @param f The function which allows us to visit vertices as we explore the graph. This value can be null, in which case it is ignored.
	 * @return Returns a BFS tree as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> BFS(IGraph<V,E> G, int start, BFSVisitor<V,E> f)
	{
		if(G == null)
			throw new NullPointerException();
		
		if(start < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		IGraph<V,E> ret = new AdjacencyListGraph<V,E>(true);
		Dictionary<Integer,Integer> ID_translator = new Dictionary<Integer,Integer>();
		
		Queue<Pair<Integer,Integer>> verts = new Queue<Pair<Integer,Integer>>();
		verts.Enqueue(new Pair<Integer,Integer>(-1,start));
		
		while(!verts.IsEmpty())
		{
			Pair<Integer,Integer> src = verts.Dequeue();
			int s_src;
			
			// We may accidentally put things into the queue multiple times, so let's be careful not to visit them multiple times
			if(ID_translator.ContainsKey(src.Item2))
				continue;
			
			ID_translator.Add(src.Item2,s_src = ret.AddVertex(G.GetVertex(src.Item2)));
			
			// If we came from somewhere, add the edge between us and them
			if(src.Item1 > -1)
				ret.AddEdge(ID_translator.Get(src.Item1),s_src,G.GetEdge(src.Item1,src.Item2));
			
			if(f != null)
				f.Visit(G,src.Item2,ret,s_src);
			
			// Find all children that haven't been visited yet and enqueue them
			for(Integer n : G.Neighbors(src.Item2))
				if(!ID_translator.ContainsKey(n))
					verts.Enqueue(new Pair<Integer,Integer>(src.Item2,n));
		}
		
		return ret;
	}
	
	/**
	 * Computes a DFS of {@code G} starting from the first unvisited vertex(s).
	 * The DFS will continue selecting new start vertices until each vertex has been visited exactly once.
	 * The resultant forest will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the DFS proceeded over the edge away from the starting vertex.
	 * <br><br>
	 * If a vertex is outside of a previously constructed tree but can reach the tree later, then it will belong to a disjoint tree.
	 * For example, in the graph A -> B -> C, if we start at B and then start at A, we will get the forest A and B -> C.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the DFS in.
	 * @param f The function which allows us to visit vertices as we explore the graph. This value can be null, in which case it is ignored.
	 * @return Returns a DFS forest as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 */
	public static <V,E> IGraph<V,E> DFS(IGraph<V,E> G)
	{return DFS(G,null);}
	
	/**
	 * Computes a DFS of {@code G} starting from the first unvisited vertex(s).
	 * The DFS will continue selecting new start vertices until each vertex has been visited exactly once.
	 * The resultant forest will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the DFS proceeded over the edge away from the starting vertex.
	 * <br><br>
	 * If a vertex is outside of a previously constructed tree but can reach the tree later, then it will belong to a disjoint tree.
	 * For example, in the graph A -> B -> C, if we start at B and then start at A, we will get the forest A and B -> C.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the DFS in.
	 * @param f The function which allows us to visit vertices as we explore the graph. This value can be null, in which case it is ignored.
	 * @return Returns a DFS forest as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 */
	public static <V,E> IGraph<V,E> DFS(IGraph<V,E> G, DFSVisitor<V,E> f)
	{
		if(G == null)
			throw new NullPointerException();
		
		// We will have a forest, so the resulting graph must be sparse, so an adjacency list backed graph is good to return
		IGraph<V,E> ret = new AdjacencyListGraph<V,E>(true);
		Dictionary<Integer,Integer> ID_translator = new Dictionary<Integer,Integer>();
		
		for(Integer start : G.VertexIDs())
		{
			if(ID_translator.ContainsKey(start))
				continue;
			
			Stack<Integer> verts = new Stack<Integer>();
			verts.Push(start);
			
			while(!verts.IsEmpty())
			{
				int src = verts.Peek();
				int s_src;
				
				if(!ID_translator.ContainsKey(src))
				{
					ID_translator.Add(src,s_src = ret.AddVertex(G.GetVertex(src)));
					
					// We need to get to the previous thing to add our new edge if we're not the start node
					if(start != src)
					{
						verts.Pop();
						ret.AddEdge(ID_translator.Get(verts.Peek()),s_src,G.GetEdge(verts.Peek(),src));
						verts.Push(src);
					}
					
					if(f != null)
						f.Visit(G,src,ret,s_src,true);
				}
				else
					s_src = ID_translator.Get(src);
				
				// Find the first child that hasn't been visited yet and go into it
				for(Integer n : G.Neighbors(src))
					if(!ID_translator.ContainsKey(n))
					{
						verts.Push(n);
						break;
					}
				
				// If there is no child, then we perform our post visit and pop this vertex off of the stack
				if(verts.Peek() == src)
				{
					if(f != null)
						f.Visit(G,src,ret,s_src,false);
					
					verts.Pop();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Computes a DFS of {@code G} starting from the vertex with ID {@code start}.
	 * The DFS will not visit any vertices unreachable from {@code start}.
	 * The resultant tree will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the DFS proceeded over the edge away from the starting vertex.
	 * Unvisited vertices will not be part of the returned graph.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the DFS in.
	 * @param start The vertex to start the DFS on.
	 * @return Returns a DFS forest as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> DFS(IGraph<V,E> G, int start)
	{return DFS(G,start,null);}
	
	/**
	 * Computes a DFS of {@code G} starting from the vertex with ID {@code start}.
	 * The DFS will not visit any vertices unreachable from {@code start}.
	 * The resultant tree will be a directed graph regardless of if {@code G} is directed or undirected.
	 * The edge directions indicate which direction the DFS proceeded over the edge away from the starting vertex.
	 * Unvisited vertices will not be part of the returned graph.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to perform the DFS in.
	 * @param start The vertex to start the DFS on.
	 * @param f The function which allows us to visit vertices as we explore the graph. This value can be null, in which case it is ignored.
	 * @return Returns a DFS tree as described above.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 */
	public static <V,E> IGraph<V,E> DFS(IGraph<V,E> G, int start, DFSVisitor<V,E> f)
	{
		if(G == null)
			throw new NullPointerException();
		
		if(start < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		IGraph<V,E> ret = new AdjacencyListGraph<V,E>(true);
		Dictionary<Integer,Integer> ID_translator = new Dictionary<Integer,Integer>();
		
		Stack<Integer> verts = new Stack<Integer>();
		verts.Push(start);
		
		while(!verts.IsEmpty())
		{
			int src = verts.Peek();
			int s_src;
			
			if(!ID_translator.ContainsKey(src))
			{
				ID_translator.Add(src,s_src = ret.AddVertex(G.GetVertex(src)));
				
				// We need to get to the previous thing to add our new edge if we're not the start node
				if(start != src)
				{
					verts.Pop();
					ret.AddEdge(ID_translator.Get(verts.Peek()),s_src,G.GetEdge(verts.Peek(),src));
					verts.Push(src);
				}
				
				if(f != null)
					f.Visit(G,src,ret,s_src,true);
			}
			else
				s_src = ID_translator.Get(src);
			
			// Find the first child that hasn't been visited yet and go into it
			for(Integer n : G.Neighbors(src))
				if(!ID_translator.ContainsKey(n))
				{
					verts.Push(n);
					break;
				}
			
			// If there is no child, then we perform our post visit and pop this vertex off of the stack
			if(verts.Peek() == src)
			{
				if(f != null)
					f.Visit(G,src,ret,s_src,false);
				
				verts.Pop();
			}
		}
		
		return ret;
	}
	
	/**
	 * Finds a minimum length path from {@code start} to every other vertex in a weighted graph {@code G}.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to search for a path within.
	 * @param cmp The means by which {@code E} types are compared.
	 * @param sum The means by which {@code E} types are summed.
	 * @param start The vertex ID to start from.
	 * @return
	 * Returns the minimum length paths from {@code start} to every other vertex in {@code G}.
	 * This will be given as a set of dictionary entries.
	 * The first value in the dictionary is the total weight of the path from {@code start} to the vertex in question.
	 * The second value is the previous vertex on the shorted path from {@code start} to that vertex.
	 * A full path can be constructed by following the previous vertices back to {@code start}.
	 * The dictionary entries for start will always be {@code (null,-1)}.
	 * <br><br>
	 * If a vertex is unreachable from {@code start} in {@code G}, then it will not have an entry in the dictionary returned.
	 * @throws NullPointerException Thrown if {@code G}, {@code cmp}, or {@code sum} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} or {@code end} is not a vertex ID in the graph.
	 */
	public static <V,E> Dictionary<Integer,Pair<E,Integer>> Dijkstra(IGraph<V,E> G, Comparator<? super E> cmp, SumComputer<E> sum, int start)
	{
		if(G == null || cmp == null || sum == null)
			throw new NullPointerException();
		
		if(start < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		// We store distances to vertices here by mapping a vertex ID to is E distance along with the previous vertex on its path
		Dictionary<Integer,Pair<E,Integer>> distances = new Dictionary<Integer,Pair<E,Integer>>();
		
		// Put the initial vertex into the dictionary
		// We do not mark null as a distance, because we don't know if that's a valid distance, but note that the -1 marks having the zero distance
		// We could instead use a null pair to mark the start vertex, but that will require null checking too many things to bother
		distances.Add(start,new Pair<E,Integer>(null,-1));
		
		// We'll have a seperate visited lookup because the priority queue will be log n time rather than constant time
		HashTable<Integer> visited = new HashTable<Integer>();
		
		// We always have visited the start vertex
		visited.add(start);
		
		// The priority queue sorts based on distance
		// We will keep only unvisited vertices in here along with their tentative weights
		PriorityQueueTree<Pair<Integer,E>> Q = new PriorityQueueTree<Pair<Integer,E>>((p1,p2) -> cmp.compare(p1.Item2,p2.Item2));
		
		// Add all of the tentative paths out of the start vertex
		for(Integer n : G.Neighbors(start))
		{
			E e = G.GetEdge(start,n);
			
			distances.Put(n,new Pair<E,Integer>(e,start));
			Q.Add(new Pair<Integer,E>(n,e));
		}
		
		// While we have an unvisited vertex and haven't found our destination
		while(!Q.IsEmpty())
		{
			// Dequeue the root
			// This vertex will now be set in stone
			Pair<Integer,E> p = Q.Dequeue();
			
			visited.add(p.Item1);
			
			// For each neighbor of p, we need to update the tentative distance and potentially the queue
			for(Integer n : G.Neighbors(p.Item1))
			{
				// We skip over already visited vertices
				if(visited.contains(n))
					continue;
				
				E e = sum.Sum(p.Item2,G.GetEdge(p.Item1,n));
				
				if(!distances.ContainsKey(n) || cmp.compare(e,distances.Get(n).Item1) < 0)
				{
					// Remove the neighbor from the queue and then add it again with its new smaller priority
					if(distances.ContainsKey(n))
						Q.Remove(new Pair<Integer,E>(n,distances.Get(n).Item1));
					
					Q.Add(new Pair<Integer,E>(n,e));
					distances.Put(n,new Pair<E,Integer>(e,p.Item1));
				}
			}
		}
		
		return distances;
	}
	
	/**
	 * Finds a minimum length path from {@code start} to {@code end} in a weighted graph {@code G}.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to search for a path within.
	 * @param cmp The means by which {@code E} types are compared.
	 * @param sum The means by which {@code E} types are summed.
	 * @param start The vertex ID to start from.
	 * @param end The vertex ID to end at.
	 * @return Returns the minimum length path from {@code start} to {@code end} in {@code G} or null if there is no such path.
	 * @throws NullPointerException Thrown if {@code G}, {@code cmp}, or {@code sum} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} or {@code end} is not a vertex ID in the graph.
	 */
	public static <V,E> Iterable<Integer> Dijkstra(IGraph<V,E> G, Comparator<? super E> cmp, SumComputer<E> sum, int start, int end)
	{
		if(G == null || cmp == null || sum == null)
			throw new NullPointerException();
		
		if(start < 0 || end < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		if(!G.ContainsVertex(end))
			throw new NoSuchVertexException(end);
		
		// We store distances to vertices here by mapping a vertex ID to is E distance along with the previous vertex on its path
		Dictionary<Integer,Pair<E,Integer>> distances = new Dictionary<Integer,Pair<E,Integer>>();
		
		// Put the initial vertex into the dictionary
		// We do not mark null as a distance, because we don't know if that's a valid distance, but note that the -1 marks having the zero distance
		// We could instead use a null pair to mark the start vertex, but that will require null checking too many things to bother
		distances.Add(start,new Pair<E,Integer>(null,-1));
		
		// We'll have a seperate visited lookup because the priority queue will be log n time rather than constant time
		HashTable<Integer> visited = new HashTable<Integer>();
		
		// We always have visited the start vertex
		visited.add(start);
		
		// The priority queue sorts based on distance
		// We will keep only unvisited vertices in here along with their tentative weights
		PriorityQueueTree<Pair<Integer,E>> Q = new PriorityQueueTree<Pair<Integer,E>>((p1,p2) -> cmp.compare(p1.Item2,p2.Item2));
		
		// Add all of the tentative paths out of the start vertex
		for(Integer n : G.Neighbors(start))
		{
			E e = G.GetEdge(start,n);
			
			distances.Put(n,new Pair<E,Integer>(e,start));
			Q.Add(new Pair<Integer,E>(n,e));
		}
		
		// While we have an unvisited vertex and haven't found our destination
		while(!Q.IsEmpty())
		{
			// Dequeue the root
			// This vertex will now be set in stone
			Pair<Integer,E> p = Q.Dequeue();
			
			// If this is our destination, we're done
			if(p.Item1 == end)
			{
				LinkedList<Integer> ret = new LinkedList<Integer>();
				int cur = p.Item1;
				
				do
				{
					ret.AddFront(cur);
					cur = distances.Get(cur).Item2;
				}
				while(cur != -1);
				
				return ret;
			}
			
			visited.add(p.Item1);
			
			// For each neighbor of p, we need to update the tentative distance and potentially the queue
			for(Integer n : G.Neighbors(p.Item1))
			{
				// We skip over already visited vertices
				if(visited.contains(n))
					continue;
				
				E e = sum.Sum(p.Item2,G.GetEdge(p.Item1,n));
				
				if(!distances.ContainsKey(n) || cmp.compare(e,distances.Get(n).Item1) < 0)
				{
					// Remove the neighbor from the queue and then add it again with its new smaller priority
					if(distances.ContainsKey(n))
						Q.Remove(new Pair<Integer,E>(n,distances.Get(n).Item1));
					
					Q.Add(new Pair<Integer,E>(n,e));
					distances.Put(n,new Pair<E,Integer>(e,p.Item1));
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Finds a minimum length path from {@code start} to {@code end} in an unweighted graph {@code G}.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to search for a path within.
	 * @param start The vertex ID to start from.
	 * @param end The vertex ID to end at.
	 * @return Returns a minimum length unweighted path from {@code start} to {@code end} in {@code G} or null if there is no such path.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} or {@code end} is not a vertex ID in the graph.
	 * @implNote This algorithm uses a BFS to search for a minimum length path on an unweighted graph.
	 */
	public static <V,E> Iterable<Integer> FindPath(IGraph<V,E> G, int start, int end)
	{
		if(G == null)
			throw new NullPointerException();
		
		if(start < 0 || end < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		if(!G.ContainsVertex(end))
			throw new NoSuchVertexException(end);
		
		// A visited ID with its previous ID
		Dictionary<Integer,Integer> visited = new Dictionary<Integer,Integer>();
		
		Queue<Pair<Integer,Integer>> verts = new Queue<Pair<Integer,Integer>>();
		verts.Enqueue(new Pair<Integer,Integer>(start,-1));
		
		while(!verts.IsEmpty())
		{
			Pair<Integer,Integer> src = verts.Dequeue();
			
			// We may accidentally put things into the queue multiple times, so let's be careful not to visit them multiple times
			if(visited.ContainsKey(src.Item1))
				continue;
			
			visited.Add(src.Item1,src.Item2);
			
			if(src.Item1 == end)
			{
				LinkedList<Integer> ret = new LinkedList<Integer>();
				int cur = src.Item1;
				
				do
				{
					ret.AddFront(cur);
					cur = visited.Get(cur);
				}
				while(cur != -1);
				
				return ret;
			}
			
			// Find all children that haven't been visited yet and enqueue them
			for(Integer n : G.Neighbors(src.Item1))
				if(!visited.ContainsKey(n))
					verts.Enqueue(new Pair<Integer,Integer>(n,src.Item1));
		}
		
		return null;
	}
	
	/**
	 * Computes the length of a path given by a sequence of vertex IDs in {@code G}.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to compute a path length in.
	 * @param path The path in {@code G} of interest. This should be given by a sequence of vertex IDs.
	 * @param sum The means by which {@code E} types are summed.
	 * @return Returns the length of {@code path} in {@code G}. If {@code path} specifies a path of zero or one vertices, null is returned.
	 * @throws NullPointerException Thrown if {@code G}, {@code path}, or {@code sum} is null.
	 * @throws IllegalArgumentException Thrown if any vertex ID in {@code path} is negative.
	 * @throws NoSuchVertexException Thrown if any vertex ID in {@code path} is not a vertex ID in the graph.
	 * @throws NoSuchEdgeException Thrown if any adjacent pairs of vertex IDs in {@code path} are not connected by an edge in {@code G}.
	 */
	public static <V,E> E PathLength(IGraph<V,E> G, Iterable<? extends Integer> path, SumComputer<E> sum)
	{
		if(G == null || path == null || sum == null)
			throw new NullPointerException();
		
		if(!path.iterator().hasNext())
			return null;
		
		boolean first = true;
		
		int prev = -1;
		E ret = null;
		
		for(Integer cur : path)
		{
			if(prev > -1)
				if(first)
				{
					ret = G.GetEdge(prev,cur);
					first = false;
				}
				else
					ret = sum.Sum(ret,G.GetEdge(prev,cur));
			
			prev = cur;
		}
		
		return ret;
	}
	
	/**
	 * Computes a minimum spanning tree of {@code G} via Prim's algorithm.
	 * The graph must be connected and undirected.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to find a minimum spanning tree within.
	 * @param cmp The means by which we compare {@code E} types.
	 * @return Returns a minimum spanning tree of {@code G}.
	 * @throws NullPointerException Thrown if {@code G} or {@code cmp} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative or if {@code G} is directed.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 * @throws UnconnectedGraphException Thrown if {@code G} is an unconnected graph.
	 */
	public static <V,E> IGraph<V,E> Prim(IGraph<V,E> G, Comparator<? super E> cmp)
	{return Prim(G,cmp,G.VertexCount() == 0 ? 0 : G.VertexIDs().iterator().next());}
	
	/**
	 * Computes a minimum spanning tree of {@code G} via Prim's algorithm starting from {@code start}.
	 * The graph must be connected and undirected.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to find a minimum spanning tree within.
	 * @param cmp The means by which we compare {@code E} types.
	 * @param start The vertex to start growing a minimum spanning tree from.
	 * @return Returns a minimum spanning tree of {@code G}.
	 * @throws NullPointerException Thrown if {@code G} or {@code cmp} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative or if {@code G} is directed.
	 * @throws NoSuchVertexException Thrown if {@code start} is not a vertex ID in the graph.
	 * @throws UnconnectedGraphException Thrown if {@code G} is an unconnected graph.
	 */
	public static <V,E> IGraph<V,E> Prim(IGraph<V,E> G, Comparator<? super E> cmp, int start)
	{
		if(G == null || cmp == null)
			throw new NullPointerException();
		
		if(G.IsDirected())
			throw new IllegalArgumentException("The graph must be undirected.");
		
		if(start < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		// We're going to construct a tree, so an adjacency graph will be a good fit for building it
		IGraph<V,E> ret = new AdjacencyListGraph<V,E>(false);
		
		Dictionary<Integer,Integer> ID_translation = new Dictionary<Integer,Integer>();
		ID_translation.Add(start,ret.AddVertex(G.GetVertex(start)));
		
		// Add the initial edges out of our start vertex to the queue
		PriorityQueue<Vector2i> Q = new PriorityQueue<Vector2i>((Vector2i v1,Vector2i v2) -> cmp.compare(G.GetEdge(v1.X,v1.Y),G.GetEdge(v2.X,v2.Y)),G.OutboundEdges(start));
		
		// We loop until we run out of edges or we're done
		while(!Q.IsEmpty() && ret.VertexCount() < G.VertexCount())
		{
			Vector2i e = Q.Dequeue();
			
			// If we've already visited a vertex, do nothing
			if(ID_translation.ContainsKey(e.Y))
				continue;
			
			// We can now add a new vertex and edge
			int src;
			
			ID_translation.Add(e.Y,src = ret.AddVertex(G.GetVertex(e.Y)));
			ret.AddEdge(ID_translation.Get(e.X),src,G.GetEdge(e.X,e.Y));
			
			// Add each unvisited neighbor of the new vertex
			for(Vector2i ee : G.OutboundEdges(e.Y))
				if(!ID_translation.ContainsKey(ee.Y))
					Q.Enqueue(ee);
		}
		
		// If we haven't added every vertex, we're in trouble
		if(ret.VertexCount() != G.VertexCount())
			throw new UnconnectedGraphException();
		
		return ret;
	}
	
	/**
	 * Determines if {@code start} is reachable from {@code end}.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to search to determine reachability in.
	 * @param start The vertex ID to start from.
	 * @param end The vertex ID to end at.
	 * @return Returns true if there is a path from {@code start} to {@code end} in {@code G} and false otherwise.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative.
	 * @throws NoSuchVertexException Thrown if {@code start} or {@code end} is not a vertex ID in the graph.
	 * @implNote This algorithm uses a BFS to search for a minimum length path on an unweighted graph.
	 */
	public static <V,E> boolean Reachable(IGraph<V,E> G, int start, int end)
	{
		if(G == null)
			throw new NullPointerException();
		
		if(start < 0 || end < 0)
			throw new IllegalArgumentException();
		
		if(!G.ContainsVertex(start))
			throw new NoSuchVertexException(start);
		
		if(!G.ContainsVertex(end))
			throw new NoSuchVertexException(end);
		
		HashTable<Integer> visited = new HashTable<Integer>();
		
		Queue<Integer> verts = new Queue<Integer>();
		verts.Enqueue(start);
		
		while(!verts.IsEmpty())
		{
			int src = verts.Dequeue();
			
			// We may accidentally put things into the queue multiple times, so let's be careful not to visit them multiple times
			if(visited.contains(src))
				continue;
			
			if(src == end)
				return true;
			
			visited.add(src);
			
			// Find all children that haven't been visited yet and enqueue them
			for(Integer n : G.Neighbors(src))
				if(!visited.contains(n))
					verts.Enqueue(n);
		}
		
		return false;
	}
	
	/**
	 * Computes a topological sort of {@code G}.
	 * The graph must be directed and acyclic.
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 * @param G The graph to topologically sort.
	 * @return Returns a topological sort of {@code G}.
	 * @throws NullPointerException Thrown if {@code G} is null.
	 * @throws IllegalArgumentException Thrown if {@code G} is undirected.
	 * @throws CyclicGraphException Thrown if {@code G} contains a cycle.
	 */
	public static <V,E> Iterable<Integer> TopologicalSort(IGraph<V,E> G)
	{
		if(G == null)
			throw new NullPointerException();
		
		if(G.IsUndirected())
			throw new IllegalArgumentException();
		
		LinkedList<Integer> ret = new LinkedList<Integer>();
		
		// Seen vertices are those we have seen but not yet visited twice (once on the way out and once on the way back)
		// If there is a cycle in the graph, we will find it with a DFS using seen vertices and no fully visited vertcies
		// We will add vertices to this on the way out and remove them from it on the way back
		// This is because there can be multiple paths to a vertex
		HashTable<Integer> seen = new HashTable<Integer>();
		
		DFS(G,(g,g_id,s,s_id,forward) ->
		{
			if(forward)
			{
				for(Integer n : g.Neighbors(g_id))
					if(seen.contains(n))
						throw new CyclicGraphException();
				
				seen.add(g_id);
			}
			else
			{
				ret.AddFront(g_id);
				seen.remove(g_id);
			}
			
			return;
		});
		
		return ret;
	}
	
	/**
	 * A function that allows us to visit vertices in a DFS-like search.
	 * This corresponds to a search which explores a graph using a stack, allowing us to visit vertices before their neighbors and/or after their neighbors.
	 * @author Dawn Nye
	 * @param <V> The data type stored in vertices.
	 * @param <E> The data type stored in edges.
	 */
	@FunctionalInterface public interface DFSVisitor<V,E>
	{
		/**
		 * Visits a vertex.
		 * @param g The graph the search is being performed within.
		 * @param g_me The vertex ID in {@code g} being visited.
		 * @param search The search graph being built.
		 * @param s_me The vertex ID in {@code search} being visited.
		 * @param forward
		 * If true, then the traversal calling this is proceeding forward away from the start vertex.
		 * If false, then the traversal call this is proceeding backward toward the start vertex.
		 */
		public void Visit(IGraph<V,E> g, int g_me, IGraph<V,E> search, int s_me, boolean forward);
	}
	
	/**
	 * A function that allows us to visit vertices in a BFS-like search.
	 * This corresponds to a search which explores a graph using a queue, allowing us to visit vertices only when we first reach them right before adding their neighbors to the queue.
	 * @author Dawn Nye
	 * @param <V> The data type stored in vertices.
	 * @param <E> The data type stored in edges.
	 */
	@FunctionalInterface public interface BFSVisitor<V,E>
	{
		/**
		 * Visits a vertex.
		 * @param g The graph the search is being performed within.
		 * @param g_me The vertex ID in {@code g} being visited.
		 * @param search The search graph being built.
		 * @param s_me The vertex ID in {@code search} being visited.
		 */
		public void Visit(IGraph<V,E> g, int g_me, IGraph<V,E> search, int s_me);
	}
	
	/**
	 * Computes the sum of a generic type.
	 * @author Dawn Nye
	 * @param <E> The type to sum.
	 */
	@FunctionalInterface public interface SumComputer<E>
	{
		/**
		 * Computes the sum of {@code e1} and {@code e2}.
		 * @param e1 The left-hand side of the sum.
		 * @param e2 The right-hand side of the sum.
		 * @return Returns the sum of {@code e1} + {@code e2}.
		 * @throws NullPointerException Thrown if {@code e1} or {@code e2} is null and null values are not permitted to be summed.
		 */
		public E Sum(E e1, E e2);
	}
}