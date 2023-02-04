package gamecore.datastructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.queues.PriorityQueue;
import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.tuples.Pair;

/**
 * A self-balancing Axis-Aligned Bounding Box Tree.
 * Allows for (typically) log time collision checking against static objects.
 * Can also be updated in log time via additions or removals.
 * @author Dawn Nye
 * @param <T> The type of data stored in this AABB Tree.
 */
public class AABBTree<T> implements Collection<T>
{
	/**
	 * Creates a new AABB tree.
	 * @param boundary_extractor The means by which bounding data is extracted.
	 * @throws NullPointerException Thrown if {@code boundary_extractor} or {@code c} is null.
	 */
	public AABBTree(BoundaryExtractor<T> boundary_extractor)
	{
		if(boundary_extractor == null)
			throw new NullPointerException();
		
		BoundingBoxExtractor = boundary_extractor;
		
		Root = null;
		Count = 0;
		
		return;
	}

	/**
	 * Creates a new AABB tree.
	 * @param boundary_extractor The means by which bounding data is extracted.
	 * @param c Initializes the tree to contain the given collection of items.
	 * @throws NullPointerException Thrown if {@code boundary_extractor} or {@code c} is null.
	 */
	public AABBTree(BoundaryExtractor<T> boundary_extractor, Iterable<? extends T> c)
	{
		this(boundary_extractor);
		addAll(c);
		
		return;
	}
	
	public boolean add(T e)
	{
		CellRectangle boundary = BoundingBoxExtractor.ExtractBoundary(e);

		// If we can't generate a valid boundary, we're done
		if(boundary == null || boundary.IsDegenerate())
			return false;

		// We'll always succeed now, so just increment it
		Count++;

		if(Root == null)
		{
			Root = new Node(boundary,e);
			return true;
		}

		Node n = Root;

		while(!n.IsLeaf())
		{
			// Go toward the section that creates the smallest change in area
			float left_area = boundary.Union(n.Left.Boundary).Area() - n.Left.Boundary.Area();
			float right_area = boundary.Union(n.Right.Boundary).Area() - n.Right.Boundary.Area();

			if(left_area <= right_area)
				n = n.Left;
			else
				n = n.Right;
		}

		Node right = new Node(boundary,e);
		Node parent = new Node(n.Parent,n,right);

		// This is a one time only edge case we have to catch
		if(parent.IsRoot())
			Root = parent;

		parent.LinkAdjacentNodes(n.IsRoot() || n.Parent.Right != n);
		parent.UpdateProperties();

		return true;
	}
	
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(add(t))
				ret = true;
		
