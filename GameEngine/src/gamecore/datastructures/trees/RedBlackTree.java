package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.datastructures.trees.nodes.RedBlackTreeNode;

/**
 * An abstract balanced binary tree implemented via a red-black scheme.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * @author Dawn Nye
 * @param <T> The type of object stored in the tree.
 */
public class RedBlackTree<T> extends AbstractRedBlackTree<T,RedBlackTreeNode<T>>
{
	/**
	 * Creates an empty red-black tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	public RedBlackTree(Comparator<T> cmp)
	{
		super(cmp);
		return;
	}
	
	/**
	 * Creates a red-black tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	public RedBlackTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		super(seed,cmp);
		return;
	}
	
	protected RedBlackTreeNode<T> CreateNode(T data, RedBlackTreeNode<T> parent, RedBlackTreeNode<T> left, RedBlackTreeNode<T> right, boolean is_left_child)
	{return new RedBlackTreeNode<T>(data,parent,left,right,is_left_child,false);}
}
