package gamecore.datastructures.trees;

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
	protected CompleteBinaryTree()
	{
		super();
		return;
	}
	
	/**
	 * Creates a binary tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	protected CompleteBinaryTree(Iterable<? extends T> seed)
	{
		super(seed);
		return;
	}
	
	protected CompleteBinaryTreeNode<T> CreateNode(T data, CompleteBinaryTreeNode<T> parent, CompleteBinaryTreeNode<T> left, CompleteBinaryTreeNode<T> right, boolean is_left_child)
	{return new CompleteBinaryTreeNode<T>(data,parent,left,right,is_left_child);}
}