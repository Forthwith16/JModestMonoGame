package gamecore.datastructures.trees;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.queues.Queue;
import gamecore.datastructures.queues.Stack;
import gamecore.datastructures.trees.nodes.AbstractBinaryTreeNode;
import gamecore.datastructures.tuples.Pair;


/**
 * An abstract binary tree.
 * This defines the common underlying methods of a binary tree. 
 * In order to facilitate this, we also have a generic parameter for the node type to enable nodes to store any necessary information within them.
 * Duplicates are allowed in the tree unless the derived class prohibits this.
 * Moreover, null entries are similarly allowed unless the derived class prohibits them.
 * <br><br>
 * To specify precisely the behavior of this tree, the key functions not override are {@code AddN}, {@code RemoveN}, and {@code Contains}.
 * The former two methods are protected and add/remove a node from the tree, which {@code Add} and {@code Remove} use to return a boolean.
 * There is no need to override either {@code Add} or {@code Remove} because of this except in niche use cases.
 * <br><br>
 * We give the default implementations of {@code AddN}, {@code RemoveN}, and {@code Contains} below.
 * <ul>
 * 	<li>{@code AddN}: Adds the new element to the first avilable location in the tree. This is at the bottommost level of the tree, moving from left to right.</li>
 * 	<li>{@code RemoveN}: Removes an element from the tree and replaces it with the last element in the tree's bottommost level, searching from right to left.</li>
 * 	<li>{@code Contains}: Brute force searches for an element in the tree.</li>
 * </ul>
 * In short, this is a complete binary tree at all times.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 * @param <NODE> The node type to build the tree with.
 */
public abstract class AbstractBinaryTree<T,NODE extends AbstractBinaryTreeNode<T,NODE>> implements ITree<T>
{
	/**
	 * Creates an empty binary tree.
	 */
	protected AbstractBinaryTree()
	{
		Root = null;
		Count = 0;
		
		return;
	}
	
	/**
	 * Creates a binary tree initially populated with {@code seed}.
	 * @param seed The initial values to palce in the tree. They will be added to the tree one by one in the order they appear.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	protected AbstractBinaryTree(Iterable<? extends T> seed)
	{
		if(seed == null)
			throw new NullPointerException();
		
		Root = null;
		Count = 0;
		
		for(T t : seed)
			Add(t);
		
		return;
	}
	
	/**
	 * Creates a node for this tree.
	 * All variables may be null (except {@code data} if the tree prohibits null entries).
	 * @param data The data to place in the node.
	 * @param parent The parent of the node. If this value is null, then the node's parent will be set to itself, thus making it a root node.
	 * @param left The left child of the node.
	 * @param right The right child of the node.
	 * @param is_left_child If true, then the node created should be a left child of its parent. If false, it should be a right child of its parent. If the parent is null, this value can be ignored.
	 * @return Returns a new node for this tree properly linked into the tree with the given parent/child nodes. Additional processing need not be done in this function.
	 */
	protected abstract NODE CreateNode(T data, NODE parent, NODE left, NODE right, boolean is_left_child);
	
	public boolean Add(T t)
	{
		NODE n = AddN(t);
		
		if(n != null)
			PropogatePropertyAdd(n);
		
		return n != null;
	}
	
	/**
	 * Adds {@code t} to the tree.
	 * @param t the item to add.
	 * @return Returns the node containing {@code t} added to the tree or null if {@code t} could not be added.
	 */
	protected NODE AddN(T t)
	{
		// We special case the root
		if(IsEmpty())
		{
			Root = CreateNode(t,null,null,null,false);
			Count++;
			
			return Root;
		}
		
		// We can calculate the first free position in the tree from its size
		// For example, suppose our size (after insertion) is 1001b
		// We ignore the first 1 and then interpret the remaining bits from left to right as instructions
		// 0's indicate a left movement while 1's indicate a right movement
		NODE cur = Root;
		NODE prev = Root;
		
		// We want to ignore the first 1 bit, as it's not part of our instructions, so we can loop until mask is exactly 0
		int mask = Integer.highestOneBit(++Count) >> 1;
		
		while(mask > 0)
		{
			prev = cur;
			
			if((Count & mask) > 0)
				cur = cur.Right();
			else
				cur = cur.Left();
			
			mask >>= 1;
		}
		
		// Even cases are left children; odd cases are right children
		return CreateNode(t,prev,null,null,(Count & 0b1) == 0);
	}
	
