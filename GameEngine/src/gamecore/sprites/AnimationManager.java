package gamecore.sprites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import gamecore.datastructures.maps.Dictionary;
import gamecore.datastructures.tuples.KeyValuePair;
import gamecore.gui.gamecomponents.AnimatedComponent;

/**
 * Manages a collection of animations.
 * @author Dawn Nye
 */
public class AnimationManager
{
	/**
	 * Creates a new animation manager.
	 * This will swap the animation of {@code ac} to the idle animation.
	 * <br><br>
	 * The animations are loaded from the {@code src} file.
	 * The file format is as follows with each parameter on its own line.
	 * <ul>
	 * 	<li>A positive integer {@code n} specifying the number of animations to load.</li>
	 * 	<li>The name of the idle animation.</li>
	 * 	<li>A series of {@code n} animation specifications.</li>
	 * 	<ul>
	 * 		<li>The name of the animation (by which code may reference it here).</li>
	 * 		<li>The path to the animation file (including the file name).</li>
	 * 	</ul>
	 * </ul>
	 * @param ac The animated component being managed.
	 * @param src The source file of the animations.
	 * @throws NullPointerException Thrown if {@code ac} or {@code src} is null.
	 * @throws FileNotFoundException Thrown if {@code src} is missing.
	 * @throws IOException Thrown if an error reading {@code src} occurs.
	 */
	public AnimationManager(AnimatedComponent ac, File src) throws IOException
	{
		Animator = ac;
		Animations = new Dictionary<String,Animation>();
		
		try(Scanner fin = new Scanner(src))
		{
			// Read the number of animations
			int n = Integer.parseInt(fin.nextLine());
			
			// Read the idle animation
			IdleAnimation = fin.nextLine();
			
			// Read in the animation names and sources
			for(int i = 0;i < n;i++)
				Animations.Put(fin.nextLine(),new Animation(new File(fin.nextLine()))); // We use put so that we never throw an error on duplicate names (that's not our problem)
		}
		
		ActiveAnimation = null;
		SwapAnimation(IdleAnimation);
		
		return;
	}
	
	/**
	 * Changes the active animation to {@code name}.
	 * @param name The name of the animation to switch to.
	 * @return Returns true if the animation could be swapped and false otherwise.
	 * @throws NullPointerException Thrown if {@code name} is null.
	 * @throws NoSuchElementException Thrown if the named animation is not an animation.
	 */
	public boolean SwapAnimation(String name)
	{
		if(!ContainsAnimation(name))
			return false;
		
		if(ContainsAnimation(GetActiveAnimation()))
			GetAnimation(GetActiveAnimation()).Stop();
		
		Animation a = GetAnimation(name);
		a.Play();
		
		ActiveAnimation = name;
		Animator.SwapAnimations(a);
		
		return true;
	}
	
	/**
	 * Obtains the idle animation.
	 * @throws NoSuchElementException Thrown if the idle animation is not an animation.
	 */
	public Animation GetIdleAnimation()
	{return Animations.Get(IdleAnimation);}
	
	/**
	 * Obtains the idle animation.
	 * @return Returns the idle animation or null if there is no idle animation.
	 */
	public Animation TryGetIdleAnimation()
	{
		KeyValuePair<String,Animation> p = Animations.TryGet(IdleAnimation);
		
		if(p == null)
			return null;
		
		return p.Item2;
	}
	
	/**
	 * Changes the idle animation to {@code name}.
	 * If {@code name} does not exist, then nothing will happen.
	 * @param name The new name of the idle animation.
	 * @return Returns true if the idle animation changed and false otherwise.
	 */
	public boolean ChangeIdleAnimation(String name)
	{
		if(!Animations.ContainsKey(name))
			return false;
		
		IdleAnimation = name;
		return true;
	}
	
	/**
	 * Obtains the named animation.
	 * @param name The name of the animation.
	 * @throws NoSuchElementException Thrown if the named animation is not an animation.
	 * @throws NullPointerException Thrown if {@code name} is null.
	 */
	public Animation GetAnimation(String name)
	{
		if(name == null)
			throw new NullPointerException();
		
		return Animations.Get(name);
	}
	
	/**
	 * Obtains the named animation.
	 * @param name The name of the animation.
	 * @return Returns the named animation or null if there is no such animation.
	 * @throws NullPointerException Thrown if {@code name} is null.
	 */
	public Animation TryGetAnimation(String name)
	{
		if(name == null)
			throw new NullPointerException();
		
		KeyValuePair<String,Animation> p = Animations.TryGet(IdleAnimation);
		
		if(p == null)
			return null;
		
		return p.Item2;
	}
	
	/**
	 * Renames an animation by the name of {@code o_name} to {@code n_name}.
	 * If there is no animation named {@code o_name} or there is an animation named {@code n_name}, then nothing will happen.
	 * This will also rename the idle animation if necessary.
	 * @param o_name The original name of the animation.
	 * @param n_name The new name of the animation.
	 * @return Returns true if this component was modified and false otherwise.
	 * @throws NullPointerException Thrown if {@code o_name} or {@code n_name} is null.
	 */
	public boolean RenameAnimation(String o_name, String n_name)
	{
		if(o_name == null || n_name == null)
			throw new NullPointerException();
		
		if(!Animations.ContainsKey(o_name) || !Animations.ContainsKey(n_name))
			return false;
		
		Animation a = GetAnimation(o_name);
		
		if(a == null)
			return false;
		
		if(!Animations.RemoveByKey(o_name))
			return false;
		
		if(!Animations.Add(n_name,a))
		{
			Animations.Add(o_name,a); // Try to put back the old name
			return false;
		}
		
		if(o_name.equals(IdleAnimation))
			IdleAnimation = n_name;
		
		return true;
	}
	
	/**
	 * Determines if this has an animation named {@code name}.
	 * @param name The name of the animation to search for.
	 * @return Returns true if this contains the named animation and false otherwise.
	 */
	public boolean ContainsAnimation(String name)
	{
		if(name == null)
			return false;
		
		return Animations.ContainsKey(name);
	}
	
	/**
	 * Returns the name of the active animation.
	 */
	public String GetActiveAnimation()
	{return ActiveAnimation;}
	
	/**
	 * Obtains a list of all the animations in this manager by name.
	 */
	public Iterable<String> Animations()
	{return Animations.Keys();}
	
	/**
	 * Determines the number of animations in this manager.
	 */
	public int Count()
	{return Animations.Count();}
	
	/**
	 * The thing that is being animated.
	 */
	protected final AnimatedComponent Animator;
	
	/**
	 * The animnations of being managed.
	 */
	protected final Dictionary<String,Animation> Animations;
	
	/**
	 * The idle animation name.
	 */
	protected String IdleAnimation;
	
	/**
	 * The active animation.
	 */
	protected String ActiveAnimation;
}