package gamecore.datastructures.graphs.exceptions;

/**
 * An exception thrown when a graph is cyclic when it should be acyclic.
 * @author Dawn Nye
 */
public class CyclicGraphException extends RuntimeException
{
	/**
	 * Creates a new CyclicGraphException.
	 */
	public CyclicGraphException()
	{
		super();
		return;
	}
	
	/**
	 * Creates a new CyclicGraphException.
	 * @param msg The message in the exception.
	 */
	public CyclicGraphException(String msg)
	{
		super(msg);
		return;
	}
}