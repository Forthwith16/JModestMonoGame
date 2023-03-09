package gamecore.datastructures.trees;

import java.util.Comparator;

import gamecore.LINQ.LINQ;
import gamecore.datastructures.trees.nodes.AbstractRedBlackTreeNode;

/**
 * An abstract balanced binary tree implemented via a red-black scheme.
 * Duplicates are not permitted in the tree, but null is allowed if the {@code Comparator} used to compare {@code T} types permits it.
 * Note that the {@code Comparator} is assumed to return 0 only if items are equal, not merely equivalent.
 * @author Dawn Nye
 * @param <T> The type of object stored in the tree.
 * @param <NODE> The node type to build the tree with.
 */
public abstract class AbstractRedBlackTree<T,NODE extends AbstractRedBlackTreeNode<T,NODE>> extends AbstractBalancedBinarySearchTree<T,NODE>
{
	/**
	 * Creates an empty red-black tree.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} is null.
	 */
	protected AbstractRedBlackTree(Comparator<T> cmp)
	{
		super(cmp);
		return;
	}
	
	/**
	 * Creates a red-black tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @param cmp The means by which we compare elements of the tree.
	 * @throws NullPointerException Thrown if {@code cmp} or {@code seed} is null.
	 */
	protected AbstractRedBlackTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		super(seed,cmp);
		return;
	}
	
	protected Iterable<NODE> BalanceAdd(NODE n)
	{
		// There are eight cases (two pairs of which are symmetric), the first three of which are easy
		// If we are the root, we need not do anything
		// We insert nodes red, so if our parent is black, we don't need to do anything
		if(n.IsRoot() || n.Parent().IsBlack())
			return LINQ.Append(LINQ.Empty(),n);
		
		// If the parent is red and the root, we only need to color it red to be done
		if(n.Parent().IsRoot() && n.Parent().IsRed())
		{
			n.Parent().MakeBlack();
			return LINQ.Append(LINQ.Empty(),n);
		}
		
		// For all remaining cases, we need the parent, grandparent, and uncle, so let's just fetch them
		NODE parent = n.Parent();
		NODE uncle = n.GetUncle(); // May not exist (if so, it's treated as black)
		NODE grandparent = n.GetGrandparent(); // Must exist since we covered both cases where n's parent is the root (the case where the root is black is covered by doing nothing)
		
		// If we have a red parent, a red uncle, and a black grandparent, then we can preserve the black height by making the middle generation black and the grandparent red
		if(parent.IsRed() && (uncle != null && uncle.IsRed()) && grandparent.IsBlack())
		{
			parent.MakeBlack();
			grandparent.MakeRed();
			uncle.MakeBlack();
			
			Iterable<NODE> ret = BalanceAdd(grandparent);
			
			if(LINQ.Contains(ret,n))
				return ret;
			
			return LINQ.Append(ret,n);
		}
		
		// We'll need to keep track of what we modify
		Iterable<NODE> ret = LINQ.Empty();
		
		// We have four cases remaining, and we know the uncle is black now (it may not exist, however, which is treated as black)
		// If we are a right child of a left child, let's rotate left to get into a more convenient rotation form
		if(n.IsRightChild() && parent.IsLeftChild())
		{
			if(parent.LeftRotation())
				Root = parent.Parent(); // This should never happen, but just in case
			
			// We changed these nodes that won't be caught by the next case
			ret = LINQ.Append(ret,parent);
			
			if(parent.HasRightChild())
				ret = LINQ.Append(ret,parent.Right());
			
			// We're not done yet!
			// We still need to rotate right so that n is placed in the position of its grandparent
			// To make this happen, we rotate right on its new left child, which was its old parent
			// We save a little time by updating the family dynamic and simply falling through to the last case below
			parent = n;
			n = n.Left();
		}
		else if(n.IsLeftChild() && parent.IsRightChild()) // We with the previous case, if we're a left child of a right child, let's rotate right to get into a more convenient rotation form
		{
			if(parent.RightRotation())
				Root = parent.Parent(); // This should never happen, but just in case
			
			// We changed these nodes that won't be caught by the next case
			ret = LINQ.Append(ret,parent);
			
			if(parent.HasLeftChild())
				ret = ret = LINQ.Append(ret,parent.Left());
			
			// To finish just as before, we update the family dynamic
			parent = n;
			n = n.Right();
		}
		
		// We now know we're an 'outer child' with a black uncle, so we need to rotate still
		// This is NOT an else if because we want to fall through from the prevous two cases into these two
		if(n.IsLeftChild() && parent.IsLeftChild())
		{
			// We now know that we are a left child with a black uncle, so we need to rotate right at the grandparent
			// We'll then need to recolor the grandparent red and the parent black
			if(grandparent.RightRotation())
				Root = parent;
			
			// The following nodes all have changed links we may need to process
			ret = LINQ.Append(LINQ.Append(ret,parent),grandparent);
			
			if(grandparent.HasLeftChild())
				ret = LINQ.Append(ret,grandparent.Left());
			
			if(!parent.IsRoot())
				ret = LINQ.Append(ret,parent.Parent());
		}
		else // if(n.IsRightChild() && parent.IsRightChild()) It is impossible for any other case to get here, so we don't need to check this condition but leave it commented here for readability
		{
			if(grandparent.LeftRotation())
				Root = parent;
			
			// The following nodes all have changed links we may need to process
			ret = LINQ.Append(LINQ.Append(ret,parent),grandparent);
			
			if(grandparent.HasRightChild())
				ret = LINQ.Append(ret,grandparent.Right());
			
			if(!parent.IsRoot())
				ret = LINQ.Append(ret,parent.Parent());
		}
		
		// We recolor identically, so let's just throw these out here
		parent.MakeBlack();
		grandparent.MakeRed();
		
		// We don't need to call this function recursively now since the top of the tree is now black and the black height is unchanged 
		return ret;
	}
	
	protected Iterable<NODE> BalanceRemove(NODE n)
	{
		// If we removed the root node, we altered the black height for everyone and don't need to worry about anything anymore
		if(n.IsRoot())
			return LINQ.Append(LINQ.Empty(),n);
		
		// If we deleted a red node, we're good
		// We only consider this case if this is the node we actually deleted
		if(n.IsRed()) // We will only ever call this function on red nodes that were truly deleted, not merely recursed upon, so we don't need to check that n is not part of the tree anymore
			return LINQ.Append(LINQ.Empty(),n);
		
		// Prep for adding things to return
		Iterable<NODE> ret = LINQ.Append(LINQ.Empty(),n);
		
		// We'll need this information anyway, so grab it now
		NODE sibling = n.GetSibling();
		
		// We now know that we deleted a black node (hence sibling cannot be null), so we need to rebalance the black height
		// If we have a red sibling, we need to do some preprocessing before we can fall through to the remaining cases
		if(sibling.IsRed()) // null siblings are black
		{
			// We lost a black node, so fixing this is going to be a chore
			// We first recolor the parent and sibling
			n.Parent().MakeRed(); // We know the parent is not red because the silbing IS red
			sibling.MakeBlack();
			
			// We now rotate left if we were a left child and rotate right if we were a right child to (mostly) rebalance the black heights
			if(n.IsLeftChild())
			{
				if(n.Parent().LeftRotation())
					Root = sibling;
				
				// Add the affected nodes to ret
				ret = LINQ.Append(ret,sibling);
				ret = LINQ.Append(ret,n.Parent());
				
				if(n.Parent().HasRightChild())
					ret = LINQ.Append(ret,n.Parent().Right());
				
				if(!sibling.IsRoot())
					ret = LINQ.Append(ret,sibling.Parent());
			}
			else // if(n.IsRightChild(was_left_child)) In this case, we know that n is not the root, so we don't need to check this
			{
				if(n.Parent().RightRotation())
					Root = sibling;
				
				// Add the affected nodes to ret
				ret = LINQ.Append(ret,sibling);
				ret = LINQ.Append(ret,n.Parent());
				
				if(n.Parent().HasLeftChild())
					ret = LINQ.Append(ret,n.Parent().Left());
				
				if(!sibling.IsRoot())
					ret = LINQ.Append(ret,sibling.Parent());
			}
			
			// We don't return or recurse here because we want to fall through to the next cases
			// Let's also update our family dynamics
			sibling = n.GetSibling();
		}
		
		// We now know that our sibling is black
		// We have four cases
		// There are two cases where our sibling's children are black depending on the color of the parent
		if((!sibling.HasLeftChild() || sibling.Left().IsBlack()) && (!sibling.HasRightChild() || sibling.Right().IsBlack())) /// Nibling order is unimportant and we have sibling, so who cares about Get_Nibling()
		{
			// With a red parent, n's side of the family is missing a black, so we'll share with our sibling by swapping their color with our parent's
			// With a black parent, the sibling's side of the family has one too many blacks, so we need only make the sibling (who has black children) red
			// In both cases, we want to make our sibling red to make things right
			sibling.MakeRed();
			
			// If our parent is red, we were actually exchanging its color with our sibling (who has black children)
			if(n.Parent().IsRed())
			{
				n.Parent().MakeBlack();
				return ret; // In this case, the black height is unchanged, so we can be done immediately
			}
			
			// If we have a black parent, then we still need to correct a black height imbalance
			// While our subtree has its black height in order, the greater tree may not since we lost a black height
			// To fix this, we recurse on the parent (and note that we use only logic that work regardless of whether or not n belongs to the tree, so recursing on a node still in the tree is fine)
			return LINQ.Distinct(LINQ.Concatenate(ret,BalanceRemove(n.Parent()))); // Note that n's parent is necessarily black;
		}
		
		// We now know that our sibling is black, has at least one red child, and its black height is one too big
		// The only cases that will matter to our logic is what color the 'outer nibling' is
		// If the outer nibling is black (hence the inner nibling must be red), then we'll be able to recolor and rotate into a situation where the outer nibling is red, so let's do that first
		NODE outer_nib = n.GetOuterNibling();
		
		if(outer_nib == null || outer_nib.IsBlack())
		{
			// We only need access to the inner nibling in this case
			NODE inner_nib = n.GetInnerNibling();
			
			// We want to get into a position where our outer nibling is red
			// To do this, we'll swap the colors of the inner nibling and the sibling
			sibling.MakeRed();
			inner_nib.MakeBlack(); // The inner nibling cannot be null since it is red
			
			// We now want to rotate about the sibling so that the inner nibling becomes its parent
			if(inner_nib.IsLeftChild())
			{
				// We need to perform a right rotation so that the sibling's left child comes up to be its parent
				if(sibling.RightRotation())
					Root = sibling.Parent(); // This should never happen, but just to be safe
				
				// Add the affected nodes to ret (we'll call LINQ.Distinct later)
				ret = LINQ.Append(ret,sibling);
				ret = LINQ.Append(ret,inner_nib);
				ret = LINQ.Append(ret,inner_nib.Parent()); // There MUST be a parent of the inner nibling
				
				if(inner_nib.HasLeftChild())
					ret = LINQ.Append(ret,inner_nib.Left());
				
				// Update the family dynamics
				sibling = n.Parent().Right();
				outer_nib = sibling.Right();
			}
			else // if(inner_nib.IsRightChild()) We don't need to check this case because the inner nibling cannot be the root
			{
				// We need to perform a left rotation so that the sibling's right child comes up to be its parent
				if(sibling.LeftRotation())
					Root = sibling.Parent(); // This should never happen, but just to be safe
				
				// Add the affected nodes to ret (we'll call LINQ.Distinct later)
				ret = LINQ.Append(ret,sibling);
				ret = LINQ.Append(ret,inner_nib);
				ret = LINQ.Append(ret,inner_nib.Parent()); // There MUST be a parent of the inner nibling
				
				if(inner_nib.HasRightChild())
					ret = LINQ.Append(ret,inner_nib.Right());
				
				// Update the family dynamics
				sibling = n.Parent().Left();
				outer_nib = sibling.Left();
			}
		}
		
		// We can now guarantee  that the outer nibling is red (and hence also exists)
		// The inner nibling can be black or red as it desires without affecting the remaining logic
		// We first recolor the sibling in its parent's color (this may have no change)
		if(sibling.Parent().IsBlack())
			sibling.MakeBlack();
		else
			sibling.MakeRed();
		
		// Next, we color the parent and outer nibling black
		sibling.Parent().MakeBlack();
		outer_nib.MakeBlack();
		
		// Lasly, we need to perform an inward rotation
		// That is if n was a left child, we need to rotate parent left
		// If n was a right child, we need to rotate parent right
		if(n.IsLeftChild())
		{
			// We need a left rotation at parent
			if(sibling.Parent().LeftRotation())
				Root = sibling;
			
			// Add the affected nodes to ret (we'll call LINQ.Distinct later)
			ret = LINQ.Append(ret,sibling);
			ret = LINQ.Append(ret,sibling.Left());
			
			if(!sibling.IsRoot())
				ret = LINQ.Append(ret,sibling.Parent());
			
			if(sibling.Left().HasRightChild())
				ret = LINQ.Append(ret,sibling.Left().Right());
		}
		else // if(n.IsRightChild(was_left_child) We know n was not the root, so we don't need to check this case
		{
			// We need a right rotation at parent
			if(sibling.Parent().RightRotation())
				Root = sibling;
			
			// Add the affected nodes to ret (we'll call LINQ.Distinct later)
			ret = LINQ.Append(ret,sibling);
			ret = LINQ.Append(ret,sibling.Right());
			
			if(!sibling.IsRoot())
				ret = LINQ.Append(ret,sibling.Parent());
			
			if(sibling.Left().HasLeftChild())
				ret = LINQ.Append(ret,sibling.Right().Left());
		}
		
		// We are now done and have the black height balanced
		return LINQ.Distinct(ret);
	}
}
