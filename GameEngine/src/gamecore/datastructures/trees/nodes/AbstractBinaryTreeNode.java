package gamecore.datastructures.trees.nodes;

/**
 * The bare bones requirements to be a node in a binary tree.
 * Null values are permitted to be in the data field unless the child class prohibits it.
 * @author Dawn Nye
 * @param <T> The type of data to store in this node.
 * @param <ME>
 * This is the child class's type.
 * For example, we want a BSTNode<T> class that extends this class to create a node type for an ordinary binary search tree.
 * To do this, we extend ABTNode<T,BSTNode<T>>.
 * This enables us to use the derived node type within this class.
 * This is important because it avoids having to cast {@code Left}, {@code Right}, and {@code Parent} every time it's used with a derived class.
 */
public abstract class AbstractBinaryTreeNode<T,ME extends AbstractBinaryTreeNode<T,ME>>
{
	/**
	 * Creates a node with the given children and parent.
	 * @param data The data to put into the node.
	 * @param parent The parent of this node. If this value is null, then the node's parent will be set to itself, thus making it a root node.
	 * @param left The left child of this node.
	 * @param right The right child of this node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child. This value is ignored if this node is a root.
	 */
	protected AbstractBinaryTreeNode(T data, ME parent, ME left, ME right, boolean is_left_child)
	{
		Data = data;
		StitchTogether(parent,left,right,is_left_child);
		
		return;
	}
	
	/**
	 * Stitches this node into the tree with the given surrounding nodes.
	 * @param parent The parent node. If this value is null, the node's parent will be set to itself (making it a root node).
	 * @param left The left child node.
	 * @param right The right child node.
	 * @param is_left_child If true, then this is a left child. If false, then this is right child. This value is ignored if this node becomes a root.
	 */
	public void StitchTogether(ME parent, ME left, ME right, boolean is_left_child)
	{
		Parent = parent;
		Left = left;
		Right = right;
		
		if(Parent == null) // We're a root node and need to set up properly by making ourself our own parent
			Parent = (ME)this;
		else if(!IsRoot())
			if(is_left_child)
				Parent.Left = (ME)this;
			else
				Parent.Right = (ME)this;
		
		if(HasLeftChild())
			Left.Parent = (ME)this;
		
		if(HasRightChild())
			Right.Parent = (ME)this;
		
		return;
	}
	
	/**
	 * Stitches this node into the tree with its already existant links used as the thread.
	 * This is useful for stitching a node back into a tree after it was removed.
	 * @param was_left_child If true, this node was a left child of its parent. If false, it was a right child. This value is ignored it this node was a root.
	 */
	public void IdentityStitch(boolean was_left_child)
	{
		StitchTogether(Parent,Left,Right,was_left_child);
		return;
	}
	
	/**
	 * Performs a left rotation at this node in the tree.
	 * This node's right child becomes its parent.
	 * It's left child remains unchange.
	 * It's right child's left child becomes it's right child.
	 * Lastly, it's right child's right child remains unchanged.
	 * @return Returns true if this node's parent is now the root and false otherwise.
	 * @throws IllegalStateException Thrown if this node does not have a right child.
	 */
	public boolean LeftRotation()
	{
		if(!HasRightChild())
			throw new IllegalStateException();
		
		boolean ret = IsRoot();
		
		ME parent = ret ? Right : Parent;
		boolean left = IsLeftChild();
		
		StitchTogether(Right,Left,Right.Left,true);
		Parent.StitchTogether(parent,Parent.Left,Parent.Right,left);
		
		return ret;
	}
	
