package gamecore.datastructures.queues;

import java.util.Iterator;

import gamecore.datastructures.LinkedList;

/**
 * A double-ended queue datastructure which permits constant time addition and removal to both ends of the queue.
 * @author Dawn Nye
 * @param <T> The type to store in the deque.
 */
public class Deque<T> implements IDeque<T>
{
	/**
	 * Creates an empty deque.
	 */
	public Deque()
	{
		D = new LinkedList<T>();
		return;
	}
	
	/**
	 * Creates a new deque with {@code seed} as the initial elements added so that the deque contains the elements in order.
	 * The front of the queue contains the first element of {@code seed} and the top of the stack contains the last element of {@code seed}.
	 * @param seed The initial items to add.
	 * @throws NullPointerException Thrown if {@code seed} is null.
	 */
	public Deque(Iterable<? extends T> seed)
	{
		D = new LinkedList<T>(seed);
		return;
	}
	
	public boolean AddFront(T t)
	{return D.AddFront(t);}
	
	public boolean AddAllFront(Iterable<? extends T> c)
	{return D.AddAllFront(c);}
	
	public T Front()
	{return D.Front();}
	
	public T PollFront()
	{return D.RemoveFront();}
	
	public boolean AddBack(T t)
	{return D.AddLast(t);}
	
	public boolean AddAllBack(Iterable<? extends T> c)
	{return D.AddAllLast(c);}
	
	public T Back()
	{return D.Last();}
	
	public T PollBack()
	{return D.RemoveLast();}
	
	public boolean Push(T t)
	{return AddBack(t);}
	
	public boolean PushAll(Iterable<? extends T> c)
	{return AddAllBack(c);}
	
	public T Pop()
	{return PollBack();}
	
	public T Peek()
	{return Back();}
	
	public boolean Enqueue(T t)
	{return AddBack(t);}
	
	public boolean EnqueueAll(Iterable<? extends T> c)
	{return AddAllBack(c);}
	
	public T Dequeue()
	{return PollFront();}
	
	public void Clear()
	{
		D.clear();
		return;
	}
	
	public int Count()
	{return D.size();}
	
	public boolean IsEmpty()
	{return D.isEmpty();}
	
	public Iterator<T> iterator()
	{return D.iterator();}
	
	/**
	 * The backing datastructure for this deque.
	 */
	protected LinkedList<T> D;
}
