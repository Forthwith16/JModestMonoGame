package gamecore.sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.Scanner;

import gamecore.IUpdatable;
import gamecore.LINQ.LINQ;
import gamecore.datastructures.ArrayList;
import gamecore.datastructures.CellRectangle;
import gamecore.datastructures.LinkedList;
import gamecore.datastructures.tuples.Pair;
import gamecore.observe.IObservable;
import gamecore.observe.IObserver;
import gamecore.time.TimePartition;
import gamecore.time.TimePartition.TimeEvent;

/**
 * Encapsulates a 2D animation.
 * @author Dawn Nye
 */
public class Animation implements IUpdatable, IObservable<TimeEvent>
{
	/**
	 * Loads an animation from an animation file specification.
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
	 * 		If the time dimensions are variable, then the following appear with each entry occurring on its own line..
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
	 * 	<li>An integer specifying the number of frames in the animation. This value must be at least one.</li>
	 * 	<li>
	 * 		For each frame of the animation, a pair of values on a single line seperated by white space.
	 * 		The first is an integer index into the sprite sheet for the frame's image. This value must be a valid sprite index.
	 * 		The second is a long duration for the frame specified in milliseconds. This value must be at least 1L.
	 * 	</li>
	 * 	<li>An optional boolean value (true/false) indicating if the animation should loop. If this value is absent, it defaults to false.</li>
	 * 	<li>
	 * 		If the animation loops, two values on a single line seperated by whitespace.
	 * 		First, an integer start frame for the loop.
	 * 		Second, an integer end frame for the loop.
	 * 		If the animation does not loop, both of these values are absent.
	 * 	</li>
	 * </ul>
	 * @param source The animation file.
	 * @throws FileNotFoundException Thrown if {@code source} does not exist or if the sprite sheet it specifies does not exist.
	 * @throws IllegalFormatException Thrown if {@code source} is not a properly formatted animation file.
	 * @throws IOException Thrown if an error is encountered while reading {@code source}.
	 * @throws NullPointerException Thrown if {@code source} is null.
	 */
	public Animation(File source) throws IOException
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
			
			Sprites = SpriteSheet.LoadSprites(sprite_sheet.getName(),sprite_sheet,x_dim,y_dim);
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
			
