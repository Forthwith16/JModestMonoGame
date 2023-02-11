package gamecore.datastructures;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A double linked list backed collection.
 * The linked list possesses a dummy head and tail node.
 * @author Dawn Nye
 */
public class LinkedList<T> implements List<T>
{
	/**
	 * Creates an empty doubly linked list.
	 */
	public LinkedList()
	{
		Head = new Node();
		Tail = new Node();
		Head.MakeSequential(Tail);
		
		Count = 0;
		return;
	}
	
	/**
	 * Creates a doubly linked list with an initial population specified by {@code c}.
	 * @param c The initial elements to populate this linked list with.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public LinkedList(Iterable<? extends T> c)
	{
		Head = new Node();
		Tail = new Node();
		Head.MakeSequential(Tail);
		
		Count = 0;
		AddAllLast(c);
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof List)
		{
			List l = (List)obj;
			
			if(size() != l.size())
				return false;
			
			Iterator mine = iterator();
			Iterator yours = l.iterator();
			
			while(mine.hasNext())
			{
				Object mobj = mine.next();
				Object yobj = yours.next();
				
				if(!(mobj == null ? yobj == null : mobj.equals(yobj)))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Obtains the {@code index}th node of the list.
	 * This can obtain the head node or the tail node at index -1 and size() respectively.
	 * @param index The index of the node to return.
	 * @return Returns the {@code index}th node of the list.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is less than -1 or greater than size().
	 */
	protected Node GetNode_i(int index)
	{
		if(index < -1 || index > size())
			throw new IndexOutOfBoundsException();
		
		Node n;
		
		if(index < (size() >> 1))
		{
			n = Head;
			
			for(int i = -1;i < index;i++)
				n = n.Next;
		}
		else
		{
			n = Tail;
			
			for(int i = size();i > index;i--)
				n = n.Previous;
		}
		
		return n;
	}
	
	public boolean add(T e)
	{
		new Node(Tail.Previous,Tail,e);
		Count++;
		
		return true;
	}
	
	public void add(int index, T t)
	{
		Insert(t,index);
		return;
	}
	
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(add(t))
				ret = true;
		
