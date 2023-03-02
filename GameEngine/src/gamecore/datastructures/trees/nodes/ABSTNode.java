package gamecore.datastructures.trees.nodes;

/**
 * The bare bones requirements to be a node in a binary tree.
 * Null values are permitted to be in the data field unless the child class prohibits it.
 * @author Dawn Nye
 * @param <T> The type of data to store in this node.
 * @param <ME> The derived class's node type. We need this so we can get our return types right without forcing derived classes to cast all the time. Changing parameter types to this also prohibits the mixing of node types.
 */
public abstract class ABSTNode<T,ME extends ABSTNode>
{
	/**
	 * Creates a node with no children and is its own parent.
	 * @param data The data to put into the node.
	 */
	protected ABSTNode(T data)
	{
		Data = data;
		StitchTogether((ME)this,(ME)null,(ME)null,false); // Left/Right child doesn't matter when you're your own parent
		
		return;
	}
	
	/**
	 * Creates a node with the given parent but no children.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 */
	protected ABSTNode(T data, ME parent, boolean is_left_child)
	{
		Data = data;
		StitchTogether(parent,null,null,is_left_child);
		
		return;
	}
	
	/**
	 * Creates a node with the given children but is its own parent.
	 * @param data The data to put into the node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 */
	protected ABSTNode(T data, ME left, ME right)
	{
		Data = data;
		StitchTogether((ME)this,left,right,false); // Left/Right child doesn't matter when you're your own parent
		
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
	protected ABSTNode(T data, ME parent, ME left, ME right, boolean is_left_child)
	{
		Data = data;
		StitchTogether(parent,left,right,is_left_child);
		
		return;
	}
	
	/**
	 * Stitches this node into the tree with the given surrounding nodes.
	 * @param parent The parent node (if null or itself, it will become a root node).
	 * @param left The left child node.
	 * @param right The right child node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child.
	 */
	public void StitchTogether(ME parent, ME left, ME right, boolean is_left_child)
	{
		Parent = parent;
		Left = left;
		Right = right;
		
		if(Parent != null && Parent != this)
			if(is_left_child)
				Parent.Left = this;
			else
				Parent.Right = this;
		
		if(Left != null)
			Left.Parent = this;
		
		if(Right != null)
			Right.Parent = this;
		
		return;
	}
	
	/**
	 * Finds the next node in the binary tree (if there is one).
	 * @return Returns the next node in the binary tree or null if there is no next node.
	 */
	public ME FindNextNode()
	{
		if(Right != null)
		{
			ME n = Right;
			
			while(n.Left != null)
				n = (ME)n.Left;
			
			return n;
		}
		
		if(Parent == this && Right == null)
			return null;
		
		ME n = (ME)this;
		
		while(n == n.Parent.Right)
			if(n.Parent == n.Parent.Parent)
				return null;
			else
				n = (ME)n.Parent;
		
		return (ME)n.Parent;
	}
	
	/**
	 * Finds the previous node in the binary tree (if there is one).
	 * @return Returns the previous node in the binary tree or null if there is no previous node.
	 */
	public ME FindPreviousNode()
	{
		if(Left != null)
		{
			ME n = Left;
			
			while(n.Right != null)
				n = (ME)n.Right;
			
			return n;
		}
		
		if(Parent == this && Left == null)
			return null;
		
		ME n = (ME)this;
		
		while(n == n.Parent.Left)
			if(n.Parent == n.Parent.Parent)
				return null;
			else
				n = (ME)n.Parent;
		
		return (ME)n.Parent;
	}
	
	/**
	 * Determines if this is a root node.
	 */
	public boolean IsRoot()
	{return this == Parent;}
	
	/**
	 * Determines if this is a leaf node.
	 */
	public boolean IsLeaf()
	{return !HasLeftChild() && !HasRightChild();}
	
	/**
	 * Determines if this is an internal node.
	 */
	public boolean IsInternalNode()
	{return !IsLeaf() && !IsRoot();}
	
	/**
	 * Determines if this is a left child of its parent.
	 */
	public boolean IsLeftChild()
	{return !IsRoot() && Parent.Left == this;}
	
	/**
	 * Determines if this is a right child of its parent.
	 */
	public boolean IsRightChild()
	{return !IsRoot() && Parent.Right == this;}
	
	/**
	 * Determines if this node has <i>exactly</i> one child.
	 */
	public boolean HasOneChild()
	{return !IsLeaf() && !HasTwoChildren();}
	
	/**
	 * Determines if this node has two children.
	 */
	public boolean HasTwoChildren()
	{return HasLeftChild() && HasRightChild();}
	
	/**
	 * Determines if this node has a left child.
	 */
	public boolean HasLeftChild()
	{return Left != null;}
	
	/**
	 * Determines if this node has a right child.
	 */
	public boolean HasRightChild()
	{return Right != null;}
	
	@Override public String toString()
	{return Data == null ? "null" : Data.toString();}
	
	/**
	 * The data in this node.
	 */
	public T Data;
	
	/**
	 * The parent of this node.
	 * If this itself, then the node is its own parent and thus is a root.
	 */
	public ME Parent;
	
	/**
	 * The left child of this node (if any).
	 */
	public ME Left;
	
	/**
	 * The right child of this node (if any).
	 */
	public ME Right;
}
