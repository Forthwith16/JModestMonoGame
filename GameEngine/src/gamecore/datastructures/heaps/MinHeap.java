package gamecore.datastructures.heaps;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.LinkedList;
import gamecore.datastructures.tuples.Triple;

/**
 * Implements a min heap.
 * @author Dawn Nye
 * @param <T> The type of data in the heap.
 */
public class MinHeap<T> implements IHeap<T>
{
	/**
	 * Creates a new min heap with the given ordering.
	 * @param cmp The means by which items are sorted.
	 */
	public MinHeap(Comparator<T> cmp)
	{
		Ordering = cmp;
		Root = null;
		
		return;
	}
	
	/**
	 * Creates a new min heap with the given ordering.
	 * @param seed The initial set of objects to fill this heap with.
	 * @param cmp The means by which items are sorted.
	 */
	public MinHeap(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		Ordering = cmp;

		LinkedList<Triple<T,Node,Boolean>> q = new LinkedList<Triple<T,Node,Boolean>>(); // The item, the parent, and a left child flag
		Iterator<? extends T> iter = seed.iterator();

		// If we have any elements whatsoever, initialize them here so that we don't need a special case in the loop
		if(iter.hasNext())
			Root = new Node(iter.next(),null,null,null);

		// Queue up the root's children if they exist
		if(iter.hasNext())
			q.AddLast(new Triple<T,Node,Boolean>(iter.next(),Root,true));

		if(iter.hasNext())
			q.AddLast(new Triple<T,Node,Boolean>(iter.next(),Root,false));

		// Now we can loop normally
		while(!q.isEmpty())
		{
			// Get the next item to put into the heap
			Triple<T,Node,Boolean> t = q.RemoveFront();
			Node n = new Node(t.Item1,t.Item2,null,null);
			
			// If this is supposed to be a left child, make it so; similarly with right children
			if(t.Item3)
				t.Item2.Left = n;
			else
				t.Item2.Right = n;

			// If there's enough stuff left for a left child, make it so
			if(iter.hasNext())
				q.AddLast(new Triple<T,Node,Boolean>(iter.next(),n,true));

			// If there's enough stuff left for a right child, make it so
			if(iter.hasNext())
				q.AddLast(new Triple<T,Node,Boolean>(iter.next(),n,false));
		}

		if(Root != null)
			FastHeapify(Root);
		
		return;
	}
	
	/**
	 * Heapifies the heap in linear time.
	 * @param n The current heap root.
	 */
	protected void FastHeapify(Node n)
	{
		n.Count = 1;

		if(n.Left != null)
		{
			FastHeapify(n.Left);
			n.Count += n.Left.Count;
		}

		if(n.Right != null)
		{
			FastHeapify(n.Right);
			n.Count += n.Right.Count;
		}

		PercolateDown(n);
		return;
	}
	
	/**
	 * Given a node, percolates its data downward to maintain the heap property.
	 * @param n The node to percolate down.
	 */
	protected void PercolateDown(Node n)
	{
		if(n.Left == null && n.Right == null)
			return;

		if(n.Left != null)
		{
			// If Right has nothing or is bigger than Left, we only care about what's going on with Left
			if(n.Right == null || Ordering.compare(n.Left.Item,n.Right.Item) < 0)
			{
				if(Ordering.compare(n.Item,n.Left.Item) > 0)
				{
					Swap(n,n.Left);
					PercolateDown(n.Left);
				}

				return;
			}
			else if(n.Right != null && Ordering.compare(n.Item,n.Right.Item) > 0) // This favors swaping with Right when Left and Right are equal, but that's fine
			{
				Swap(n,n.Right);
				PercolateDown(n.Right);
			}

			return;
		}

		if(n.Right != null && Ordering.compare(n.Item,n.Right.Item) > 0)
		{
			Swap(n,n.Right);
			PercolateDown(n.Right);
		}
		
		return;
	}
	
	public T RemoveTop()
	{return RemoveMin();}
	
	/**
	 * Gets and removes the minimum element from this heap.
	 * @return Returns the minmum element of this heap or null if the heap is empty.
	 */
	public T RemoveMin()
	{
		if(IsEmpty())
			return null;

		T ret = Root.Item;

		Node n = GetLastNode(Root);
		
		// Check if we're removing the root
		if(n.Parent == null)
		{
			Root = null;
			return ret;
		}

		Swap(Root,n);

		n = n.Parent;
		SubtractOne(n);

		if(n.Right != null)
			n.Right = null;
		else
			n.Left = null;
		
		PercolateDown(Root);
		return ret;
	}

	public boolean Add(T t)
	{
		if(Root == null)
		{
			Root = new Node(t,null,null,null);
			Root.Count = 1;

			return true;
		}

		PercolateUp(AddNewNode(Root,t));
		return true;
	}
	
	public boolean AddAll(Iterable<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(Add(t))
				ret = true;
		
		return ret;
	}

	/**
	 * Maintains the heap property upwards.
	 * @param n The node whose data we want to percolate up.
	 */
	protected void PercolateUp(Node n)
	{
		if(n.Parent == null)
			return;

		if(Ordering.compare(n.Item,n.Parent.Item) < 0)
		{
			Swap(n,n.Parent);
			PercolateUp(n.Parent);
		}

		return;
	}
	
