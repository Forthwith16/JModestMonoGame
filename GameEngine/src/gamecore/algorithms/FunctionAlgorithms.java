package gamecore.algorithms;

import gamecore.datastructures.HashTable;


/**
 * A collection of function-based algorithms.
 * @author Dawn Nye
 */
public final class FunctionAlgorithms
{
	/**
	 * No one will make one of these.
	 */
	private FunctionAlgorithms()
	{return;}
	
	/**
	 * Determines if there is a cycle in a function {@code func} starting from {@code initial_input}.
	 * For instance, if {@code func} is {@code π sin(x)} starting from x = π/2, then we obtain
	 * <ul>
	 * 	<li>π sin(π/2) = π,</li>
	 * 	<li>π sin(π) = 0,</li>
	 * 	<li>π sin(0) = 0.</li>
	 * </ul>
	 * In this example, the cycle length is 1 because we end at a fixed point 0.
	 * <br><br>
	 * Another example would be the inverse sign function (returns 1 if x is positive, 0 if x is 0, and -1 if x is negative), {@code -sgn(x)}.
	 * Starting from x = 1, we obtain
	 * <ul>
	 * 	<li>-sgn(1) = -1,</li>
	 * 	<li>-sgn(-1) = 1.</li>
	 * </ul>
	 * Here we have a cycle length of 2.
	 * If we had started from x = 2, we would still have a cycle length of 2, but it would take one attempt longer to discover.
	 * If we had started from x = 0 instead, we would have a cycle length of 1 since 0 is a fixed point of {@code -sgn(x)}.
	 * <br><br>
	 * In contrast, the function {@code f(x) = x + 1} has no cycle since no sequence of inputs will take us to an input that we've already visited.
	 * <br><br>
	 * Note that user is responsible for ensuring that equality checks on types {@code T} succeed.
	 * If {@code T} is {@code Double}, for instance, the user must be careful of floating point errors in function calculation.
	 * @param <T> The input/output type of the function.
	 * @param func The function to search for a cycle within.
	 * @param initial_input The initial input to the function.
	 * @param max_depth The maximum number of input-output combinations to search. The initial input and its output counts as the first.
	 * @return Returns true if a cycle is found or false if there is no (discovered) cycle.
	 * @throws NullPointerException Thrown if {@code func} is null or if {@code initial_input} is null.
	 * @throws IllegalArgumentException Thrown if {@code max_depth} is nonpositive.
	 * @implSpec
	 * Suppose the cycle length is n (if no cycle will be discovered, then n = 0), the number of evaluations prior to the cycle is m, and {@code func} can be evaluated in averge case O(f) time.
	 * Then the runtime of this algorithm is average case O((n + m)f).
	 * In the first example above, m = 2 and n = 1.
	 * In the second example, m = 0 and n = 2.
	 */
	public static <T> boolean HasCycle(SingleInputFunction<T,T> func, T initial_input, int max_depth)
	{
		if(func == null)
			throw new NullPointerException();
		
		if(max_depth < 1)
			throw new IllegalArgumentException();
		
		HashTable<T> table = new HashTable<T>();
		int count = 0;
		
		do
		{
			if(table.contains(initial_input))
				return true;
			
			table.add(initial_input);
			initial_input = func.eval(initial_input);
		}
		while(count++ < max_depth);
		
		return false;
	}
	
	/**
	 * Describes a single-input single-output function.
	 * @author Dawn Nye
	 * @param <I> The input type.
	 * @param <O> The output type.
	 */
	@FunctionalInterface public interface SingleInputFunction<I,O>
	{
		/**
		 * Evaluates this function with the given input.
		 * @param input The input to this function.
		 * @return Returns the calculated output of this function input {@code input}.
		 */
		public O eval(I input);
	}
}