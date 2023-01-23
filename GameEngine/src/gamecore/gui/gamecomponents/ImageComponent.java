package gamecore.gui.gamecomponents;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import gamecore.IDrawable;

/**
 * A component that draws an image.
 * @author Dawn Nye
 */
public class ImageComponent extends AffineComponent implements IDrawable
{
	/**
	 * Creates an empty image component.
	 */
	protected ImageComponent()
	{
		super();
		img = null;
		
		return;
	}
	
	/**
	 * Creates a component which draws an image.
	 * @param path The local path to the image.
	 * @throws NullPointerException Thrown if {@code path} is null.
	 */
	public ImageComponent(File path)
	{
		super();
		
		if(path == null)
			throw new NullPointerException();
		
		img = LoadImage(path);
		
		if(img != null)
			SetDimensionsToImage();
		
		return;
	}
	
	/**
	 * Creates a component which draws an image.
	 * @param stream The source data for the image.
	 * @throws NullPointerException Thrown if {@code stream} is null.
	 */
	public ImageComponent(ImageInputStream stream)
	{
		super();
		
		if(stream == null)
			throw new NullPointerException();
		
		img = LoadImage(stream);
		
		if(img != null)
			SetDimensionsToImage();
		
		return;
	}
	
	/**
	 * Creates a component which draws an image.
	 * @param stream The source data for the image.
	 * @throws NullPointerException Thrown if {@code stream} is null.
	 */
	public ImageComponent(InputStream stream)
	{
		super();
		
		if(stream == null)
			throw new NullPointerException();
		
		img = LoadImage(stream);
		
		if(img != null)
			SetDimensionsToImage();
		
		return;
	}
	
	/**
	 * Creates a component which draws an image.
	 * @param url The external path to the image.
	 * @throws NullPointerException Thrown if {@code url} is null.
	 */
	public ImageComponent(URL url)
	{
		super();
		
		if(url == null)
			throw new NullPointerException();
		
		img = LoadImage(url);
		
		if(img != null)
			SetDimensionsToImage();
		
		return;
	}
	
	/**
	 * Creates a component which draws an image.
	 * @param img The image to draw.
	 */
	public ImageComponent(Image img)
	{
		super();
		
		this.img = img;
		
		if(this.img != null)
			SetDimensionsToImage();
		
		return;
	}
	
	/**
	 * Loads an image from an unspecified source.
	 * @param src The image source.
	 * @return Returns the image if it could be loaded from the {@code src} and null otherwise.
	 */
	protected Image LoadImage(Object src)
	{
		if(src == null)
			return null;
		
		if(src instanceof File)
			return LoadImage((File)src);
		else if(src instanceof ImageInputStream)
			return LoadImage((ImageInputStream)src);
		else if(src instanceof InputStream)
			return LoadImage((InputStream)src);
		else if(src instanceof URL)
			return LoadImage((URL)src);
		else if(src instanceof Image)
			return (Image)src;
		
		return null;
	}
	
	/**
	 * Loads an image from a local path.
	 * @param path The image path.
	 * @return Returns the image or null if there was an error in the image creation process.
	 */
	protected Image LoadImage(File path)
	{
		try
		{return ImageIO.read(path);}
		catch(IOException e)
		{}
		
		return null;
	}
	
	/**
	 * Loads an image from an input stream.
	 * @param stream The source data for the image.
	 * @return Returns the image or null if there was an error in the image creation process.
	 */
	protected Image LoadImage(ImageInputStream stream)
	{
		try
		{return ImageIO.read(stream);}
		catch(IOException e)
		{}
		
		return null;
	}
	
	/**
	 * Loads an image from an input stream.
	 * @param stream The source data for the image.
	 * @return Returns the image or null if there was an error in the image creation process.
	 */
	protected Image LoadImage(InputStream stream)
	{
		try
		{return ImageIO.read(stream);}
		catch(IOException e)
		{}
		
		return null;
	}
	
	/**
	 * Loads an image from a url.
	 * @param url The image path.
	 * @return Returns the image or null if there was an error in the image creation process.
	 */
	protected Image LoadImage(URL url)
	{
		try
		{return ImageIO.read(url);}
		catch(IOException e)
		{}
		
		return null;
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
	
	/**
	 * Sets the dimensions of this component.
	 * @param w The width.
	 * @param h The height.
	 * @throws IllegalArgumentException Thrown if {@code w} or {@code h} is nonpositive.
	 */
	public void SetDimensions(int w, int h)
	{
		if(w < 1 || h < 1)
			throw new IllegalArgumentException();
		
		// Only change the bounds if necessary to perhaps save time with swing
		if(w != getWidth() || h != getHeight())
			setBounds(getX(),getY(),w,h);
		
		return;
	}
	
	/**
	 * Sets the component's dimensions to its image's dimensions.
	 * @throws IllegalStateException Thrown if the image was not loaded correctly.
	 */
	public void SetDimensionsToImage()
	{
		if(img == null)
			throw new IllegalStateException();
		
		SetDimensions(img.getWidth(null),img.getHeight(null));
		return;
	}
	
	@Override public void paint(Graphics g)
	{
		if(img != null)
			((Graphics2D)g).drawImage(img,GetTransformation(true),null);
		
		super.paint(g);
		return;
	}
	
	/**
	 * The image to draw.
	 */
	protected Image img;
	
	/**
	 * If true, this component is initialized.
	 */
	protected boolean Initialized;
	
	/**
	 * If true, this component has been disposed of.
	 */
	protected boolean Disposed;
}
