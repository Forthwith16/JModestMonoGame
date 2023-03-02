package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.datastructures.trees.nodes.BSTNode;

/**
 * A bare bones binary search tree with no fancy features such as a self-balancing structure.
 * Efficient if used properly, but very much not so otherwise.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 */
public class BinarySearchTree<T> extends ABST<T,BSTNode<T>>
{
	/**
	 * Creates an empty binary search tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	public BinarySearchTree(Comparator<T> cmp)
	{
		super(cmp);
		return;
	}
	
	/**
	 * Creates a binary search tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	public BinarySearchTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		super(seed,cmp);
		return;
	}
	
	public boolean Add(T t)
	{
		if(Root == null)
		{
			Root = new BSTNode<T>(t);
			Count++;
			
			return true;
		}
		
		BSTNode<T> n = Root;
		
		while(true)
		{
			int result = Comparer.compare(t,n.Data);
			
			if(result == 0)
				return false;
			else if(result < 0)
				if(!n.HasLeftChild())
				{
					new BSTNode(t,n,null,null,true);
					Count++;
					
					return true;
				}
				else
					n = n.Left;
			else // An else if, but formatting this way make it clear what's going on
				if(!n.HasRightChild())
				{
					new BSTNode(t,n,null,null,false);
					Count++;
					
					return true;
				}
				else
					n = n.Right;
		}
	}
	
	public boolean Remove(T t)
	{
		if(Root == null)
			return false;
		
		BSTNode<T> n = Root;
		
		while(true)
		{
			int result = Comparer.compare(t,n.Data);
			
			if(result == 0)
			{
				// If we have a leaf, deletion is easy
				if(n.IsLeaf())
				{
					if(n.IsRoot())
						Root = null; // If this is the root, just destroy it
					else // Figure out which child this is
						if(n.IsLeftChild())
							n.Parent.Left = null;
						else
							n.Parent.Right = null;
					
					Count--;
					return true;
				}
				else if(!n.HasRightChild()) // If we only have a left child, deletion is easy
				{
					if(n.IsRoot())
					{
						Root = n.Left;
						Root.Parent = Root;
					}
					else
					{
						// The parent of n becomes the parent of the child
						n.Left.Parent = n.Parent;
						
						// Figure out which child n is
						if(n.IsLeftChild())
							n.Parent.Left = n.Left;
						else
							n.Parent.Right = n.Left;
					}
					
					Count--;
					return true;
				}
				else if(!n.HasLeftChild()) // If we only have a right child, deletion is easy
				{
					if(n.IsRoot())
					{
						Root = n.Right;
						Root.Parent = Root;
					}
					else
					{
						// The parent of n becomes the parent of the child
						n.Right.Parent = n.Parent;
						
						// Figure out which child n is
						if(n.Parent.Left == n)
							n.Parent.Left = n.Right;
						else
							n.Parent.Right = n.Right;
					}
					
					Count--;
					return true;
				}
				else // Let's just swap n's data with the data of the successor of n (it must exist since n has two children now) and the delete the successor node
				{
					BSTNode<T> next = n.FindNextNode();
					
					T temp = n.Data;
					n.Data = next.Data;
					next.Data = temp;
					
					n = next; // We can skip all the way ahead, since we know this is the next problem node
				}
			}
			else if(result < 0)
				if(!n.HasLeftChild())
					return false;
				else
					n = n.Left;
			else // An else if, but formatting this way make it clear what's going on
				if(!n.HasRightChild())
					return false;
				else
					n = n.Right;
		}
	}
}