package gamecore.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JFrame;

import gamecore.datastructures.vectors.Vector2i;
import gamecore.input.KeyboardStateMonitor;

/**
 * Allows a user to do basic positioning when adding things to a frame like a sane person.
 * Also kills the layout manager so that we can position things where we want them to be instead of where swing thinks they should be but never are.
 * Also has other cool properties explicitly identified so that they are not buried in Java's swing library.
 * @author Dawn Nye
 */
public class AbsoluteFrame extends JFrame
{
	/**
	 * Creates an absolute frame with dimensions specified by {@code dim}
	 * @param title The title of the frame.
	 * @param icon The frame icon. If this value is null, it is ignored.
	 * @param dim The dimensions of the frame.
	 * @throws NullPointerException Thrown if {@code dim} is null.
	 */
	public AbsoluteFrame(String title, Image icon, Vector2i dim)
	{
		this(title,icon,dim.X,dim.Y);
		return;
	}
	
	/**
	 * Creates an absolute frame with dimensions {@code w} by {@code h}.
	 * @param title The title of the frame.
	 * @param icon The frame icon. If this value is null, it is ignored.
	 * @param w The width of the frame.
	 * @param h The height of the frame.
	 */
	public AbsoluteFrame(String title, Image icon, int w, int h)
	{
		this(title,icon,w,h,new Color(100,149,237)); // The default background color should be cornflower blue
		return;
	}
	
	/**
	 * Creates an absolute frame with dimensions specified by {@code dim}
	 * @param title The title of the frame.
	 * @param icon The frame icon. If this value is null, it is ignored.
	 * @param dim The dimensions of the frame.
	 * @param bgc The background color of the frame. If this value is null, cornflower blue is used as a default.
	 * @throws NullPointerException Thrown if {@code dim} is null.
	 */
	public AbsoluteFrame(String title, Image icon, Vector2i dim, Color bgc)
	{
		this(title,icon,dim.X,dim.Y,bgc);
		return;
	}
	
	/**
	 * Creates an absolute frame with dimensions {@code w} by {@code h}.
	 * @param title The title of the frame.
	 * @param icon The frame icon. If this value is null, it is ignored.
	 * @param w The width of the frame.
	 * @param h The height of the frame.
	 * @param bgc The background color of the frame. If this value is null, cornflower blue is used as a default.
	 */
	public AbsoluteFrame(String title, Image icon, int w, int h, Color bgc)
	{
		setTitle(title);
		
		if(icon != null)
			setIconImage(icon);
		
		setSize(w,h);
		setResizable(false);
		
		getContentPane().setLayout(null);
		getContentPane().setBackground(bgc == null ? new Color(100,149,237) : bgc);
		
		addKeyListener(KeyboardStateMonitor.GetMonitor());
		setFocusTraversalKeysEnabled(false);
		
		getContentPane().addKeyListener(KeyboardStateMonitor.GetMonitor());
		getContentPane().setFocusTraversalKeysEnabled(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		return;
	}
	
	/**
	 * Forces the frame to draw.
	 */
	public void Repaint()
	{
		getContentPane().repaint();
		return;
	}
	
	/**
	 * Adds a component to this frame.
	 * @param c The component to add.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponent(Component c)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		getContentPane().add(c);
		return;
	}
	
	/**
	 * Adds a component to this frame.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} is null. 
	 */
	public void AddComponent(Component c, int index)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		getContentPane().add(c,index);
		return;
	}
	
	/**
	 * Adds a component to this frame at the specified position.
	 * @param c The component to add.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponent(Component c, int x, int y)
	{
		AddComponent(c,x,y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this frame at the specified position.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponent(Component c, int index, int x, int y)
	{
		AddComponent(c,index,x,y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this frame at the specified position.
	 * @param c The component to add.
	 * @param v The position to add the component at.
	 * @throws NullPointerException Thrown if {@code c} or {@code v} is null.
	 */
	public void AddComponent(Component c, Vector2i v)
	{
		AddComponent(c,v.X,v.Y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this frame at the specified position.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @param v The position to add the component at.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} or {@code v} is null.
	 */
	public void AddComponent(Component c, int index, Vector2i v)
	{
		AddComponent(c,index,v.X,v.Y,c.getWidth(),c.getHeight());
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param v The position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 * @throws NullPointerException Thrown if {@code c} or {@code v} is null.
	 */
	public void AddComponent(Component c, Vector2i v, int w, int h)
	{
		AddComponent(c,v.X,v.Y,w,h);
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @param v The position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} or {@code v} is null.
	 */
	public void AddComponent(Component c, int index, Vector2i v, int w, int h)
	{
		AddComponent(c,index,v.X,v.Y,w,h);
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponent(Component c, int x, int y, int w, int h)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		c.setBounds(x,y,w,h);
		getContentPane().add(c);
		
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @param x The x position to add the component at.
	 * @param y The y position to add the component at.
	 * @param w The width of the component.
	 * @param h The height of the component.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponent(Component c, int index, int x, int y, int w, int h)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		c.setBounds(x,y,w,h);
		getContentPane().add(c,index);
		
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param rect The bounds of the component.
	 * @throws NullPointerException Thrown if {@code c} or {@code rect} is null.
	 */
	public void AddComponent(Component c, Rectangle rect)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		c.setBounds(rect);
		getContentPane().add(c);
		
		return;
	}
	
	/**
	 * Adds a component to this frame with the specified bounds.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @param rect The bounds of the component.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} or {@code rect} is null.
	 */
	public void AddComponent(Component c, int index, Rectangle rect)
	{
		c.addKeyListener(KeyboardStateMonitor.GetMonitor());
		c.setFocusTraversalKeysEnabled(false);
		
		c.setBounds(rect);
		getContentPane().add(c,index);
		
		return;
	}
	
	/**
	 * Adds a component to this frame.
	 * The component's bounds will be set to this frame's bounds.
	 * @param c The component to add.
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponentFrameBounded(Component c)
	{
		AddComponent(c,getContentPane().getBounds());
		return;
	}
	
	/**
	 * Adds a component to this frame.
	 * The component's bounds will be set to this frame's bounds.
	 * @param c The component to add.
	 * @param index The position at which to insert the component, or -1 to append the component to the end.
	 * @throws IllegalArgumentException Thrown if {@code index} is invalid; if {@code c} is a child of this container, the valid range is [-1,getComponentCount() - 1]; if component is not a child of this container, the valid range is [-1,getComponentCount()].
	 * @throws NullPointerException Thrown if {@code c} is null.
	 */
	public void AddComponentFrameBounded(Component c, int index)
	{
		AddComponent(c,index,getContentPane().getBounds());
		return;
	}
}
