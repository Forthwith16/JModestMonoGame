package gamecore.datastructures.graphs;

import gamecore.datastructures.LinkedList;
import gamecore.datastructures.vectors.Vector2i;
import gamecore.observe.IObservable;
import gamecore.observe.IObserver;

/**
 * A graph that consists of many unconnected components existing in the same space.
 * If two equal vertices from different components are identified, then the two components they belong to are merged together.
 * <br><br>
 * Note that because this involves removing at least one vertex, vertex IDs will be lost in the merging process.
 * When this happens, the smaller vertex ID will always be the one kept.
 * If their data is distinct, then only the data of the vertex with the smaller ID will be kept.
 * <br><br>
 * Similarly, if two vertices are merged into a single vertex, then their edges are merged together as well.
 * If there are two edges going to the same destination (or coming from the same source), then the edge (and its data) from the smaller vertex ID is the one kept.
 * <br><br>
 * Notifactions of merge events for this graph are sent out both before and after a merge completes.
 * @author Dawn Nye
 * @param <V> The type of data in the vertices.
 * @param <E> The type of data in the edges.
 * @implNote This graph class is build on top of an adjacency matrix.
 */
public class SpeculativeGraph<V,E> extends AdjacencyMatrixGraph<V,E> implements IObservable<SpeculativeGraph.MergeEvent<V,E>>
{
	/**
	 * Creates a new vision graph.
	 * @param dir If true, this will be a directed graph. If false, this will be an undirected graph.
	 * @param inspector The means by which common vertices are identified between graphs.
	 * @throws NullPointerException Thrown if {@code inspector} is null.
	 */
	public SpeculativeGraph(boolean dir,GraphAnalyzer<V,E> inspector)
	{
		super(dir);
		
		if(inspector == null)
			throw new NullPointerException();
		
		Inspector = inspector;
		Subscribers = new LinkedList<IObserver<MergeEvent<V,E>>>();
		
		return;
	}
	
	@Override public int AddVertex(V vertex)
	{
		int ret = super.AddVertex(vertex);
		
		if(ret > -1)
			TryMerge(ret,-1);
		
		return ret;
	}
	
	@Override public V SetVertex(int vertex, V data)
	{
		V ret = super.SetVertex(vertex,data);
		TryMerge(vertex,-1);
		
		return ret;
	}
	
	@Override public boolean RemoveVertex(int vertex)
	{
		if(super.RemoveVertex(vertex))
		{
			TryMerge(-1,-1);
			return true;
		}
		
		return false;
	}
	
	@Override public boolean AddEdge(int src, int dst, E data)
	{
		if(super.AddEdge(src,dst,data))
		{
			TryMerge(src,dst);
			return true;
		}
		
		return false;
	}
	
	@Override public E PutEdge(int src, int dst, E data)
	{
		E ret = super.PutEdge(src,dst,data);
		TryMerge(src,dst);
		
		return ret;
	}
	
	@Override public E SetEdge(int src, int dst, E data)
	{
		E ret = super.SetEdge(src,dst,data);
		TryMerge(src,dst);
		
		return ret;
	}
	