		return ret;
	}
	
	public boolean addAll(int index, Collection<? extends T> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		Node n = GetNode_i(index - 1);
		
		for(T t : c)
		{
			new Node(t).Insert(n,n.Next);
			n = n.Next;
			Count++;
		}
		
		return true;
	}
	
	/**
	 * Adds {@code e} to the front of the list.
	 * @param e The item to add.
	 * @return Returns true if this collection was changed as a result of this call.
	 */
	public boolean AddFront(T e)
	{
		new Node(Head,Head.Next,e);
		Count++;
		
		return true;
	}
	
	/**
	 * Adds all elements of {@code c} to the front of the list in the order they appear.
	 * @param c The set of things to add.
	 * @return Returns true if this collection was changed as a result of this call.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean AddAllFront(Iterable<? extends T> c)
	{
		boolean ret = false;
		Node TrueHead = Head;
		
		for(T t : c)
			if(AddFront(t))
			{
				Head = Head.Next; // Move the head forward one so we can just keep using AddFront
				ret = true;
			}
		
		Head = TrueHead;
		return ret;
	}
	
	/**
	 * Adds {@code e} to the back of the list.
	 * @param e The item to add.
	 * @return Returns true if this collection was changed as a result of this call.
	 */
	public boolean AddLast(T e)
	{return add(e);}
	
	/**
	 * Adds all elements of {@code c} to the back of the list in the order they appear.
	 * @param c The set of things to add.
	 * @return Returns true if this collection was changed as a result of this call.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public boolean AddAllLast(Iterable<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			if(AddLast(t))
				ret = true;
		
		return ret;
	}
	
	/**
	 * Inserts {@code e} at index {@code index}.
	 * @param e The item to insert.
	 * @param index The index to insert at. Must be between (inclusive) 0 and size().
	 * @return Returns true if this collection was changed as a result of this call.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than size().
	 */
	public boolean Insert(T e, int index)
	{
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		Node n = GetNode_i(index);
		new Node(e).Insert(n.Previous,n);
		Count++;
		
		return true;
	}

	public boolean remove(Object o)
	{
		Node n = Head;
		
		while((n = n.Next) != Tail)
			if(n.Item == o || n.Item != null && n.Item.equals(o))
			{
				n.Extirpate();
				Count--;
				
				return true;
			}
		
		return false;
	}
	
	public T remove(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		Node n = GetNode_i(index);
		n.Extirpate();
		Count--;
		
		return n.Item;
	}

	/**
	 * Gets and removes the front element of this list.
	 * @return Returns the front element of this list.
	 * @throws NoSuchElementException Thrown if this list is empty.
	 */
	public T RemoveFront()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		
		T ret = Head.Next.Item;
		Head.Next.Extirpate();
		Count--;
		
		return ret;
	}
	
	/**
	 * Gets and removes the last element of this list.
	 * @return Returns the last element of this list.
	 * @throws NoSuchElementException Thrown if this list is empty.
	 */
	public T RemoveLast()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		
		T ret = Tail.Previous.Item;
		Tail.Previous.Extirpate();
		Count--;
		
		return ret;
	}
	
	public boolean removeAll(Collection<?> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		boolean ret = false;
		Node n = Head;
		
		while((n = n.Next) != Tail)
			if(c.contains(n.Item))
			{
				n.Extirpate();
				Count--;
				
				ret = true;
			}
		
		return ret;
	}

	public boolean retainAll(Collection<?> c)
	{
		if(c == null)
			throw new NullPointerException();
		
		boolean ret = false;
		Node n = Head;
		
		while((n = n.Next) != Tail)
			if(!c.contains(n.Item))
			{
				n.Extirpate();
				Count--;
				
				ret = true;
			}
		
		return ret;
	}
	
	public T get(int index)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		return GetNode_i(index).Item;
	}
	
	public T set(int index, T t)
	{
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		Node n = GetNode_i(index);
		T ret = n.Item;
		n.Item = t;
		
		return ret;
	}
	
	public int indexOf(Object o)
	{
		Node n = Head.Next;
		
		for(int i = 0;i < size();i++)
			if(n.Item == null ? o == null : n.Item.equals(o))
				return i;
			else
				n = n.Next;
		
		return -1;
	}
	
	public int lastIndexOf(Object o)
	{
		Node n = Tail.Previous;
		
		for(int i = size() - 1;i > -1;i--)
			if(n.Item == null ? o == null : n.Item.equals(o))
				return i;
			else
				n = n.Previous;
		
		return -1;
	}
	
	public boolean contains(Object o)
	{
		for(T t : this)
			if(t == o || t != null && t.equals(o))
				return true;
		
		return false;
	}
	
	public boolean containsAll(Collection<?> c)
	{
		for(Object t : c)
			if(!contains(t))
				return false;
		
		return true;
	}

	public void clear()
	{
		Head.MakeSequential(Tail);
		Count = 0;
		
		return;
	}
	
	public int size()
	{return Count;}
	
	public boolean isEmpty()
	{return size() == 0;}	
	
	public Iterator<T> iterator()
	{return new NodeIterator();}
	
	public ListIterator<T> listIterator()
	{return new NodeListIterator();}
	
	public ListIterator<T> listIterator(int index)
	{
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		ListIterator<T> ret = new NodeListIterator();
		
		// We have to iterate over the list anyway, so just do it here
		for(int i = 0;i < index;i++)
			ret.next();
		
		return ret;
	}
	
	public List<T> subList(int from, int to)
	{
		if(from < 0 || to > size() ||from > to)
			throw new IndexOutOfBoundsException();
		
		return new UniversalSublist<T>(this,from,to);
	}
	
	public Object[] toArray()
	{
		Object[] ret = new Object[size()];
		int i = 0;
		
		for(T t : this)
			ret[i++] = t;
		
		return ret;
	}

	public <E> E[] toArray(E[] a)
	{
		E[] ret = a.length >= size() ? a : (E[])Array.newInstance(a.getClass().getComponentType(),size());
		int i = 0;
		
		for(T t : this)
			ret[i++] = (E)t;
		
		if(ret.length > size())
			ret[size()] = null;
		
		return ret;
	}
	
	@Override public String toString()
	{
		if(isEmpty())
			return "{}";
		
		String ret = "{";
		
		Node n = Head;
		
		while((n = n.Next) != Tail)
			ret += n.Item + (n.Next == Tail ? "" : ", ");
		
		return ret + "}";
	}
	
	@Override public int hashCode()
	{
		int hash = 1;
		
		for(T t : this)
			hash = 31 * hash + (t == null ? 0 : t.hashCode());
		
		return hash;
	}
	
	/**
	 * Gets the front element of this list.
	 * @return Returns the front element of this list.
	 * @throws NoSuchElementException Thrown if this list is empty.
	 */
	public T Front()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		
		return Head.Next.Item;
	}
	
	/**
	 * Gets the last element of this list.
	 * @return Returns the last element of this list.
	 * @throws NoSuchElementException Thrown if this list is empty.
	 */
	public T Last()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		
		return Tail.Previous.Item;
	}
	
	/**
	 * The dummy head of this double linked list.
	 */
	protected Node Head;
	
	/**
	 * The dummy tail of this doubly linked list.
	 */
	protected Node Tail;
	
	/**
	 * The number of elements in this collection.
	 */
	protected int Count;
	
	/**
	 * Contains a single element of the linked list.
	 * @author Dawn Nye
	 */
	protected class Node
	{
		/**
		 * Constructs and empty node.
		 */
		public Node()
		{
			Next = Previous = null;
			return;
		}
		
		/**
		 * Constructs a node containing data but no neighbors.
		 * @param t The data to place in this node.
		 */
		public Node(T t)
		{
			Next = Previous = null;
			Item = t;
			
			return;
		}
		
		/**
		 * Constructs a node with neighbors but no data.
		 * @param n The next node.
		 * @param p The previous node.
		 */
		public Node(Node n, Node p)
		{
			Insert(n,p);
			Item = null;
			
			return;
		}
		
		/**
		 * Constructs a node with neighbors and the provided data.
		 * @param n The next node.
		 * @param p The previous node.
		 * @param t The data to place in this node.
		 */
		public Node(Node n, Node p, T t)
		{
			Insert(n,p);
			Item = t;
			
			return;
		}
		
		/**
		 * Makes {@code n} the next node of this node.
		 * If it is null, then this will become the end of the list.
		 * @param n The node to make sequential after this one.
		 */
		public void MakeSequential(Node n)
		{
			Next = n;
			
			if(n != null)
				n.Previous = this;
			
			return;
		}
		
		/**
		 * Inserts this node between {@code p} and {@code n}.
		 * This will not throw an exception if either is null.
		 * @param p The previous node to insert after.
		 * @param n The next node to insert before.
		 */
		public void Insert(Node p, Node n)
		{
			Next = n;
			
			if(n != null)
				n.Previous = this;
			
			Previous = p;
			
			if(p != null)
				p.Next = this;
			
			return;
		}
		
		/**
		 * Removes this node from the list.
		 */
		public void Extirpate()
		{
			if(Next != null)
				Next.Previous = Previous;
			
			if(Previous != null)
				Previous.Next = Next;
			
			return;
		}
		
		/**
		 * The node following this one.
		 * A value of null indicates no such node exists.
		 */
		public Node Next;
		
		/**
		 * The node prior to this one.
		 * A value of null indicates no such node exists.
		 */
		public Node Previous;
		
		/**
		 * The data contained in this node.
		 */
		public T Item;
	}
	
	/**
	 * Iterates over node items like a boss.
	 * @author Dawn Nye
	 */
	protected class NodeIterator implements Iterator<T>
	{
		/**
		 * Creates a new node iterator.
		 */
		public NodeIterator()
		{
			n = Head;
			NotReadyToRemove = true;
			
			return;
		}
		
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			n = n.Next;
			NotReadyToRemove = false;
			
			return n.Item;
		}

		public boolean hasNext()
		{return n.Next != Tail;}
		
		public void remove()
		{
			if(NotReadyToRemove)
				throw new IllegalStateException();
			
			n.Extirpate();
			Count--;
			
			NotReadyToRemove = true;
			return;
		}
		
		/**
		 * The current node.
		 */
		protected Node n;
		
		/**
		 * If true, we've called remove since the last call to next.
		 */
		protected boolean NotReadyToRemove;
	}
	
	/**
	 * Iterates over node items like a double boss.
	 * @author Dawn Nye
	 */
	protected class NodeListIterator implements ListIterator<T>
	{
		/**
		 * Creates a new node iterator.
		 */
		public NodeListIterator()
		{
			n = Head;
			Index = -1;
			
			CanRemove = false;
			
			return;
		}
		
		public T next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			n = n.Next;
			Index++;
			CanRemove = true;
			
			return n.Item;
		}
		
		public T previous()
		{
			if(!hasPrevious())
				throw new NoSuchElementException();
			
			n = n.Previous;
			Index++;
			CanRemove = true;
			
			return n.Next.Item;
		}
		
		public int nextIndex()
		{return Index + 1;}
		
		public int previousIndex()
		{return Index;}
		
		public boolean hasNext()
		{return n != Tail.Previous;}
		
		public boolean hasPrevious()
		{return n != Head;}
		
		public void add(T t)
		{
			Node temp = new Node(t);
			temp.Insert(n,n.Next);
			n = temp;
			
			Index++;
			Count++;
			
			CanRemove = false;
			return;
		}
		
		public void set(T t)
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			(WasNext ? n : n.Next).Item = t;
			return;
		}
		
		public void remove()
		{
			if(!CanRemove)
				throw new IllegalStateException();
			
			if(WasNext)
			{
				n.Extirpate();
				n = n.Previous;
			}
			else
				n.Next.Extirpate();
			
			Count--;
			CanRemove = false;
			
			return;
		}
		
		/**
		 * The current node.
		 */
		protected Node n;
		
		/**
		 * The next index.
		 */
		protected int Index;
		
		/**
		 * If true, then we can remove.
		 */
		private boolean CanRemove;
		
		/**
		 * If true, then the last call to prev/next was a next.
		 * If false, then the last call to prev/next was a prev.
		 */
		private boolean WasNext;
	}
}
