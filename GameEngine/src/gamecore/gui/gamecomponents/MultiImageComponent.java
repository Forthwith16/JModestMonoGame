package gamecore.gui.gamecomponents;

import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.stream.ImageInputStream;

import gamecore.datastructures.ArrayList;
import gamecore.sprites.SpriteSheet;

/**
 * A component that draws one of a selection of images.
 * This can be used for animation or for pure convenience.
 * @author Dawn Nye
 */
public class MultiImageComponent extends ImageComponent
{
	/**
	 * Creates an empty multiple image component.
	 */
	public MultiImageComponent()
	{
		super();
		
		Images = new ArrayList<Image>();
		img = null;
		
		SelectedIndex = -2;
		SetSelectedImage(-1);
		
		return;
	}
	
	/**
	 * Creates a multiple image component.
	 * The objects in {@code images} will be ignored if they are null or not amongst the following types: File, ImageInputStream, InputStream, URL, Image.
	 * Invalid images that can't be loaded will also be ignored.
	 * @param images The list of images to load.
	 * @throws NullPointerException Thrown if {@code images} is null.
	 */
	public MultiImageComponent(Iterable<Object> images)
	{
		super();
		Images = new ArrayList<Image>();
		
		for(Object obj : images)
		{
			Image add = LoadImage(obj);
			
			if(add != null)
				Images.add(add);
		}
		
		SelectedIndex = -1;
		SetSelectedImage(Images.size() > 0 ? 0 : -1);
		
		return;
	}
	
	/**
	 * Creates a multiple image component.
	 * @param ss The source of the images to add to the component.
	 * @throws NullPointerException Thrown if {@code ss} is null.
	 */
	public MultiImageComponent(SpriteSheet ss)
	{
		super();
		Images = new ArrayList<Image>();
		
		for(int i = 0;i < ss.Count();i++)
			Images.add(ss.GetSprite(i));
		
		SelectedIndex = -1;
		SetSelectedImage(Images.size() > 0 ? 0 : -1);
		
		return;
	}
	
	/**
	 * Creates a multiple image component using the same images found in {@code img}.
	 * The selected image is initialized to no selected image.
	 * This constructor is more efficient than others for duplicate image loads, as it shares the image objects.
	 * @param img The source of the images.
	 */
	public MultiImageComponent(MultiImageComponent img)
	{
		super();
		
		Images = new ArrayList<Image>(img.Images);
		img = null;
		
		SelectedIndex = -2;
		SetSelectedImage(-1);
		
		return;
	}
	
	/**
	 * Sets the displayed image.
	 * If the component was hidden, this causes it to become displayed (although if {@code index} is out of bounds, still no image is drawn).
	 * @param index The index to display. This value can be negative or greater than the number of available images, in which case no image is displayed.
	 */
	public void SetSelectedImage(int index)
	{
		if(index >= 0 && index < Images.size())
			img = Images.get(index);
		else
			img = null;
		
		SelectedIndex = index;
		return;
	}
	
	/**
	 * Determines the selected image.
	 * @return Returns the selected image index.
	 */
	public int GetSelectedImage()
	{return SelectedIndex;}
	
	/**
	 * Shows the component's image.
	 */
	public void Show()
	{
		SetSelectedImage(SelectedIndex);
		return;
	}
	
	/**
	 * Hides the component's image.
	 */
	public void Hide()
	{
		img = null;
		return;
	}
	
	/**
	 * Adds an image to the component.
	 * @param path The local path to the image.
	 * @return Returns true if the image was added and false otherwise.
	 */
	public boolean AddImage(File path)
	{return AddImage((Object)path);}
	
	/**
	 * Adds an image to the component.
	 * @param path The local path to the image.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	public boolean AddImage(File path, int index)
	{return AddImage((Object)path,index);}
	
	/**
	 * Adds an image to the component.
	 * @param stream The image source.
	 * @return Returns true if the image was added and false otherwise.
	 */
	public boolean AddImage(ImageInputStream stream)
	{return AddImage((Object)stream);}
	
	/**
	 * Adds an image to the component.
	 * @param stream The image source.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	public boolean AddImage(ImageInputStream stream, int index)
	{return AddImage((Object)stream,index);}
	
	/**
	 * Adds an image to the component.
	 * @param stream The image source.
	 * @return Returns true if the image was added and false otherwise.
	 */
	public boolean AddImage(InputStream stream)
	{return AddImage((Object)stream);}
	
	/**
	 * Adds an image to the component.
	 * @param stream The image source.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	public boolean AddImage(InputStream stream, int index)
	{return AddImage((Object)stream,index);}
	
	/**
	 * Adds an image to the component.
	 * @param url The image source.
	 * @return Returns true if the image was added and false otherwise.
	 */
	public boolean AddImage(URL url)
	{return AddImage((Object)url);}
	
	/**
	 * Adds an image to the component.
	 * @param url The image source.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	public boolean AddImage(URL url, int index)
	{return AddImage((Object)url,index);}
	
	/**
	 * Adds an image to the component.
	 * @param image The image to add.
	 * @return Returns true if the image was added and false otherwise.
	 */
	public boolean AddImage(Image image)
	{return AddImage((Object)image);}
	
	/**
	 * Adds an image to the component.
	 * @param image The image to add.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	public boolean AddImage(Image image, int index)
	{return AddImage((Object)image,index);}
	
	/**
	 * Adds an image to the component.
	 * @param src The image source.
	 * @return Returns true if the image was added and false otherwise.
	 */
	protected boolean AddImage(Object src)
	{
		Image img = LoadImage(src);
		
		if(img == null)
			return false;
		
		return Images.add(img);
	}
	
	/**
	 * Adds an image to the component.
	 * @param src The image source.
	 * @param index The index the image should have in the list of images.
	 * @return Returns true if the image was added and false otherwise.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than the number of images in the component.
	 */
	protected boolean AddImage(Object src, int index)
	{
		if(index < 0 || index > Images.size())
			throw new IndexOutOfBoundsException();
		
		Image img = LoadImage(src);
		
		if(img == null)
			return false;
		
		Images.add(index,img);
		
		if(SelectedIndex >= index)
			SelectedIndex++;
		
		return true;
	}
	
	/**
	 * Gets the image at {@code index}.
	 * @param index The index of the image to fetch.
	 * @return Retruns the image at index {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least the number of images in the component.
	 */
	public Image GetImage(int index)
	{return Images.get(index);}
	
	/**
	 * Removes the image at {@code index}.
	 * @param index The index of the image to remove.
	 * @return Returns the image at index {@code index}.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least the number of images in the component.
	 */
	public Image Remove(int index)
	{
		Image ret = Images.remove(index);
		
		if(index == SelectedIndex)
			SetSelectedImage(-1);
		else if(index > SelectedIndex)
			SelectedIndex--;
		
		return ret;
	}
	
	/**
	 * Determines the number of images in the component.
	 * @return Returns the number of images in the component.
	 */
	public int ImageCount()
	{return Images.size();}
	
	/**
	 * Removes all iamges from the component.
	 */
	public void ClearImages()
	{
		Images.clear();
		SetSelectedImage(-1);
		
		return;
	}
	
	/**
	 * The images we can display.
	 */
	protected ArrayList<Image> Images;
	
	/**
	 * The current image to display.
	 * If this value is out of bounds, no image is display and no error is thrown.
	 */
	protected int SelectedIndex;
}
