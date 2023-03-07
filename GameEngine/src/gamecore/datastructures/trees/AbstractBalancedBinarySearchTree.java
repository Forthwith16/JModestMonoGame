package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.datastructures.trees.nodes.AbstractBinaryTreeNode;

/**
 * An abstract balanced binary search tree.
 * This defines the common underlying methods of a balanced binary search tree.
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
 * 	<li>
 * 		{@code AddN}: Adds the new element so that at every node, all elements of the node's left subtree are smaller than it and all elements of its right subtree are larger than it.
 * 		After this, if the add succeeded, it calls the {@code BalaceAdd} method on the added node to rebalance the tree.
 * 	</li>
 * 	<li>
 * 		{@code RemoveN}: Removes an element from the tree while maintaining the ordering specified in {@code Add}.
 * 		After this, if the remove succeeded, it calls the {@code BalanceRemove} method on the removed node to rebalance the tree.
 * 	</li>
 * 	<li>{@code Contains}: Takes advantage of the ordering to search efficiently for an element.</li>
 * </ul>
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 * @param <NODE> The node type to build the tree with.
 */ // We need no additional information ourselves in a node, so we only require at least an AbstractBinaryTreeNode
public abstract class AbstractBalancedBinarySearchTree<T,NODE extends AbstractBinaryTreeNode<T,NODE>> extends AbstractBinarySearchTree<T,NODE>
{
	/**
	 * Creates an empty balanced binary search tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	protected AbstractBalancedBinarySearchTree(Comparator<T> cmp)
	{
		super(cmp);
		return;
	}
	
	/**
	 * Creates a balanced binary search tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	protected AbstractBalancedBinarySearchTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		super(seed,cmp);
		return;
	}
	
	@Override protected NODE AddN(T t)
	{
		NODE ret = super.AddN(t);
		
		if(ret != null)
			BalanceAdd(ret);
		
		return ret;
	}
	
	/**
	 * Balances the tree about the node {@code n}.
	 * @param n
	 * This is the node added to the tree.
	 * Its links will be fully initialized, and its neighbors links connect to it.
	 * @implNote The most reasonable implementation of this method is usually recursive, so a liberal interpretation of 'adding {@code n}' to the tree is often necessary.
	 */
	protected abstract void BalanceAdd(NODE n);
	
	@Override protected NODE RemoveN(T t)
	{
		NODE ret = super.RemoveN(t);
		
		if(ret != null) // If ret is the root, the boolean has no meaning, so just go with false
			BalanceRemove(ret,ret.IsRoot() ? false : (Comparer.compare(ret.Data,ret.Parent.Data) < 0 ? true : false));
		
		return ret;
	}
	
	/**
	 * Balances the tree about the node {@code n}.
	 * @param n
	 * This is the node removed from the tree.
	 * Its links will be intact (but what it was once linked to will have forgotten it).
	 * @param was_left_child
	 * If true, then {@code n} was the left child of its parent.
	 * If false, it was the right child.
	 * However, if the {@code n} was the root, then this value has no meaning.
	 * We provide this information merely for convenience instead of having to recompute it.
	 * @implNote The most reasonable implementation of this method is usually recursive, so a liberal interpretation of having 'removed {@code n}' from the tree is often necessary.
	 * @implNote
	 * Some of {@code n}'s helper methods such as {@code GetSibling()} require {@code n} to be properly linked into the tree.
	 * Because this is not the case here, be careful to use their alternatives (i.e. {@code GetSibling(boolean)}) which provide {@code was_last_child} to permit the computation.
	 */
	protected abstract void BalanceRemove(NODE n, boolean was_left_child);
}