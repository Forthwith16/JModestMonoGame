package gamecore.datastructures.graphs.exceptions;

/**
 * An exception thrown when a graph is unconnected when it should be.
 * @author Dawn Nye
 */
public class UnconnectedGraphException extends RuntimeException
{
	/**
	 * Creates a new UnconnectedGraphException.
	 */
	public UnconnectedGraphException()
	{
		super();
		return;
	}
	
	/**
	 * Creates a new UnconnectedGraphException.
	 * @param msg The message in the exception.
	 */
	public UnconnectedGraphException(String msg)
	{
		super(msg);
		return;
	}
}