	/**
	 * Propogates a binary tree property starting from {@code n}.
	 * This version of propogation is used after a node has been <i>added</i>.
	 * @param n The node to start propogating a property from. This does nothing if {@code n} is null.
	 */
	protected void PropogatePropertyAdd(NODE n)
	{
		if(n == null)
			return;
		
		for(PropogationDirection dir : MaintainPropertyAdd(n))
			switch(dir)
			{
			case PARENT:
				PropogatePropertyAdd(n.Parent());
				break;
			case LEFT:
				PropogatePropertyAdd(n.Left());
				break;
			case RIGHT:
				PropogatePropertyAdd(n.Right());
				break;
			}
		
		return;
	}
	
	/**
	 * Maintains some additional structural property of the binary tree.
	 * This version of this maintenance function is used for when a node has been <i>added</i> to the tree.
	 * @param n The node that was added or an ancestor of it as the property is propogated up the tree.
	 * @return Returns a set of enum flags specifying which directions the property must be propogated to be true everywhere. If this value is empty, then there is nothing left to do.
	 */
	protected EnumSet<PropogationDirection> MaintainPropertyAdd(NODE n)
	{return EnumSet.noneOf(PropogationDirection.class);}
	
	public boolean Remove(T t)
	{
		NODE n = RemoveN(t);
		
		if(n != null)
			PropogatePropertyRemove(n);
		
		return n != null;
	}
	
	/**
	 * Removes {@code t} from the tree.
	 * @param t the item to remove.
	 * @return Returns the node containing {@code t} removed from the tree or null if {@code t} could not be removed. The node returned should have its own links remain intact but should be otherwise disconnected from the tree.
	 */
	protected NODE RemoveN(T t)
	{
		NODE rem = Find(Root,t);
		
		if(rem == null)
			return null;
		
		// We'll swap the data we want to remove with a garbage node
		NODE last = FindLastNode(Root);
		
		// We'll preserve the data just in case
		SwapNodeContents(rem,last);
		
		if(last.IsLeftChild())
			last.Parent().StitchLeftChild(null);
		else
			last.Parent().StitchRightChild(null);
		
		// It's very important we do this after we find the last node, or else we'll get lost
		Count--;
		
		// Since the root is its own parent, we can get away with not special casing the root in this remove method
		if(Count == 0)
			Root = null;
		
		// If we have a property to maintain with the node we modified but didn't remove, do so
		if(rem != last)
			PropogatePropertyRemove(rem);
		
		return last;
	}
	
	/**
	 * Propogates a binary tree property starting from {@code n}.
	 * This version of propogation is used after a node has been <i>removed</i>.
	 * @param n The node to start propogating a property from. This may initially be the removed node after it has already been removed from the tree. This does nothing if {@code n} is null.
	 */
	protected void PropogatePropertyRemove(NODE n)
	{
		if(n == null)
			return;
		
		for(PropogationDirection dir : MaintainPropertyRemove(n))
			switch(dir)
			{
			case PARENT:
				PropogatePropertyRemove(n.Parent());
				break;
			case LEFT:
				PropogatePropertyRemove(n.Left());
				break;
			case RIGHT:
				PropogatePropertyRemove(n.Right());
				break;
			}
		
		return;
	}
	
	/**
	 * Maintains some additional structural property of the binary tree.
	 * This version of this maintenance function is used for when a node has been <i>removed</i> from the tree.
	 * @param n The node that was removed (and hence is no longer part of the tree) or some other node reached via propogation through the tree.
	 * @return Returns a set of enum flags specifying which directions the property must be propogated to be true everywhere. If this value is empty, then there is nothing left to do.
	 */
	protected EnumSet<PropogationDirection> MaintainPropertyRemove(NODE n)
	{return EnumSet.noneOf(PropogationDirection.class);}
	
	/**
	 * Finds a node containing {@code t}.
	 * @param n The root node.
	 * @param t The item to look for (there may be more than one).
	 * @return Returns a node containing {@code t} if there is one and null otherwise.
	 */
	protected NODE Find(NODE n, T t)
	{
		if(n == null)
			return null;
		
		if(n.Data == null ? t == null : n.Data.equals(t))
			return n;
		
		NODE ret = Find(n.Left(),t);
		
		if(ret != null)
			return ret;
		
		return Find(n.Right(),t);
	}
	