		return ret;
	}
	
	/**
	 * Adds all of the elements in the specified collection to this collection(optional operation).
	 * The behavior of this operation is undefined ifthe specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if thespecified collection is this collection, and this collection isnonempty.)
	 * @param c collection containing elements to be added to this collection
	 * @return {@code true} if this collection changed as a result of the call
	 * @throws UnsupportedOperationException - if the addAll operation is not supported by this collection
	 * @throws ClassCastException - if the class of an element of the specified collection prevents it from being added to this collection
	 * @throws NullPointerException - if the specified collection contains a null element and this collection does not permit null elements, or if the specified collection is null
	 * @throws IllegalArgumentException - if some property of an element of the specified collection prevents it from being added to this collection
	 * @throws IllegalStateException - if not all the elements can be added at this time due to insertion restrictions
	 */
	public boolean addAll(Iterable<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(add(t))
				ret = true;
		
		return ret;
	}
	
	public boolean remove(Object o)
	{
		Node n = Find((T)o);
		
		if(n == null)
			return false;
		
		// We WILL remove something now, so just decrement it already
		Count--;
		
		// If we get the root, then there's nothing else in the tree since only leaves can hold data
		if(n.IsRoot())
		{
			Root = null;
			return true;
		}
		
		// If we get a child of the root, then we need to handle this edge case
		if(n.Parent.IsRoot())
		{
			if(Root.Left == n)
				Root = Root.Right;
			else
				Root = Root.Left;

			Root.Parent = null;
			return true;
		}

		// We're not in a special case, so just go ahead and get stuff done
		// Grab the node that we're not going to get rid of
		if(n.Parent.Left == n)
			n = n.Parent.Right;
		else
			n = n.Parent.Left;

		// Set n to be the appropriate child of the grandparent
		if(n.Parent.Parent.Left == n.Parent)
			n.Parent.Parent.Left = n;
		else
			n.Parent.Parent.Right = n;

		// The new parent is the old grandparent
		n.Parent = n.Parent.Parent;

		// Now we can just recurse up the tree
		n.Parent.UpdateProperties();

		return true;
	}
	
	public boolean removeAll(Collection<?> c)
	{
		boolean ret = false;
		
		for(Object obj : c)
			while(remove(obj))
				ret = true;
		
		return ret;
	}
	
	public boolean retainAll(Collection<?> c)
	{
		return false;
	}
	
	/**
	 * Finds the first instance of {@code item} via a breadth first search of the tree.
	 * @param item The item to find.
	 * @return The first instance of the given item in the tree or null if it doesn't exist.
	 */
	protected Node Find(T item)
	{
		CellRectangle boundary = BoundingBoxExtractor.ExtractBoundary(item);

		// If we can't generate a valid boundary, we're done
		if(boundary == null || boundary.IsDegenerate())
			return null;
		
		Queue<Node> frontier = new Queue<Node>();
		
		if(Root != null && boundary.Intersects(Root.Boundary))
			frontier.Enqueue(Root);
		
		while(frontier.Count() > 0)
		{
			Node n = frontier.Dequeue();
			
			if(n.IsLeaf())
				if(n.Data == item || n.Data != null && n.Data.equals(item))
					return n;
				else
					continue;
			
			if(boundary.Intersects(n.Left.Boundary))
				frontier.Enqueue(n.Left);
			
			if(boundary.Intersects(n.Right.Boundary))
				frontier.Enqueue(n.Right);
		}
		
		return null;
	}
	
	public boolean contains(Object obj)
	{return Find((T)obj) != null;}
	
	public boolean containsAll(Collection<?> c)
	{
		for(Object obj : c)
			if(!contains(obj))
				return false;
		
		return true;
	}
	
	public void clear()
	{
		Root = null;
		Count = 0;
		
		return;
	}
	
	/**
	 * Collects all of the items which intersect with the given one.
	 * @param item The item to look check intersections against.
	 * @return Returns an enumerable list of items which intersect with the given one.
	 */
	public Iterable<T> Query(T item)
	{
		LinkedList<T> ret = new LinkedList<T>();
		CellRectangle boundary = BoundingBoxExtractor.ExtractBoundary(item);
		
		// If we can't generate a valid boundary, we're done
		if(boundary == null || boundary.IsDegenerate())
			return ret;
		
		Queue<Node> frontier = new Queue<Node>();
		
		if(Root != null && boundary.Intersects(Root.Boundary))
			frontier.Enqueue(Root);
		
		while(frontier.Count() > 0)
		{
			Node n = frontier.Dequeue();
			
			if(n.IsLeaf())
			{
				ret.AddLast(n.Data);
				continue;
			}
			
			if(boundary.Intersects(n.Left.Boundary))
				frontier.Enqueue(n.Left);
			
			if(boundary.Intersects(n.Right.Boundary))
				frontier.Enqueue(n.Right);
		}
		
		return ret;
	}
	
	/**
	 * Collects all of the items which intersect with the given one.
	 * @param item The item to look check intersections against.
	 * @param smallest_first If true, then the ordering of the returned types will have those with the smallest intersection first. If false, the largest intersections will come first instead.
	 * @return Returns an enumerable list of items which intersect with the given one in order.
	 */
	public Iterable<T> Query(T item, boolean smallest_first)
	{
		CellRectangle boundary = BoundingBoxExtractor.ExtractBoundary(item);

		// If we can't generate a valid boundary, we're done
		if(boundary == null || boundary.IsDegenerate())
			return new LinkedList<T>();
		
		PriorityQueue<Pair<T,Integer>> q = new PriorityQueue<Pair<T,Integer>>((a,b) -> a.Item2.compareTo(b.Item2),smallest_first);
		Queue<Node> frontier = new Queue<Node>();
		
		if(Root != null && boundary.Intersects(Root.Boundary))
			frontier.Enqueue(Root);

		while(frontier.Count() > 0)
		{
			Node n = frontier.Dequeue();

			if(n.IsLeaf())
			{
				// Intersects is way faster than intersect, and we typically won't need to intersect often enough for the repeated operation here to matter rather than enqueing area with the data
				q.Enqueue(new Pair<T,Integer>(n.Data,boundary.Intersection(n.Boundary).Area()));
				continue;
			}
			
			if(boundary.Intersects(n.Left.Boundary))
				frontier.Enqueue(n.Left);

			if(boundary.Intersects(n.Right.Boundary))
				frontier.Enqueue(n.Right);
		}

		LinkedList<T> ret = new LinkedList<T>();

		for(Pair<T,Integer> e : q)
			ret.AddLast(e.Item1);

		return ret;
	}
	
	/**
	 * Collects all of the items that collide with the given area.
	 * @param area The area to look for things in.
	 * @return Returns an enumerable list of items which intersect with the given area.
	 */
	public Iterable<T> DirectQuery(CellRectangle area)
	{
		LinkedList<T> ret = new LinkedList<T>();

		// If we can't generate a valid boundary, we're done
		if(area == null || area.IsDegenerate())
			return ret;

		Queue<Node> frontier = new Queue<Node>();
		
		if(Root != null && area.Intersects(Root.Boundary))
			frontier.Enqueue(Root);

		while(frontier.Count() > 0)
		{
			Node n = frontier.Dequeue();

			if(n.IsLeaf())
			{
				ret.AddLast(n.Data);
				continue;
			}
			
			if(area.Intersects(n.Left.Boundary))
				frontier.Enqueue(n.Left);

			if(area.Intersects(n.Right.Boundary))
				frontier.Enqueue(n.Right);
		}

		return ret;
	}
	
	/**
	 * Collects all of the items that collide with the given area.
	 * @param area The area to look for things in.
	 * @param smallest_first If true, then the ordering of the returned types will have those with the smallest intersection first. If false, the largest intersections will come first instead.
	 * @return Returns an enumerable list of items which intersect with the given area in order.
	 */
	public Iterable<T> DirectQuery(CellRectangle area, boolean smallest_first)
	{
		// If we can't generate a valid boundary, we're done
		if(area == null || area.IsDegenerate())
			return new LinkedList<T>();

		PriorityQueue<Pair<T,Integer>> q = new PriorityQueue<Pair<T,Integer>>((a,b) -> a.Item2.compareTo(b.Item2),smallest_first);
		Queue<Node> frontier = new Queue<Node>();
		
		if(Root != null && area.Intersects(Root.Boundary))
			frontier.Enqueue(Root);

		while(frontier.Count() > 0)
		{
			Node n = frontier.Dequeue();

			if(n.IsLeaf())
			{
				// Intersects is way faster than intersect, and we typically won't need to intersect often enough for the repeated operation here to matter rather than enqueing area with the data
				q.Enqueue(new Pair<T,Integer>(n.Data,area.Intersection(n.Boundary).Area()));
				continue;
			}
			
			if(area.Intersects(n.Left.Boundary))
				frontier.Enqueue(n.Left);

			if(area.Intersects(n.Right.Boundary))
				frontier.Enqueue(n.Right);
		}

		LinkedList<T> ret = new LinkedList<T>();

		for(Pair<T,Integer> e : q)
			ret.AddLast(e.Item1);

		return ret;
	}
	
	public int size()
	{return Count;}
	
	public boolean isEmpty()
	{return size() == 0;}
	
	public Iterator<T> iterator()
	{return new NodeEnumerator(Root);}
	
	public Object[] toArray()
	{
		Object[] ret = new Object[size()];
		int i = 0;
		
		for(T t : this)
			ret[i++] = t;
		
		return ret;
	}
	
	public <E> E[] toArray(E[] a)
	{
		E[] ret = a.length >= size() ? a : (E[])Array.newInstance(a.getClass().getComponentType(),size());
		int i = 0;
		
		for(T t : this)
			ret[i++] = (E)t;
		
		if(ret.length > size())
			ret[size()] = null;
		
		return ret;
	}
	
	/**
	 * The root node of this tree.
	 */
	protected Node Root;
	
	/**
	 * The number of items in this tree.
	 */
	protected int Count;
	
	/**
	 * Extracts bounding boxes from data.
	 */
	protected BoundaryExtractor<T> BoundingBoxExtractor;
	
	/**
	 * A node for the AABB tree.
	 * Note that these nodes will ALWAYS have either no children or two children by design.
	 * @author Dawn Nye
	 */
	protected class Node
	{
		/**
		 * Creates a new leaf node.
		 * @param boundary The boundary of the leaf.
		 * @param data The data inside the leaf.
		 */
		public Node(CellRectangle boundary, T data)
		{
			Parent = Left = Right = null;

			Boundary = boundary;
			Data = data;

			Height = 0;
			return;
		}
		
		/**
		 * Creates an internal node.
		 * @param parent The parent of the node.
		 * @param l The left node. This should not be null.
		 * @param r The right node. This should not be null.
		 */
		public Node(Node parent, Node l, Node r)
		{
			Parent = parent;
			Left = l;
			Right = r;

			Boundary = null; // We'll asign this later
			Data = null; // Internal nodes don't get data

			Height = -1; // Leave this unassigned for now to deal with all at once later
			return;
		}

		/**
		 * Links the parent and children nodes (if they exist) to this one.
		 * To use this function, the node's parent, left, and right must all be properly assigned.
		 * @param left If true, then this is a left node of its parent (if it has one).
		 */
		public void LinkAdjacentNodes(boolean left)
		{
			if(!IsRoot())
				if(left)
					Parent.Left = this;
				else
					Parent.Right = this;

			// Nodes either have zero or two children
			if(Left != null)
			{
				Left.Parent = this;
				Right.Parent = this;
			}

			return;
		}
		
		/**
		 * Updates the properties of this node upward.
		 * If must be the case that both the left and right child are not null since this will never be called on leaves (they're harmless).
		 */
		public void UpdateProperties()
		{
			boolean needs_updating = false;
			
			// ROTATION LOGIC
			// First check if we need to rotate the tree (this can only ever happen once per add)
			if(Math.abs(Left.Height - Right.Height) > 1)
			{
				Node[] order = null;

				// Check if we're left heavy
				if(Left.Height > Right.Height)
				{
					// Check if we're left-left heavy
					if(Left.Left.Height > Left.Right.Height)
					{
						// Assign the correct grandchildren
						order = PickMinimizingOrder(Left.Left.Left,Left.Left.Right,Left.Right,Right);

						// Move this node to a useful position
						Right = Left.Left;
					}
					else if(Left.Left.Height < Left.Right.Height) // Check if we're left-right heavy
					{
						// Assign the correct grandchildren
						order = PickMinimizingOrder(Left.Left,Left.Right.Left,Left.Right.Right,Right);
						
						// Move this node to a useful position
						Right = Left.Right;
					}
					else // This is a special case we can only reach from removal
					{
						// Collect the nodes of interest
						Node A = Left.Left;
						Node B = Left.Right;
						Node C = Right;
						Node D = Left;

						// Determine which node we should move to the right
						float AC_area = A.Boundary.Union(C.Boundary).Area();
						float BC_area = B.Boundary.Union(C.Boundary).Area();

						if(AC_area < BC_area) // We use strictly less than here to preserve left-right order when all else is equal
						{
							D.Left = A;
							Left = B;
						}
						else
						{
							D.Left = B;
							Left = A;
						}

						// Move the old dummy node to a new position to make itself useful
						Right = D;
						D.Right = C;

						// Link everything up
						LinkAdjacentNodes(!IsRoot() && Parent.Left == this);
						Right.LinkAdjacentNodes(false);

						// Assign the new boundary to D
						D.Boundary = D.Left.Boundary.Union(D.Right.Boundary);
					}

					// Update the heights of the children (grandchildren are unchanged) so long as we're not in the special removal case
					if(order != null)
						Left.Height--; // The tree is balanced now, so we can just subtract one (the new right doesn't change)
				}
				else // We're right heavy
				{
					// Check if we're right-right heavy
					if(Right.Left.Height < Right.Right.Height)
					{
						// Assign the correct grandchildren
						order = PickMinimizingOrder(Left,Right.Left,Right.Right.Left,Right.Right.Right);

						// Move this node to a useful position
						Left = Right.Right;
					}
					else if(Right.Left.Height > Right.Right.Height) // Check if we're right-left heavy
					{
						// Assign the correct grandchildren
						order = PickMinimizingOrder(Left,Right.Left.Left,Right.Left.Right,Right.Right);

						// Move this node to a useful position
						Left = Right.Left;
					}
					else // This is a special case we can only reach from removal
					{
						// Collect the nodes of interest
						Node A = Right.Left;
						Node B = Right.Right;
						Node C = Left;
						Node D = Right;

						// Determine which node we should move to the right
						float AC_area = A.Boundary.Union(C.Boundary).Area();
						float BC_area = B.Boundary.Union(C.Boundary).Area();

						if(AC_area < BC_area) // We use strictly less than here to preserve left-right order when all else is equal
						{
							D.Right = A;
							Right = B;
						}
						else
						{
							D.Right = B;
							Right = A;
						}

						// Move the old dummy node to a new position to make itself useful
						Left = D;
						D.Left = C;

						// Link everything up
						LinkAdjacentNodes(!IsRoot() && Parent.Left == this);
						Left.LinkAdjacentNodes(true);

						// Assign the new boundary to D
						D.Boundary = D.Left.Boundary.Union(D.Right.Boundary);
					}

					// Update the heights of the children (grandchildren are unchanged) so long as we're not in the special removal case
					if(order != null)
						Right.Height--; // The tree is balanced now, so we can just subtract one (the new left doesn't change)
				}

				// Assign the grandchildren in the appropriate order so long as we're not in the special removal case
				if(order != null)
				{
					Left.Left = order[0];
					Left.Right = order[1];
					Right.Left = order[2];
					Right.Right = order[3];

					// In all cases, we first link this node and then its children
					LinkAdjacentNodes(!IsRoot() && Parent.Left == this);

					Left.LinkAdjacentNodes(true);
					Right.LinkAdjacentNodes(false);

					// Now we can update the boundaries
					Left.Boundary = Left.Left.Boundary.Union(Left.Right.Boundary);
					Right.Boundary = Right.Left.Boundary.Union(Right.Right.Boundary);
				}
			}
			// END ROTATION LOGIC

			// Update the height of this node
			int h = Math.max(Left.Height,Right.Height) + 1;
			
			// If the height is unchanged, we don't need to propogate it upward
			if(Height != h)
			{
				Height = h;
				needs_updating = true;
			}

			// Update the boundary of this node
			CellRectangle r = Left.Boundary.Union(Right.Boundary);

			// If the boundary is unchanged, we don't need to propogate it upward
			if(Boundary != r)
			{
				Boundary = r;
				needs_updating = true;
			}

			// If we're not the root node and have something to do, propogate that information upward
			if(!IsRoot() && needs_updating)
				Parent.UpdateProperties();

			return;
		}

		/// <summary>
		/// Arranges the nodes so that the first two nodes returned and the last two nodes returned minimize the sum of their minimum bounding box areas.
		/// </summary>
		/// <param name="A">The first node.</param>
		/// <param name="B">The second node.</param>
		/// <param name="C">The third node.</param>
		/// <param name="D">The fourth node.</param>
		protected Node[] PickMinimizingOrder(Node A, Node B, Node C, Node D)
		{
			float ABCD_area = A.Boundary.Union(B.Boundary).Area() + C.Boundary.Union(D.Boundary).Area();
			float ACBD_area = A.Boundary.Union(C.Boundary).Area() + B.Boundary.Union(D.Boundary).Area();
			float ADBC_area = A.Boundary.Union(D.Boundary).Area() + B.Boundary.Union(C.Boundary).Area();
			
			if(ABCD_area <= ACBD_area)
				if(ABCD_area <= ADBC_area)
					return (Node[])new Object[] {A,B,C,D};
				else
					return (Node[])new Object[] {A,D,B,C};
			else if(ACBD_area <= ADBC_area)
				return (Node[])new Object[] {A,C,B,D};

			// else
			return (Node[])new Object[] {A,D,B,C};
		}
		
		/**
		 * Outputs the string representation of the boundary associated with this node.
		 */
		@Override public String toString()
		{return Boundary.toString();}

		/**
		 * Determines if this is a root node by checking if it has no parent.
		 * @return Returns true if this is a root node and false otherwise.
		 */
		public boolean IsRoot()
		{return Parent == null;}

		/**
		 * Determines if this node is a leaf. 
		 * @return Returns ture if this is a leaf and false otherwise.
		 */
		public boolean IsLeaf()
		{return Left == null && Right == null;}

		/**
		 * The parent of this node, if any.
		 */
		public Node Parent;

		/**
		 * The left child of this node, if any.
		 */
		public Node Left;

		/**
		 * The right child of this node, if any.
		 */
		public Node Right;

		/**
		 * The bounding box of this node.
		 */
		public CellRectangle Boundary;

		/**
		 * The data in this node.
		 */
		public T Data;

		/**
		 * The height of this node in its tree.
		 */
		public int Height;
	}
	
	
	/**
	 * Enumerates over the nodes of an AABB tree.
	 * @author Dawn Nye
	 */
	protected class NodeEnumerator implements Iterator<T>
	{
		/**
		 * Creates a new node enumerator to iterate over the tree.
		 * @param root The root node of the AABB tree.
		 */
		public NodeEnumerator(Node root)
		{
			Root = root;
			CurrentNode = null;
			NextNode =  null;
			
			done = Root == null;
			return;
		}
		
		public boolean hasNext()
		{return MoveNext();}
		
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			// After a call to hasNext, NextNode is always defined it hasNext returned true
			CurrentNode = NextNode;
			NextNode = null;
			
			return CurrentNode.Data;
		}
		
		/**
		 * Places the next element from {@code CurrentNode} into {@code Next Node} or null if no such node exists.
		 * @return Returns true if there is a next element and false if one does not exist.
		 */
		protected boolean MoveNext()
		{
			if(NextNode != null)
				return true;
			
			if(done)
				return false;
			
			NextNode = CurrentNode;
			
			// If this is the first item we're going to, trail down leftward until we hit a leaf (there WILL be one eventually)
			if(NextNode == null)
			{
				if(Root == null)
					return false;

				NextNode = Root;

				while(!NextNode.IsLeaf())
					NextNode = NextNode.Left;

				return true;
			}

			// Crawl up until we're a left child
			while(!NextNode.IsRoot() && NextNode.Parent.Right == NextNode)
				NextNode = NextNode.Parent;

			// If we broke on the root, we're done
			if(NextNode.IsRoot())
			{
				NextNode = null;
				done = true;
				
				return false;
			}

			// Now that we know we're a left child, go up one, right one (these nodes MUST exist), and then go all the way left
			NextNode = NextNode.Parent.Right;

			while(!NextNode.IsLeaf())
				NextNode = NextNode.Left;

			return true;
		}

		/**
		 * Obtains the current node of the iterator.
		 * @return Returns the current node of the iterator.
		 */
		public Node CurrentNode()
		{return CurrentNode;}
		
		/**
		 * The current node of the enumerator.
		 */
		protected Node CurrentNode;
		
		/**
		 * The next node.
		 * This will be null when it is not relevant.
		 */
		protected Node NextNode;
		
		/**
		 * The starting node of this enumeration. 
		 */
		protected Node Root;
		
		/**
		 * If true, then enumeration has finished.
		 */
		protected boolean done;
	}
	
	/**
	 * Extracts a bounding box from a generic type.
	 * @author Dawn Nye
	 * @param <E> The type of data to extract a bounding box from.
	 */
	@FunctionalInterface public interface BoundaryExtractor<E>
	{
		/**
		 * Extracts a bounding box from a generic piece of data.
		 * @param data The data to extract a bounding box from.
		 * @return Returns the bounding box for the provided data or null if no bounding box exists (perhaps when {@code data} is null).
		 */
		public abstract CellRectangle ExtractBoundary(E data);
	}
}
