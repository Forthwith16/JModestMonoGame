package gamecore;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.NoSuchElementException;

import gamecore.LINQ.LINQ;
import gamecore.datastructures.Dictionary;
import gamecore.datastructures.LinkedList;
import gamecore.datastructures.vectors.Vector2i;
import gamecore.gui.AbsoluteFrame;

/**
 * The game engine that runs the game's main loop logic.  
 * @author Dawn Nye
 */
public abstract class GameEngine implements Runnable
{
	/**
	 * Constructs a new game engine.
	 * @param title The title of the window.
	 * @param icon The icon of the window.
	 * @param width The width of the window.
	 * @param height The height of the window.
	 */
	protected GameEngine(String title, Image icon, int width, int height)
	{
		this(title,icon,width,height,30,null);
		return;
	}
	
	/**
	 * Constructs a new game engine.
	 * @param title The title of the window.
	 * @param icon The icon of the window.
	 * @param width The width of the window.
	 * @param height The height of the window.
	 * @param bgc The background color of the window.
	 */
	protected GameEngine(String title, Image icon, int width, int height, Color bgc)
	{
		this(title,icon,width,height,30,bgc);
		return;
	}
	
	/**
	 * Constructs a new game engine.
	 * @param title The title of the window.
	 * @param icon The icon of the window.
	 * @param width The width of the window.
	 * @param height The height of the window.
	 * @param fps The target (update) frames per second.
	 */
	protected GameEngine(String title, Image icon, int width, int height, int fps)
	{
		this(title,icon,width,height,fps,null);
		return;
	}
	
	/**
	 * Constructs a new game engine.
	 * @param title The title of the window.
	 * @param icon The icon of the window.
	 * @param width The width of the window.
	 * @param height The height of the window.
	 * @param fps The target (update) frames per second.
	 * @param bgc The background color of the window.
	 */
	protected GameEngine(String title, Image icon, int width, int height, int fps, Color bgc)
	{
		Game = this;
		Window = new AbsoluteFrame(title,icon,width,height,bgc);
		
		Initialized = false;
		Initializing = false;
		
		Continue = true;
		Paused = false;
		PauseExitCondition = null;
		Frame = 0;
		
		Finished = false;
		Finishing = false;
		
		GameComponents = new LinkedList<IUpdatable>();
		Services = new Dictionary<Class,Object>();
		
		FPS = fps;
		AllottedATPF = (long)(1000.0 / FPS); // 1000 ms / s * 1/FPS s / frames
		TickDeficit = 0L;
		TimeStamp = -1L;
		ElapsedTime = 0L;
		
		return;
	}
	