	/**
	 * Performs a right rotation at this node in the tree.
	 * This node's left child becomes its parent.
	 * It's right child remains unchange.
	 * It's left child's right child becomes it's left child.
	 * Lastly, it's left child's left child remains unchanged.
	 * @return Returns true if this node's parent is now the root and false otherwise.
	 * @throws IllegalStateException Thrown if this node does not have a left child.
	 */
	public boolean RightRotation()
	{
		if(!HasLeftChild())
			throw new IllegalStateException();
		
		boolean ret = IsRoot();
		
		ME parent = ret ? Left : Parent;
		boolean left = IsLeftChild();
		
		StitchTogether(Left,Left.Right,Right,false);
		Parent.StitchTogether(parent,Parent.Left,Parent.Right,left);
		
		return ret;
	}
	
	/**
	 * Finds the next node in the binary tree (if there is one).
	 * @return Returns the next node in the binary tree or null if there is no next node.
	 */
	public ME FindNextNode()
	{
		if(HasRightChild())
		{
			ME n = Right;
			
			while(n.HasLeftChild())
				n = n.Left;
			
			return n;
		}
		
		if(IsRoot() && !HasRightChild())
			return null;
		
		ME n = (ME)this;
		
		while(n.IsRightChild())
			if(n.Parent.IsRoot())
				return null;
			else
				n = n.Parent;
		
		return n.Parent;
	}
	
	/**
	 * Finds the previous node in the binary tree (if there is one).
	 * @return Returns the previous node in the binary tree or null if there is no previous node.
	 */
	public ME FindPreviousNode()
	{
		if(HasLeftChild())
		{
			ME n = Left;
			
			while(n.HasRightChild())
				n = n.Right;
			
			return n;
		}
		
		if(IsRoot() && !HasLeftChild())
			return null;
		
		ME n = (ME)this;
		
		while(n.IsLeftChild())
			if(n.Parent.IsRoot())
				return null;
			else
				n = n.Parent;
		
		return n.Parent;
	}
	
	/**
	 * Determines if this node is properly part of a tree.
	 * That means that its parent is not null, it is either a root node or a child of its parent, and its children (when present) have it as their parent.
	 * @return
	 */
	public boolean IsPartOfTree()
	{return Parent != null && (IsRoot() || IsLeftChild() || IsRightChild()) && (!HasLeftChild() || Left.Parent == this) && (!HasRightChild() || Right.Parent == this);}
	
	/**
	 * Gets the node that is now in its old position after being removed from the tree.
	 * @return Returns the node that replace this node (in the absolute positional sense described above), null if there is no node there, or this node itself if it is still part of the tree.
	 * @implNote This method requires this node's links to remain intact, its parent to have not moved unless this was a root, in which case its children (if it had any) must to still belong to the tree.
	 */
	public ME GetReplacement(boolean was_left_child)
	{return IsRoot() ? (HasLeftChild() ? Left.GetRoot() : (HasRightChild() ? Right.GetRoot() : null)) : (IsLeftChild(was_left_child) ? Parent.Left : Parent.Right);}
	
	/**
	 * Gets the root node of the tree this belongs to.
	 * @implNote This works even if this node is no longer part of a tree so long as the chain of {@code Parent} variables still leads to the current root.
	 */
	public ME GetRoot()
	{
		// This initialization can't go wrong
		ME ret = Parent;
		
		while(!ret.IsRoot())
			ret = ret.Parent;
		
		return ret;
	}
	
	/**
	 * Obtains the grandparent of this node.
	 * @return Returns the grandparent of this node. If this node has no grandparent, null is returned instead.
	 */
	public ME GetGrandparent()
	{return HasGrandparent() ? Parent.Parent : null;}
	
	/**
	 * Obtains the sibling of this node.
	 * @return Returns the sibling of this node. If the node has no sibling, null is returned instead.
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public ME GetSibling()
	{return IsRoot() ? null : (IsLeftChild() ? Parent.Right : Parent.Left);}
	
	/**
	 * Obtains the sibling of this node.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @return Returns the sibling of this node. If the node had no sibling, null is returned instead.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent and sibling have not moved.
	 */
	public ME GetSibling(boolean was_left_child)
	{return IsRoot() ? null : (IsLeftChild(was_left_child) ? Parent.Right : Parent.Left);}
	
