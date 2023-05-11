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
 * Their data will be merged together using a function specified at construction time.
 * This data update will occur immediately after the function is called rather than delaying action until after all other data updates have been collected.
 * This data update occurs before edge data updates.
 * <br><br>
 * Similarly, if two vertices are merged into a single vertex, then their edges are merged together as well.
 * Edge data is merged together similarly to vertex data by using a function specified at construction time.
 * These data updates will occur immediately after the function is called rather than delaying action until after all other data updates have been collected.
 * These data updates occur after the vertex data update.
 * In a directed graph, outbound edges are also updated before inbound edges.
 * Internal to each category, alias edges (those between the vertex that will not survive the merge and another vertex) are updated before nonalias edges.
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
	 * @param v_select The means by which vertex data is merged together.
	 * @param e_select The means by which edge data is merged together.
	 * @throws NullPointerException Thrown if {@code inspector} is null.
	 */
	public SpeculativeGraph(boolean dir, GraphAnalyzer<V,E> inspector, VertexSelector<V,E> v_select, EdgeSelector<V,E> e_select)
	{
		super(dir);
		
		if(inspector == null)
			throw new NullPointerException();
		
		Inspector = inspector;
		VSelect = v_select;
		ESelect = e_select;
		
		Subscribers = new LinkedList<IObserver<MergeEvent<V,E>>>();
		MergeInProgress = false;
		
		return;
	}
	
	@Override public int AddVertex(V vertex)
	{
		int ret = super.AddVertex(vertex);
		
		if(ret > -1)
			ret = TryMerge(ret,-1);
		
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
	 * @return Returns the resulting vertex ID of {@code id1} if that vertex is merged into another vertex or {@code id1} if no change occurred.
	 * @implNote Note that {@code id1} and {@code id2} will both be -1 only when a vertex is removed.
	 */
	protected int TryMerge(int id1, int id2)
	{
		// Keep track of what id1 becomes
		int ret = id1;
		
		// If we're already merging, don't start up another merge
		if(MergeInProgress)
			return ret;
		
		MergeInProgress = true;
		
		// Find the initial merge (if we have one at all)
		Vector2i p = Inspector.FindCommonVertex(this,id1,id2,0);
		
		// We will want to know what merge we're on
		int merge = 0;
		
		// If we're given two vertices to merge and they're valid, do it
		while(p != null)
		{
			// We'll need to verify if what we got was correct
			MergeEvent<V,E> e = new MergeEvent<V,E>(this,p.X,p.Y,false,++merge);
			
			// If this merge event is invalid, then we can't merge
			if(!e.IsValid())
				break;
			
			// Notify all observers
			NotifyAll(e);
			
			// Update our return value if necessary
			if(p.Y == ret)
				ret = p.X; // We keep the smaller vertex ID
			
			// Update the vertex data first, as it's probably independnet of the edge data
			SetVertex(p.X,VSelect.SelectVertexData(this,p.X,p.Y));
			
			// Merge like you've never merged before!
			// First, we need to copy all the outgoing edges to the alias vertex into the real vertex (keep old data in already existant edges)
			for(Vector2i v : OutboundEdges(p.Y))
				PutEdge(p.X,v.Y,ESelect.SelectEdgeData(this,new Vector2i(p.X,v.Y),v));
			
			// We need to update nonalias edges as well if we haven't already
			for(Vector2i v : OutboundEdges(p.X))
				if(!ContainsEdge(p.Y,v.Y))
					PutEdge(p.X,v.Y,ESelect.SelectEdgeData(this,v,new Vector2i(p.Y,v.Y)));
			
			// Next, we need to copy all the incoming edges to the alias vertex into the real vertex (keep old data in already existant edges)
			// We only need to do this if the graph is directed
			if(IsDirected())
			{
				for(Vector2i v : InboundEdges(p.Y))
					PutEdge(v.X,p.X,ESelect.SelectEdgeData(this,new Vector2i(v.X,p.X),v)); // Add will fail if the edge already exists
			
				// We need to update nonalias edges as well if we haven't already
				for(Vector2i v : InboundEdges(p.X))
					if(!ContainsEdge(v.X,p.Y))
						PutEdge(v.X,p.X,ESelect.SelectEdgeData(this,v,new Vector2i(v.Y,p.Y)));
			}
			
			// Lastly, we need to destroy the alias vertex (this will remove all of its edges as well)
			RemoveVertex(p.Y);
			
			// Now that we've merged, we'll notify our observers of that fact
			e = new MergeEvent<V,E>(this,id1,id2,true,merge);
			NotifyAll(e);
			
			// Update the id parameters now that we have something to merge
			id1 = p.X; // This may be different than the ID we provided
			id2 = -1; // This is always -1 since we can only merge vertex IDs now, never modify the edge set
			
			// Now check to see if we have anything else we need to merge
			p = Inspector.FindCommonVertex(this,id1,id2,merge);
		}
		
		MergeInProgress = false;
		return ret;
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
	 * Used to merge vertex data together.
	 */
	protected VertexSelector<V,E> VSelect;
	
	/**
	 * Used to merge edge data together.
	 */
	protected EdgeSelector<V,E> ESelect;
	
	/**
	 * The observers of this class.
	 */
	protected LinkedList<IObserver<MergeEvent<V,E>>> Subscribers;
	
	/**
	 * If true, there is a merge in progress and we don't want to try merging things that we're already merging as they get merged.
	 */
	protected boolean MergeInProgress;
	
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
		 * @param merge_num This is the number of merges already performed since the last graph operation.
		 * @return
		 * Returns a pair of vertex IDs.
		 * These vertex IDs should correspond to a single vertex which two unconnected components of {@code g} have in common.
		 * The first value returned must be the smaller ID and the second value must be the larger ID.
		 * If there is no such vertex, null is returned instead.
		 * @implNote Note that {@code id1} and {@code id2} will both be -1 only when a vertex is removed.
		 */
		public Vector2i FindCommonVertex(IGraph<V,E> g, int id1, int id2, int merge_num);
	}
	
	/**
	 * Encapsulates vertex data selection when two vertices are merged.
	 * @author Dawn Nye
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 */
	@FunctionalInterface public interface VertexSelector<V,E>
	{
		/**
		 * Selects which vertex data should survive when the vertices with IDs {@code id1} and {@code id2} are merged.
		 * @param g The graph the merge is being performed in.
		 * @param id1 The first vertex ID to merge. This is the vertex that will survive the merge.
		 * @param id2 The second vertex ID to merge. This is the vertex that will not survive the merge.
		 * @return Returns the vertex data to place in the merged vertex. This can be either datum of the old vertices or a new combined datum.
		 */
		public V SelectVertexData(IGraph<V,E> g, int id1, int id2);
	}
	
	/**
	 * Encapsulates edge data selection when two edges are merged.
	 * @author Dawn Nye
	 * @param <V> The type of data in the vertices.
	 * @param <E> The type of data in the edges.
	 */
	@FunctionalInterface public interface EdgeSelector<V,E>
	{
		/**
		 * Selects which edge data should survive when the edges with (source,destination) IDs specified by {@code e1} and {@code e2} are merged.
		 * @param g The graph the merge is being performed in.
		 * @param e1
		 * The first edge to merge.
		 * This edge is between the vertex that will survive the merge and another vertex (potentially the vertex that will not survive the merge).
		 * The {@code X} value specifies the source vertex ID and the {@code Y} value species the destination vertex ID.
		 * @param e2
		 * The second edge to merge.
		 * This edge is between the vertex that will not survive the merge and another vertex (potentially the vertex that will survive the merge).
		 * The {@code X} value specifies the source vertex ID and the {@code Y} value species the destination vertex ID.
		 * @return Returns the edge data to place in the merged edge. This can be either datum of the old edges or a new combined datum.
		 */
		public E SelectEdgeData(IGraph<V,E> g, Vector2i e1, Vector2i e2);
	}
	
	/**
	 * Encapsulates the essence of a merge event for a graph when two vertices are being merged into a single vertex.
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
		 * @param merge_num The merge number.
		 * @throws NullPointerException Thrown if {@code g} is null.
		 */
		public MergeEvent(IGraph<V,E> g, int id, int alias, boolean merged, int merge_num)
		{
			if(g == null)
				throw new NullPointerException();
			
			G = g;
			ID = id;
			Alias = alias;
			
			Merged = merged;
			MergeNumber = merge_num;
			
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
		 * This value will always be invalid after the merge has been performed.
		 */
		public final int Alias;
		
		/**
		 * After a graph operation, multiple merges may be performed.
		 * This event represents the {@code MergeNumber}th merge.
		 */
		public final int MergeNumber;
		
		/**
		 * If true, then the vertices have been merged.
		 * If false, then the vertices have not been merged yet.
		 */
		public final boolean Merged;
	}
}