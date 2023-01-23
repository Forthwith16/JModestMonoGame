package gamecore.datastructures.tuples;

/**
 * A triple class.
 * @author Dawn Nye
 * @param <R> The first type of item to store.
 * @param <S> The second type of item to store.
 * @param <T> The third type of item to store.
 */
public class Triple<R,S,T>
{
	/**
	 * Collects three elements together.
	 * @param r The first item.
	 * @param s The second item.
	 * @param t The third item.
	 */
	public Triple(R r, S s, T t)
	{
		Item1 = r;
		Item2 = s;
		Item3 = t;
		
		return;
	}
	
	/**
	 * Creates a shallow copy of {@code t}.
	 * @param t The triple to duplicate.
	 */
	public Triple(Triple<? extends R,? extends S,? extends T> t)
	{
		Item1 = t.Item1;
		Item2 = t.Item2;
		Item3 = t.Item3;
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof Triple)
		{
			Triple p = (Triple)obj;
			return (Item1 == p.Item1 || Item1 != null && Item1.equals(p.Item1)) && (Item2 == p.Item2 || Item2 != null && Item2.equals(p.Item2)) && (Item3 == p.Item3 || Item3 != null && Item3.equals(p.Item3));
		}
		
		return false;
	}
	
	@Override public String toString()
	{return "(" + Item1 + ", " + Item2 + ", " + Item3 + ")";}
	
	@Override public int hashCode()
	{
		if(Item1 == null)
			if(Item2 == null)
				if(Item3 == null)
					return 0; // Got nothing better
				else
					return Item3.hashCode();
			else  // Item2 != null
				if(Item3 == null)
					return Item2.hashCode();
				else
					return Item2.hashCode() + 31 * Item3.hashCode();
		else // Item1 != null
			if(Item2 == null)
				if(Item3 == null)
					return Item1.hashCode();
				else
					return Item1.hashCode() + 31 * Item2.hashCode();
			else // Item1 != null && Item2 != null
				if(Item3 == null)
					return Item1.hashCode() + 31 * Item2.hashCode();
				else
					return Item1.hashCode() + 31 * Item2.hashCode() + 4093 * Item3.hashCode();
	}
	
	/**
	 * The first item of this triple.
	 */
	public final R Item1;
	
	/**
	 * The second item of this triple.
	 */
	public final S Item2;
	
	/**
	 * The third item of this triple.
	 */
	public final T Item3;
}