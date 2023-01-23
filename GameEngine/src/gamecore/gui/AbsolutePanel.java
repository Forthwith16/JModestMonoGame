package gamecore.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.UIManager;

import gamecore.datastructures.vectors.Vector2i;

/**
 * Allows a user to do basic positioning when adding things to a panel like a sane person.
 * Also kills the layout manager so that we can position things where we want them to be instead of where swing thinks they should be but never are.
 * Also has other cool properties explicitly identified so that they are not buried in Java's swing library.
 * @author Dawn Nye
 */
public class AbsolutePanel extends JPanel
{
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 */
	public AbsolutePanel(int w, int h)
	{
		this(0,0,w,h,UIManager.getColor("Panel.background"));
		return;
	}
	
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * Also intializes the background color of this panel to {@code bg}.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 * @param bg The background color of this panel.
	 */
	public AbsolutePanel(int w, int h, Color bg)
	{
		this(0,0,w,h,bg);
		return;
	}
	
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * It is positioned realative to its parent at {@code v}.
	 * @param v The relative position to the panel's parent.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 */
	public AbsolutePanel(Vector2i v, int w, int h)
	{
		this(v.X,v.Y,w,h,UIManager.getColor("Panel.background"));
		return;
	}
	
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * It is positioned realative to its parent at ({@code x},{@code y}).
	 * @param x The relative x position to the panel's parent.
	 * @param y The relative y position to the panel's parent.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 */
	public AbsolutePanel(int x, int y, int w, int h)
	{
		this(x,y,w,h,UIManager.getColor("Panel.background"));
		return;
	}
	
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * It is positioned realative to its parent at {@code v}.
	 * Also intializes the background color of this panel to {@code bg}.
	 * @param v The relative position to the panel's parent.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 * @param bg The background color of this panel.
	 */
	public AbsolutePanel(Vector2i v, int w, int h, Color bg)
	{
		this(v.X,v.Y,w,h,bg);
		return;
	}
	
	/**
	 * Creates an absolute panel that has width {@code w} and height {@code h}.
	 * It is positioned realative to its parent at ({@code x},{@code y}).
	 * Also intializes the background color of this panel to {@code bg}.
	 * @param x The relative x position to the panel's parent.
	 * @param y The relative y position to the panel's parent.
	 * @param w The width of the panel.
	 * @param h The height of the panel.
	 * @param bg The background color of this panel.
	 */
	public AbsolutePanel(int x, int y, int w, int h, Color bg)
	{
		super(true);
		
		setBounds(x,y,w,h);
		setBackground(bg);
		setLayout(null);
		
		return;
	}
	
	/**
	 * Adds a component to this panel at the specified position.
	 * @param c The component to add.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 */
	public void add(Component c, int x, int y)
	{
		add(c,x,y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this panel at the specified position.
	 * @param c The component to add.
	 * @param v The position to add the component at.
	 */
	public void add(Component c, Vector2i v)
	{
		add(c,v.X,v.Y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this panel with the specified bounds.
	 * @param c The component to add.
	 * @param v The position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 */
	public void add(Component c, Vector2i v, int w, int h)
	{
		add(c,v.X,v.Y,w,h);
		return;
	}
	
	/**
	 * Adds a component to this panel with the specified bounds.
	 * @param c The component to add.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 */
	public void add(Component c, int x, int y, int w, int h)
	{
		c.setBounds(x,y,w,h);
		add(c);
		
		return;
	}
}