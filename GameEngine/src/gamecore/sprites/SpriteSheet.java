package gamecore.sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.IllegalFormatException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import gamecore.datastructures.ArrayList;
import gamecore.datastructures.CellRectangle;
import gamecore.datastructures.Dictionary;
import gamecore.datastructures.LinkedList;
import gamecore.datastructures.vectors.Vector2i;

/**
 * Creates a collection of images from a sprite sheet.
 * Each file is only loaded once (per tile dimensions) to improve runtimes so that a sprite sheet may be quickly obtained multiple times.
 * In addition, the sprites all share the same backing image data to save space.
 * @author Dawn Nye
 */
public class SpriteSheet
{
	/**
	 * Retrieves an already loaded sprite sheet called {@code name}.
	 * @param name The name of the sprite sheet to load.
	 * @return Returns the loaded sprite sheet with name {@code name}.
	 * @throws NoSuchElementException Thrown if {@code name} is not a loaded sprite sheet.
	 * @throws NullPointerException Thrown if {@code name} is null.
	 */
	public static SpriteSheet LoadSprites(String name)
	{return LoadedSprites.Get(name);}
	
	/**
	 * Loads a sprite sheet from a sprite sheet file specification.
	 * The details of the file format are as follows, with each entry occurring on its own line in the file.
	 * <ul>
	 * 	<li>A local path to a sprite sheet (the file name will be used as the sprite sheet name).</li>
	 * 	<li>A boolean value (true/false) specifying if the tile dimensions of the sprite sheet are given as a constant (true) or if they are variable (false).</li>
	 * 	<li>
	 * 		If the tile dimensions are given as a constant, then two integers should appear on the same line seperated by whitespace.
	 * 		The first is the x axis tiling dimension.
	 * 		The second is the y axis tiling dimension.
	 * 		Both values must be at least 1.
	 * 		<br><br>
	 * 		If the time dimensions are variable, then the following appear with each entry occurring on its own line.
	 * 		<ul>
	 * 			<li>An integer specifying the number of sprites.</li>
	 * 			<li>
	 * 				For each sprite, four values on a single line seperated by white space.
	 * 				The first is an integer specifying the left x position of the sprite in the sprite sheet. This value is permitted to be out of bounds.
	 * 				The second is an integer specifying the top y position of the sprite in the sprite sheet. This value is permitted to be out of bounds.
	 * 				The third is an integer specifying the width of the sprite. This value must be at least 1.
	 * 				The fourth is an integer specifying the height of the sprite. This value must be at least 1.
	 * 			</li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 * @param source The sprite sheet file.
	 * @throws FileNotFoundException Thrown if {@code source} does not exist or if the sprite sheet it specifies does not exist.
	 * @throws IllegalFormatException Thrown if {@code source} is not a properly formatted sprite sheet file.
	 * @throws IOException Thrown if an error is encountered while reading {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public static SpriteSheet LoadSprites(File source) throws IOException
	{
		// Initial error checks
		if(source == null)
			throw new NullPointerException();
		
		if(!source.exists())
			throw new FileNotFoundException();
		
		// Load the file
		Scanner fin = new Scanner(source);
		
		// Load the sprite sheet path
		if(!fin.hasNextLine())
			throw new IOException();
		
		String sprite_path = fin.nextLine();
		File sprite_sheet = new File(sprite_path);
		
		if(!sprite_sheet.exists())
			throw new FileNotFoundException();
		
		// Determine if we have constant tiling
		if(!fin.hasNextBoolean())
			throw new IOException();
		
		boolean constant_tiling = fin.nextBoolean();
		
		// If we have constant tiling, life is easy
		if(constant_tiling)
		{
			if(!fin.hasNextInt())
				throw new IOException();
			
			int x_dim = fin.nextInt();
			
			if(x_dim < 1 || !fin.hasNextInt())
				throw new IOException();
			
			int y_dim = fin.nextInt();
			
			if(y_dim < 1)
				throw new IOException();
			
			fin.close();
			return SpriteSheet.LoadSprites(sprite_sheet.getName(),sprite_sheet,x_dim,y_dim);
		}
		else // In this case, we have to load each sprite rectangle seperately
		{
			// First get the number of sprites
			if(!fin.hasNextInt())
				throw new IOException();
			
			int num_sprites = fin.nextInt();
			
			// For each sprite, load its rectangle
			LinkedList<CellRectangle> sprites = new LinkedList<CellRectangle>();
			
			for(int i = 0;i < num_sprites;i++)
			{
				if(!fin.hasNextInt())
					throw new IOException();
				
				int left = fin.nextInt();
				
				if(!fin.hasNextInt())
					throw new IOException();
				
				int top = fin.nextInt();
				
				if(!fin.hasNextInt())
					throw new IOException();
				
				int width = fin.nextInt();
				
				if(width < 1 || !fin.hasNextInt())
					throw new IOException();
				
				int height = fin.nextInt();
				
				if(height < 1)
					throw new IOException();
				
				sprites.add(new CellRectangle(left,top,width,height));
			}
			
			fin.close();
			return SpriteSheet.LoadSprites(sprite_sheet.getName(),sprite_sheet,sprites);
		}
	}
	
	/**
	 * Loads a sprite sheet from {@code file} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param file The local path to the image source.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code file} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, File file, int tx, int ty)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,file,tx,ty));
	}
	
	/**
	 * Loads a sprite sheet from {@code file} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param file The local path to the image source.
	 * @param tdim The tile dimensions.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code file} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, File file, Vector2i tdim)
	{return LoadSprites(name,file,tdim.X,tdim.Y);}
	
	/**
	 * Loads a sprite sheet from {@code file} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param file The local path to the image source.
	 * @param tdims The tile dimensions. Each rectangle specifies a source rectangle from the image. Null entires will be ignored, and out of bounds rectangles are not wrapped around. Empty images are discarded.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code file} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, File file, Iterable<CellRectangle> tdims)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,file,tdims));
	}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, ImageInputStream stream, int tx, int ty)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,stream,tx,ty));
	}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tdim The tile dimensions.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, ImageInputStream stream, Vector2i tdim)
	{return LoadSprites(name,stream,tdim.X,tdim.Y);}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tdims The tile dimensions. Each rectangle specifies a source rectangle from the image. Null entires will be ignored, and out of bounds rectangles are not wrapped around. Empty images are discarded.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, ImageInputStream stream, Iterable<CellRectangle> tdims)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,stream,tdims));
	}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, InputStream stream, int tx, int ty)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,stream,tx,ty));
	}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tdim The tile dimensions.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, InputStream stream, Vector2i tdim)
	{return LoadSprites(name,stream,tdim.X,tdim.Y);}
	
	/**
	 * Loads a sprite sheet from {@code stream} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param stream The image source.
	 * @param tdims The tile dimensions. Each rectangle specifies a source rectangle from the image. Null entires will be ignored, and out of bounds rectangles are not wrapped around. Empty images are discarded.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code stream} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, InputStream stream, Iterable<CellRectangle> tdims)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,stream,tdims));
	}
	
	/**
	 * Loads a sprite sheet from {@code url} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param url The remote path to the image source.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code url} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, URL url, int tx, int ty)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,url,tx,ty));
	}
	
	/**
	 * Loads a sprite sheet from {@code url} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param url The remote path to the image source.
	 * @param tdim The tile dimensions.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code url} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, URL url, Vector2i tdim)
	{return LoadSprites(name,url,tdim.X,tdim.Y);}
	
	/**
	 * Loads a sprite sheet from {@code url} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param url The remote path to the image source.
	 * @param tdims The tile dimensions. Each rectangle specifies a source rectangle from the image. Null entires will be ignored, and out of bounds rectangles are not wrapped around. Empty images are discarded.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code url} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, URL url, Iterable<CellRectangle> tdims)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		return FinishLoading(new SpriteSheet(name,url,tdims));
	}
	
	/**
	 * Loads a sprite sheet from {@code src} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param src The source image.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code url} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, BufferedImage src, int tx, int ty)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		// This may not trigger during the construction process in edge cases, so do it now
		if(src == null)
			throw new NullPointerException();
		
		return FinishLoading(new SpriteSheet(name,src,tx,ty));
	}
	
	/**
	 * Loads a sprite sheet from {@code src} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param src The source image.
	 * @param tdim The tile dimensions.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code url} if it exists, or null if no sprite sheet could be created.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, BufferedImage src, Vector2i tdim)
	{return LoadSprites(name,src,tdim.X,tdim.Y);}
	
	/**
	 * Loads a sprite sheet from {@code src} and gives it the name {@code name}.
	 * The image is tiled into sub image sprites according to the provided tile dimensions.
	 * @param name The name to give the sprite sheet. If a sprite sheet with this name is already loaded, this simply returns that instead.
	 * @param src The source image.
	 * @param tdims The tile dimensions. Each rectangle specifies a source rectangle from the image. Null entires will be ignored, and out of bounds rectangles are not wrapped around. Empty images are discarded.
	 * @return Returns a sprite sheet by the name of {@code name} if it already exists, then a sprite sheet created from {@code src}.
	 * @throws NullPointerException Thrown if {@code name} is not a loaded sprite sheet and any parameter is null.
	 */
	public static SpriteSheet LoadSprites(String name, BufferedImage src, Iterable<CellRectangle> tdims)
	{
		if(LoadedSprites.ContainsKey(name))
			return LoadedSprites.Get(name);
		
		// This may not trigger during the construction process in edge cases, so do it now
		if(src == null)
			throw new NullPointerException();
		
		return FinishLoading(new SpriteSheet(name,src,tdims));
	}
	
