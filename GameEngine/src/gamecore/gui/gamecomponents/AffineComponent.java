package gamecore.gui.gamecomponents;

import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import gamecore.datastructures.matrices.Matrix2D;
import gamecore.datastructures.vectors.Vector2d;

/**
 * Provides the basic framework for a component able to undergo affine transformations.
 * Note that to be consistent with Java's AffineTransformation, transformations are applied FILO to points.
 * This is to say that when we apply a new transformation T, we get M' = M * T and we transform points via M * p.
 * In this way, the last transform applied is the first to take effect upon points.
 * @author Dawn Nye
 */
public abstract class AffineComponent extends JComponent
{
	/**
	 * Creates a new affine component.
	 */
	public AffineComponent()
	{
		super();
		
		Parent = null;
		ResetTransformation();
		
		return;
	}
	
	/**
	 * Creates a new affine component.
	 * @param m The initial transformation.
	 */
	public AffineComponent(Matrix2D m)
	{
		super();
		
		Parent = null;
		SetTransformation(m);
		
		return;
	}
	
	/**
	 * Creates a new affine component.
	 * @param m The initial transformation.
	 */
	public AffineComponent(AffineTransform m)
	{
		super();

		Parent = null;
		SetTransformation(m);
		
		return;
	}
	
	/**
	 * Translates this by {@code c}.
	 * @param c The translation.
	 */
	public void Translate(double c)
	{
		Translate(c,c);
		return;
	}
	
	/**
	 * Translates this by {@code cx} and {@code cy}.
	 * @param cx The x translation.
	 * @param cy The y translation.
	 */
	public void Translate(double cx, double cy)
	{
		M.Translate(cx,cy);
		return;
	}
	
	/**
	 * Translates this by {@code v}.
	 * @param v The translation.
	 */
	public void Translate(Vector2d v)
	{
		Translate(v.X,v.Y);
		return;
	}
	
	/**
	 * Rotates this by {@code theta}.
	 * @param theta The angle of rotation specified in radians.
	 */
	public void Rotate(double theta)
	{
		M.Rotate(theta);
		return;
	}
	
	/**
	 * Applies a clean 90 degree rotation {@code times} times.
	 * This is done in a single multiplication.
	 * This is equivalent to a left multiplication by a rotation matrix.
	 * @param times The number of 90 degree rotations to perform.
	 */
	public void Rotate90(int times)
	{
		M.Rotate90(times);
		return;
	}
	
	/**
	 * Rotates this by {@code theta} about the point ({@code origin_x},{@code origin_y}).
	 * @param theta The angle of rotation specified in radians.
	 * @param origin_x The x coordinate of the point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 * @param origin_y The y coordinate of the point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 */
	public void Rotate(double theta, double origin_x, double origin_y)
	{
		M.Translate(-origin_x,-origin_y);
		M.Rotate(theta);
		M.Translate(origin_x,origin_y);
		
		return;
	}
	
	/**
	 * Applies a clean 90 degree rotation {@code times} times about the point ({@code origin_x},{@code origin_y}).
	 * This is done in a single multiplication.
	 * This is equivalent to a left multiplication by a rotation matrix.
	 * @param times The number of 90 degree rotations to perform.
	 * @param origin_x The x coordinate of the point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 * @param origin_y The y coordinate of the point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 */
	public void Rotate90(int times, double origin_x, double origin_y)
	{
		M.Translate(-origin_x,-origin_y);
		M.Rotate90(times);
		M.Translate(origin_x,origin_y);
		
		return;
	}
	
	/**
	 * Rotates this by {@code theta} about the point {@code origin}.
	 * @param theta The angle of rotation specified in radians.
	 * @param origin The point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 */
	public void Rotate(double theta, Vector2d origin)
	{
		Rotate(theta,origin.X,origin.Y);
		return;
	}
	
	/**
	 * Applies a clean 90 degree rotation {@code times} times about the point {@code origin}.
	 * This is done in a single multiplication.
	 * This is equivalent to a left multiplication by a rotation matrix.
	 * @param times The number of 90 degree rotations to perform.
	 * @param origin The point to rotate this about. Equivalent to translating -origin, rotating, and then translating origin.
	 */
	public void Rotate90(int times, Vector2d origin)
	{
		Rotate90(times,origin.X,origin.Y);
		return;
	}
	
	/**
	 * Scales this by {@code c}.
	 * @param c The scale.
	 */
	public void Scale(double c)
	{
		Scale(c,c);
		return;
	}
	
	/**
	 * Scales this by {@code cx} and {@code cy}.
	 * @param cx The x scale.
	 * @param cx The y scale.
	 */
	public void Scale(double cx, double cy)
	{
		M.Scale(cx,cy);
		return;
	}
	
	/**
	 * Scales this by {@code v}.
	 * @param v The scale.
	 */
	public void Scale(Vector2d v)
	{
		Scale(v.X,v.Y);
		return;
	}
	
