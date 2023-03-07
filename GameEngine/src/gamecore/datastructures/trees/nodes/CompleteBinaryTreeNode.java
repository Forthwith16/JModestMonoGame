package gamecore.datastructures.trees.nodes;

/**
 * The node of a complete binary tree.
 * @author Dawn Nye
 */
public class CompleteBinaryTreeNode<T> extends AbstractBinaryTreeNode<T,CompleteBinaryTreeNode<T>>
{
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node. If this value is null, then the node's parent will be set to itself, thus making it a root node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child. This value is ignored if this node is a root.
	 */
	public CompleteBinaryTreeNode(T data, CompleteBinaryTreeNode<T> parent, CompleteBinaryTreeNode<T> left, CompleteBinaryTreeNode<T> right, boolean is_left_child)
	{
		super(data,parent,left,right,is_left_child);
		return;
	}
}