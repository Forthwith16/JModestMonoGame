package gamecore.datastructures.graphs.exceptions;

/**
 * An exception thrown when a vertex doesn't exist.
 * @author Dawn Nye
 */
public class NoSuchVertexException extends RuntimeException
{
	/**
	 * Creates a new NoSuchVertexException.
	 * @param id The ID of the missing vertex.
	 */
	public NoSuchVertexException(int id)
	{
		super();
		ID = id;
		
		return;
	}
	
	/**
	 * Creates a new NoSuchVertexException.
	 * @param id The ID of the missing vertex.
	 * @param msg The message in the exception.
	 */
	public NoSuchVertexException(int id, String msg)
	{
		super(msg);
		ID = id;
		
		return;
	}
	
	/**
	 * The ID of the missing vertex.
	 */
	public final int ID;
}