package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.datastructures.trees.nodes.AbstractBinaryTreeNode;

/**
 * An abstract binary search tree.
 * This defines the common underlying methods of a binary search tree.
 * In order to facilitate this, we also have a generic parameter for the node type to enable nodes to store any necessary information within them.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * <br><br>
 * To specify precisely the behavior of this tree, the key functions not override are {@code AddN}, {@code RemoveN}, and {@code Contains}.
 * The former two methods are protected and add/remove a node from the tree, which {@code Add} and {@code Remove} use to return a boolean.
 * There is no need to override either {@code Add} or {@code Remove} because of this except in niche use cases.
 * <br><br>
 * We give the default implementations of {@code AddN}, {@code RemoveN}, and {@code Contains} below.
 * <ul>
 * 	<li>{@code AddN}: Adds the new element so that at every node, all elements of the node's left subtree are smaller than it and all elements of its right subtree are larger than it.</li>
 * 	<li>{@code RemoveN}: Removes an element from the tree while maintaining the ordering specified in {@code Add}.</li>
 * 	<li>{@code Contains}: Takes advantage of the ordering to search efficiently for an element.</li>
 * </ul>
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 * @param <NODE> The node type to build the tree with.
 */
public abstract class AbstractBinarySearchTree<T,NODE extends AbstractBinaryTreeNode<T,NODE>> extends AbstractBinaryTree<T,NODE>
{
	/**
	 * Creates an empty binary search tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	protected AbstractBinarySearchTree(Comparator<? super T> cmp)
	{
		super();
		
		if(cmp == null)
			throw new NullPointerException();
		
		Comparer = cmp;
		return;
	}
	
	/**
	 * Creates a binary search tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	protected AbstractBinarySearchTree(Iterable<? extends T> seed, Comparator<? super T> cmp)
	{
		super(); // We can't initialize Comparer (even by cheating) before calling the base constructor, so we have to settle for the default base constructor
		
		if(cmp == null || seed == null)
			throw new NullPointerException();
		
		Comparer = cmp;
		
		// Since we settled for the lesser base constructor, we have to do this ourselves here
		for(T t : seed)
			Add(t);
		
		return;
	}
	
	@Override protected NODE AddN(T t)
	{
		if(IsEmpty())
		{
			Root = CreateNode(t,null,null,null,false);
			Count++;
			
			return Root;
		}
		
		NODE n = Root;
		
		while(true)
		{
			int result = Comparer.compare(t,n.Data);
			
			if(result == 0)
				return null;
			else if(result < 0)
				if(n.HasLeftChild())
					n = n.Left();
				else
				{
					Count++;
					return CreateNode(t,n,null,null,true);
				}
			else // An else if, but formatting this way make it clear what's going on
				if(n.HasRightChild())
					n = n.Right();
				else
				{
					Count++;
					return CreateNode(t,n,null,null,false);
				}
		}
	}
	
	@Override public boolean Remove(T t)
	{
		NODE n = RemoveN(t);
		
		if(n != null)
			PropogatePropertyRemove(n);
		
		return n != null;
	}
	
	@Override protected NODE RemoveN(T t)
	{
		NODE n = Find(Root,t);
		
		while(n != null)
		{
			// If we have a leaf, deletion is easy
			if(n.IsLeaf())
			{
				if(n.IsRoot())
					Root = null; // If this is the root, just destroy it
				else // Figure out which child this is
					if(n.IsLeftChild())
						n.Parent().StitchLeftChild(null);
					else
						n.Parent().StitchRightChild(null);
				
				Count--;
				return n;
			}
			else if(!n.HasRightChild()) // If we only have a left child, deletion is easy
			{
				if(n.IsRoot())
				{
					Root = n.Left();
					Root.MakeRoot();
				}
				else if(n.IsLeftChild()) // The parent of n becomes the parent of the child
					n.Parent().StitchLeftChild(n.Left());
				else
					n.Parent().StitchRightChild(n.Left());
				
				Count--;
				return n;
			}
			else if(!n.HasLeftChild()) // If we only have a right child, deletion is easy
			{
				if(n.IsRoot())
				{
					Root = n.Right();
					Root.MakeRoot();
				}
				else if(n.IsLeftChild()) // The parent of n becomes the parent of the child
					n.Parent().StitchLeftChild(n.Right());
				else
					n.Parent().StitchRightChild(n.Right());
				
				Count--;
				return n;
			}
			else // Let's just swap n's data with the data of the successor of n (it must exist since n has two children now) and the delete the successor node
			{
				NODE next = n.FindNextNode();
				
				// We'll clobber the removed node's data so that we can retain its positional information
				SwapNodeContents(n,next);
				
				// We want to maintain a property on the node we just swapped
				PropogatePropertyRemove(n);
				
				// We can skip all the way ahead, since we know this is the next problem node
				n = next;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds a node containing {@code t}.
	 * Note that this implementation is more efficient than its parent class's since we can take advantage of the search tree structure.
	 * @param n The root node.
	 * @param t The item to look for (there may be more than one).
	 * @return Returns a node containing {@code t} if there is one and null otherwise.
	 */
	@Override protected NODE Find(NODE n, T t)
	{
		if(IsEmpty())
			return null;
		
		while(true)
		{
			int result = Comparer.compare(t,n.Data);
			
			if(result == 0)
				return n;
			else if(result < 0)
				if(n.HasLeftChild())
					n = n.Left();
				else
					return null;
			else // An else if, but formatting this way make it clear what's going on
				if(n.HasRightChild())
					n = n.Right();
				else
					return null;
		}
	}
	
	@Override public boolean Contains(T t)
	{return Find(Root,t) != null;}
	
	/**
	 * Compares {@code T} types.
	 */
	protected Comparator<? super T> Comparer;
}