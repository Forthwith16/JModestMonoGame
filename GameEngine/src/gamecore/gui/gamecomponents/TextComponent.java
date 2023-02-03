package gamecore.gui.gamecomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import gamecore.IDrawable;

/**
 * A component that writes a string.
 * @author Dawn Nye
 */
public class TextComponent extends AffineComponent implements IDrawable
{
	/**
	 * Creates a new text component.
	 * It draws with plain black Courier font at size 18. 
	 * @param text The string to draw.
	 * @throws NullPointerException Thrown if {@code text} is null.
	 */
	public TextComponent(String text)
	{
		if(text == null)
			throw new NullPointerException();
		
		Text = text;
		Font = new Font("Courier",Font.PLAIN,18);
		Color = Color.BLACK;
		
		Display = true;
		return;
	}
	
	/**
	 * Creates a new text component.
	 * @param text The string to draw.
	 * @param font The font to draw with.
	 * @param c The color to draw the string with.
	 * @throws NullPointerException Thrown if {@code text}, {@code font}, or {@code c} is null.
	 */
	public TextComponent(String text, Font font, Color c)
	{
		if(text == null || font == null || c == null)
			throw new NullPointerException();
		
		Text = text;
		Font = font;
		Color = c;
		
		Display = true;
		return;
	}
	
	public void Initialize()
	{
		Initialized = true;
		return;
	}
	
	public boolean Initialized()
	{return Initialized;}
	
	public void Update(long delta)
	{return;}
	
	public void Dispose()
	{
		Disposed = true;
		return;
	}
	
	public boolean Disposed()
	{return Disposed;}
	
	public void Draw()
	{
		repaint();
		return;
	}
	
	@Override public void paint(Graphics g)
	{
		if(Display && Text != null)
		{
			Graphics2D g2 = (Graphics2D)g;
			
			g2.setFont(Font);
			g2.setColor(Color);
			g2.setTransform(GetTransformation(true));
			
			g2.drawString(Text,0,0);
		}
		
		super.paint(g);
		return;
	}
	
	/**
	 * Hides the text.
	 */
	public void Hide()
	{
		Display = false;
		return;
	}
	
	/**
	 * Shows the text.
	 */
	public void Show()
	{
		Display = true;
		return;
	}
	
	/**
	 * Obtains the current message.
	 */
	public String GetText()
	{return Text;}
	
	/**
	 * Sets the message to write.
	 * @param str The string to write.
	 * @throws NullPointerException Thrown if {@code str} is null.
	 */
	public void SetText(String str)
	{
		if(str == null)
			throw new NullPointerException();
		
		Text = str;
		return;
	}
	
	/**
	 * Obtains the current font.
	 */
	public Font GetFont()
	{return Font;}
	
	/**
	 * Sets the font.
	 * @param f The font to draw with.
	 * @throws NullPointerException Thrown if {@code f} is null.
	 */
	public void SetFont(Font f)
	{
		if(f == null)
			throw new NullPointerException();
		
		Font = f;
		return;
	}
	
	/**
	 * Obtains the current color.
	 */
	public Color GetColor()
	{return Color;}
	
	/**
	 * Sets the text color.
	 * @param c The color to draw with.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void SetColor(Color c)
	{
		if(c == null)
			throw new NullPointerException();
		
		Color = c;
		return;
	}
	
	/**
	 * The text to draw.
	 */
	protected String Text;
	
	/**
	 * The font to draw the text in.
	 */
	protected Font Font;
	
	/**
	 * The color to draw the text.
	 */
	protected Color Color;
	
	/**
	 * If true, we should display the text.
	 */
	protected boolean Display;
	
	/**
	 * If true, this component is initialized.
	 */
	protected boolean Initialized;
	
	/**
	 * If true, this component has been disposed of.
	 */
	protected boolean Disposed;
}
