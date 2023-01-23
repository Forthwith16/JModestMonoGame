package gamecore.LINQ;

import java.util.Iterator;
import java.util.NoSuchElementException;

import gamecore.datastructures.ArrayList;
import gamecore.datastructures.tuples.Pair;

/**
 * Provides extensions to iterable objects.
 * @author Dawn Nye
 */
public final class LINQ
{
	/**
	 * Determines if {@code source} contains an instance of {@code obj}.
	 * @param <T> The iterable type.
	 * @param source The source iterable object.
	 * @param obj The object to look for in {@code source}.
	 * @return Returns true if {@code source} contains {@code obj} and false otherwise.
	 */
	public static <T> boolean Contains(Iterable<? extends T> source, T obj)
	{
		Iterator Iter = source.iterator();
		
		while(Iter.hasNext())
			if(obj == null ? Iter.next() == null : obj.equals(Iter.next()))
				return true;
		
		return false;
	}
	
	/**
	 * Counts the number of elements of an iterable object.
	 * @param <T> The type of object to iterate.
	 * @param source The iterable object to count.
	 * @return Returns the number of elements in {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> int Count(Iterable<? extends T> source)
	{
		Iterator Iter = source.iterator();
		int ret = 0;
		
		while(Iter.hasNext())
		{
			Iter.next();
			ret++;
		}
		
		return ret;
	}
	
	/**
	 * Filters an iterable object so that each element iterated appears exactly once.
	 * @param <T> The type of object to iterate.
	 * @param source The iterable object to filter.
	 * @return Returns a new iterable object which iterates each value exactly once without duplicates.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> Iterable<T> Distinct(Iterable<? extends T> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{
						if(Next != null)
							return true;
						
						while(Iter.hasNext())
						{
							T temp = Iter.next();
							
							if(!Items.contains(temp))
							{
								Next = temp;
								Items.add(temp);
								
								return true;
							}
						}
						
						return false;
					}
					
					public T next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						T ret = Next;
						Next = null;
						return ret;
					}
					
					protected ArrayList<T> Items = new ArrayList<T>();
					protected T Next = null;
					protected Iterator<? extends T> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Creates an empty iterable object with type {@code T}.
	 * @param <T> The type to iterate.
	 * @return Returns an empty iterable object with type {@code T}.
	 */
	public static <T> Iterable<T> Empty()
	{return new ArrayList<T>();}
	