	/**
	 * Obtains the uncle of this node.
	 * @return Returns the uncle of this node. If this no has no uncle, null is returned instead.
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public ME GetUncle()
	{return HasUncle() ? (Parent.IsLeftChild() ? GetGrandparent().Right : GetGrandparent().Left) : null;}
	
	/**
	 * Obtains the uncle of this node.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @return Returns the uncle of this node. If this no has no uncle, null is returned instead.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, grandparent, and uncle have not moved.
	 */
	public ME GetUncle(boolean was_left_child)
	{return HasUncle() ? (IsLeftChild(was_left_child) ? GetGrandparent().Right : GetGrandparent().Left) : null;}
	
	/**
	 * Obtains the 'inner nibling' of this node.
	 * This is to say this returns the child of this node's sibling that is as close to it as possible.
	 * If this is a left child of its parent, then it will get the left nibling and if it's a right child, the right nibling.
	 * @return Returns the inner nilbing of this node. If this no has no inner nibling, null is returned instead.
	 * @implNote This method works only if the node is part of the tree, meaning it's parent links back to it.
	 */
	public ME GetInnerNibling()
	{return HasSibling() ? (IsLeftChild() ? GetSibling().Left : GetSibling().Right) : null;}
	
	/**
	 * Obtains the 'inner nibling' of this node.
	 * This is to say this returns the child of this node's sibling that is as close to it as possible.
	 * If this is a left child of its parent, then it will get the left nibling and if it's a right child, the right nibling.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @return Returns the inner nibling of this node. If this no has no inner nibling, null is returned instead.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, sibling, and inner nibling have not moved.
	 */
	public ME GetInnerNibling(boolean was_left_child)
	{return HasSibling(was_left_child) ? (IsLeftChild(was_left_child) ? GetSibling(was_left_child).Left : GetSibling(was_left_child).Right) : null;}
	
	/**
	 * Obtains the 'outer nibling' of this node.
	 * This is to say this returns the child of this node's sibling that is as far from it as possible.
	 * If this is a left child of its parent, then it will get the right nibling and vice versa.
	 * @return Returns the outer nilbing of this node. If this no has no outer nibling, null is returned instead.
	 * @implNote This method works only if the node is part of the tree, meaning it's parent links back to it.
	 */
	public ME GetOuterNibling()
	{return HasSibling() ? (IsLeftChild() ? GetSibling().Right : GetSibling().Left) : null;}
	
	/**
	 * Obtains the 'outer nibling' of this node.
	 * This is to say this returns the child of this node's sibling that is as far from it as possible.
	 * If this is a left child of its parent, then it will get the right nibling and vice versa.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @return Returns the outer nibling of this node. If this no has no outer nibling, null is returned instead.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, sibling, and outer nibling have not moved.
	 */
	public ME GetOuterNibling(boolean was_left_child)
	{return HasSibling(was_left_child) ? (IsLeftChild(was_left_child) ? GetSibling(was_left_child).Right : GetSibling(was_left_child).Left) : null;}
	
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
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public boolean IsLeftChild()
	{return !IsRoot() && Parent.Left == this;}
	
	/**
	 * Determines if this is a left child of its parent.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact.
	 */
	public boolean IsLeftChild(boolean was_left_child)
	{return !IsRoot() && was_left_child;}
	
	/**
	 * Determines if this is a right child of its parent.
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public boolean IsRightChild()
	{return !IsRoot() && Parent.Right == this;}
	
	/**
	 * Determines if this is a right child of its parent.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact.
	 */
	public boolean IsRightChild(boolean was_left_child)
	{return !IsRoot() && !was_left_child;}
	
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
	
	/**
	 * Determines if this node has a grandparent.
	 */
	public boolean HasGrandparent()
	{return !Parent.IsRoot();} // If this is the root node, it's parent is the root; similarly, if this is a child of the root, then its parent is the root; in all other cases, we have a grandparent
	
