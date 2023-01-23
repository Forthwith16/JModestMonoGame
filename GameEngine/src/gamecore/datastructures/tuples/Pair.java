package gamecore.datastructures.tuples;

/**
 * An immutable pair class.
 * @author Dawn Nye
 * @param <S> The first type of item to store.
 * @param <T> The second type of item to store.
 */
public class Pair<S,T>
{
	/**
	 * Pairs two elements together.
	 * @param s The first item.
	 * @param t The second item.
	 */
	public Pair(S s, T t)
	{
		Item1 = s;
		Item2 = t;
		
		return;
	}
	
	/**
	 * Creates a shallow copy of {@code p}.
	 * @param p The pair to duplicate.
	 */
	public Pair(Pair<? extends S,? extends T> p)
	{
		Item1 = p.Item1;
		Item2 = p.Item2;
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof Pair)
		{
			Pair p = (Pair)obj;
			return (Item1 == p.Item1 || Item1 != null && Item1.equals(p.Item1)) && (Item2 == p.Item2 || Item2 != null && Item2.equals(p.Item2));
		}
		
		return false;
	}
	
	@Override public String toString()
	{return "(" + Item1 + ", " + Item2 + ")";}
	
	@Override public int hashCode()
	{return Item1 == null ? (Item2 == null ? 0 : Item2.hashCode()) : (Item1.hashCode() + (Item2 == null ? 0 : (Item2.hashCode() << 5) - Item2.hashCode()));}
	
	/**
	 * The first item of this pair.
	 */
	public final S Item1;
	
	/**
	 * The second item of this pair.
	 */
	public final T Item2;
}
