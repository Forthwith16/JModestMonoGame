package gamecore.datastructures.queues;

import java.util.Comparator;
import java.util.Iterator;

import gamecore.datastructures.heaps.IHeap;
import gamecore.datastructures.heaps.MaxHeap;
import gamecore.datastructures.heaps.MinHeap;

/**
 * A priority queue backed with a heap.
 * @author Dawn Nye
 * @param <T> The type of data stored in the queue.
 */
public class PriorityQueue<T> implements IQueue<T>
{
	/**
	 * Creates an empty priority queue.
	 * Low priority values occur first.
	 * @param cmp The means by which items are compared.
	 */
	public PriorityQueue(Comparator<T> cmp)
	{
		this(cmp,true);
		return;
	}
	
	/**
	 * Creates an empty priority queue.
	 * @param cmp The means by which items are compared.
	 * @param min If true, then low prioritiey values occur before high prioritiey values. If false, then high prioritiey values will occur first instead.
	 */
	public PriorityQueue(Comparator<T> cmp, boolean min)
	{
		if(min)
			Heap = new MinHeap<T>(cmp);
		else
			Heap = new MaxHeap<T>(cmp);
		
		return;
	}
	
	/**
	 * Creates a priority queue.
	 * Low priority values occur first.
	 * @param cmp The means by which items are compared.
	 * @param seed The initial elements to add to the queue.
	 */
	public PriorityQueue(Comparator<T> cmp, Iterable<? extends T> seed)
	{
		this(cmp,seed,true);
		return;
	}
	
	/**
	 * Creates a priority queue.
	 * @param cmp The means by which items are compared.
	 * @param seed The initial elements to add to the queue.
	 * @param min If true, then low prioritiey values occur before high prioritiey values. If false, then high prioritiey values will occur first instead.
	 */
	public PriorityQueue(Comparator<T> cmp, Iterable<? extends T> seed, boolean min)
	{
		if(min)
			Heap = new MinHeap<T>(seed,cmp);
		else
			Heap = new MaxHeap<T>(seed,cmp);
		
		return;
	}
	
	public boolean Enqueue(T t)
	{return Heap.Add(t);}
	
	public boolean EnqueueAll(Iterable<? extends T> c)
	{return Heap.AddAll(c);}
	
	public T Dequeue()
	{return IsEmpty() ? null : Heap.RemoveTop();}
	
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
	{return Heap.iterator();}
	
	public T Front()
	{return IsEmpty() ? null : Heap.Top();}
	
	/**
	 * The backing data strcuture for the queue.
	 */
	public IHeap<T> Heap;
}
