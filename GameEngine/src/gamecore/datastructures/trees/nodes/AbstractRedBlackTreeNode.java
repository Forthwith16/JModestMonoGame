package gamecore.datastructures.trees.nodes;

/**
 * The basics of a node of a red-black tree.
 * @author Dawn Nye
 * @param <T> The type of data to store in the node.
 * @param <ME>
 * This is the child class's type.
 * See {@code AbstractBinaryTreeNode} for why this is important.
 */
public abstract class AbstractRedBlackTreeNode<T,ME extends AbstractRedBlackTreeNode<T,ME>> extends AbstractBinaryTreeNode<T,ME>
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
	protected AbstractRedBlackTreeNode(T data, ME parent, ME left, ME right, boolean is_left_child, boolean color)
	{
		super(data,parent,left,right,is_left_child);
		
		Color = color;
		return;
	}
	
	/**
	 * Makes this into a black node.
	 */
	public void MakeBlack()
	{
		Color = true;
		return;
	}
	
	/**
	 * Makes this into a red node.
	 */
	public void MakeRed()
	{
		Color = false;
		return;
	}
	
	/**
	 * Determines if this is a black node.
	 */
	public boolean IsBlack()
	{return Color;}
	
	/**
	 * Determines if this is a red node.
	 */
	public boolean IsRed()
	{return !Color;}
	
	@Override public String toString()
	{return "Data: " + (Data != null ? Data.toString() : "null") + "\nParent: " + Parent.Data.toString() + "\nLeft: " + (Left != null ? (Left.Data != null ? Left.Data.toString() : "null entry") : "null") + "\nRight: " + (Right != null ? (Right.Data != null ? Right.Data.toString() : "null entry") : "null") + "\nColor: " + (Color ? "Black" : "Red");}
	
	/**
	 * The color of the node.
	 * If true, the node is black.
	 * If false, the ndoe is red.
	 */
	private boolean Color;
}