	public final void run()
	{
		// Initialize the engine first
		Initializing = true;
		Initialize();
		
		// The LinkedList iterator cannot miss any components that are after the component currently being initialized
		// This is true no matter what malicious add order new components come in with
		// Even if the component currently being initialized is removed, the way the iterator is implemented means this is not a problem
		for(IUpdatable component : GameComponents)
			if(!component.Initialized())
				component.Initialize();
		
		LateInitialize();
		
		// Add a listener to pick up window closes so we can handle them ourselves
		Window.setDefaultCloseOperation(Window.DO_NOTHING_ON_CLOSE);
		Window.addWindowListener(new WindowAdapter()
							{
								@Override public void windowClosing(WindowEvent e)
								{
									Quit();
									
									super.windowClosing(e);
									return;
								}
							});
		
		Initializing = false;
		Initialized = true;
		
		// Force the first paint
		for(IDrawable d : LINQ.Select(LINQ.Where(GameComponents,c -> c instanceof IDrawable),c -> (IDrawable)c))
			d.Draw();
		
		Window.Repaint();
		
		// Now sleep off the first frame
		TimeStamp = System.currentTimeMillis();
		try
		{Thread.sleep(AllottedATPF);} // We don't need to update until after we have a first paint
		catch(Exception e)
		{} 
		
		// Perform the main game loop
		while(Continue)
		{
			// If we're paused, we shouldn't do anything
			// Note that if we decide to quit while paused, we should do one final update to make sure all values are finalized
			while(Paused)
			{
				// Just in case our sleep causes our thread to become inactive longer than we actually want
				long temp = System.currentTimeMillis();
				
				try
				{Thread.sleep(AllottedATPF);}
				catch(Exception e)
				{}
				
				// Paused time doesn't count as ellapsed time
				temp = System.currentTimeMillis() - temp;
				TimeStamp += temp;
				
				if(PauseExitCondition.ExitCondition(temp / 1000.0)) // We give elapsed time in terms of seconds, not milliseconds
					Play();
			}
			
			// First obtain the time delta
			long delta = System.currentTimeMillis() - TimeStamp;
			
			ElapsedTime += delta;
			TimeStamp = System.currentTimeMillis();
			
			if(delta > AllottedATPF)
				TickDeficit += delta - AllottedATPF;
			
			// Now perform the game logic updates
			Frame++;
			Update(delta);
			
			for(IUpdatable component : GameComponents)
				component.Update(delta);
			
			LateUpdate(delta);
			
			// Now force the system to redraw now that everything has updated
			for(IDrawable d : LINQ.Select(LINQ.Where(GameComponents,c -> c instanceof IDrawable),c -> (IDrawable)c))
				d.Draw();
			
			Window.Repaint();
			
			// Lastly, sleep if we need to in order to sync with our desired FPS
			// Alternatively, don't sleep if we need to catch up to our desired FPS
			long RemainingTime = AllottedATPF - (System.currentTimeMillis() - TimeStamp);
			
			// If we're behind, log it and don't sleep
			if(RemainingTime <= 0)
				TickDeficit -= RemainingTime;
			else // We're ahead, so only sleep if we're not behind from previous frames
				if(TickDeficit > 0L)
				{
					// We can't recover more time than we're behind or more time than we have, so pick the min
					long RecoveryTime = Math.min(TickDeficit,RemainingTime);
					TickDeficit -= RecoveryTime;
					
					// If we have leftover time, sleep on it
					if(RemainingTime > RecoveryTime)
						try
						{Thread.sleep(RemainingTime - RecoveryTime);}
						catch(Exception e)
						{}
				}
				else // We have no tick deficit, so just sleep on whatever time we didn't use
					try
					{Thread.sleep(RemainingTime);}
					catch(Exception e)
					{}
		}
		
		// When the main game loop exists, we need to clean up
		Finishing = true;
		Dispose();
		
		for(IUpdatable component : GameComponents)
			if(!component.Disposed())
				component.Dispose();
		
		LateDispose();
		
		// Dispose of our window
		Window.dispose();
		
		Finishing = false;
		Finished = true;
		
		return;
	}
	
	/**
	 * Performs any initialization logic before game component initialization logic is executed.
	 */
	protected abstract void Initialize();
	
	/**
	 * Performs any initialization logic after game component initialization logic is executed.
	 */
	protected abstract void LateInitialize();
	
	/**
	 * Performs any update logic before game components' update logic is executed.
	 */
	protected abstract void Update(long delta);
	
	/**
	 * Performs any update logic after game components' update logic is executed.
	 */
	protected abstract void LateUpdate(long delta);
	
	/**
	 * Performs any disposal logic before game components' disposal logic is executed.
	 */
	protected abstract void Dispose();
	
	/**
	 * Performs any disposal logic after game components' disposal logic is executed.
	 */
	protected abstract void LateDispose();
	
	/**
	 * Adds a new game component to this engine.
	 * @param component The component to add.
	 * @return Returns true if the component was added and false otherwise.
	 * @throws IllegalArgumentException Thrown if {@code component} is already part of this game engine.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws NullPointerException Thrown if {@code component} is null.
	 */
	public boolean AddComponent(IUpdatable component)
	{
		if(Finished())
			throw new IllegalStateException();
		
		if(component == null)
			throw new NullPointerException();
		
		if(ContainsComponent(component))
			throw new IllegalArgumentException();
		
		if(!GameComponents.AddLast(component))
			return false;
		
		if(component instanceof Component)
			Window.AddComponentFrameBounded((Component)component);
		
		if((Initialized() || Initializing()) && !component.Initialized())
			component.Initialize();
		
		return true;
	}
	
