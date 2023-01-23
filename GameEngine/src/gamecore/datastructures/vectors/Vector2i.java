package gamecore.datastructures.vectors;

/**
 * A basic two component vector with integer values.
 * @author Dawn Nye
 */
public class Vector2i
{
	/**
	 * Creates a zero vector.
	 */
	public Vector2i()
	{
		this(0,0);
		return;
	}
	
	/**
	 * Creates a vector. 
	 * @param x The x component of the vector.
	 * @param y The y component of the vector.
	 */
	public Vector2i(int x, int y)
	{
		X = x;
		Y = y;
		
		MagnitudeCalculated = false;
		return;
	}
	
	/**
	 * Creates a duplicate of {@code v}.
	 * @param v The vector to copy.
	 */
	public Vector2i(Vector2i v)
	{
		this(v.X,v.Y);
		return;
	}
	
	/**
	 * Creates a duplicate of {@code v}.
	 * @param v The vector to copy. It's components will be rounded to integers.
	 */
	public Vector2i(Vector2d v)
	{
		this((int)Math.round(v.X),(int)Math.round(v.Y));
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof Vector2i)
		{
			Vector2i v = (Vector2i)obj;
			return X == v.X && Y == v.Y;
		}
		
		if(obj instanceof Vector2d)
			return new Vector2d(this).equals(obj);
		
