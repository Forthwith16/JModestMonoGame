package gamecore.datastructures;

import gamecore.datastructures.vectors.Vector2d;
import gamecore.datastructures.vectors.Vector2i;

/**
 * A rectangle class.
 * The rectangle encloses a group of cells.
 * For example, a rectangle of width and height 1 at the origin encloses just the cell (0,0).
 * This means it has Left = Right and Top = Bottom despite the width and height being nonzero.
 * @author Dawn Nye
 */
public class CellRectangle
{
	/**
	 * Creates a new rectangle.
	 * @param x The leftmost position of the rectangle.
	 * @param y The topmost position of the rectangle.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @throws IllegalArgumentException Thrown if either {@code w} or {@code h} is negative.
	 */
	public CellRectangle(int x, int y, int w, int h)
	{
		if(w < 0 || h < 0)
			throw new IllegalArgumentException();
		
		Left = x;
		Right = x + w - 1;
		Width = w;
		
		Top = y;
		Bottom = y + h - 1;
		Height = h;
		
		return;
	}
	
	/**
	 * Creates a new rectangle.
	 * @param pos The top-left position of the rectangle.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @throws IllegalArgumentException Thrown if either {@code w} or {@code h} is negative.
	 */
	public CellRectangle(Vector2i pos, int w, int h)
	{
		this(pos.X,pos.Y,w,h);
		return;
	}
	
	/**
	 * Creates a new rectangle.
	 * @param x The leftmost position of the rectangle.
	 * @param y The topmost position of the rectangle.
	 * @param dim The dimensions of the rectangle.
	 * @throws IllegalArgumentException Thrown if either {@code dim.X} or {@code dim.Y} is negative.
	 */
	public CellRectangle(int x, int y, Vector2i dim)
	{
		this(x,y,dim.X,dim.Y);
		return;
	}
	
	/**
	 * Creates a new rectangle.
	 * @param pos The top-left position of the rectangle.
	 * @param dim The dimensions of the rectangle.
	 * @throws IllegalArgumentException Thrown if either {@code w} or {@code h} is negative.
	 */
	public CellRectangle(Vector2i pos, Vector2i dim)
	{
		this(pos.X,pos.Y,dim.X,dim.Y);
		return;
	}
	