	/**
	 * Adds a new game component to this engine.
	 * @param component The component to add.
	 * @param where The index to insert the component at.
	 * @return Returns true if the component was added and false otherwise.
	 * @throws IllegalArgumentException Thrown if {@code component} is already part of this game engine.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or greater than {@code size()}.
	 * @throws NullPointerException Thrown if {@code component} is null.
	 */
	public boolean AddComponent(IUpdatable component, int where)
	{
		if(Finished())
			throw new IllegalStateException();
		
		if(component == null)
			throw new NullPointerException();
		
		if(ContainsComponent(component))
			throw new IllegalArgumentException();
		
		if(!GameComponents.Insert(component,where))
			return false;
		
		if(component instanceof Component)
			Window.AddComponentFrameBounded((Component)component);
		
		if((Initialized() || Initializing()) && !component.Initialized())
			component.Initialize();
		
		return true;
	}
	
	/**
	 * Obtains the game component at index {@code where}.
	 * @param where The index of the game component to retrieve.
	 * @return Returns the game component at the specified index.
	 * @throws IndexOutOfBoundsException Thrown if {@code where} is negative or at least {@code ComponentsCount()}.
	 */
	public IUpdatable GetComponent(int where)
	{return GameComponents.get(where);}
	
	/**
	 * Removes {@code component} from the game engine.
	 * @param component The component to remove.
	 * @return Returns true if the component was removed and false otherwise.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws NullPointerException Thrown if {@code component} is null.
	 */
	public boolean RemoveComponent(IUpdatable component)
	{
		if(Finished())
			throw new IllegalStateException();
		
		if(component == null)
			throw new NullPointerException();
		
		if(!GameComponents.remove(component))
			return false;
		
		if(component instanceof Component)
			Window.remove((Component)component);
		
		return true;
	}
	
	/**
	 * Removes {@code component} from the game engine.
	 * @param component The component to remove.
	 * @param dispose If true, the removed component will be disposed if it has been initialized.
	 * @return Returns true if the component was removed and false otherwise.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws NullPointerException Thrown if {@code component} is null.
	 */
	public boolean RemoveComponent(IUpdatable component, boolean dispose)
	{
		if(Finished())
			throw new IllegalStateException();
		
		if(component == null)
			throw new NullPointerException();
		
		if(!GameComponents.remove(component))
			return false;
		
		if(component instanceof Component)
			Window.remove((Component)component);
		
		if(dispose && component.Initialized() && !component.Disposed())
			component.Dispose();
		
		return true;
	}
	
	/**
	 * Removes the game component at index {@code where}.
	 * @param where The index of the game component to remove.
	 * @return Returns the removed game component.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws IndexOutOfBoundsException Thrown if {@code where} is negative or at least {@code ComponentsCount()}.
	 */
	public IUpdatable RemoveComponent(int where)
	{
		if(Finished())
			throw new IllegalStateException();
		
		IUpdatable ret = GameComponents.remove(where);
		
		if(ret instanceof Component)
			Window.remove((Component)ret);
		
		return ret;
	}
	
	/**
	 * Removes the game component at index {@code where}.
	 * @param where The index of the game component to remove.
	 * @param dispose If true, the removed component will be disposed if it has been initialized.
	 * @return Returns the removed game component.
	 * @throws IllegalStateException Thrown if this engine is already finished.
	 * @throws IndexOutOfBoundsException Thrown if {@code where} is negative or at least {@code ComponentsCount()}.
	 */
	public IUpdatable RemoveComponent(int where, boolean dispose)
	{
		if(Finished())
			throw new IllegalStateException();
		
		IUpdatable ret = GameComponents.remove(where);
		
		if(ret instanceof Component)
			Window.remove((Component)ret);
		
		if(dispose && ret.Initialized() && !ret.Disposed())
			ret.Dispose();
		
		return ret;
	}
	
	/**
	 * Determines if this game engine contains {@code componenent}.
	 * @param component The component to look for.
	 * @return Returns true if the component is found and false otherwise.
	 */
	public boolean ContainsComponent(IUpdatable component)
	{
		if(component == null)
			return false;
		
		for(IUpdatable u : GameComponents)
			if(component == u)
				return true;
		
		return false;
	}
	
	/**
	 * Removes all game components from the game engine.
	 */
	public void ClearComponents()
	{
		GameComponents.clear();
		return;
	}
	
	/**
	 * Determines the number of game components in the game engine.
	 * @return Returns the number of game components in the game engine.
	 */
	public int ComponentsCount()
	{return GameComponents.size();}
	
