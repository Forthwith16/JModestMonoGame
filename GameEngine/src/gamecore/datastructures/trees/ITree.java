package gamecore.datastructures.trees;

import java.util.NoSuchElementException;

import gamecore.datastructures.ICollection;

/**
 * Defines the bare bones basics of a tree.
 * An implementation of this interface may be a Cartesian tree, a segment tree, an AABB tree, or any other related data structure.
 * Whether the tree is self-balancing depends on the data structure it represents and the implementation.
 * The order of iteration for a tree depends on the implementation, but an in-order traversal is often the correct choice.
 * @author Dawn Nye
 * @param <T> The type to store in the tree.
 */
public interface ITree<T> extends ICollection<T>
{
	/**
	 * Adds {@code t} to the tree.
	 * @param t The item to add.
	 * @return Returns true if the item was added and false otherwise.
	 */
	public boolean Add(T t);
	
	/**
	 * Removes {@code t} from the tree.
	 * @param t The item to remove.
	 * @return Returns true if the item was removed and false otherwise.
	 */
	public boolean Remove(T t);
	
	/**
	 * Determines if the tree contains {@code t}.
	 * @param t The item to search for.
	 * @return Returns true if the tree contains {@code t} and false otherwise.
	 */
	public boolean Contains(T t);
	
	/**
	 * Obtains the data at the root of the tree.
	 * @throws NoSuchElementException Thrown if the tree is empty.
	 */
	public T Root();
	
	/**
	 * Clears the tree of all elements.
	 */
	public void Clear();
	
	/**
	 * Determines the number of items in the tree.
	 */
	public int Count();
	
	/**
	 * Determines if the tree is empty.
	 * @return Returns true if the tree is empty and false otherwise.
	 */
	public boolean IsEmpty();
	
	/**
	 * Performs a pre-order traversal of the tree.
	 * This visits the elements of the tree in topological order and is equivalent to a depth-first search.
	 * @param f The function to call when visiting each node.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 */
	public void PreOrderTraversal(TraversalFunction<T> f);
	
	/**
	 * Performs a post-order traversal of the tree.
	 * This visits the elements of the tree in a reversed topological order and is equivalent to a depth-first search.
	 * @param f The function to call when visiting each node.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 */
	public void PostOrderTraversal(TraversalFunction<T> f);
	
	/**
	 * Performs an in-order traversal of the (binary) tree.
	 * This visits the elements of the tree in ascending order.
	 * @param f The function to call when visiting each node.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 * @throws UnsupportedOperationException Thrown if this tree is not a binary tree and has no alternative meaning assigned to an in-order traversal.
	 */
	public void InOrderTraversal(TraversalFunction<T> f);
	
	/**
	 * Performs a reverse in-order traversal of the (binary) tree.
	 * This visits the elements of the tree in descending order.
	 * @param f The function to call when visiting each node.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 * @throws UnsupportedOperationException Thrown if this tree is not a binary tree and has no alternative meaning assigned to a reverse in-order traversal.
	 */
	public void ReverseInOrderTraversal(TraversalFunction<T> f);
	
	/**
	 * Performs a level-order traversal of the tree.
	 * This is equivalent to a breadth-first search.
	 * @param f The function to call when visiting each node.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 */
	public void LevelOrderTraversal(TraversalFunction<T> f);
	
	/**
	 * Allows any arbitrary operation to be performed at each node of a tree during a traversal.
	 * During each visit of a node, the following information is provided: the node's data, its position (index) in the traversal, and its depth in the tree.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface TraversalFunction<E>
	{
		/**
		 * Visis a node during a traversal of a tree.
		 * @param e The data inside of the node currently being visited.
		 * @param index During a traversal, the node currently being visited is the {@code index}th node visited.
		 * @param depth During a traversal, the node currently being visited is at (zero-index) dpeth {@code depth} in the tree.
		 */
		public void Visit(E e, int index, int depth);
	}
}
