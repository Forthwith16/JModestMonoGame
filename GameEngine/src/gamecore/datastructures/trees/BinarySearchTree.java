package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.datastructures.trees.nodes.BinarySearchTreeNode;

/**
 * A bare bones binary search tree with no fancy features such as a self-balancing structure.
 * Efficient if used properly, but very much not so otherwise.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 */
public class BinarySearchTree<T> extends AbstractBinarySearchTree<T,BinarySearchTreeNode<T>>
{
	/**
	 * Creates an empty binary search tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	public BinarySearchTree(Comparator<? super T> cmp)
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
	public BinarySearchTree(Iterable<? extends T> seed, Comparator<? super T> cmp)
	{
		super(seed,cmp);
		return;
	}
	
	protected BinarySearchTreeNode<T> CreateNode(T data, BinarySearchTreeNode<T> parent, BinarySearchTreeNode<T> left, BinarySearchTreeNode<T> right, boolean is_left_child)
	{return new BinarySearchTreeNode<T>(data,parent,left,right,is_left_child);}
}