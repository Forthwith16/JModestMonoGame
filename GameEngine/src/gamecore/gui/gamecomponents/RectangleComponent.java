package gamecore.gui.gamecomponents;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Draws a rectangle.
 * This is great for simple backgrounds or to cover up other things.
 * @author Dawn Nye
 */
public class RectangleComponent extends ImageComponent
{
	/**
	 * Creates a drawable rectangle.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param c The color of the rectangle. This supports transparency.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is negative.
	 */
	public RectangleComponent(int w, int h, Color c)
	{
		super(CreateImage(w,h,c));
		return;
	}
	
	/**
	 * Creates a drawable rectangle.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param f A function which produces colors for the rectangle. This supports transparency.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is negative.
	 */
	public RectangleComponent(int w, int h, ColorFunction f)
	{
		super(CreateImage(w,h,f));
		return;
	}
	
	/**
	 * Creates an image of dimension {@code w} by {@code h} with color {@code c}.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param c The color the rectangle. This supports transparency.
	 * @return Returns a new image of the specified size and color.
	 * @implNote Although it would be faster and more efficient to create a single pixel to draw and scale it up, to keep things 100% pixel perfect, we'll just create the entire rectangle.
	 */
	protected static BufferedImage CreateImage(int w, int h, Color c)
	{
		BufferedImage ret = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		int rgba = c.getRGB();
		
		for(int i = 0;i < w;i++)
			for(int j = 0;j < h;j++)
				ret.setRGB(i,j,rgba);
		
		return ret;
	}
	
	/**
	 * Creates an image of dimension {@code w} by {@code h} with color chosen according to {@code f}.
	 * @param w The width of the rectangle.
	 * @param h The height of the rectangle.
	 * @param f A function which produces colors for the rectangle. This supports transparency.
	 * @return Returns a new image of the specified size and color.
	 * @implNote Although it would be faster and more efficient to create a single pixel to draw and scale it up, to keep things 100% pixel perfect, we'll just create the entire rectangle.
	 */
	protected static BufferedImage CreateImage(int w, int h, ColorFunction f)
	{
		BufferedImage ret = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		
		for(int i = 0;i < w;i++)
			for(int j = 0;j < h;j++)
				ret.setRGB(i,j,f.GetColor(i,j).getRGB());
		
		return ret;
	}
	
	/**
	 * A color generating function.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface ColorFunction
	{
		/**
		 * Gets the color at the position ({@code x},{@code y}).
		 */
		public Color GetColor(int x, int y);
		
		/**
		 * Creates a horizontal gradient color function.
		 * The colors will be linearly interpolated from left to right from {@code l} to {@code r}.
		 * @param w The width of the gradient. This is provided as a float to avoid casting from an int to a double each call.
		 * @param l The color of the gradient at the farthestmost left.
		 * @param r The color of the gradient at the farthestmost right.
		 * @return Returns a function which produces a horizontal gradient as specified.
		 */
		public static ColorFunction HorizontalGradient(float w, Color l, Color r)
		{
			return (x,y) ->
			{
				float t = x / (w - 1.0f);
				float t1 = 1.0f - t;
				
				return new Color((int)(l.getRed() * t + r.getRed() * t1),
							  (int)(l.getGreen() * t + r.getGreen() * t1),
							  (int)(l.getBlue() * t + r.getBlue() * t1),
							  (int)(l.getAlpha() * t + r.getAlpha() * t1));
			};
		}
		
		/**
		 * Creates a vertical gradient color function.
		 * The colors will be linearly interpolated from top to bottom from {@code t} to {@code b}.
		 * @param h The height of the gradient. This is provided as a float to avoid casting from an int to a double each call.
		 * @param t The color of the gradient at the farthestmost top.
		 * @param b The color of the gradient at the farthestmost bottom.
		 * @return Returns a function which produces a horizontal gradient as specified.
		 */
		public static ColorFunction VerticalGradient(float h, Color t, Color b)
		{
			return (x,y) ->
			{
				float t0 = y / (h - 1.0f);
				float t1 = 1.0f - t0;
				
				return new Color((int)(t.getRed() * t1 + b.getRed() * t0),
							  (int)(t.getGreen() * t1 + b.getGreen() * t0),
							  (int)(t.getBlue() * t1 + b.getBlue() * t0),
							  (int)(t.getAlpha() * t1 + b.getAlpha() * t0));
			};
		}
	}
}