	/**
	 * Adds a service to the game engine if one of its type does not already exist.
	 * @param <T> The service type.
	 * @param service The service.
	 * @return Returns true if the service was added and false otherwise.
	 * @throws NullPointerException Thrown if {@code service} is null.
	 */
	public <T> boolean AddService(T service)
	{return Services.Add(service.getClass(),service);}
	
	/**
	 * Adds or replaces a service in the game engine if one of its type.
	 * @param <T> The service type.
	 * @param service The service.
	 * @return Returns the old service of {@code service}'s type if one exists or {@code service} otherwise.
	 * @throws NullPointerException Thrown if {@code service} is null.
	 */
	public <T> T PutService(Class type, T service)
	{return (T)Services.Put(service.getClass(),service);}
	
	/**
	 * Obtains a service from the game engine.
	 * @param <T> The service type. Since Java is bad and forgets its generic types, we have to provide {@code type} as well.
	 * @param type The type of service to get.
	 * @return Returns the service.
	 * @throws NoSuchElementException Thrown if {@code type} is not a service type in the game engine.
	 */
	public <T> T GetService(Class type)
	{return (T)Services.Get(type);}
	
	/**
	 * Removes a service from the game engine.
	 * @param type The type of service to remove.
	 * @return Returns true if a service was removed and false otherwise.
	 */
	public boolean RemoveService(Class type)
	{return Services.Remove(type);}
	
	/**
	 * Removes all services from the game engine.
	 */
	public void ClearServices()
	{
		Services.Clear();
		return;
	}
	
	/**
	 * Determines the number of services in the game engine.
	 * @return Returns the number of services in the game engine.
	 */
	public int ServiceCount()
	{return Services.Count();}
	
	/**
	 * Unpauses execution of the game engine.
	 * This only affects updates, not initialization or disposal.
	 */
	public void Play()
	{
		Paused = false;
		PauseExitCondition = null;
		
		return;
	}
	
	/**
	 * Pauses execution of the game engine.
	 * This only affects updates, not initialization or disposal.
	 */
	public void Pause()
	{
		Paused = true;
		PauseExitCondition = null;
		
		return;
	}
	
	/**
	 * Pauses execution of the game engine with a specified exit condition.
	 * This only affects updates, not initialization or disposal.
	 * @param end The exit condition for the paused state. This will be checked in between each skipped frame.
	 */
	public void Pause(ExitCondition end)
	{
		Paused = true;
		PauseExitCondition = end;
		
		return;
	}
	
	/**
	 * Causes the game engine to exit after the current update cycle finishes.
	 */
	public void Quit()
	{
		Continue = false;
		return;
	}
	
	/**
	 * Sets the game window size to have the specified dimensions.
	 * @param dim The dimensions of the game window. Width is the first component and height is the second.
	 * @throws IllegalArgumentException Thrown if either component of {@code dim} is nonpositive.
	 */
	public void SetWindowSize(Vector2i dim)
	{
		SetWindowSize(dim.X,dim.Y);
		return;
	}
	
	/**
	 * Sets the game window size to have the specified dimensions.
	 * @param width The width of the game window.
	 * @param height The height of the game window.
	 * @throws IllegalArgumentException Thrown if {@code width} or {@code height} is nonpositive.
	 */
	public void SetWindowSize(int width, int height)
	{
		if(width <= 0 || height <= 0)
			throw new IllegalArgumentException();
		
		Window.setSize(width,height);
		
		for(Component comp : LINQ.Select(LINQ.Where(GameComponents,c -> c instanceof Component),c -> (Component)c))
			comp.setSize(width,height);
		
		return;
	}
	
	/**
	 * Determines the game window size.
	 * @return Returns the game window size. Width is the first component and height is the second.
	 */
	public Vector2i GetWindowSize()
	{return new Vector2i(Window.getWidth(),Window.getHeight());}
	
	/**
	 * Determines if the game engine is currently initializing.
	 * @return Returns true if the game engine is actively initializing and false otherwise.
	 */
	public boolean Initializing()
	{return Initializing;}
	
	/**
	 * Determines if the game engine is yet initialized.
	 * @return Returns true if the game engine is initialized and false otherwise.
	 */
	public boolean Initialized()
	{return Initialized;}
	
