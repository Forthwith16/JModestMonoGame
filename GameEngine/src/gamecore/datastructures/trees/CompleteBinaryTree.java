package gamecore.datastructures.trees;

import java.util.Iterator;

import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.trees.nodes.CompleteBinaryTreeNode;

/**
 * A complete binary tree data structure.
 * Duplicates <i>are</i> permitted in the tree, as are null entries.
 * This tree will ensure that it remains complete at all times.
 * The {@code Add} function will not disrupt node order in pursuit of that goal.
 * However, {@code Remove} makes no garuntee about the resulting node order after removing a node.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 */
public class CompleteBinaryTree<T> extends AbstractBinaryTree<T,CompleteBinaryTreeNode<T>>
{
	/**
	 * Creates an empty binary tree.
	 */
	public CompleteBinaryTree()
	{
		super();
		return;
	}
	
	/**
	 * Creates a binary tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	public CompleteBinaryTree(Iterable<? extends T> seed)
	{
		super();
		
		if(seed == null)
			throw new NullPointerException();
		
		// Since we have a complete tree for sure down this branch of the hierarchy, we can build a tree fast (child classes can fix whatever properties they want to maintain between nodes themselves)
		// We can't just slam the nodes into place with add since that will take n log n time
		// We'll thus build the tree via a level-order traversal to make it complete
		BuildTreeFast(seed.iterator());
		
		return;
	}
	
	/**
	 * Builds a tree in linear time.
	 * @param seed The items to put into the tree.
	 */
	protected void BuildTreeFast(Iterator<? extends T> seed)
	{
		if(seed == null || !seed.hasNext())
			return;
		
		Queue<CompleteBinaryTreeNode<T>> Q = new Queue<CompleteBinaryTreeNode<T>>();
		Root = new CompleteBinaryTreeNode<T>(seed.next(),null,null,null,false);
		Count++;
		
		Q.Enqueue(Root);
		
		while(seed.hasNext())
		{
			CompleteBinaryTreeNode<T> n = Q.Dequeue();
			
			Q.Enqueue(new CompleteBinaryTreeNode<T>(seed.next(),n,null,null,true));
			Count++;
			
			if(seed.hasNext())
			{
				Q.Enqueue(new CompleteBinaryTreeNode<T>(seed.next(),n,null,null,false));
				Count++;
			}
		}
		
		return;
	}
	
	protected CompleteBinaryTreeNode<T> CreateNode(T data, CompleteBinaryTreeNode<T> parent, CompleteBinaryTreeNode<T> left, CompleteBinaryTreeNode<T> right, boolean is_left_child)
	{return new CompleteBinaryTreeNode<T>(data,parent,left,right,is_left_child);}
}