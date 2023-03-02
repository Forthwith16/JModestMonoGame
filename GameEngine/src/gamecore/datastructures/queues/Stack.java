package gamecore.datastructures.queues;

import java.util.Iterator;

import gamecore.datastructures.LinkedList;

/**
 * A stack backed by a LinkedList.
 * @author Dawn Nye
 * @param <T> The generic type to put into the stack.
 */
public class Stack<T> implements IStack<T>
{
	/**
	 * Creates an empty stack.
	 */
	public Stack()
	{
		S = new LinkedList<T>();
		return;
	}
	
	/**
	 * Creates a new stack with {@code c} stored in the stack.
	 * The elemnts are added so that the first element of {@code c} is the last element to pop from the stack, while the last element of {@code c} is the first element to pop from the stack.
	 * @param c The elements to add to the stack initially.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public Stack(Iterable<? extends T> c)
	{
		S = new LinkedList<T>(c);
		return;
	}
	
	public boolean Push(T t)
	{return S.AddLast(t);}
	
	public boolean PushAll(Iterable<? extends T> c)
	{return S.AddAllLast(c);}
	
	public T Pop()
	{return S.RemoveLast();}
	
	public T Peek()
	{return S.Last();}
	
	public boolean Add(T t)
	{return Push(t);}

	public boolean Remove(T t)
	{return S.Remove(t);}

	public boolean Contains(T t)
	{return S.Contains(t);}
	
	public void Clear()
	{
		S.clear();
		return;
	}
	
	public int Count()
	{return S.size();}
	
	public boolean IsEmpty()
	{return S.isEmpty();}
	
	public Iterator<T> iterator()
	{return S.iterator();}
	
	@Override public String toString()
	{
		if(IsEmpty())
			return "{}";
		
		String ret = "{";
		
		for(T t : this)
			ret += t + ",";
		
		return ret.substring(0,ret.length() - 1) + "}";
	}
	
	/**
	 * The backing datastructure for the stack.
	 */
	protected LinkedList<T> S;
}
