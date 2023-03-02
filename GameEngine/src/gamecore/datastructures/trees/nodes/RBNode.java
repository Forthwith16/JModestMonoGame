package gamecore.datastructures.trees.nodes;

/**
 * The node of a red-black tree.
 * @author Dawn Nye
 */
public class RBNode<T> extends ABSTNode<T,RBNode<T>>
{
	/**
	 * Creates a node with no children and is its own parent.
	 * @param data The data to put into the node.
	 * @param color If true, this will be a black node. If false, this will be a red node.
	 */
	public RBNode(T data, boolean color)
	{
		super(data);
		
		Color = color;
		return;
	}
	
	/**
	 * Creates a node with the given parent but no children.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 * @param color If true, this will be a black node. If false, this will be a red node.
	 */
	public RBNode(T data, RBNode<T> parent, boolean is_left_child, boolean color)
	{
		super(data,parent,is_left_child);
		
		Color = color;
		return;
	}
	
	/**
	 * Creates a node with the given children but is its own parent.
	 * @param data The data to put into the node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param color If true, this will be a black node. If false, this will be a red node.
	 */
	public RBNode(T data, RBNode<T> left, RBNode<T> right, boolean color)
	{
		super(data,left,right);
		
		Color = color;
		return;
	}
	
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 * @param color If true, this will be a black node. If false, this will be a red node.
	 */
	public RBNode(T data, RBNode<T> parent, RBNode<T> left, RBNode<T> right, boolean is_left_child, boolean color)
	{
		super(data,parent,left,right,is_left_child);
		
		Color = color;
		return;
	}
	
	/**
	 * Makes this into a black node.
	 */
	protected void MakeBlack()
	{
		Color = true;
		return;
	}
	
	/**
	 * Makes this into a red node.
	 */
	protected void MakeRed()
	{
		Color = false;
		return;
	}
	
	/**
	 * Determines if this is a black node.
	 */
	protected boolean IsBlack()
	{return Color;}
	
	/**
	 * Determines if this is a red node.
	 */
	protected boolean IsRed()
	{return !Color;}
	
	/**
	 * The color of the node.
	 * If true, the node is black.
	 * If false, the ndoe is red.
	 */
	private boolean Color;
}