	/**
	 * Finishes loading a new sprite sheet.
	 * @param ss The new sprite sheet.
	 * @return Returns the new sprite sheet if it is valid and null otherwise.
	 * @throws NullPointerException Thrown if {@code ss} is null or {@code ss}'s name is null.
	 */
	protected static SpriteSheet FinishLoading(SpriteSheet ss)
	{
		if(ss.IsValid())
			LoadedSprites.Add(ss.Name(),ss);
		else
			return null;
		
		return ss;
	}
	
	/**
	 * Creates a sprite sheet from {@code src} with tile dimensions {@code tx} by {@code ty}.
	 * @param name The name to give the sprite sheet.
	 * @param src The image source.
	 * @param tx The tile width.
	 * @param ty The tile height.
	 * @throws NullPointerException Thrown if any parameter is null.
	 */
	protected SpriteSheet(String name, Object src, int tx, int ty)
	{
		SharedConstruction(name,src);
		
		for(int y = 0;y <= Source.getHeight() - ty;y += ty)
			for(int x = 0;x <= Source.getWidth() - tx;x += tx)
				Sprites.add(Source.getSubimage(x,y,tx,ty));
		
		return;
	}
	
	/**
	 * Creates a sprite sheet from {@code src} with tile dimensions specified by {@code tdims}.
	 * @param name The name to give the sprite sheet.
	 * @param src The image source.
	 * @param tdims A sequence of tile positions and dimensions to cut {@code src} up into. These are permitted to overlap. Null entries are ignored, and out of bounds coordiantes do not wrap around. Empty images are discarded.
	 * @throws NullPointerException Thrown if any parameter is null.
	 */
	protected SpriteSheet(String name, Object src, Iterable<CellRectangle> tdims)
	{
		SharedConstruction(name,src);
		CellRectangle bounds = new CellRectangle(0,0,Source.getWidth(),Source.getHeight());
		
		for(CellRectangle rect : tdims)
			if(rect != null)
			{
				CellRectangle common = bounds.Intersection(rect);
				
				if(!common.IsEmpty())
					Sprites.add(Source.getSubimage(common.Left(),common.Top(),common.Width(),common.Height()));
			}
		
		return;
	}
	
