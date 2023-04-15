package gamecore.datastructures.graphs.exceptions;


/**
 * An exception thrown when an edge doesn't exist.
 * @author Dawn Nye
 */
public class NoSuchEdgeException extends RuntimeException
{
	/**
	 * Creates a new NoSuchEdgeException.
	 * @param src The source vertex ID of the edge.
	 * @param dst The destination vertex ID of the edge.
	 */
	public NoSuchEdgeException(int src, int dst)
	{
		super();
		
		Source = src;
		Destination = dst;
		
		return;
	}
	
	/**
	 * Creates a new NoSuchEdgeException.
	 * @param src The source vertex ID of the edge.
	 * @param dst The destination vertex ID of the edge.
	 * @param msg The message in the exception.
	 */
	public NoSuchEdgeException(int src, int dst, String msg)
	{
		super(msg);
		
		Source = src;
		Destination = dst;
		
		return;
	}
	
	/**
	 * The ID of the source vertex.
	 */
	public final int Source;
	
	/**
	 * The ID of the destination vertex.
	 */
	public final int Destination;
}