package gamecore.datastructures.trees;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.queues.Stack;
import gamecore.datastructures.trees.nodes.ABSTNode;
import gamecore.datastructures.tuples.Pair;

/**
 * An abstract binary search tree.
 * This defines the common underlying methods of a binary search tree but leaves {@code Add} and {@code Remove} up to the child class to implement.
 * In order to facilitate this, we also have a generic parameter for the node type to enable nodes to store any necessary information within them.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 * @param <NODE> The node type to build the tree with.
 */
public abstract class ABST<T,NODE extends ABSTNode<T,NODE>> implements ITree<T>
{
	/**
	 * Creates an empty binary search tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	protected ABST(Comparator<T> cmp)
	{
		if(cmp == null)
			throw new NullPointerException();
		
		Root = null;
		Count = 0;
		
		Comparer = cmp;
		return;
	}
	
	/**
	 * Creates a binary search tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	protected ABST(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		if(cmp == null || seed == null)
			throw new NullPointerException();
		
		Root = null;
		Count = 0;
		
		Comparer = cmp;
		
		for(T t : seed)
			Add(t);
		
		return;
	}
	
	public boolean Contains(T t)
	{
		if(Root == null)
			return false;
		
		NODE n = Root;
		
		while(true)
		{
			int result = Comparer.compare(t,n.Data);
			
			if(result == 0)
				return true;
			else if(result < 0)
				if(n.Left == null)
					return false;
				else
					n = n.Left;
			else // An else if, but formatting this way make it clear what's going on
				if(n.Right == null)
					return false;
				else
					n = n.Right;
		}
	}
	
	public T Root()
	{
		if(Root == null)
			throw new NoSuchElementException();
		
		return Root.Data;
	}
	
	public void Clear()
	{
		Root = null;
		Count = 0;
		
		return;
	}
	
	public int Count()
	{return Count;}
	
	public boolean IsEmpty()
	{return Root == null;}
	
	public void PreOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(Root == null)
			return;
		
		Stack<Pair<NODE,Integer>> S = new Stack<Pair<NODE,Integer>>();
		S.Push(new Pair<NODE,Integer>(Root,0));
		
		int index = 0;
		
		while(!S.IsEmpty())
		{
			Pair<NODE,Integer> p = S.Pop();
			
			// Visit the node first
			f.Visit(p.Item1.Data,index++,p.Item2);
			
			// Now visit the children (push right first so we visit it second)
			if(p.Item1.Right != null)
				S.Push(new Pair<NODE,Integer>(p.Item1.Right,p.Item2 + 1));
			
			if(p.Item1.Left != null)
				S.Push(new Pair<NODE,Integer>(p.Item1.Left,p.Item2 + 1));
		}
		
		return;
	}
	
	public void PostOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(Root == null)
			return;
		
		RPostOrderTraversal(f,Root,0,0);
		return;
	}
	
	/**
	 * It is <b>way</b> easier to do a post-order traversal recursively, so let's just do that.
	 * @param f The function to call when visiting each node.
	 * @param n The root node of the recursive call.
	 * @param index The index we're currently at.
	 * @param depth The depth we're currently at.
	 * @return Returns the next index available. If {@code n} is null, this will just be {@code index}.
	 */
	private int RPostOrderTraversal(TraversalFunction<T> f, NODE n, int index, int depth)
	{
		if(n == null)
			return index;
		
		// Visit the children first
		index = RPostOrderTraversal(f,n.Left,index,depth + 1);
		index = RPostOrderTraversal(f,n.Right,index,depth + 1);
		
		// Now visit this node
		f.Visit(n.Data,index,depth);
		
		// We processed one index in this recursive call, so return index + 1
		return index + 1;
	}
	
	public void InOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(Root == null)
			return;
		
		RInOrderTraversal(f,Root,0,0);
		return;
	}
	
	/**
	 * It is <b>way</b> easier to do a in-order traversal recursively, so let's just do that.
	 * @param f The function to call when visiting each node.
	 * @param n The root node of the recursive call.
	 * @param index The index we're currently at.
	 * @param depth The depth we're currently at.
	 * @return Returns the next index available. If {@code n} is null, this will just be {@code index}.
	 */
	private int RInOrderTraversal(TraversalFunction<T> f, NODE n, int index, int depth)
	{
		if(n == null)
			return index;
		
		// Visit the left child first
		index = RInOrderTraversal(f,n.Left,index,depth + 1);
		
		// Now visit this node
		f.Visit(n.Data,index++,depth);
		
		// Lastly, visit the right child
		return RInOrderTraversal(f,n.Right,index,depth + 1);
	}
	
	public void LevelOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(Root == null)
			return;
		
		Queue<Pair<NODE,Integer>> S = new Queue<Pair<NODE,Integer>>();
		S.Enqueue(new Pair<NODE,Integer>(Root,0));
		
		int index = 0;
		
		while(!S.IsEmpty())
		{
			Pair<NODE,Integer> p = S.Dequeue();
			
			// Visit the node first
			f.Visit(p.Item1.Data,index++,p.Item2);
			
			// Now visit the children
			if(p.Item1.Left != null)
				S.Enqueue(new Pair<NODE,Integer>(p.Item1.Left,p.Item2 + 1));
			
			if(p.Item1.Right != null)
				S.Enqueue(new Pair<NODE,Integer>(p.Item1.Right,p.Item2 + 1));
		}
		
		return;
	}
	
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			public boolean hasNext()
			{
				// If we know we're done, just say so
				if(done)
					return false;
				
				// If we haven't started iterating yet, we need the smallest node
				// That node is the furthest left node from the root (which can be the root itself)
				if(n == null)
				{
					n = Root;
					
					while(n.Left != null)
						n = n.Left;
					
					return true;
				}
				
				return !done;
			}
			
			public T next()
			{
				if(!hasNext())
					throw new NoSuchElementException();
				
				// Save the return value
				T ret = n.Data;
				
				// Advance the cursor
				n = n.FindNextNode();
				
				// If we advanced too far, we're done
				if(n == null)
					done = true;
				
				return ret;
			}
			
			public NODE n = null;
			public boolean done = Root == null;
		};
	}
	
	@Override public String toString()
	{
		if(IsEmpty())
			return "{}";
		
		String ret = "{";
		
		for(T t : this)
			ret += t + ",";
		
		return ret.substring(0,ret.length() - 1) + "}";
	}
	
	/**
	 * The root node of the tree.
	 * This is null when the tree is empty.
	 */
	protected NODE Root;
	
	/**
	 * The number of items in the tree.
	 */
	protected int Count;
	
	/**
	 * Compares {@code T} types.
	 */
	protected Comparator<T> Comparer;
}
