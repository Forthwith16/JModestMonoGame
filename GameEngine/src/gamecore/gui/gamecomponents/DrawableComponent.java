package gamecore.gui.gamecomponents;

import java.awt.geom.AffineTransform;

import gamecore.IDrawable;
import gamecore.datastructures.matrices.Matrix2D;

/**
 * The base class for drawable components such as {@code TextComponent} or {@code ImageComponent}.
 * @author Dawn Nye
 */
public abstract class DrawableComponent extends AffineComponent implements IDrawable
{
	
	/**
	 * Creates a new drawable component.
	 */
	public DrawableComponent()
	{
		super();
		
		Camera = new Matrix2D();
		IgnoreCamera = false;
		
		return;
	}
	
	/**
	 * Creates a new drawable component.
	 * @param m The initial transformation.
	 */
	public DrawableComponent(Matrix2D m)
	{
		super(m);
		
		Camera = new Matrix2D();
		IgnoreCamera = false;
		
		return;
	}
	
	/**
	 * Creates a new drawable component.
	 * @param m The initial transformation.
	 */
	public DrawableComponent(AffineTransform m)
	{
		super(m);
		
		Camera = new Matrix2D();
		IgnoreCamera = false;
		
		return;
	}
	
	public void Draw(Matrix2D cam)
	{
		Camera = cam;
		
		repaint();
		return;
	}
	
	/**
	 * Sets if the view matrix should be ignored or used.
	 * @param no If true, the view matrix will be ignored. If false, the view matrix will be used.
	 */
	public void UseCamera(boolean no)
	{
		IgnoreCamera = no;
		return;
	}
	
	/**
	 * Determines if this component ignores the view matrix.
	 */
	public boolean IgnoresCamera()
	{return IgnoreCamera;}
	
	/**
	 * Determines if this component uses the view matrix.
	 */
	public boolean UsesCamera()
	{return !IgnoresCamera();}
	
	/**
	 * Obtains a deep copy of the matrix backing the affine transformation.
	 * Multiplied into it will be the view transformation.
	 * @param include_parent If true, we include the parent transform. If false, we omit it. If there is no parent, then the parent matrix is treated as the identity matrix.
	 * @return Returns a deep copy of the matrix transformation behind this AffineComponent. This will include the view matrix premultiplied into it.
	 */
	public Matrix2D GetWorldViewMatrix(boolean include_parent)
	{return IgnoresCamera() ? GetMatrix(include_parent) : GetMatrix(include_parent).LeftMultiply(Camera);}
	
	/**
	 * Obtains the affine transformation applied to the contents of this component.
	 * Multiplied into it will be the view transformation.
	 * @param include_parent If true, we include the parent transform. If false, we omit it. If there is no parent, then the parent matrix is treated as the identity matrix.
	 * @return Returns the affine transformation applied to the contents of this component. This will be expressed in world-view coordinates instead of just world coordinates.
	 */
	public AffineTransform GetWorldViewTransformation(boolean include_parent)
	{return IgnoresCamera() ? GetMatrix(include_parent).ToAffine() : GetMatrix(include_parent).LeftMultiply(Camera).ToAffine();}
	
	/**
	 * The view matrix.
	 */
	protected Matrix2D Camera;
	
	/**
	 * If true, we ignore the view matrix.
	 */
	protected boolean IgnoreCamera;
}