		return false;
	}
	
	/**
	 * Adds this vector with {@code v}.
	 * @param v The vector to add.
	 * @return Returns a new vector whose value is this + {@code v}.
	 */
	public Vector2i Add(Vector2i v)
	{return new Vector2i(X + v.X,Y + v.Y);}
	
	/**
	 * Adds this vector with {@code v}.
	 * @param v The vector to add.
	 * @return Returns a new vector whose value is this + {@code v}.
	 */
	public Vector2d Add(Vector2d v)
	{return new Vector2d(X + v.X,Y + v.Y);}
	
	/**
	 * Substracts {@code v} from this vector.
	 * @param v The vector to subtract.
	 * @return Returns a new vector whose value is this - {@code v}.
	 */
	public Vector2i Subtract(Vector2i v)
	{return new Vector2i(X - v.X,Y - v.Y);}
	
	/**
	 * Substracts {@code v} from this vector.
	 * @param v The vector to subtract.
	 * @return Returns a new vector whose value is this - {@code v}.
	 */
	public Vector2d Subtract(Vector2d v)
	{return new Vector2d(X - v.X,Y - v.Y);}
	
	/**
	 * Multiplies this vector with {@code c}.
	 * @param c The scalar to multiply by.
	 * @return Returns a new vector whose value is {@code c} * this.
	 */
	public Vector2i Multiply(int c)
	{return new Vector2i(c * X,c * Y);}
	
	/**
	 * Multiplies this vector with {@code c}.
	 * @param c The scalar to multiply by.
	 * @return Returns a new vector whose value is {@code c} * this.
	 */
	public Vector2d Multiply(double c)
	{return new Vector2d(c * X,c * Y);}
	
	/**
	 * Performs a component-wise multiplication of this vector with {@code v}.
	 * @param v The vector to multiply by.
	 * @return Returns a new vector whose value is the component-wise product of this and {@code v}.
	 */
	public Vector2i Multiply(Vector2i v)
	{return new Vector2i(X * v.X,Y * v.Y);}
	
	/**
	 * Performs a component-wise multiplication of this vector with {@code v}.
	 * @param v The vector to multiply by.
	 * @return Returns a new vector whose value is the component-wise product of this and {@code v}.
	 */
	public Vector2d Multiply(Vector2d v)
	{return new Vector2d(X * v.X,Y * v.Y);}
	
	/**
	 * Divides this vector by {@code c}.
	 * @param c The scalar to divide by.
	 * @return Returns a new vector whose value is this / {@code c}. Integer arithmetic will be used to calculate its values.
	 */
	public Vector2i Divide(int c)
	{return new Vector2i(X / c,Y / c);}
	
	/**
	 * Divides this vector by {@code c}.
	 * @param c The scalar to divide by.
	 * @return Returns a new vector whose value is this / {@code c}.
	 */
	public Vector2d Divide(double c)
	{return new Vector2d(X / c,Y / c);}
	
	/**
	 * Performs a component-wise division of this vector by {@code v}.
	 * @param v The vector to divide by.
	 * @return Returns a new vector whose value is the component-wise division of this by {@code v}. Integer arithmetic will be used to calculate its values.
	 */
	public Vector2i Divide(Vector2i v)
	{return new Vector2i(X / v.X,Y / v.Y);}
	
	/**
	 * Performs a component-wise division of this vector by {@code v}.
	 * @param v The vector to divide by.
	 * @return Returns a new vector whose value is the component-wise division of this by {@code v}.
	 */
	public Vector2d Divide(Vector2d v)
	{return new Vector2d(X / v.X,Y / v.Y);}
	
	/**
	 * Calculates the dot product of this vector with {@code v}.
	 * @param v The vector to calculate the dot product with.
	 * @return Returns the dot product of this and {@code v}.
	 */
	public int Dot(Vector2i v)
	{return X * v.X + Y * v.Y;}
	
	/**
	 * Calculates the dot product of this vector with {@code v}.
	 * @param v The vector to calculate the dot product with.
	 * @return Returns the dot product of this and {@code v}.
	 */
	public double Dot(Vector2d v)
	{return X * v.X + Y * v.Y;}
	
	/**
	 * Determines the angle this vector makes with {@code v}.
	 * @param v The vector to calculate the angle with.
	 * @return Returns the angle between this vector and {@code v}.
	 * The angle returned is always the smaller angle between 0 and π.
	 */
	public double Angle(Vector2i v)
	{return Math.acos(Dot(v) / Magnitude() / v.Magnitude());}
	
	/**
	 * Determines the angle this vector makes with {@code v}.
	 * @param v The vector to calculate the angle with.
	 * @return Returns the angle between this vector and {@code v}.
	 * The angle returned is always the smaller angle between 0 and π.
	 */
	public double Angle(Vector2d v)
	{return Math.acos(Dot(v) / Magnitude() / v.Magnitude());}
	
	/**
	 * Determines if this is the zero vector.
	 * @return Returns true iff this is the zero vector.
	 */
	public boolean IsZero()
	{return X == 0 && Y == 0;}
	
	/**
	 * Gets the squared magnitude of this vector.
	 * This is faster to compute than the magnitude and is often sufficient for use in calculations.
	 * @return Returns the squared magnitude of this vector.
	 */
	public int SqMagnitude()
	{return X * X + Y * Y;}
	
	/**
	 * Gets the magnitude of this vector.
	 * @return Returns the magnitude of this vector.
	 */
	public double Magnitude()
	{
		if(MagnitudeCalculated)
			return Magnitude;
		
		MagnitudeCalculated = true;
		return Magnitude = Math.sqrt(X * X + Y * Y);
	}
	
	/**
	 * Gets a normalized version of the vector.
	 * @return Returns a unit vector with the same direction as this one.
	 */
	public Vector2d Normalized()
	{return Divide(Magnitude());}
	
	@Override public String toString()
	{return "<" + X + "," + Y + ">";}
	
	@Override public int hashCode()
	{return X + (Y << 5) - Y;}
	
	/**
	 * The x component of this vector.
	 */
	public final int X;
	
	/**
	 * The y component of this vector.
	 */
	public final int Y;
	
	/**
	 * True only if the magnitude has been calculated.
	 */
	protected boolean MagnitudeCalculated;
	
	/**
	 * The magnitude of this vector.
	 */
	protected double Magnitude;
	
	/**
	 * The zero vector.
	 */
	public static final Vector2i ZERO = new Vector2i(0,0);
	
	/**
	 * The unit x vector.
	 */
	public static final Vector2i UNIT_X = new Vector2i(1,0);
	
	/**
	 * The unit y vector.
	 */
	public static final Vector2i UNIT_Y = new Vector2i(0,1);
	
	/**
	 * The one vector.
	 */
	public static final Vector2i ONE = new Vector2i(1,1);
	
	/**
	 * The unit left vector.
	 */
	public static final Vector2i LEFT = new Vector2i(-1,0);
	
	/**
	 * The unit right vector.
	 */
	public static final Vector2i RIGHT = new Vector2i(1,0);
	
	/**
	 * The unit up vector.
	 */
	public static final Vector2i UP = new Vector2i(0,-1);
	
	/**
	 * The unit down vector.
	 */
	public static final Vector2i DOWN = new Vector2i(0,1);
}