	/**
	 * Adds and returns a new node at the first available space in the head.
	 * @param n The subheap root.
	 * @param t The item to add.
	 * @return Returns the new node created in the heap.
	 */
	protected Node AddNewNode(Node n, T t)
	{
		if(n.Right == null) // If the right child is empty, we're at a base case
			if(n.Left == null)
			{
				n.Left = new Node(t,n,null,null);
				n.Left.Count = 0;

				AddOne(n.Left);
				return n.Left;
			}
			else
			{
				n.Right = new Node(t,n,null,null);
				n.Right.Count = 0;

				AddOne(n.Right);
				return n.Right;
			}

		// There's room on the left if both sides have equal size or if the left isn't full yet
		if(n.Left.Count == n.Right.Count || CountSetBits(n.Left.Count + 1) != 1)
			return AddNewNode(n.Left,t);

		return AddNewNode(n.Right,t);
	}
	
	/**
	 * Subtracts one from the count of this heap. 
	 * @param n The subheap to subtract one count upward from.
	 */
	protected void SubtractOne(Node n)
	{
		n.Count--;

		if(n.Parent == null)
			return;
		
		SubtractOne(n.Parent);
		return;
	}

	/**
	 * Adds one to the count of this heap.
	 * @param n The subheap to add one count upward to.
	 */
	protected void AddOne(Node n)
	{
		n.Count++;

		if(n.Parent == null)
			return;

		AddOne(n.Parent);
		return;
	}

	/**
	 * Gets the last Node in the heap.
	 * @param n The subheap to grab the last node of.
	 * @return Returns the last node in the heap.
	 */
	protected Node GetLastNode(Node n)
	{
		if(n.Right == null) // If we don't have two children, we're at a base case
			if(n.Left == null)
				return n;
			else
				return n.Left; // We can only ever have one child if we're directly above the last node

		// To be a full binary tree and proceed right, the left node have one less than a power of two nodes in it AND the right one must not
		if(n.Left.Count != n.Right.Count && (CountSetBits(n.Left.Count + 1) != 1 || CountSetBits(n.Right.Count + 1) == 1))
			return GetLastNode(n.Left);
		
		return GetLastNode(n.Right);
	}

	/**
	 * Counts the number of set bits in {@code n}.
	 * @param n The integer to count the set bits of.
	 * @return Returns the number of set bits in {@code n}
	 */
	protected int CountSetBits(int n)
	{
		int ret = 0;

		while(n != 0)
		{
			n &= (n - 1);
			ret++;
		}
		
		return ret;
	}

	/**
	 * Swaps the data in the two given nodes.
	 * @param n1 The first node to swap.
	 * @param n2 The second node to swap.
	 */
	protected void Swap(Node n1, Node n2)
	{
		T temp = n1.Item;
		n1.Item = n2.Item;
		n2.Item = temp;

		return;
	}
	
	public T Top()
	{return Min();}
	
	/**
	 * Returns the minimum element of this heap (or T's default value if there is none).
	 */
	public T Min()
	{return IsEmpty() ? null : Root.Item;}
	
	public void Clear()
	{
		Root = null;
		return;
	}

	public int Count()
	{
		if(Root == null)
			return 0;

		return Root.Count;
	}
	
	public boolean IsEmpty()
	{return Count() == 0;}
	
	public Iterator<T> iterator()
	{return new MinHeapEnumerator(Root,Ordering);}
	
	@Override public String toString()
	{
		if(Root == null)
			return "{}";
		
		String ret = "{";
		
		for(T t : this)
			ret += t + ", ";
		
		return ret.substring(0,ret.length() - 2) + "}";
	}
	
	/**
	 * The ordering of items.
	 */
	protected Comparator<T> Ordering;

	/**
	 * The root node of the heap.
	 */
	protected Node Root;
	
	/**
	 * The node class for building this heap as a tree.
	 * @author Dawn Nye
	 */
	protected class Node
	{
		/**
		 * Creates a new heap node.
		 * @param item The item to store in the heap.
		 * @param p The parent node in the heap.
		 * @param l The left child of this node in the heap.
		 * @param r The right child of this node in the heap.
		 */
		public Node(T item, Node p, Node l, Node r)
		{
			Parent = p;
			
			Left = l;
			Right = r;
			
			Item = item;
			
			Count = (l == null ? 0 : l.Count) + (r == null ? 0 : r.Count) + 1;
			return;
		}
		
		@Override public String toString()
		{return Item.toString();}
		
		public T Item;
		public Node Parent;
		public Node Left;
		public Node Right;
		public int Count;
	}
	
	/**
	 * Enumerates a min heap.
	 * @author Dawn Nye
	 */
	protected class MinHeapEnumerator implements Iterator<T>
	{
		/**
		 * Creates a new heap enumerator.
		 * @param root The root of the heap.
		 * @param cmp Compares things of T type.
		 */
		public MinHeapEnumerator(Node root, Comparator<T> cmp)
		{
			Root = root;
			q = new MinHeap<Node>((n1,n2) -> cmp.compare(n1.Item,n2.Item));

			if(Root != null)
				q.Add(Root);

			CurrentNode = null;
			return;
		}

		public boolean hasNext()
		{return q.Count() > 0;}

		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			MoveNext();
			return CurrentNode.Item;
		}
		
		/**
		 * Goes to the next element (if one exists).
		 * @return Returns true if it can get to the next element and false if one does not exist.
		 */
		protected boolean MoveNext()
		{
			if(q.Count() == 0)
			{
				CurrentNode = null;
				return false;
			}

			CurrentNode = q.RemoveMin();
			
			if(CurrentNode.Left != null)
				q.Add(CurrentNode.Left);

			if(CurrentNode.Right != null)
				q.Add(CurrentNode.Right);
			
			return true;
		}
		
		/**
		 * Returns the current node of the enumerator.
		 */
		protected Node CurrentNode;

		/**
		 * The root node of the heap.
		 */
		protected Node Root;

		/**
		 * The heap of things to explore.
		 */
		protected MinHeap<Node> q;
	}
}