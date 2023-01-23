package gamecore.datastructures.heaps;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Implements a max heap.
 * @author Dawn Nye
 * @param <T> The type of data in the heap.
 */
public class MaxHeap<T> implements IHeap<T>
{
	/**
	 * Creates a new max heap with the given ordering.
	 * @param cmp The means by which items are sorted.
	 */
	public MaxHeap(Comparator<T> cmp)
	{
		Heap = new MinHeap<T>((a,b) -> -cmp.compare(a,b));
		return;
	}
	
	/**
	 * Creates a new max heap with the given ordering.
	 * @param seed The initial set of objects to fill this heap with.
	 * @param cmp The means by which items are sorted.
	 */
	public MaxHeap(Iterable<? extends T> seed, Comparator<T> cmp)
	{
		Heap = new MinHeap<T>(seed,(a,b) -> -cmp.compare(a,b));
		return;
	}
	
	public boolean Add(T t)
	{return Heap.Add(t);}
	
	public boolean AddAll(Iterable<? extends T> c)
	{return Heap.AddAll(c);}
	
	public T RemoveTop()
	{return Heap.RemoveTop();}
	
	public T RemoveMax()
	{return Heap.RemoveMin();}
	
	public T Top()
	{return Heap.Top();}
	
	public T Max()
	{return Heap.Min();}
	
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
	
	@Override public String toString()
	{return Heap.toString();}
	
	/**
	 * The backing heap data structure.
	 */
	protected MinHeap<T> Heap;
}
