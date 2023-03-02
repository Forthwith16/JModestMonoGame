package gamecore.datastructures.trees.nodes;

/**
 * The node of an ordinary binary search tree.
 * @author Dawn Nye
 */
public class BSTNode<T> extends ABSTNode<T,BSTNode<T>>
{
	/**
	 * Creates a node with no children and is its own parent.
	 * @param data The data to put into the node.
	 */
	public BSTNode(T data)
	{
		super(data);
		return;
	}
	
	/**
	 * Creates a node with the given parent but no children.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 */
	public BSTNode(T data, BSTNode<T> parent, boolean is_left_child)
	{
		super(data,parent,is_left_child);
		return;
	}
	
	/**
	 * Creates a node with the given children but is its own parent.
	 * @param data The data to put into the node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 */
	public BSTNode(T data, BSTNode<T> left, BSTNode<T> right)
	{
		super(data,left,right);
		return;
	}
	
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 */
	public BSTNode(T data, BSTNode<T> parent, BSTNode<T> left, BSTNode<T> right, boolean is_left_child)
	{
		super(data,parent,left,right,is_left_child);
		return;
	}
}