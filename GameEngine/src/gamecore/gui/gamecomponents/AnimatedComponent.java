package gamecore.gui.gamecomponents;

import gamecore.IUpdatable;
import gamecore.observe.IObserver;
import gamecore.sprites.Animation;
import gamecore.time.TimePartition.TimeEvent;

/**
 * An animated component.
 * @author Dawn Nye
 */
public class AnimatedComponent extends ImageComponent implements IUpdatable, IObserver<TimeEvent>
{
	/**
	 * Creates an animated GUI component.
	 * The animation {@code a} is kept for utilization rather than copied.
	 * @param a The animation to use for the animated component. 
	 */
	public AnimatedComponent(Animation a)
	{
		super();
		TheAnimation = a;
		
		return;
	}
	
	@Override public void Initialize()
	{
		if(Initialized())
			return;
		
		super.Initialize();
		Initialized = false;
		
		if(TheAnimation != null)
		{
			TheAnimation.Initialize();
			TheAnimation.Subscribe(this);
			SetSelectedImage(TheAnimation.CurrentFrame());
		}
		
		Initialized = true;
		return;
	}
	
	@Override public void Update(long delta)
	{
		if(!Initialized() || Disposed())
			return;
		
		super.Update(delta);
		
		if(TheAnimation != null)
			TheAnimation.Update(delta);
		
		return;
	}
	
	@Override public void Dispose()
	{
		if(Disposed())
			return;
		
		if(TheAnimation != null)
			TheAnimation.Dispose();
		
		super.Dispose();
		Disposed = true;
		
		return;
	}
	
	public void OnNext(TimeEvent event)
	{
		if(!event.IsSegmentChange())
			return;
		
		SetSelectedImage(event.NewTimeSegement);
		return;
	}
	
	public void OnError(Exception e)
	{return;}
	
	public void OnCompleted()
	{return;}
	
	/**
	 * Sets the displayed image.
	 * @param index The frame to display. This value can be negative or greater than the number of frames, in which case no image is displayed.
	 */
	protected void SetSelectedImage(int index)
	{
		if(TheAnimation != null)
		{
			img = null;
			SelectedIndex = -1;
			
			return;
		}
		
		if(index >= 0 && index < TheAnimation.FrameCount())
			img = TheAnimation.GetFrame(index);
		else
			img = null;
		
		SelectedIndex = index;
		return;
	}
	
	/**
	 * Swaps the current animation out for {@code a}.
	 * @param a The new animation for this component. If {@code a} is not initialized, it becomes initialized.
	 * @implNote The old animation is not dispsoed of or otherwise destructed.
	 */
	public void SwapAnimations(Animation a)
	{
		if(TheAnimation != null)
			TheAnimation.Unsubscribe(this);
		
		TheAnimation = a;
		
		if(a != null)
		{
			TheAnimation.Initialize();
			TheAnimation.Subscribe(this);
			SetSelectedImage(TheAnimation.CurrentFrame());
		}
		
		return;
	}
	
	/**
	 * Causes the animation to play.
	 */
	public void Play()
	{
		if(TheAnimation != null)
			TheAnimation.Play();
		
		return;
	}
	
	/**
	 * Causes the animation to pause.
	 */
	public void Pause()
	{
		if(TheAnimation != null)
			TheAnimation.Pause();
		
		return;
	}
	
	/**
	 * Causes the animation to stop and reset.
	 */
	public void Stop()
	{
		if(TheAnimation != null)
			TheAnimation.Stop();
		
		return;
	}
	
	/**
	 * Determines if the animation is playing.
	 * @return Returns true if the animation is playing and false otherwise.
	 */
	public boolean IsPlaying()
	{
		if(TheAnimation == null)
			return false;
		
		return TheAnimation.IsPlaying();
	}
	
	/**
	 * Determines if the animation is paused.
	 * @return Returns true if the animation is paused and false otherwise.
	 */
	public boolean IsPaused()
	{
		if(TheAnimation == null)
			return false;
		
		return TheAnimation.IsPaused();
	}
	
