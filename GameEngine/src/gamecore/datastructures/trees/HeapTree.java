package gamecore.datastructures.trees;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;

import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.trees.nodes.CompleteBinaryTreeNode;

/**
 * Creates a tree-hierarchy heap backed by a complete binary tree.
 * The iteration order is still an in-order traversal and as such does not produce its contents in the order they would be removed from the heap. 
 * @author Dawn Nye
 * @param <T> The type to store in the heap.
 */
public class HeapTree<T> extends CompleteBinaryTree<T>
{
	/**
	 * Creates an empty heap.
	 * @param cmp The comparator used to sort the heap. The heap will place the smallest item at the top.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	public HeapTree(Comparator<T> cmp)
	{
		super();
		
		if(cmp == null)
			throw new NullPointerException();
		
		Comparer = (n1,n2) -> cmp.compare(n1.Data,n2.Data);
		EnablePercolation = true;
		
		return;
	}
	
	/**
	 * Creates a heap initially populated with {@code seed}.
	 * The heap will be built in linear time.
	 * @param seed The initial values to palce in the heap.
	 * @param cmp The comparator used to sort the heap. The heap will place the smallest item at the top.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	public HeapTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		super();
		
		if(seed == null || cmp == null)
			throw new NullPointerException();
		
		Comparer = (n1,n2) -> cmp.compare(n1.Data,n2.Data);
		
		// We can't just slam the nodes into place with add and then heapify since that will take n log n time
		// We'll initially build the tree via a level-order traversal
		BuildTreeFast(seed.iterator());
		
		if(!IsEmpty())
			FastHeapify(Root);
		
		EnablePercolation = true;
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
	
	/**
	 * To build the tree fast, we need to perform a post-order traversal on it.
	 * We can't use the built in version, unfortunately, since we require the nodes rather than the data to do this fast.
	 * @param n The root of the sub-heap to heapify.
	 * @throws NullPointerException Thrown if {@code n} is null.
	 */
	protected void FastHeapify(CompleteBinaryTreeNode<T> n)
	{
		if(n.IsLeaf())
			return;
		
		if(n.HasLeftChild())
			FastHeapify(n.Left());
		
		if(n.HasRightChild())
			FastHeapify(n.Right());
		
		PropogatePropertyRemove(n); // Since EnablePropogation is still false as this point, we can use the MaintainPropertyRemove function to prercolate down
		return;
	}
	
	@Override protected EnumSet<PropogationDirection> MaintainPropertyAdd(CompleteBinaryTreeNode<T> n)
	{
		if(!EnablePercolation)
			return EnumSet.noneOf(PropogationDirection.class);
		
		// We can only propogate upward on an add
		if(n.IsRoot())
			return EnumSet.noneOf(PropogationDirection.class);

		if(Comparer.compare(n,n.Parent()) < 0)
		{
			SwapNodeContents(n,n.Parent());
			return EnumSet.of(PropogationDirection.PARENT);
		}
		
		return EnumSet.noneOf(PropogationDirection.class);
	}
	
	@Override protected EnumSet<PropogationDirection> MaintainPropertyRemove(CompleteBinaryTreeNode<T> n)
	{
		// We never have to worry about the constructor removing things before percolation is enabled, so who cares
		if(n.IsLeaf())
			return EnumSet.noneOf(PropogationDirection.class);
		
		// There isn' necessarily a relation between the nodes we swapped, so we might need to percolate up or down
		// As a convenient hack, we disable upward propogation here when percolation is disabled so that we don't have to write more code for building the heap in linear time
		if(EnablePercolation && !n.IsRoot() && Comparer.compare(n,n.Parent()) < 0)
		{
			SwapNodeContents(n,n.Parent());
			return EnumSet.of(PropogationDirection.PARENT);
		}
		
		// If we don't have a left child, we need only check the right child
		if(!n.HasLeftChild())
			if(Comparer.compare(n,n.Right()) > 0)
			{
				SwapNodeContents(n,n.Right());
				return EnumSet.of(PropogationDirection.RIGHT);
			}
			else
				return EnumSet.noneOf(PropogationDirection.class);
		
		// If we don't have a right child, we need only check the left child
		if(!n.HasRightChild())
			if(Comparer.compare(n,n.Left()) > 0)
			{
				SwapNodeContents(n,n.Left());
				return EnumSet.of(PropogationDirection.LEFT);
			}
			else
				return EnumSet.noneOf(PropogationDirection.class);
		
		// We have two children and need to pick the minimum to compare ourselves against
		if(Comparer.compare(n.Left(),n.Right()) < 0)
			if(Comparer.compare(n,n.Left()) > 0) // Left child is smaller in this case
			{
				SwapNodeContents(n,n.Left());
				return EnumSet.of(PropogationDirection.LEFT);
			}
			else
				return EnumSet.noneOf(PropogationDirection.class);
		else // An else if, but this formatting is clearer
			if(Comparer.compare(n,n.Right()) > 0) // Right child is smaller in this case
			{
				SwapNodeContents(n,n.Right());
				return EnumSet.of(PropogationDirection.RIGHT);
			}
			else
				return EnumSet.noneOf(PropogationDirection.class);
	}
	
	/**
	 * In order to build the heap in linear time, we need to disable percolation until after the initial heap is created.
	 */
	protected boolean EnablePercolation;
	
	/**
	 * The means by which we compare elements.
	 * The heap will place the smallest item at the top.
	 */
	protected Comparator<CompleteBinaryTreeNode<T>> Comparer;
}