	/**
	 * Duplicates a rectangle.
	 * @param rect The rectangle to duplicate.
	 */
	public CellRectangle(CellRectangle rect)
	{
		this(rect.Left,rect.Top,rect.Width,rect.Height);
		return;
	}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj instanceof CellRectangle)
		{
			CellRectangle rect = (CellRectangle)obj;
			return Left == rect.Left && Right == rect.Right && Top == rect.Top && Bottom == rect.Bottom;
		}
		
		return false;
	}
	
	/**
	 * Creates a minimal bounding rectangle around this and {@code rect}.
	 * Empty rectangles are ignored.
	 * @param rect The rectangle to union with.
	 * @return Returns the minimal rectangle encompasing both rectangles. Empty rectangle positions are ignored. If both rectangles are empty, then {@code EMPTY} is returned.
	 * @throws NullPointerException Thrown if {@code rect} is null.
	 */
	public CellRectangle Union(CellRectangle rect)
	{
		if(rect.IsEmpty())
			if(IsEmpty())
				return EMPTY;
			else
				return this;
		else if(IsEmpty())
			return rect;
		
		int l = Math.min(Left,rect.Left);
		int t = Math.min(Top,rect.Top);
		
		return new CellRectangle(l,t,Math.max(Right,rect.Right) - l + 1,Math.max(Bottom,rect.Bottom) - t + 1);
	}
	
	/**
	 * Calculates the intersection of this rectangle and {@code rect}.
	 * @param rect The rectangle to intersect with.
	 * @return Returns the intersection of the rectangles or {@code EMPTY} if either is empty or they do not intersect.
	 * @throws NullPointerException Thrown if {@code rect} is null.
	 */
	public CellRectangle Intersection(CellRectangle rect)
	{
		if(!Intersects(rect) || IsEmpty() || rect.IsEmpty())
			return EMPTY;
		
		int l = Math.max(Left,rect.Left);
		int t = Math.max(Top,rect.Top);
		
		return new CellRectangle(l,t,Math.min(Right,rect.Right) - l + 1,Math.min(Bottom,rect.Bottom) - t + 1);
	}
	
	/**
	 * Determines if this intersects with {@code rect}
	 * @param rect The rectangle to check for intersection
	 * @return Returns true if this and {@code rect} 
	 * @throws NullPointerException Thrown if {@code rect} is null.
	 */
	public boolean Intersects(CellRectangle rect)
	{return Left <= rect.Right && Right >= rect.Left && Top <= rect.Bottom && Bottom >= rect.Top;}
	
	/**
	 * Determines if this rectangle contains the point {@code pos}
	 * @param pos The point to check for containment.
	 * @return Returns true if this rectangle contains {@code pos} and false otherwise.
	 * @throws NullPointerException Thrown if {@code pos} is null.
	 */
	public boolean Contains(Vector2i pos)
	{return pos.X >= Left && pos.X <= Right && pos.Y >= Top && pos.Y <= Bottom;}
	
	/**
	 * Determines if this rectangle contains the rectangle {@code rect}
	 * @param rect The rectangle to check for containment.
	 * @return Returns true if this rectangle contains {@code rect} and false otherwise.
	 * @throws NullPointerException Thrown if {@code rect} is null.
	 */
	public boolean Contains(CellRectangle rect)
	{return rect.Left >= Left && rect.Right <= Right && rect.Top >= Top && rect.Bottom <= Bottom;}
	
	/**
	 * Determines if this rectangle is a single point.
	 * @return Returns true if this rectangle is a point with width one and height one.
	 */
	public boolean IsPoint()
	{return Width == 1 && Height == 1;}
	
	/**
	 * Determines if this rectangle is a line.
	 * @return Returns true if at least one of this rectangle's width or height is one.
	 */
	public boolean IsLine()
	{return Width == 1 || Height == 1;}
	
	/**
	 * Determines if this rectangle is a singularity.
	 * @return Returns true if this rectangle has both zero width and zero height.
	 */
	public boolean IsSingular()
	{return Width == 0 && Height == 0;}
	
	/**
	 * Determines if this rectangle is empty.
	 * @return Returns true if either the height and width of this rectangle are both zero.
	 */
	public boolean IsEmpty()
	{return Width == 0 || Height == 0;}
	
	/**
	 * Determines if this rectangle is degenerate.
	 * @return Returns true if this rectangle is either a degenerate line or a degenerate point.
	 */
	public boolean IsDegenerate()
	{return IsEmpty();}
	
	/**
	 * Returns the position of this rectangle.
	 * @return Returns ({@code Left},{@code Top}).
	 */
	public Vector2i Position()
	{return new Vector2i(Left,Top);}
	
	/**
	 * Returns the top-left position of this rectangle.
	 * @return Returns ({@code Left},{@code Top}).
	 */
	public Vector2i TopLeft()
	{return new Vector2i(Left,Top);}
	
	/**
	 * Returns the top-right position of this rectangle.
	 * @return Returns ({@code Right},{@code Top}).
	 */
	public Vector2i TopRight()
	{return new Vector2i(Right,Top);}
	
	/**
	 * Returns the bottom-left position of this rectangle.
	 * @return Returns ({@code Left},{@code Bottom}).
	 */
	public Vector2i BottomLeft()
	{return new Vector2i(Left,Bottom);}
	
	/**
	 * Returns the bottom-right position of this rectangle.
	 * @return Returns ({@code Right},{@code Bottom}).
	 */
	public Vector2i BottomRight()
	{return new Vector2i(Right,Bottom);}
	
	/**
	 * Returns the center of this rectangle.
	 * @return Returns the center of the rectangle.
	 */
	public Vector2d Center()
	{return Position().Add(BottomRight().Subtract(TopLeft()).Divide(2.0));}
	
	/**
	 * Returns the dimensions of this rectangle.
	 * @return Returns ({@code Width},{@code Height}).
	 */
	public Vector2i Dimensions()
	{return new Vector2i(Width,Height);}
	
	/**
	 * Gets the area of this rectangle.
	 * @return Returns the area of this rectangle.
	 */
	public int Area()
	{return Width * Height;}
	
	/**
	 * Obtains the perimeter of the rectangle.
	 * @return Returns the perimeter of the rectangle.
	 */
	public int Perimeter()
	{return (Width + Height) << 1;}
	
	/**
	 * Obtains half the perimeter of the rectangle.
	 * This method is slightly faster than perimeter if the factor of two is not necessary.
	 * @return Returns half the perimeter of the rectangle.
	 */
	public int HalfPerimeter()
	{return Width + Height;}
	
	/**
	 * Gets the value of Left.
	 * @return Returns the value of Left.
	 */
	public int Left()
	{return Left;}
	
	/**
	 * Gets the value of Right.
	 * @return Returns the value of Right.
	 */
	public int Right()
	{return Right;}
	
	/**
	 * Gets the value of Width.
	 * @return Returns the value of Width.
	 */
	public int Width()
	{return Width;}
	
	/**
	 * Gets the value of Top.
	 * @return Returns the value of Top.
	 */
	public int Top()
	{return Top;}
	
	/**
	 * Gets the value of Bottom.
	 * @return Returns the value of Bottom.
	 */
	public int Bottom()
	{return Bottom;}
	
	/**
	 * Gets the value of Height.
	 * @return Returns the value of Height.
	 */
	public int Height()
	{return Height;}
	
	@Override public String toString()
	{return "[" + Left + "," + Top + "," + Width + "," + Height + "]";}
	
	@Override public int hashCode()
	{
		int b = Dimensions().hashCode();
		return Position().hashCode() + (b << 5) - b;
	}
	
	/**
	 * The leftmost value of this rectangle.
	 */
	protected int Left;
	
	/**
	 * The rightmost value of this rectangle.
	 */
	protected int Right;
	
	/**
	 * The width of this rectangle.
	 */
	protected int Width;
	
	/**
	 * The topmost value of this rectangle.
	 */
	protected int Top;
	
	/**
	 * The bottommost value of this rectangle.
	 */
	protected int Bottom;
	
	/**
	 * The height of this rectangle.
	 */
	protected int Height;
	
	/**
	 * An empty rectangle placed at the origin.
	 */
	protected static final CellRectangle EMPTY = new CellRectangle(0,0,0,0);
}