	/**
	 * Sets the animation to the specified frame.
	 * If the animation is in motion, it continues playback from there. 
	 * @param frame The frame to jump to.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public void SetFrame(int frame)
	{
		if(TheAnimation == null)
			return;
		
		TheAnimation.SetFrame(frame);
		return;
	}
	
	/**
	 * Determines the current frame of the animation.
	 * @return Returns the current frame of the animation.
	 */
	public int CurrentFrame()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.CurrentFrame();
	}
	
	/**
	 * The current time in the animation.
	 */
	public long CurrentTime()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.CurrentTime();
	}
	
	/**
	 * The total elapsed time according to the time partition.
	 */
	public long ElapsedTime()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.ElapsedTime();
	}
	
	/**
	 * Determines the start time of frame {@code frame}.
	 * @param frame The frame whose start time is of interest.
	 * @return Returns the start time of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long FrameStart(int frame)
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.FrameStart(frame);
	}
	
	/**
	 * Determines the end time of frame {@code frame}.
	 * @param frame The frame whose end time is of interest.
	 * @return Returns the end time of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long FrameEnd(int frame)
	{
		if(TheAnimation == null)
			return 0;
		
		return FrameStart(frame) + GetFrameDuration(frame);
	}
	
	/**
	 * Obtains the duraction of the specified frame.
	 * @param frame The frame whose duration is of interest.
	 * @return Returns the duration of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long GetFrameDuration(int frame)
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.GetFrameDuration(frame);
	}
	
	/**
	 * Determines the number of frames in this animation.
	 * @return Returns the number of frames in this animation.
	 */
	public int FrameCount()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.FrameCount();
	}
	
	/**
	 * Determines the length of the animation.
	 * @return Returns the length of the animation.
	 */
	public long AnimationLength()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.AnimationLength();
	}
	
	/**
	 * Causes the animation to loop with its last set loop start and end times.
	 * If a loop start and end has never been specified, this behavior is undefined.
	 */
	public void Loop()
	{
		if(TheAnimation == null)
			return;
		
		if(TheAnimation.Loops())
			return;
		
		TheAnimation.Loop();
		return;
	}
	
	/**
	 * Causes the animation to loop from time {@code start} to time {@code end}.
	 * @param start The loop start time.
	 * @param end The loop end time.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative, greater than the length of the animation, or if {@code start} >= {@code end}.
	 */
	public void Loop(long start, long end)
	{
		if(TheAnimation == null)
			return;
		
		if(start >= end || end > AnimationLength())
			throw new IllegalArgumentException();

		TheAnimation.Loop(start,end);
		return;
	}
	
	/**
	 * Causes the animation to loop from frame {@code start} to frame {@code end}.
	 * @param start The starting frame.
	 * @param end The (inclusive) last frame.
	 * @throws IllegalArgumentException Thrown if {@code start} > {@code end}.
	 * @throws IndexOutOfBoundsException Thrown if {@code start} or {@code end} is negative or at least {@code FrameCount()}.
	 */
	public void Loop(int start, int end)
	{
		if(TheAnimation == null)
			return;
		
		if(start > end)
			throw new IllegalArgumentException();
		
		if(start < 0 || end < 0 || end >= FrameCount())
			throw new IndexOutOfBoundsException();
		
		TheAnimation.Loop(FrameStart(start),FrameEnd(end));
		return;
	}
	
	/**
	 * Causes the animation to stop looping if it was before.
	 */
	public void StopLooping()
	{
		if(TheAnimation == null)
			return;
		
		if(!TheAnimation.Loops())
			return;
		
		TheAnimation.StopLooping();
		return;
	}
	
	/**
	 * Determines if this animation is looping.
	 * @return Returns true if the animation is looping and false otherwise.
	 */
	public boolean Loops()
	{
		if(TheAnimation == null)
			return false;
		
		return TheAnimation.Loops();
	}
	
	/**
	 * Determines when the time loop starts (if there is a time loop).
	 * This value is inclusive.
	 * @return Returns the start time of the time loop.
	 */
	public long LoopStart()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.LoopStart();
	}
	
	/**
	 * Determines when the time loop ends (if there is a time loop).
	 * This value is exclusive.
	 * @return Returns the end time of the time loop.
	 */
	public long LoopEnd()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.LoopEnd();
	}
	
	/**
	 * Determines the length of the animation.
	 * This value is undefined if the animation is not looping.
	 * @return Returns the length of the animation.
	 */
	public long LoopLength()
	{
		if(TheAnimation == null)
			return 0;
		
		return TheAnimation.LoopLength();
	}
	
	/**
	 * The animation to draw.
	 */
	protected Animation TheAnimation;
	
	/**
	 * The current image to display.
	 * If this value is out of bounds, no image is display and no error is thrown.
	 */
	protected int SelectedIndex;
}
