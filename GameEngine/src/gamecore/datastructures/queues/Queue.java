package gamecore.datastructures.queues;

import java.util.Iterator;

import gamecore.datastructures.LinkedList;

/**
 * An ordinary queue with no fancy tricks.
 * @author Dawn Nye
 * @param <T> The type of data stored in the queue.
 */
public class Queue<T> implements IQueue<T>
{
	/**
	 * Creates an empty queue.
	 */
	public Queue()
	{
		Q = new LinkedList<T>();
		return;
	}
	
	/**
	 * Creates a queue.
	 * @param seed The initial elements to add to the queue.
	 */
	public Queue(Iterable<? extends T> seed)
	{
		Q = new LinkedList<T>(seed);
		return;
	}
	
	public boolean Enqueue(T t)
	{return Q.AddLast(t);}
	
	public boolean EnqueueAll(Iterable<? extends T> c)
	{return Q.AddAllLast(c);}
	
	public T Dequeue()
	{return IsEmpty() ? null : Q.RemoveFront();}
	
	public void Clear()
	{
		Q.clear();
		return;
	}
	
	public int Count()
	{return Q.size();}
	
	public boolean IsEmpty()
	{return Q.isEmpty();}
	
	public Iterator<T> iterator()
	{return Q.iterator();}
	
	public T Front()
	{return IsEmpty() ? null : Q.Front();}
	
	/**
	 * The backing data strcuture for the queue.
	 */
	public LinkedList<T> Q;
}
