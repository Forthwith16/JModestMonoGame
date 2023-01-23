package gamecore.datastructures.matrices;

import java.awt.geom.AffineTransform;

import gamecore.GlobalConstants;
import gamecore.datastructures.vectors.Vector2d;

/**
 * A simple 3x3 matrix class for performing computer graphics calculations.
 * Since Java does matrix multiplication in the wrong order for computer graphics and simply overloading concatenate and preConcatenate doesn't work, we have to implement a matrix ourselves.
 * @author Dawn Nye
 */
public class Matrix2D
{
	/**
	 * Constructs a new matrix representing the identity transformation.
	 */
	public Matrix2D()
	{
		M = new double[3][3];
		
		M[0][0] = 1.0;
		M[0][1] = 0.0;
		M[0][2] = 0.0;
		
		M[1][0] = 0.0;
		M[1][1] = 1.0;
		M[1][2] = 0.0;
		
		M[2][0] = 0.0;
		M[2][1] = 0.0;
		M[2][2] = 1.0;
		
		return;
	}
	
	/**
	 * Constructs a matrix with the given entries.
	 * Each entry is indexed via row and then column.
	 * The first three parameters are the first row.
	 * The second three parameters are the second row.
	 */
	public Matrix2D(double m00, double m01, double m02, double m10, double m11, double m12)
	{
		M = new double[3][3];
		
		M[0][0] = m00;
		M[0][1] = m01;
		M[0][2] = m02;
		
		M[1][0] = m10;
		M[1][1] = m11;
		M[1][2] = m12;
		
		M[2][0] = 0.0;
		M[2][1] = 0.0;
		M[2][2] = 1.0;
		
		return;
	}
	
