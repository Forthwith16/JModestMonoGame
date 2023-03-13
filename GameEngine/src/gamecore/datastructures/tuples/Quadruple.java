package gamecore.datastructures.tuples;

/**
 * A quadruple class.
 * @author Dawn Nye
 * @param <R> The first type of item to store.
 * @param <S> The second type of item to store.
 * @param <T> The third type of item to store.
 * @param <W> The fourth type of item to store.
 */
public class Quadruple<R,S,T,W>
{
	/**
	 * Collects four elements together.
	 * @param r The first item.
	 * @param s The second item.
	 * @param t The third item.
	 * @param w The fourth item
	 */
	public Quadruple(R r, S s, T t, W w)
	{
		Item1 = r;
		Item2 = s;
		Item3 = t;
		Item4 = w;
		
		return;
	}
	
	/**
	 * Creates a shallow copy of {@code t}.
	 * @param t The quadruple to duplicate.
	 */
	public Quadruple(Quadruple<? extends R,? extends S,? extends T,? extends W> t)
	{
		Item1 = t.Item1;
		Item2 = t.Item2;
		Item3 = t.Item3;
		Item4 = t.Item4;
		
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof Quadruple)
		{
			Quadruple p = (Quadruple)obj;
			return (Item1 == p.Item1 || Item1 != null && Item1.equals(p.Item1)) && (Item2 == p.Item2 || Item2 != null && Item2.equals(p.Item2)) && (Item3 == p.Item3 || Item3 != null && Item3.equals(p.Item3)) && (Item4 == p.Item4 || Item4 != null && Item4.equals(p.Item4));
		}
		
		return false;
	}
	
	@Override public String toString()
	{return "(" + Item1 + ", " + Item2 + ", " + Item3 + ", " + Item4 + ")";}
	
	@Override public int hashCode()
	{
		int ret = Item1 == null ? 1 : Item1.hashCode();
		ret = ret * 31 + (Item2 == null ? 1 : Item2.hashCode());
		ret = ret * 31 + (Item3 == null ? 1 : Item3.hashCode());
		ret = ret * 31 + (Item4 == null ? 1 : Item4.hashCode());
		
		return ret;
	}
	
	/**
	 * The first item of this quadruple.
	 */
	public final R Item1;
	
	/**
	 * The second item of this quadruple.
	 */
	public final S Item2;
	
	/**
	 * The third item of this quadruple.
	 */
	public final T Item3;
	
	/**
	 * The fourth item of this quadruple.
	 */
	public final W Item4;
}