			Sprites = SpriteSheet.LoadSprites(sprite_sheet.getName(),sprite_sheet,sprites);
		}
		
		// Next we load the frames
		Frames = new ArrayList<Pair<Integer,Long>>();
		
		if(!fin.hasNextInt())
			throw new IOException();
		
		int num_frames = fin.nextInt();
		
		if(num_frames < 1)
			throw new IOException();
		
		// For each frame, we load the index into Sprites and its duration
		for(int i = 0;i < num_frames;i++)
		{
			if(!fin.hasNextInt())
				throw new IOException();
			
			int index = fin.nextInt();
			
			if(index < 0 || index >= Sprites.Count() || !fin.hasNextLong())
				throw new IOException();
			
			long duration = fin.nextLong();
			
			if(duration < 1L)
				throw new IOException();
			
			Frames.add(new Pair<Integer,Long>(index,duration));
		}
		
		Clock = new TimePartition(LINQ.Select(Frames,(next,prev) -> prev == null ? next.Item2 : next.Item2 + prev));
		Clock.MaximumTime(true,Clock.GetSegment(Clock.SegmentCount() - 1)); // The maximum time is inclusive, so this is technically 1 ms longer than it should be, but for the sake of (exclusive) loop ends, this is fine
		Clock.RemoveSegment(Clock.SegmentCount() - 1); // The last segment is the (exclusive) end time of the final frame, and we don't want there to be a segment transition there 
		
		// We load the optional loop data last
		if(fin.hasNextBoolean() && fin.nextBoolean())
		{
			// We loop, so we need to segments to loop between
			if(!fin.hasNextInt())
				throw new IOException();
			
			int start = fin.nextInt();
			
			if(start < 0 || !fin.hasNextInt())
				throw new IOException();
			
			int end = fin.nextInt();
			
			if(end < start || end >= FrameCount())
				throw new IOException();
			
			Loop(start,end);
		}
		
		fin.close();
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	/**
	 * Creates an animation from {@code ss}.
	 * The frames of the animation are specified by an index into {@code ss} and a duration given in milliseconds.
	 * @param ss The sprite sheet containing the animation source data.
	 * @param frames The frames of the animation specified by (index,duration) as detailed above.
	 * @throws NullPointerException Thrown if {@code ss} or {@code frames} is null.
	 */
	public Animation(SpriteSheet ss, Iterable<Pair<Integer,Long>> frames)
	{
		Sprites = ss;
		Frames = new ArrayList<Pair<Integer,Long>>(frames);
		
		Clock = new TimePartition(LINQ.Select(frames,(next,prev) -> prev == null ? next.Item2 : next.Item2 + prev));
		Clock.MaximumTime(true,Clock.GetSegment(Clock.SegmentCount() - 1)); // The maximum time is inclusive, so this is technically 1 ms longer than it should be, but for the sake of (exclusive) loop ends, this is fine
		Clock.RemoveSegment(Clock.SegmentCount() - 1); // The last segment is the (exclusive) end time of the final frame, and we don't want there to be a segment transition there 
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	/**
	 * Creates an animation from {@code ss}.
	 * The frames of the animation are taken from {@code ss} in order and each is given a duration equal to {@code duration}.
	 * @param ss The sprite sheet containing the animation source data.
	 * @param duration The duration of each frame given in milliseconds.
	 * @throws IllegalArgumentException Thrown if {@code duration} is nonpositive.
	 * @throws NullPointerException Thrown if {@code ss} is null.
	 */
	public Animation(SpriteSheet ss, long duration)
	{
		if(duration < 1L)
			throw new IllegalArgumentException();
		
		Sprites = ss;
		Frames = new ArrayList<Pair<Integer,Long>>();
		
		for(int i = 0;i < ss.Count();i++)
			Frames.add(new Pair<Integer,Long>(i,duration));
		
		Clock = new TimePartition(LINQ.Select(Frames,(next,prev) -> prev == null ? next.Item2 : next.Item2 + prev));
		Clock.MaximumTime(true,Clock.GetSegment(Clock.SegmentCount() - 1)); // The maximum time is inclusive, so this is technically 1 ms longer than it should be, but for the sake of (exclusive) loop ends, this is fine
		Clock.RemoveSegment(Clock.SegmentCount() - 1); // The last segment is the (exclusive) end time of the final frame, and we don't want there to be a segment transition there 
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	/**
	 * Creates an animation from {@code ss}.
	 * The frames of the animation are specified by an index into {@code ss} and a duration given in milliseconds.
	 * @param ss The sprite sheet containing the animation source data.
	 * @param frames The frames of the animation specified by (index,duration) as detailed above.
	 * @param loop If true, then the entire animation will be set to loop. If false, the animation will play once.
	 * @throws NullPointerException Thrown if {@code ss} or {@code frames} is null.
	 */
	public Animation(SpriteSheet ss, Iterable<Pair<Integer,Long>> frames, boolean loop)
	{
		Sprites = ss;
		Frames = new ArrayList<Pair<Integer,Long>>(frames);
		
		Clock = new TimePartition(LINQ.Select(frames,(next,prev) -> prev == null ? next.Item2 : next.Item2 + prev));
		Clock.MaximumTime(true,Clock.GetSegment(Clock.SegmentCount() - 1)); // The maximum time is inclusive, so this is technically 1 ms longer than it should be, but for the sake of (exclusive) loop ends, this is fine
		Clock.RemoveSegment(Clock.SegmentCount() - 1); // The last segment is the (exclusive) end time of the final frame, and we don't want there to be a segment transition there 
		
		if(loop)
			Loop(0,FrameCount() - 1);
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	/**
	 * Creates an animation from {@code ss}.
	 * The frames of the animation are taken from {@code ss} in order and each is given a duration equal to {@code duration}.
	 * @param ss The sprite sheet containing the animation source data.
	 * @param duration The duration of each frame given in milliseconds.
	 * @param loop If true, then the entire animation will be set to loop. If false, the animation will play once.
	 * @throws IllegalArgumentException Thrown if {@code duration} is nonpositive.
	 * @throws NullPointerException Thrown if {@code ss} is null.
	 */
	public Animation(SpriteSheet ss, long duration, boolean loop)
	{
		if(duration < 1L)
			throw new IllegalArgumentException();
		
		Sprites = ss;
		Frames = new ArrayList<Pair<Integer,Long>>();
		
		for(int i = 0;i < ss.Count();i++)
			Frames.add(new Pair<Integer,Long>(i,duration));
		
		Clock = new TimePartition(LINQ.Select(Frames,(next,prev) -> prev == null ? next.Item2 : next.Item2 + prev));
		Clock.MaximumTime(true,Clock.GetSegment(Clock.SegmentCount() - 1)); // The maximum time is inclusive, so this is technically 1 ms longer than it should be, but for the sake of (exclusive) loop ends, this is fine
		Clock.RemoveSegment(Clock.SegmentCount() - 1); // The last segment is the (exclusive) end time of the final frame, and we don't want there to be a segment transition there 
		
		if(loop)
			Loop(0,FrameCount() - 1);
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	/**
	 * Creates a duplicate animation.
	 * The new animation does not retain the same state as {@code a}.
	 * Rather, it is constructed to its base so that the elapsed time and current time are both zero.
	 * It also must be initialized before it can be used further.
	 * Loops data and other miscellaneous settings, however, do carry over.
	 * @param a The animation to copy.
	 * @throws NullPointerException Thrown if {@code a} is null.
	 */
	public Animation(Animation a)
	{
		Sprites = a.Sprites;
		Frames = new ArrayList<Pair<Integer,Long>>(a.Frames);
		
		Clock = new TimePartition(a.Clock);
		
		Initialized = false;
		Disposed = false;
		
		return;
	}
	
	public void Initialize()
	{
		Clock.Initialize();
		
		Initialized = true;
		return;
	}
	
	public boolean Initialized()
	{return Initialized;}
	
	public void Update(long delta)
	{
		if(!Initialized() || Disposed())
			return;
		
		Clock.Update(delta);
		return;
	}
	
	public void Dispose()
	{
		Clock.Dispose();
		
		Disposed = true;
		return;
	}
	
	public boolean Disposed()
	{return Disposed;}
	
	/**
	 * Causes {@code eye} to begin observing this (or rather the clock controlling the animation).
	 * Observers are allowed to subscribe multiple times if desired.
	 * Observers are garunteed to be notified in the order of subscription.
	 * @param eye The observer.
	 * @throws NullPointerException Thrown if {@code eye} is null.
	 */
	public void Subscribe(IObserver<TimeEvent> eye)
	{
		Clock.Subscribe(eye);
		return;
	}
	
	/**
	 * Causes {@code eye} to stop observing this (or rather the clock controlling the animation).
	 * Only removes at most the first/oldest instance of {@code eye} if subscribed multiple times.
	 * @param eye THe observer.
	 * @throws NullPointerException Thrown if {@code eye} is null.
	 */
	public void Unsubscribe(IObserver<TimeEvent> eye)
	{
		Clock.Unsubscribe(eye);
		return;
	}
	
	/**
	 * Causes the animation to play.
	 */
	public void Play()
	{
		Clock.Play();
		return;
	}
	
	/**
	 * Causes the animation to pause.
	 */
	public void Pause()
	{
		Clock.Pause();
		return;
	}
	
	/**
	 * Causes the animation to stop and reset.
	 */
	public void Stop()
	{
		Clock.Stop();
		return;
	}
	
	/**
	 * Determines if the animation is playing.
	 * @return Returns true if the animation is playing and false otherwise.
	 */
	public boolean IsPlaying()
	{return Clock.Playing();}
	
	/**
	 * Determines if the animation is paused.
	 * @return Returns true if the animation is paused and false otherwise.
	 */
	public boolean IsPaused()
	{return Clock.Paused();}
	
	/**
	 * Sets the animation to the specified frame.
	 * If the animation is in motion, it continues playback from there. 
	 * @param frame The frame to jump to.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public void SetFrame(int frame)
	{
		boolean pause = Clock.Paused();
		Clock.Play(frame);
		
		if(pause)
			Clock.Pause();
		
		return;
	}
	
	/**
	 * Obtains the current frame's image.
	 * @return Returns the image drawn during the current frame.
	 */
	public BufferedImage GetFrame()
	{return Sprites.GetSprite(Frames.get(CurrentFrame()).Item1);}
	
	/**
	 * Obtains the image drawn for this animation at frame {@code frame}.
	 * @param frame The frame of interest.
	 * @return Returns the image drawn at the specified frame.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public BufferedImage GetFrame(int frame)
	{
		if(frame < 0 || frame >= FrameCount())
			throw new IndexOutOfBoundsException();
		
		return Sprites.GetSprite(Frames.get(frame).Item1);
	}
	
	/**
	 * Determines the current frame of the animation.
	 * @return Returns the current frame of the animation.
	 */
	public int CurrentFrame()
	{return Clock.Segment();}
	
	/**
	 * The current time in the animation.
	 */
	public long CurrentTime()
	{return Clock.CurrentTime();}
	
	/**
	 * The total elapsed time according to the time partition.
	 */
	public long ElapsedTime()
	{return Clock.ElapsedTime();}
	
	/**
	 * Determines the start time of frame {@code frame}.
	 * @param frame The frame whose start time is of interest.
	 * @return Returns the start time of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long FrameStart(int frame)
	{
		if(frame < 0 || frame >= FrameCount())
			throw new IndexOutOfBoundsException();
		
		return Clock.GetSegment(frame);
	}
	
	/**
	 * Determines the end time of frame {@code frame}.
	 * @param frame The frame whose end time is of interest.
	 * @return Returns the end time of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long FrameEnd(int frame)
	{return FrameStart(frame) + GetFrameDuration(frame);}
	
	/**
	 * Obtains the duraction of the specified frame.
	 * @param frame The frame whose duration is of interest.
	 * @return Returns the duration of frame {@code frame}.
	 * @throws IndexOutOfBoundsException Thrown if {@code frame} is negative or at least {@code FrameCount()}.
	 */
	public long GetFrameDuration(int frame)
	{return Frames.get(frame).Item2;}
	
	/**
	 * Determines the number of frames in this animation.
	 * @return Returns the number of frames in this animation.
	 */
	public int FrameCount()
	{return Frames.size();}
	
	/**
	 * Determines the length of the animation.
	 * @return Returns the length of the animation.
	 */
	public long AnimationLength()
	{return Clock.EndOfTime();}
	
	/**
	 * Causes the animation to loop with its last set loop start and end times.
	 * If a loop start and end has never been specified, this behavior is undefined.
	 */
	public void Loop()
	{
		if(Clock.Looping())
			return;
		
		Clock.Loop(true,Clock.LoopStart(),Clock.LoopEnd());
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
		if(start >= end || end > AnimationLength())
			throw new IllegalArgumentException();
		
		Clock.Loop(true,start,end);
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
		if(start > end)
			throw new IllegalArgumentException();
		
		if(start < 0 || end < 0 || end >= FrameCount())
			throw new IndexOutOfBoundsException();
		
		Clock.Loop(true,FrameStart(start),FrameEnd(end));
		return;
	}
	
	/**
	 * Causes the animation to stop looping if it was before.
	 */
	public void StopLooping()
	{
		if(!Clock.Looping())
			return;
		
		Clock.Loop(false,-1L,-1L);
		return;
	}
	
	/**
	 * Determines if this animation is looping.
	 * @return Returns true if the animation is looping and false otherwise.
	 */
	public boolean Loops()
	{return Clock.Looping();}
	
	/**
	 * Determines when the time loop starts (if there is a time loop).
	 * This value is inclusive.
	 * @return Returns the start time of the time loop.
	 */
	public long LoopStart()
	{return Clock.LoopStart();}
	
	/**
	 * Determines when the time loop ends (if there is a time loop).
	 * This value is exclusive.
	 * @return Returns the end time of the time loop.
	 */
	public long LoopEnd()
	{return Clock.LoopEnd();}
	
	/**
	 * Determines the length of the animation.
	 * This value is undefined if the animation is not looping.
	 * @return Returns the length of the animation.
	 */
	public long LoopLength()
	{return Clock.LoopLength();}
	
	/**
	 * The animation clock.
	 */
	protected TimePartition Clock;
	
	/**
	 * The images we can display.
	 */
	protected SpriteSheet Sprites;
	
	/**
	 * Frame data for the animation.
	 */
	protected ArrayList<Pair<Integer,Long>> Frames;
	
	/**
	 * If true, this animation is initialized.
	 */
	protected boolean Initialized;
	
	/**
	 * If true, this component has been disposed of.
	 */
	protected boolean Disposed;
}