	/**
	 * Constructs a matrix with the given entries.
	 * Each entry is indexed via row and then column.
	 * The first three parameters are the first row.
	 * The second three parameters are the second row.
	 * The third three parameters are the third row.
	 */
	public Matrix2D(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
	{
		M = new double[3][3];
		
		M[0][0] = m00;
		M[0][1] = m01;
		M[0][2] = m02;
		
		M[1][0] = m10;
		M[1][1] = m11;
		M[1][2] = m12;
		
		M[2][0] = m20;
		M[2][1] = m21;
		M[2][2] = m22;
		
		return;
	}
	
	/**
	 * Creates a deep copy of {@code m}.
	 * @param m The matrix to copy.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public Matrix2D(Matrix2D m)
	{
		M = new double[3][3];
		
		M[0][0] = m.M[0][0];
		M[0][1] = m.M[0][1];
		M[0][2] = m.M[0][2];
		
		M[1][0] = m.M[1][0];
		M[1][1] = m.M[1][1];
		M[1][2] = m.M[1][2];
		
		M[2][0] = m.M[2][0];
		M[2][1] = m.M[2][1];
		M[2][2] = m.M[2][2];
		
		return;
	}
	
	/**
	 * Constructs a new matrix that is identical to {@code T}
	 * @param T The AffineTransform object to copy.
	 * @throws NullPointerException Thrown if {@code T} is null.
	 */
	public Matrix2D(AffineTransform T)
	{
		M = new double[3][3];
		
		M[0][0] = T.getScaleX();
		M[0][1] = T.getShearX();
		M[0][2] = T.getTranslateX();
		
		M[1][0] = T.getShearY();
		M[1][1] = T.getScaleY();
		M[1][2] = T.getTranslateY();
		
		M[2][0] = 0.0;
		M[2][1] = 0.0;
		M[2][2] = 1.0;
		
		return;
	}
	
	/**
	 * Turns this matrix into an {@code AffineTransform}.
	 * @return Returns an {@code AffineTransform} interpretation of this matrix.
	 */
	public AffineTransform ToAffine()
	{return new AffineTransform(M[0][0],M[1][0],M[0][1],M[1][1],M[0][2],M[1][2]);}
	
	@Override public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(obj instanceof Matrix2D)
		{
			Matrix2D m = (Matrix2D)obj;
			
			for(int i = 0;i < 3;i++)
				for(int j = 0;j < 3;j++)
					if(!GlobalConstants.CloseEnough(M[i][j],m.M[i][j]))
						return false;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Performs a matrix addition of {@code this} + {@code m}.
	 * @param m The matrix to add.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Add(Matrix2D m)
	{
		for(int i = 0;i < 3;i++)
			for(int j = 0;j < 3;j++)
				M[i][j] += m.M[i][j];
		
		return this;
	}
	
	/**
	 * Performs a matrix subtraction of {@code this} - {@code m}.
	 * @param m The matrix to subtract.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Subtract(Matrix2D m)
	{
		for(int i = 0;i < 3;i++)
			for(int j = 0;j < 3;j++)
				M[i][j] -= m.M[i][j];
		
		return this;
	}
	
	/**
	 * Performs a scalar multiplication of {@code c} * {@code this}.
	 * @param c The scalar to multiply by.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Multiply(double c)
	{
		for(int i = 0;i < 3;i++)
			for(int j = 0;j < 3;j++)
				M[i][j] *= c;
		
		return this;
	}
	
	/**
	 * Performs a left multiplication of {@code m} * {@code this}.
	 * @param m The matrix to multiply this matrix by.
	 * @return Returns the result stored in this matrix.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public Matrix2D LeftMultiply(Matrix2D m)
	{
		double r00 = m.M[0][0] * M[0][0] + m.M[0][1] * M[1][0] + m.M[0][2] * M[2][0];
		double r01 = m.M[0][0] * M[0][1] + m.M[0][1] * M[1][1] + m.M[0][2] * M[2][1];
		double r02 = m.M[0][0] * M[0][2] + m.M[0][1] * M[1][2] + m.M[0][2] * M[2][2];
		
		double r10 = m.M[1][0] * M[0][0] + m.M[1][1] * M[1][0] + m.M[1][2] * M[2][0];
		double r11 = m.M[1][0] * M[0][1] + m.M[1][1] * M[1][1] + m.M[1][2] * M[2][1];
		double r12 = m.M[1][0] * M[0][2] + m.M[1][1] * M[1][2] + m.M[1][2] * M[2][2];
		
		double r20 = m.M[2][0] * M[0][0] + m.M[2][1] * M[1][0] + m.M[2][2] * M[2][0];
		double r21 = m.M[2][0] * M[0][1] + m.M[2][1] * M[1][1] + m.M[2][2] * M[2][1];
		double r22 = m.M[2][0] * M[0][2] + m.M[2][1] * M[1][2] + m.M[2][2] * M[2][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		M[2][0] = r20;
		M[2][1] = r21;
		M[2][2] = r22;
		
		return this;
	}
	
	/**
	 * Performs a right multiplication of {@code this} * {@code m}.
	 * @param m The matrix to multiply this matrix by.
	 * @return Returns the result stored in this matrix.
	 * @throws NullPointerException Thrown if {@code m} is null.
	 */
	public Matrix2D RightMultiply(Matrix2D m)
	{
		double r00 = M[0][0] * m.M[0][0] + M[0][1] * m.M[1][0] + M[0][2] * m.M[2][0];
		double r01 = M[0][0] * m.M[0][1] + M[0][1] * m.M[1][1] + M[0][2] * m.M[2][1];
		double r02 = M[0][0] * m.M[0][2] + M[0][1] * m.M[1][2] + M[0][2] * m.M[2][2];
		
		double r10 = M[1][0] * m.M[0][0] + M[1][1] * m.M[1][0] + M[1][2] * m.M[2][0];
		double r11 = M[1][0] * m.M[0][1] + M[1][1] * m.M[1][1] + M[1][2] * m.M[2][1];
		double r12 = M[1][0] * m.M[0][2] + M[1][1] * m.M[1][2] + M[1][2] * m.M[2][2];
		
		double r20 = M[2][0] * m.M[0][0] + M[2][1] * m.M[1][0] + M[2][2] * m.M[2][0];
		double r21 = M[2][0] * m.M[0][1] + M[2][1] * m.M[1][1] + M[2][2] * m.M[2][1];
		double r22 = M[2][0] * m.M[0][2] + M[2][1] * m.M[1][2] + M[2][2] * m.M[2][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		M[2][0] = r20;
		M[2][1] = r21;
		M[2][2] = r22;
		
		return this;
	}
	
	/**
	 * Performs the matrix multiplication {@code this} * ({@code x},{@code y}).
	 * {@code p} is treated as a column vector with the third component assumed to be 1.
	 * @param x The x coordinate of the point to transform.
	 * @param y The y coordinate of the point to transform.
	 * @return Returns the point obtained by multiplying by this matrix.
	 */
	public Vector2d RightMultiply(double x, double y)
	{return new Vector2d(M[0][0] * x + M[0][1] * y + M[0][2],M[1][0] * x + M[1][1] * y + M[1][2]);}
	
	/**
	 * Performs the matrix multiplication {@code this} * {@code p}.
	 * {@code p} is treated as a column vector with the third component assumed to be 1.
	 * @param p The point to transform.
	 * @return Returns the point obtained by multiplying by this matrix.
	 */
	public Vector2d RightMultiply(Vector2d p)
	{return RightMultiply(p.X,p.Y);}
	
	/**
	 * Applies a translation to this matrix.
	 * This is equivalent to a left multiplication by a translation matrix.
	 * @param tx The x translation to perform.
	 * @param ty The y translation to perform.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Translate(double tx, double ty)
	{
		double r00 = 1.0 * M[0][0] + tx * M[2][0];
		double r01 = 1.0 * M[0][1] + tx * M[2][1];
		double r02 = 1.0 * M[0][2] + tx * M[2][2];
		
		double r10 = 1.0 * M[1][0] + ty * M[2][0];
		double r11 = 1.0 * M[1][1] + ty * M[2][1];
		double r12 = 1.0 * M[1][2] + ty * M[2][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		return this;
	}
	
	/**
	 * Applies a translation to this matrix.
	 * This is equivalent to a left multiplication by a translation matrix.
	 * @param t The translation to perform.
	 * @return Returns the result stored in this matrix.
	 * @throws NullPointerException Thrown if {@code t} is null.
	 */
	public Matrix2D Translate(Vector2d t)
	{return Translate(t.X,t.Y);}
	
	/**
	 * Applies a rotation to this matrix.
	 * This is equivalent to a left multiplication by a rotation matrix.
	 * @param angle The angle to rotate by (in radians).
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Rotate(double angle)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		double r00 = cos * M[0][0] + -sin * M[1][0];
		double r01 = cos * M[0][1] + -sin * M[1][1];
		double r02 = cos * M[0][2] + -sin * M[1][2];
		
		double r10 = sin * M[0][0] + cos * M[1][0];
		double r11 = sin * M[0][1] + cos * M[1][1];
		double r12 = sin * M[0][2] + cos * M[1][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		return this;
	}
	
	/**
	 * Applies a clean 90 degree rotation {@code times} times.
	 * This is done in a single multiplication.
	 * This is equivalent to a left multiplication by a rotation matrix.
	 * @param times The number of 90 degree rotations to perform.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Rotate90(int times)
	{
		times = times % 4;
		
		if(times < 0)
			times += 4;
		
		double cos = 0.0;
		double sin = 0.0;
		
		switch(times)
		{
		case 0:
			cos = 1.0;
			sin = 0.0;
			
			break;
		case 1:
			cos = 0.0;
			sin = 1.0;
			
			break;
		case 2:
			cos = -1.0;
			sin = 0.0;
			
			break;
		case 3:
			cos = 0.0;
			sin = -1.0;
			
			break;
		}
		
		double r00 = cos * M[0][0] + -sin * M[1][0];
		double r01 = cos * M[0][1] + -sin * M[1][1];
		double r02 = cos * M[0][2] + -sin * M[1][2];
		
		double r10 = sin * M[0][0] + cos * M[1][0];
		double r11 = sin * M[0][1] + cos * M[1][1];
		double r12 = sin * M[0][2] + cos * M[1][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		return this;
	}
	
	/**
	 * Applies a scale factor to this matrix.
	 * This is equivalent to a left multiplication by a scale matrix.
	 * @param s The scale factor.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Scale(double s)
	{return Scale(s,s);}
	
	/**
	 * Applies a scale factor to this matrix.
	 * This is equivalent to a left multiplication by a scale matrix.
	 * @param sx The horizontal scale factor.
	 * @param sy The vertical scale factor.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Scale(double sx, double sy)
	{
		M[0][0] *= sx;
		M[0][1] *= sx;
		M[0][2] *= sx;
		
		M[1][0] *= sy;
		M[1][1] *= sy;
		M[1][2] *= sy;
		
		return this;
	}
	
	/**
	 * Applies a scale factor to this matrix.
	 * This is equivalent to a left multiplication by a scale matrix.
	 * @param s The scale factors.
	 * @return Returns the result stored in this matrix.
	 * @throws NullPointerException Thrown if {@code s} is null.
	 */
	public Matrix2D Scale(Vector2d s)
	{return Scale(s.X,s.Y);}
	
	/**
	 * Applies a shear transformation to this matrix.
	 * This is equivalent to a left multiplication by a shear matrix.
	 * @param sx The horizontal shear.
	 * @param sy The vertical shear.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Shear(double sx, double sy)
	{
		double r00 = 1.0 * M[0][0] + sx * M[1][0];
		double r01 = 1.0 * M[0][1] + sx * M[1][1];
		double r02 = 1.0 * M[0][2] + sx * M[1][2];
		
		double r10 = sy * M[0][0] + 1.0 * M[1][0];
		double r11 = sy * M[0][1] + 1.0 * M[1][1];
		double r12 = sy * M[0][2] + 1.0 * M[1][2];
		
		M[0][0] = r00;
		M[0][1] = r01;
		M[0][2] = r02;
		
		M[1][0] = r10;
		M[1][1] = r11;
		M[1][2] = r12;
		
		return this;
	}
	
	/**
	 * Applies a shear transformation to this matrix.
	 * This is equivalent to a left multiplication by a shear matrix.
	 * @param s The shear factors.
	 * @return Returns the result stored in this matrix.
	 * @throws NullPointerException Thrown if {@code s} is null.
	 */
	public Matrix2D Shear(Vector2d s)
	{return Shear(s.X,s.Y);}
	
	/**
	 * Performs a transpose.
	 * @return Returns the result stored in this matrix.
	 */
	public Matrix2D Transpose()
	{
		double temp = M[0][1];
		M[0][1] = M[1][0];
		M[1][0] = temp;
		
		temp = M[0][2];
		M[0][2] = M[2][0];
		M[2][0] = temp;
		
		temp = M[1][2];
		M[2][1] = M[1][2];
		M[1][2] = temp;
		
		return this;
	}
	
	/**
	 * Computes the inverse of this matrix.
	 * @return Returns the result stored in this matrix.
	 * @throws IllegalStateException Thrown if there is no inverse matrix. 
	 */
	public Matrix2D Invert()
	{
		double factor = Determinant();
		
		if(GlobalConstants.CloseEnough(factor,0.0))
			throw new IllegalStateException();
		
		factor = 1.0 / factor;
		
		double i00 = M[1][1] * M[2][2] - M[1][2] * M[2][1];
		double i01 = M[0][2] * M[2][1] - M[0][1] * M[2][2];
		double i02 = M[0][1] * M[1][2] - M[0][2] * M[1][1];
		
		double i10 = M[1][2] * M[2][0] - M[1][0] * M[2][2];
		double i11 = M[0][0] * M[2][2] - M[0][2] * M[2][0];
		double i12 = M[0][2] * M[1][0] - M[0][0] * M[1][2];
		
		double i20 = M[1][0] * M[2][1] - M[1][1] * M[2][0];
		double i21 = M[0][1] * M[2][0] - M[0][0] * M[2][1];
		double i22 = M[0][0] * M[1][1] - M[0][1] * M[1][0];
		
		M[0][0] = i00;
		M[0][1] = i01;
		M[0][2] = i02;
		
		M[1][0] = i10;
		M[1][1] = i11;
		M[1][2] = i12;
		
		M[2][0] = i20;
		M[2][1] = i21;
		M[2][2] = i22;
		
		Multiply(factor);
		return this;
	}
	
	/**
	 * Calculates the determinant of this matrix.
	 * @return Returns det({@code this}).
	 */
	public double Determinant()
	{return M[0][0] * (M[1][1] * M[2][2] - M[1][2] * M[2][1]) - M[0][1] * (M[1][0] * M[2][2] - M[1][2] * M[2][0]) + M[0][2] * (M[1][0] * M[2][1] - M[1][1] * M[2][0]);}
	
	/**
	 * Calculates the trace of this matrix.
	 * @return Returns tr({@code this}).
	 */
	public double Trace()
	{return M[0][0] + M[1][1] + M[2][2];}
	
	@Override public String toString()
	{
		String m00 = Double.toString(M[0][0]);
		String m01 = Double.toString(M[0][1]);
		String m02 = Double.toString(M[0][2]);
		
		String m10 = Double.toString(M[1][0]);
		String m11 = Double.toString(M[1][1]);
		String m12 = Double.toString(M[1][2]);
		
		String m20 = Double.toString(M[2][0]);
		String m21 = Double.toString(M[2][1]);
		String m22 = Double.toString(M[2][2]);
		
		int longest0 = Math.max(Math.max(m00.length(),m10.length()),m20.length());
		int longest1 = Math.max(Math.max(m01.length(),m11.length()),m21.length());
		int longest2 = Math.max(Math.max(m02.length(),m12.length()),m22.length());
		
		String blank0 = "";
		String blank1 = "";
		String blank2 = "";
		
		for(int i = 0;i < longest0;i++)
			blank0 += " ";
		
		for(int i = 0;i < longest1;i++)
			blank1 += " ";
		
		for(int i = 0;i < longest2;i++)
			blank2 += " ";
		
		String ret = " _ " + blank0 + " " + blank1 + " " + blank2 + " _ \n";
		ret += "|  " + Pad(m00,longest0) + " " + Pad(m01,longest1) + " " + Pad(m02,longest2) + "  |\n";
		ret += "|  " + Pad(m10,longest0) + " " + Pad(m11,longest1) + " " + Pad(m12,longest2) + "  |\n";
		ret += "|_ " + Pad(m20,longest0) + " " + Pad(m21,longest1) + " " + Pad(m22,longest2) + " _|\n";
		
		return ret;
	}
	
	@Override public int hashCode()
	{return ((((Double.hashCode(M[0][0]) * 31 + Double.hashCode(M[0][1])) * 31 + Double.hashCode(M[0][2])) * 31 + Double.hashCode(M[1][0])) * 31 + Double.hashCode(M[1][1])) * 31 + Double.hashCode(M[1][2]);}
	
	/**
	 * Pads {@code str} with front spaces until it has length {@code len}.
	 * @param str The string to pad.
	 * @param len The length to attain.
	 * @return Returns {@code str} padded with spaces until it has length at least {@code len}.
	 */
	protected String Pad(String str, int len)
	{
		while(str.length() < len)
			str = " " + str;
		
		return str;
	}
	
	/**
	 * Gets the entry M<sub>{@code row},{@code column}</sub> of the matrix M.
	 * @param row The row to access. This value should be between 0 and 2.
	 * @param column The column to access. This value should be between 0 and 2.
	 * @return Returns the row-column entry of this matrix.
	 * @throws IndexOutOfBoundsException Thrown if {@code row} or {@code column} are negative or greater than 2.
	 */
	public double GetEntry(int row, int column)
	{return M[row][column];}
	
	/**
	 * The entries of the matrix.
	 * The are specified by row first and then column.
	 */
	protected final double[][] M;
}