	/**
	 * Iterates over the first items of a list of pairs.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param source The iterable object to provide items of type {@code A}.
	 * @return Returns a new iterable object which iterates the first elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <A,B> Iterable<A> First(Iterable<Pair<A,B>> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<A>()
		{
			public Iterator<A> iterator()
			{
				return new Iterator<A>()
				{
					public boolean hasNext()
					{return Iter.hasNext();}
					
					public A next()
					{return Iter.next().Item1;}
					
					protected Iterator<Pair<A,B>> Iter = source.iterator();
				};
			}
		};
	}

	/**
	 * Iterates over an array.
	 * @param <T> The type of the array.
	 * @param src The source array to iterate over.
	 * @return Returns an iterable object that iterates over the provided array.
	 * @throws NullPointerException Thrown if {@code src} is null.
	 */
	public static <T> Iterable<T> MakeIterable(T[] src)
	{
		if(src == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{return index < src.length;}
					
					public T next()
					{return src[index++];}
					
					protected int index = 0;
				};
			}
		};
	}
	
	/**
	 * Pairs two iterable objects together.
	 * The new iterator will proceed until one source runs out of items.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param a_source The iterable object to provide items of type {@code A}.
	 * @param b_source The iterable object to provide items of type {@code B}.
	 * @return Returns a new iterable object which pairs the elements of {@code a_source} and {@code b_source} together.
	 * @throws NullPointerException Thrown if {@code a_source} or {@code b_source} is null.
	 */
	public static <A,B> Iterable<Pair<A,B>> Pair(Iterable<? extends A> a_source, Iterable<? extends B> b_source)
	{
		if(a_source == null || b_source == null)
			throw new NullPointerException();
		
		return new Iterable<Pair<A,B>>()
		{
			public Iterator<Pair<A,B>> iterator()
			{
				return new Iterator<Pair<A,B>>()
				{
					public boolean hasNext()
					{return IterA.hasNext() && IterB.hasNext();}
					
					public Pair<A,B> next()
					{return new Pair<A,B>(IterA.next(),IterB.next());}
					
					protected Iterator<? extends A> IterA = a_source.iterator();
					protected Iterator<? extends B> IterB = b_source.iterator();
				};
			}
		};
	}
	
	/**
	 * Makes an iterable object read only. 
	 * @param <T> The type of values to iterate.
	 * @param source The iterable object to make read only.
	 * @return Returns a new iterable object which is read only.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <T> Iterable<T> ReadOnly(Iterable<? extends T> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{return Iter.hasNext();}
					
					public T next()
					{return Iter.next();}
					
					public void remove()
					{throw new UnsupportedOperationException();}
					
					protected Iterator<? extends T> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Iterates over the second items of a list of pairs.
	 * @param <A> The first type of values to iterate.
	 * @param <B> The second type of values to iterate.
	 * @param source The iterable object to provide items of type {@code B}.
	 * @return Returns a new iterable object which iterates the second elements of {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static <A,B> Iterable<B> Second(Iterable<Pair<A,B>> source)
	{
		if(source == null)
			throw new NullPointerException();
		
		return new Iterable<B>()
		{
			public Iterator<B> iterator()
			{
				return new Iterator<B>()
				{
					public boolean hasNext()
					{return Iter.hasNext();}
					
					public B next()
					{return Iter.next().Item2;}
					
					protected Iterator<Pair<A,B>> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Projects each element of a sequence into a new form.
	 * @param <I> The input type.
	 * @param <O> The output type.
	 * @param source The iterable object to project into new forms.
	 * @param transformation The function transforming input objects into a new form.
	 * @return Returns a new iterable object which transforms the original values into a new form according to {@code transformation}.
	 * @throws NullPointerException Thrown if {@code source} or {@code transformation} is null.
	 */
	public static <I,O> Iterable<O> Select(Iterable<? extends I> source, SingleInputTransformation<I,O> transformation)
	{
		if(source == null || transformation == null)
			throw new NullPointerException();
		
		return new Iterable<O>()
		{
			public Iterator<O> iterator()
			{
				return new Iterator<O>()
				{
					public boolean hasNext()
					{return Iter.hasNext();}
					
					public O next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						return transformation.Evaluate(Iter.next());
					}
					
					protected Iterator<? extends I> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Projects each element of a sequence into a new form.
	 * @param <I> The input type.
	 * @param <O> The output type.
	 * @param source The iterable object to project into new forms.
	 * @param transformation The function transforming input objects into a new form.
	 * @return Returns a new iterable object which transforms the original values into a new form according to {@code transformation}.
	 * @throws NullPointerException Thrown if {@code source} or {@code transformation} is null.
	 */
	public static <I,O> Iterable<O> Select(Iterable<? extends I> source, DoubleInputTransformation<I,O> transformation)
	{
		if(source == null || transformation == null)
			throw new NullPointerException();
		
		return new Iterable<O>()
		{
			public Iterator<O> iterator()
			{
				return new Iterator<O>()
				{
					public boolean hasNext()
					{return Iter.hasNext();}
					
					public O next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						return Previous = transformation.Evaluate(Iter.next(),Previous);
					}
					
					protected O Previous = null;
					protected Iterator<? extends I> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Checks if two sequences are equal.
	 * @param <T> The sequence type.
	 * @param s1 The first sequence.
	 * @param s2 The second sequence.
	 * @return Returns true if {@code s1} and {@code s2} are the same sequence and false otherwise.
	 */
	public static <T> boolean SequenceEqual(Iterable<? extends T> s1, Iterable<? extends T> s2)
	{
		Iterator<? extends T> Iter1 = s1.iterator();
		Iterator<? extends T> Iter2 = s2.iterator();
		
		while(Iter1.hasNext() && Iter2.hasNext())
		{
			T temp = Iter1.next();
			
			if(temp == null ? Iter2.next() != null : !temp.equals(Iter2.next()))
				return false;
		}
		
		if(Iter1.hasNext() || Iter2.hasNext())
			return false;
		
		return true;
	}
	
	/**
	 * Filters an iterable object by iterating only values which satisfy {@code predicate}. 
	 * @param <T> The type of values to iterate.
	 * @param source The iterable object to filter.
	 * @param predicate The predicate which decides whether to include or exclude iterated values.
	 * @return Returns a new iterable object which iterates only values which satisfy {@code predicate}.
	 * @throws NullPointerException Thrown if {@code source} or {@code predicate} is null.
	 */
	public static <T> Iterable<T> Where(Iterable<? extends T> source, SingleInputPredicate<T> predicate)
	{
		if(source == null || predicate == null)
			throw new NullPointerException();
		
		return new Iterable<T>()
		{
			public Iterator<T> iterator()
			{
				return new Iterator<T>()
				{
					public boolean hasNext()
					{
						if(Next != null)
							return true;
						
						while(Iter.hasNext())
						{
							T temp = Iter.next();
							
							if(predicate.Evaluate(temp))
							{
								Next = temp;
								return true;
							}
						}
						
						return false;
					}
					
					public T next()
					{
						if(!hasNext())
							throw new NoSuchElementException();
						
						T ret = Next;
						Next = null;
						return ret;
					}
					
					protected T Next = null;
					protected Iterator<? extends T> Iter = source.iterator();
				};
			}
		};
	}
	
	/**
	 * Determines the value of a single input predicate.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface SingleInputPredicate<T>
	{
		/**
		 * Determines the value of a single input predicate.
		 */
		public abstract boolean Evaluate(T input);
	}
	
	/**
	 * Transforms a single input into a new form.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface SingleInputTransformation<I,O>
	{
		/**
		 * Transforms {@code input} into a new form.
		 */
		public abstract O Evaluate(I input);
	}
	
	/**
	 * Transforms a single input into a new form.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface DoubleInputTransformation<I,O>
	{
		/**
		 * Transforms {@code input} into a new form.
		 * {@code previous} is provided to give context.
		 * The value provided is null when there is no previous output value.
		 */
		public abstract O Evaluate(I input, O previous);
	}
}
