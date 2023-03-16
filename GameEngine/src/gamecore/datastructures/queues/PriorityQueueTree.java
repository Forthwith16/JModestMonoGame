package gamecore.datastructures.queues;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.trees.HeapTree;


/**
 * A priority queue such that the elements with the least priority come out first.
 * The opposite can be achieved, that is elements with the highest priority come out first, by provided a comparator that reverses the ordering.
 * Iterating over this priority queue will be done in the order elements would be removed from it.
 * @author Dawn Nye
 * @param <T> The type to put into the queue.
 */
public class PriorityQueueTree<T> implements IQueue<T>
{
	/**
	 * Creates a new priority queue.
	 * @param cmp The means by which elements of the queue are compared.
	 */
	public PriorityQueueTree(Comparator<T> cmp)
	{
		Heap = new HeapTree<T>(cmp);
		this.cmp = cmp;
		
		return;
	}
	
	/**
	 * Creates a new priority queue in linear time.
	 * @param seed The initial elements of the queue.
	 * @param cmp The means by which elements of the queue are compared.
	 */
	public PriorityQueueTree(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		Heap = new HeapTree<T>(seed,cmp);
		this.cmp = cmp;
		
		return;
	}
	
	public boolean Enqueue(T t)
	{return Heap.Add(t);}
	
	public boolean Add(T t)
	{return Enqueue(t);}
	
	public boolean EnqueueAll(Iterable<? extends T> c)
	{
		boolean ret = false;
		
		for(T t : c)
			ret |= Heap.Add(t);
		
		return ret;
	}
	
	public T Dequeue()
	{
		T ret = Front();
		Heap.Remove(ret);
		
		return ret;
	}
	
	public boolean Remove(T t)
	{return Heap.Remove(t);}
	
	public T Front()
	{return Heap.Root();}
	
	public boolean Contains(T t)
	{return Heap.Contains(t);}
	
	public void Clear()
	{
		Heap.Clear();
		return;
	}
	
	public int Count()
	{return Heap.Count();}
	
	public boolean IsEmpty()
	{return Heap.IsEmpty();}
	
	public Iterator<T> iterator()
	{
		// There's no good way to iterate over a heap in the order we would for the priority queue, so we'll take linear time to construct this iterator
		// To iterate over all of it takes n log n time anyway, so this is fine
		return new Iterator<T>()
		{
			public boolean hasNext()
			{return H.IsEmpty();}

			public T next()
			{
				if(!hasNext())
					throw new NoSuchElementException();
				
				T ret = H.Root();
				H.Remove(ret);
				
				return ret;
			}
			
			protected HeapTree<T> H = new HeapTree<T>(Heap,cmp);
		};
	}
	
	/**
	 * The heap to back this priority queue.
	 */
	protected HeapTree<T> Heap;
	
	/**
	 * The comparator, which we keep around for the iterator.
	 */
	protected Comparator<T> cmp;
}
