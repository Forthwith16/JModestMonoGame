package gamecore.datastructures.trees.nodes;

/**
 * A concrete red black tree node.
 * @author Dawn Nye
 * @param <T> The type to store in the node as data.
 */
public class RedBlackTreeNode<T> extends AbstractRedBlackTreeNode<T,RedBlackTreeNode<T>>
{
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node. If this value is null, then the node's parent will be set to itself, thus making it a root node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child. This value is ignored if this node is a root.
	 * @param color If true, this will be a black node. If false, this will be a red node.
	 */
	public RedBlackTreeNode(T data, RedBlackTreeNode<T> parent, RedBlackTreeNode<T> left, RedBlackTreeNode<T> right, boolean is_left_child, boolean color)
	{
		super(data,parent,left,right,is_left_child,color);
		return;
	}
}