	/**
	 * Dertmines if this node has an uncle.
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public boolean HasUncle()
	{return HasGrandparent() && (Parent.IsLeftChild() && GetGrandparent().HasRightChild() || Parent.IsRightChild() && GetGrandparent().HasLeftChild());}
	
	/**
	 * Dertmines if this node has an uncle.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, grandparent, and uncle have not moved.
	 */
	public boolean HasUncle(boolean was_left_child)
	{return HasGrandparent() && (Parent.IsLeftChild() && GetGrandparent().HasRightChild() || Parent.IsRightChild() && GetGrandparent().HasLeftChild());}
	
	/**
	 * Dertmines if this node has a sibling.
	 * @implNote This method works only if the node is part of the tree (meaning the parent and children's reverse links point to it).
	 */
	public boolean HasSibling()
	{return !IsRoot() && (IsLeftChild() && Parent.HasRightChild() || IsRightChild() && Parent.HasLeftChild());}
	
	/**
	 * Dertmines if this node has a sibling.
	 * @param was_left_child If true, then this node was a left child of its parent. If false, then it was a right child. If this node was a root, this value has no meaning.
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, and sibling have not moved.
	 */
	public boolean HasSibling(boolean was_left_child)
	{return !IsRoot() && (IsLeftChild(was_left_child) && Parent.HasRightChild() || IsRightChild(was_left_child) && Parent.HasLeftChild());}
	
	/**
	 * Determines if this node has an inner nibling.
	 * @return Returns true if this node has an inner nibling and false otherwise.
	 * @see {@code GetInnerNibling()}
	 * @implNote This method works only if the node is part of the tree, meaning it's parent links back to it.
	 */
	public boolean HasInnerNibling()
	{return HasSibling() && (IsLeftChild() && GetSibling().HasLeftChild() || IsRightChild() && GetSibling().HasRightChild());}
	
	/**
	 * Determines if this node has an inner nibling.
	 * @return Returns true if this node has an inner nibling and false otherwise.
	 * @see {@code GetInnerNibling(boolean)}
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, sibling, and inner nibling have not moved.
	 */
	public boolean HasInnerNibling(boolean was_left_child)
	{return HasSibling(was_left_child) && (IsLeftChild(was_left_child) && GetSibling(was_left_child).HasLeftChild() || IsRightChild(was_left_child) && GetSibling(was_left_child).HasRightChild());}
	
	/**
	 * Determines if this node has an outer nibling.
	 * @return Returns true if this node has an outer nibling and false otherwise.
	 * @see {@code GetOuterNibling()}
	 * @implNote This method works only if the node is part of the tree, meaning it's parent links back to it.
	 */
	public boolean HasOuterNibling()
	{return HasSibling() && (IsLeftChild() && GetSibling().HasLeftChild() || IsRightChild() && GetSibling().HasRightChild());}
	
	/**
	 * Determines if this node has an outer nibling.
	 * @return Returns true if this node has an outer nibling and false otherwise.
	 * @see {@code GetOuterNibling(boolean)}
	 * @implNote This method works even if the node is no longer part of the tree so long as its links remain intact and its parent, sibling, and outer nibling have not moved.
	 */
	public boolean HasOuterNibling(boolean was_left_child)
	{return HasSibling(was_left_child) && (IsLeftChild(was_left_child) && GetSibling(was_left_child).HasRightChild() || IsRightChild(was_left_child) && GetSibling(was_left_child).HasLeftChild());}
	
	@Override public String toString()
	{return "Data: " + (Data != null ? Data.toString() : "null") + "\nParent: " + Parent.Data.toString() + "\nLeft: " + (Left != null ? (Left.Data != null ? Left.Data.toString() : "null entry") : "null") + "\nRight: " + (Right != null ? (Right.Data != null ? Right.Data.toString() : "null entry") : "null");}
	
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