	/**
	 * Shears this by {@code c}.
	 * @param c The shear factor.
	 */
	public void Shear(double c)
	{
		Shear(c,c);
		return;
	}
	
	/**
	 * Shears this by {@code cx} and {@code cy}.
	 * @param cx The x shear.
	 * @param cx The y shear.
	 */
	public void Shear(double cx, double cy)
	{
		M.Shear(cx,cy);
		return;
	}
	
	/**
	 * Shears this by {@code v}.
	 * @param v The shear.
	 */
	public void Shear(Vector2d v)
	{
		Shear(v.X,v.Y);
		return;
	}
	
	/**
	 * Performs a horizontal reflection.
	 */
	public void ReflectHorizonal()
	{
		Translate(-getWidth() / 2.0,0.0);
		Scale(-1.0,1.0);
		Translate(getWidth() / 2.0,0.0);
		
		return;
	}
	
	/**
	 * Performs a horizontal reflection.
	 * @param x_origin The vertical line we are reflecting across.
	 */
	public void ReflectHorizonal(double x_origin)
	{
		Translate(-x_origin,0.0);
		Scale(-1.0,1.0);
		Translate(x_origin,0.0);
		
		return;
	}
	
	/**
	 * Performs a vertical reflection.
	 */
	public void ReflectVertical()
	{
		Translate(-getHeight() / 2.0,0.0);
		Scale(1.0,-1.0);
		Translate(0.0,getHeight() / 2.0);
		
		return;
	}
	
	/**
	 * Performs a vertical reflection.
	 * @param y_origin The horizontal line we are reflecting across.
	 */
	public void ReflectVertical(double y_origin)
	{
		Translate(-y_origin,0.0);
		Scale(1.0,-1.0);
		Translate(0.0,y_origin);
		
		return;
	}
	
	/**
	 * Transforms this image by {@code m}.
	 * This transformation is applied after all previous transformations like a sane person.
	 * @param m The transformation to undergo.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void Transform(Matrix2D m)
	{
		M.LeftMultiply(m);
		return;
	}
	
	/**
	 * Transforms this image by {@code m}.
	 * This transformation is applied after all previous transformations like a sane person.
	 * @param m The transformation to undergo.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void Transform(AffineTransform m)
	{
		Transform(new Matrix2D(m));
		return;
	}
	
	/**
	 * Transforms this image by {@code m}.
	 * @param m The transformation to undergo.
	 * @param pre If true, then the transformation is applied before all previous transformations. If false, then the transformation is applied after all previous transformations. The default is the latter.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void Transform(Matrix2D m, boolean pre)
	{
		if(pre)
			M.RightMultiply(m);
		else
			M.LeftMultiply(m);
		
		return;
	}
	
	/**
	 * Transforms this image by {@code m}.
	 * @param m The transformation to undergo.
	 * @param pre If true, then the transformation is applied before all previous transformations. If false, then the transformation is applied after all previous transformations. The default is the latter.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void Transform(AffineTransform m, boolean pre)
	{
		Transform(new Matrix2D(m));
		return;
	}
	
	/**
	 * Sets the image transformation to {@code m}.
	 * @param m The transformation to set this image's to.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void SetTransformation(Matrix2D m)
	{
		if(m == null)
			throw new NullPointerException();
		
		M = new Matrix2D(m);
		return;
	}
	
	/**
	 * Sets the image transformation to {@code m}.
	 * @param m The transformation to set this image's to.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public void SetTransformation(AffineTransform m)
	{
		SetTransformation(new Matrix2D(m));
		return;
	}
	
	/**
	 * Resets the image transformation to the identity.
	 */
	public void ResetTransformation()
	{
		M = new Matrix2D();
		return;
	}
	
	/**
	 * Obtains the affine transformation applied to the contents of this component.
	 * @param include_parent If true, we include the parent transform. If false, we omit it. If there is no parent, then the parent matrix is treated as the identity matrix.
	 * @return Returns the affine transformation applied to the contents of this component.
	 */
	public AffineTransform GetTransformation(boolean include_parent)
	{return include_parent && Parent != null ? new Matrix2D(M).RightMultiply(Parent.M).ToAffine() : M.ToAffine();}
	
	/**
	 * Sets the parent affine component.
	 * @param c The parent affine component. This can be null.
	 */
	public void SetParent(AffineComponent c)
	{
		Parent = c;
		return;
	}
	
	/**
	 * Obtains the parent affine component.
	 * @return Returns the parent affine component or null if no such parent exists.
	 */
	public AffineComponent GetParent()
	{return Parent;}
	
	/**
	 * The transformation matrix to apply to the image.
	 */
	protected Matrix2D M;
	
	/**
	 * The parent affine component, if any.
	 */
	protected AffineComponent Parent;
}