	/**
	 * Determines if the game engine is currently executing the main game loop.
	 * @return Returns true if the game engine's main game loop is running and false otherwise.
	 */
	public boolean Executing()
	{return Initialized() && !Paused() && !Finishing() && !Finished();}
	
	/**
	 * Determines if the game engine is paused.
	 * @return Returns true if the game engine is paused and false otherwise.
	 */
	public boolean Paused()
	{return Paused;}
	
	/**
	 * Obtains the current update frame.
	 * @return Returns the current update frame.
	 */
	public int Frame()
	{return Frame;}
	
	/**
	 * Obtains the number of elapsed update frames.
	 * @return Returns the current update frame. This is always equal to Frame().
	 */
	public int ElapsedFrames()
	{return Frame;}
	
	/**
	 * Determines if the game engine is currently finishing.
	 * @return Returns true if the game engine is actively finishing and false otherwise.
	 */
	public boolean Finishing()
	{return Finishing;}
	
	/**
	 * Determines if the game engine is yet finished.
	 * @return Returns true if the game engine is finished and false otherwise.
	 */
	public boolean Finished()
	{return Finished;}
	
	/**
	 * Determines if the game engine is disposed.
	 * This is equivalent to {@code Finished()}.
	 */
	public boolean Disposed()
	{return Finished();}
	
	/**
	 * Determines the total elapsed time that this game engine has been running (and not paused).
	 * This value is always zero before the game engine finishes initialization and is constant after it finishes udpating.
	 * @return Returns the total time elapsed since the first frame began to either the current time or when the last frame ended.
	 */
	public long ElapsedTime()
	{return ElapsedTime;}
	
	/**
	 * Obtains the game engine.
	 * There should only ever be one game engine running, and this is it.
	 * If there is more than one running, then the last constructed game engine is in this position.
	 * @return Returns the executing game engine.
	 */
	public static GameEngine Game()
	{return Game;}
	
	/**
	 * The game window.
	 */
	private final AbsoluteFrame Window;
	
	/**
	 * The collection of game components in this engine.
	 */
	private final LinkedList<IUpdatable> GameComponents;
	
	/**
	 * The services available to the game engine.
	 */
	private final Dictionary<Class,Object> Services;
	
	/**
	 * If true, then this engine is initializing.
	 */
	private boolean Initializing;
	
	/**
	 * If true, then this engine is initialized.
	 */
	private boolean Initialized;
	
	/**
	 * If true, we should continue the main game loop.
	 */
	private boolean Continue;
	
	/**
	 * If true, then the game engine is paused.
	 */
	private boolean Paused;
	
	/**
	 * An exit condition for the paused state.
	 * This need not be given but is convenient since the entire game engine is otherwise stuck while paused.
	 */
	private ExitCondition PauseExitCondition;
	
	/**
	 * The update frame, or rather, the number of update cycles executed.
	 * This value is such it is the update cycle currently being executed.
	 */
	private int Frame;
	
	/**
	 * If true, then this engine is finishing.
	 */
	private boolean Finishing;
	
	/**
	 * If true, then this engine is finished.
	 */
	private boolean Finished;
	
	/**
	 * The target number of (update) frames per second.
	 */
	private int FPS;
	
	/**
	 * The allotted average time per frame.
	 */
	private long AllottedATPF;
	
	/**
	 * The number of millisecond ticks the game engine is behind where it should be on its update schedule.
	 */
	private long TickDeficit;
	
	/**
	 * The time stamp for when the game engine last started updating.
	 */
	private long TimeStamp;
	
	/**
	 * The total time this game engine has been running.
	 * This value is the sum of the time each update cycle took to complete.
	 */
	private long ElapsedTime;
	
	/**
	 * The one true game object.
	 * There should only ever be one game engine running, so we keep it around as a singleton for anyone to reference.
	 */
	private static GameEngine Game;
	
	/**
	 * Determines when an exit condition has been met.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface ExitCondition
	{
		/**
		 * Determines when an exit condition has been met.
		 * @param delta The elapsed time since the last exit condition check.
		 * @return Returns true when some exit condition has been met and false otherwise.
		 */
		public abstract boolean ExitCondition(double delta);
	}
}