	/**
	 * Obtains the last node in the tree.
	 * Note that this method only works correctly if the tree is complete and {@code Count} is exactly the number of nodes in the tree.
	 * @param n The root node.
	 * @return Returns the last node in the tree or null if the tree is empty.
	 */
	protected NODE FindLastNode(NODE n)
	{
		if(IsEmpty())
			return null;
		
		// We can find the last node just like we did in Add, only we don't need to keep a prev around
		NODE cur = Root;
		
		// We want to ignore the first 1 bit, as it's not part of our instructions, so we can loop until mask is exactly 0
		int mask = Integer.highestOneBit(Count) >> 1;
		
		while(mask > 0)
		{
			if((Count & mask) > 0)
				cur = cur.Right();
			else
				cur = cur.Left();
			
			mask >>= 1;
		}
		
		return cur;
	}
	
	/**
	 * Swaps the contents of two nodes.
	 * This includes at least the data of the nodes but may also include other properties of the nodes accompanying that data.
	 */
	protected void SwapNodeContents(NODE n1, NODE n2)
	{
		T temp = n1.Data;
		n1.Data = n2.Data;
		n2.Data = temp;
		
		return;
	}
	
	public boolean Contains(T t)
	{return Find(Root,t) != null;}
	
	public T Root()
	{
		if(Root == null)
			throw new NoSuchElementException();
		
		return Root.Data;
	}
	
	public void Clear()
	{
		Root = null;
		Count = 0;
		
		return;
	}
	
	public int Count()
	{return Count;}
	
	public boolean IsEmpty()
	{return Root == null;}
	
	public void PreOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(IsEmpty())
			return;
		
		Stack<Pair<NODE,Integer>> S = new Stack<Pair<NODE,Integer>>();
		S.Push(new Pair<NODE,Integer>(Root,0));
		
		int index = 0;
		
		while(!S.IsEmpty())
		{
			Pair<NODE,Integer> p = S.Pop();
			
			// Visit the node first
			f.Visit(p.Item1.Data,index++,p.Item2);
			
			// Now visit the children (push right first so we visit it second)
			if(p.Item1.HasRightChild())
				S.Push(new Pair<NODE,Integer>(p.Item1.Right(),p.Item2 + 1));
			
			if(p.Item1.HasLeftChild())
				S.Push(new Pair<NODE,Integer>(p.Item1.Left(),p.Item2 + 1));
		}
		