	@Override public boolean RemoveEdge(int src, int dst)
	{
		if(super.RemoveEdge(src,dst))
		{
			TryMerge(src,dst);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tries to merge unconnected components of the graph together.
	 * @param id1 If this value is not -1 (an always invalid vertex ID), then this is the vertex ID of a vertex that was added/modified/merged or the source vertex of an added/removed/modified edge if {@code id2} is not -1.
	 * @param id2 If this value is not -1 (an always invalid vertex ID), then this is the vertex ID of the destination vertex of an added/removed/modified edge.
	 * @implNote Note that {@code id1} and {@code id2} will both be -1 only when a vertex is removed.
	 */
	protected void TryMerge(int id1, int id2)
	{
		// Find the initial merge (if we have one at all)
		Vector2i p = Inspector.FindCommonVertex(this,id1,id2);
		
		// If we're given two vertices to merge and they're valid, do it
		while(p != null)
		{
			// We'll need to verify if what we got was correct
			MergeEvent<V,E> e = new MergeEvent<V,E>(this,p.X,p.Y,false);
			
			// If this merge event is invalid, then we can't merge
			if(!e.IsValid())
				break;
			
			// Notify all observers
			NotifyAll(e);
			
			// Merge like you've never merged before!
			// First, we need to copy all the incoming edges to the alias vertex into the real vertex (keep old data in already existant edges)
			for(Vector2i v : InboundEdges(p.Y))
				AddEdge(v.X,p.X,GetEdge(v.X,v.Y)); // Add will fail if the edge already exists
			
			// Next, we need to copy all the outgoing edges to the alias vertex into the real vertex (keep old data in already existant edges)
			for(Vector2i v : OutboundEdges(p.Y))
				AddEdge(p.X,v.Y,GetEdge(v.X,v.Y)); // Add will fail if the edge already exists
			
			// Lastly, we need to destroy the alias vertex (this will remove all of its edges as well)
			RemoveVertex(p.Y);
			
			// Update the id parameters now that we have something to merge
			id1 = p.X; // This may be different than the ID we provided
			id2 = -1; // This is always -1 since we can only merge vertex IDs now, never modify the edge set
			
			// Now that we've merged, we'll notify our observers of that fact
			e = new MergeEvent<V,E>(this,id1,id2,true);
			NotifyAll(e);
			
			// Now check to see if we have anything else we need to merge
			p = Inspector.FindCommonVertex(this,id1,id2);
		}
		
		return;
	}
	
	public void Subscribe(IObserver<MergeEvent<V,E>> eye)
	{
		Subscribers.AddLast(eye);
		return;
	}
	
	public void Unsubscribe(IObserver<MergeEvent<V,E>> eye)
	{
		Subscribers.Remove(eye);
		return;
	}
	
	/**
	 * Notifies all observers of a merge event.
	 * @param e The merge event to send out.
	 */
	protected void NotifyAll(MergeEvent<V,E> e)
	{
		for(IObserver<MergeEvent<V,E>> eye : Subscribers)
			eye.OnNext(e);
		
		return;
	}
	
	/**
	 * The means by which we identify common vertices.
	 */
	protected GraphAnalyzer<V,E> Inspector;
	
	/**
	 * The observers of this class.
	 */
	protected LinkedList<IObserver<MergeEvent<V,E>>> Subscribers;
	
	/**
	 * A function used to identify common vertices between unconnected components.
	 * @author Dawn Nye
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 */
	@FunctionalInterface public interface GraphAnalyzer<V,E>
	{
		/**
		 * Identifies a common vertex between two unconnected components.
		 * @param g The graph to search in for a common vertex within.
		 * @param id1 If this value is not -1 (an always invalid vertex ID), then this is the vertex ID of a vertex that was added/modified/merged or the source vertex of an added/removed/modified edge if {@code id2} is not -1.
		 * @param id2 If this value is not -1 (an always invalid vertex ID), then this is the vertex ID of the destination vertex of an added/removed/modified edge.
		 * @return
		 * Returns a pair of vertex IDs.
		 * These vertex IDs should correspond to a single vertex which two unconnected components of {@code g} have in common.
		 * The first value returned must be the smaller ID and the second value must be the larger ID.
		 * If there is no such vertex, null is returned instead.
		 * @implNote Note that {@code id1} and {@code id2} will both be -1 only when a vertex is removed.
		 */
		public Vector2i FindCommonVertex(IGraph<V,E> g, int id1, int id2);
	}
	
	/**
	 * Encapsulates the esseence of a merge event for a graph when two vertices are being merged into a single vertex.
	 * @author Dawn Nye
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 */
	public static class MergeEvent<V,E>
	{
		/**
		 * Creates a new merge event.
		 * @param id The ID of the vertex surviving the merging process (this must be the smaller ID).
		 * @param alias The ID of the vertex not surviving the merging process (this must be the larger ID).
		 * @param merged If true, then the vertices have already been merged. If false, then they have not been merged yet.
		 * @throws NullPointerException Thrown if {@code g} is null.
		 */
		public MergeEvent(IGraph<V,E> g, int id, int alias, boolean merged)
		{
			if(g == null)
				throw new NullPointerException();
			
			G = g;
			ID = id;
			Alias = alias;
			
			Merged = merged;
			return;
		}
		
		/**
		 * Determines if this merge event has been sent before the merge occurs.
		 * @return Returns true if the vertices have not yet been merged and false otherwise.
		 */
		public boolean IsBeforeMerge()
		{return !Merged;}
		
		/**
		 * Determines if this merge event has been sent after the merge occurred.
		 * @return Returns true if the vertices have been merged and false otherwise.
		 */
		public boolean IsAfterMerge()
		{return Merged;}
		
		/**
		 * Determines if this is a valid merge event.
		 * @return Returns true if {@code ID} and {@code Alias} are vertex IDs within the graph.
		 */
		public boolean IsValid()
		{return ID < Alias && G.ContainsVertex(ID) && (IsAfterMerge() || G.ContainsVertex(Alias));}
		
		/**
		 * The graph the vertices are in.
		 */
		public final IGraph<V,E> G;
		
		/**
		 * The ID of the first vertex being merged.
		 * This is always the vertex ID that will survive the merging process (and is the smaller of the two IDs).
		 */
		public final int ID;
		
		/**
		 * The ID of the second vertex being merged.
		 * This is always the vertex ID that will not survive the merging process (and is the larger of the two IDs).
		 */
		public final int Alias;
		
		/**
		 * If true, then the vertices have been merged.
		 * If false, then the vertices have not been merged yet.
		 */
		public final boolean Merged;
	}
}