	/**
	 * Performs common construciton code.
	 * @param name The name of the sprite sheet being created.
	 * @param src The image source.
	 */
	protected void SharedConstruction(String name, Object src)
	{
		Source = LoadImage(src);
		Name = name;
		
		Sprites = new ArrayList<BufferedImage>();
		return;
	}
	
	/**
	 * Loads an image from an unspecified source.
	 * @param src The image source.
	 * @return Returns the image if it could be loaded from the {@code src} and null otherwise.
	 */
	protected BufferedImage LoadImage(Object src)
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
		else if(src instanceof BufferedImage)
			return (BufferedImage)src;
		
		return null;
	}
	
	/**
	 * Loads an image from a local path.
	 * @param path The image path.
	 * @return Returns the image or null if there was an error in the image creation process.
	 */
	protected BufferedImage LoadImage(File path)
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
	protected BufferedImage LoadImage(ImageInputStream stream)
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
	protected BufferedImage LoadImage(InputStream stream)
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
	protected BufferedImage LoadImage(URL url)
	{
		try
		{return ImageIO.read(url);}
		catch(IOException e)
		{}
		
		return null;
	}
	
	/**
	 * Gets the sprite at index {@code index}.
	 * @param index The index of the sprite to obtain.
	 * @return Returns the {@code index}th sprite.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least Count().
	 */
	public BufferedImage GetSprite(int index)
	{return Sprites.get(index);}
	
	/**
	 * Determines the number of sprites in the sprite sheet.
	 * @return Returns the number of sprites in the sprite sheet.
	 */
	public int Count()
	{return Sprites.size();}
	
	/**
	 * Determines if the sprite sheet is valid.
	 * @return Returns true if the sprite sheet is valid and false otherwise.
	 */
	protected boolean IsValid()
	{return Source != null;}
	
	/**
	 * Determines the name of the sprite sheet.
	 * @return Returns the name of the sprite sheet.
	 */
	public String Name()
	{return Name;}
	
	/**
	 * The name of the sprite sheet.
	 */
	protected String Name;
	
	/**
	 * The source image for the sprite sheet.
	 */
	protected BufferedImage Source;
	
	/**
	 * The sprites of this sprite sheet.
	 */
	protected ArrayList<BufferedImage> Sprites;
	
	/**
	 * The collection of already loaded sprite sheets.
	 */
	protected static Dictionary<String,SpriteSheet> LoadedSprites = new Dictionary<String,SpriteSheet>();
}