		return;
	}
	
	public void PostOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(IsEmpty())
			return;
		
		RPostOrderTraversal(f,Root,0,0);
		return;
	}
	
	/**
	 * It is <b>way</b> easier to do a post-order traversal recursively, so let's just do that.
	 * @param f The function to call when visiting each node.
	 * @param n The root node of the recursive call.
	 * @param index The index we're currently at.
	 * @param depth The depth we're currently at.
	 * @return Returns the next index available. If {@code n} is null, this will just be {@code index}.
	 */
	private int RPostOrderTraversal(TraversalFunction<T> f, NODE n, int index, int depth)
	{
		if(n == null)
			return index;
		
		// Visit the children first
		index = RPostOrderTraversal(f,n.Left(),index,depth + 1);
		index = RPostOrderTraversal(f,n.Right(),index,depth + 1);
		
		// Now visit this node
		f.Visit(n.Data,index,depth);
		
		// We processed one index in this recursive call, so return index + 1
		return index + 1;
	}
	
	public void InOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(IsEmpty())
			return;
		
		RInOrderTraversal(f,Root,0,0);
		return;
	}
	
	/**
	 * It is <b>way</b> easier to do an in-order traversal recursively, so let's just do that.
	 * @param f The function to call when visiting each node.
	 * @param n The root node of the recursive call.
	 * @param index The index we're currently at.
	 * @param depth The depth we're currently at.
	 * @return Returns the next index available. If {@code n} is null, this will just be {@code index}.
	 */
	private int RInOrderTraversal(TraversalFunction<T> f, NODE n, int index, int depth)
	{
		if(n == null)
			return index;
		
		// Visit the left child first
		index = RInOrderTraversal(f,n.Left(),index,depth + 1);
		
		// Now visit this node
		f.Visit(n.Data,index++,depth);
		
		// Lastly, visit the right child
		return RInOrderTraversal(f,n.Right(),index,depth + 1);
	}
	
	public void ReverseInOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(IsEmpty())
			return;
		
		ReverseRInOrderTraversal(f,Root,0,0);
		return;
	}
	
	/**
	 * It is <b>way</b> easier to do a reverse in-order traversal recursively, so let's just do that.
	 * @param f The function to call when visiting each node.
	 * @param n The root node of the recursive call.
	 * @param index The index we're currently at.
	 * @param depth The depth we're currently at.
	 * @return Returns the next index available. If {@code n} is null, this will just be {@code index}.
	 */
	private int ReverseRInOrderTraversal(TraversalFunction<T> f, NODE n, int index, int depth)
	{
		if(n == null)
			return index;
		
		// Visit the right child first
		index = ReverseRInOrderTraversal(f,n.Right(),index,depth + 1);
		
		// Now visit this node
		f.Visit(n.Data,index++,depth);
		
		// Lastly, visit the left child
		return ReverseRInOrderTraversal(f,n.Left(),index,depth + 1);
	}
	
	public void LevelOrderTraversal(TraversalFunction<T> f)
	{
		if(f == null)
			throw new NullPointerException();
		
		if(IsEmpty())
			return;
		
		Queue<Pair<NODE,Integer>> S = new Queue<Pair<NODE,Integer>>();
		S.Enqueue(new Pair<NODE,Integer>(Root,0));
		
		int index = 0;
		
		while(!S.IsEmpty())
		{
			Pair<NODE,Integer> p = S.Dequeue();
			
			// Visit the node first
			f.Visit(p.Item1.Data,index++,p.Item2);
			
			// Now visit the children
			if(p.Item1.HasLeftChild())
				S.Enqueue(new Pair<NODE,Integer>(p.Item1.Left(),p.Item2 + 1));
			
			if(p.Item1.HasRightChild())
				S.Enqueue(new Pair<NODE,Integer>(p.Item1.Right(),p.Item2 + 1));
		}
		
		return;
	}
	
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			public boolean hasNext()
			{
				// If we know we're done, just say so
				if(done)
					return false;
				
				// If we haven't started iterating yet, we need the smallest node
				// That node is the furthest left node from the root (which can be the root itself)
				if(n == null)
				{
					n = Root;
					
					while(n.HasLeftChild())
						n = n.Left();
					
					return true;
				}
				
				return !done;
			}
			
			public T next()
			{
				if(!hasNext())
					throw new NoSuchElementException();
				
				// Save the return value
				T ret = n.Data;
				
				// Advance the cursor
				n = n.FindNextNode();
				
				// If we advanced too far, we're done
				if(n == null)
					done = true;
				
				return ret;
			}
			
			public NODE n = null;
			public boolean done = IsEmpty();
		};
	}
	
	@Override public String toString()
	{
		if(IsEmpty())
			return "{}";
		
		MyToStringString = "Level-Order: {";
		LevelOrderTraversal((data,index,depth) -> MyToStringString += data.toString() + ",");
		
		MyToStringString = MyToStringString.substring(0,MyToStringString.length() - 1) + "}\nIn-Order: {";
		InOrderTraversal((data,index,depth) -> MyToStringString += data.toString() + ",");
		
		MyToStringString = MyToStringString.substring(0,MyToStringString.length() - 1) + "}\nReverse In-Order: {";
		ReverseInOrderTraversal((data,index,depth) -> MyToStringString += data.toString() + ",");
		
		MyToStringString = MyToStringString.substring(0,MyToStringString.length() - 1) + "}\nPre-Order: {";
		PreOrderTraversal((data,index,depth) -> MyToStringString += data.toString() + ",");
		
		MyToStringString = MyToStringString.substring(0,MyToStringString.length() - 1) + "}\nPost-Order: {";
		PostOrderTraversal((data,index,depth) -> MyToStringString += data.toString() + ",");
		
		return MyToStringString.substring(0,MyToStringString.length() - 1) + "}";
	}
	
	/**
	 * The root node of the tree.
	 * This is null when the tree is empty.
	 */
	protected NODE Root;
	
	/**
	 * The number of items in the tree.
	 */
	protected int Count;
	
	/**
	 * It's a lot easier to make {@code toString} do its job if we have this.
	 */
	protected String MyToStringString = null;
	
	/**
	 * Represents the required direction of propogation for a property in a binary tree to be maintained.
	 * @author Dawn Nye
	 */
	protected static enum PropogationDirection
	{
		PARENT,
		LEFT,
		RIGHT
	}
}