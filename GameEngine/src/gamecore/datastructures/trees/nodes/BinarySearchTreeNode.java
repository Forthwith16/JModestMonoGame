package gamecore.datastructures.trees.nodes;

/**
 * The node of an ordinary binary search tree.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 */
public class BinarySearchTreeNode<T> extends AbstractBinaryTreeNode<T,BinarySearchTreeNode<T>>
{
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node. If this value is null, then the node's parent will be set to itself, thus making it a root node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child. This value is ignored if this node is a root.
	 */
	public BinarySearchTreeNode(T data, BinarySearchTreeNode<T> parent, BinarySearchTreeNode<T> left, BinarySearchTreeNode<T> right, boolean is_left_child)
	{
		super(data,parent,left,right,is_left_child);
		return;
	}
}