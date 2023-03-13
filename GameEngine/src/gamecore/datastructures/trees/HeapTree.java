package gamecore.datastructures.trees;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.NoSuchElementException;

import gamecore.datastructures.heaps.IHeap;
import gamecore.datastructures.trees.nodes.CompleteBinaryTreeNode;


public class HeapTree<T> extends CompleteBinaryTree<T> implements IHeap<T>
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
		super(seed); // Since we just slam the nodes into place, this will build the initial tree in linear time

		if(cmp == null)
			throw new NullPointerException();
		
		Comparer = (n1,n2) -> cmp.compare(n1.Data,n2.Data);
		
		if(!IsEmpty())
			FastHeapify(Root);
		
		EnablePercolation = true;
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
	
	public boolean AddAll(Iterable<? extends T> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		boolean ret = false;
		
		for(T t : c)
			ret |= Add(t);
		
		return ret;
	}

	public T RemoveTop()
	{
		if(IsEmpty())
			throw new NoSuchElementException();
		
		T ret = Top();
		Remove(ret);
		return ret;
	}

	public T Top()
	{return Root();}
	
	@Override protected EnumSet<PropogationDirection> MaintainPropertyAdd(CompleteBinaryTreeNode<T> n)
	{
		if(!EnablePercolation)
			return EnumSet.noneOf(PropogationDirection.class);
		
		// We can only propogate upward on an add
		if(n.IsLeaf())
			if(n.IsRoot())
				return EnumSet.noneOf(PropogationDirection.class);
			else
				return EnumSet.of(PropogationDirection.PARENT);
		
		if(!n.HasLeftChild())
			if(Comparer.compare(n,n.Right()) > 0)
				SwapNodeContents(n,n.Right());
			else
				return EnumSet.noneOf(PropogationDirection.class);
		else if(!n.HasRightChild())
			if(Comparer.compare(n,n.Left()) > 0)
				SwapNodeContents(n,n.Left());
			else
				return EnumSet.noneOf(PropogationDirection.class);
		else // We know we have two children now, so pick the min
			if(Comparer.compare(n.Left(),n.Right()) < 0) // An else if, but this formatting is clearer
				if(Comparer.compare(n,n.Left()) > 0) // Left child is smaller in this case
					SwapNodeContents(n,n.Left());
				else
					return EnumSet.noneOf(PropogationDirection.class);
			else // An else if, but this formatting is clearer
				if(Comparer.compare(n,n.Right()) > 0) // Right child is smaller in this case
					SwapNodeContents(n,n.Right());
				else
					return EnumSet.noneOf(PropogationDirection.class);
		
		return EnumSet.of(PropogationDirection.PARENT);
	}
	
	protected EnumSet<PropogationDirection> MaintainPropertyRemove(CompleteBinaryTreeNode<T> n)
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
