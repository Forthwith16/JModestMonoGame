package gamecore.time;

import gamecore.IUpdatable;
import gamecore.LINQ.LINQ;
import gamecore.datastructures.ArrayList;
import gamecore.datastructures.LinkedList;
import gamecore.observe.IObservable;
import gamecore.observe.IObserver;
import gamecore.time.TimePartition.TimeEvent;

/**
 * Keeps track of time like a clock and also partitions time into disjoint, consecutive segements.
 * For example, if we want to animate a sequence of 10 images such that each image lasts .1 second, we would want to know when we should switch frames.
 * We could set up time to send us a notification when the time passes the 0 ms mark, 100 ms mark, 200 ms mark, etc.
 * If we also want that animation to loop, would could set this partition to loop when it reaches 1000 ms and jump back to 0 ms.
 * @author Dawn Nye
 */
public class TimePartition implements IUpdatable, IObservable<TimeEvent>
{
	/**
	 * Creates an empty time partition.
	 */
	public TimePartition()
	{
		Initialized = false;
		Disposed = false;

		Play = false;
		Loops = false;
		LoopStart = 0L;
		LoopEnd = Long.MAX_VALUE;
		
		TimeEnds = false;
		MaximumTime = Long.MAX_VALUE;
		
		ElapsedTime = 0L;
		CurrentTime = 0L;
		
		Segmentations = new ArrayList<Long>();
		
		Observers = new LinkedList<IObserver<TimeEvent>>();
		return;
	}
	
	/**
	 * Creates a time partition with segmentations given by {@code segment_starts}.
	 * @param segment_starts
	 * These are the times when new segments start.
	 * The initial segment always begins at 0 (a value of 0 will be ignored), and the final segment extends forever.
	 * Duplicate values will be ignored.
	 * Best practice is to provide the segmented values as sorted in advance as possible to reduce sorting time.
	 */
	public TimePartition(Iterable<Long> segment_starts)
	{
		Initialized = false;
		Disposed = false;

		Play = false;
		Loops = false;
		LoopStart = 0L;
		LoopEnd = Long.MAX_VALUE;
		
		TimeEnds = false;
		MaximumTime = Long.MAX_VALUE;
		
		ElapsedTime = 0L;
		CurrentTime = 0L;
		
		Segmentations = new ArrayList<Long>(LINQ.Distinct(LINQ.Where(segment_starts,t -> t.compareTo(0L) > 0)));
		Segmentations.sort(null);
		
		Observers = new LinkedList<IObserver<TimeEvent>>();
		return;
	}
	
	/**
	 * Creates a duplicate time partition.
	 * The new time partition does not retain time data but does keep miscellaneous state changes such as loop settings.
	 * It also does not copy the old clock's observers.
	 * The new time partition must also be initialized.
	 * @param clock The time partition to duplicate.
	 * @throws NullPointerException Thrown if {@code clock} is null.
	 */
	public TimePartition(TimePartition clock)
	{
		Initialized = false;
		Disposed = false;

		Play = false;
		Loops = clock.Loops;
		LoopStart = clock.LoopStart;
		LoopEnd = clock.LoopEnd;
		
		TimeEnds = clock.TimeEnds;
		MaximumTime = clock.MaximumTime;
		
		ElapsedTime = 0L;
		CurrentTime = 0L;
		
		Segmentations = new ArrayList<Long>(clock.Segmentations);
		
		Observers = new LinkedList<IObserver<TimeEvent>>();
		return;
	}
	
	public void Initialize()
	{
		Play();
		
		Initialized = true;
		return;
	}
	
	public boolean Initialized()
	{return Initialized;}
	
	public void Update(long delta)
	{
		if(Paused() || !Initialized() || Disposed())
			return;
		
		// Elapsed time is easy
		// It just counts total time experienced
		ElapsedTime += delta;
		
		// Current time has to factor in a possible time loop or a maximum time
		long PreviousTime = CurrentTime;
		CurrentTime += delta;
		
		// If we're in a time loop, ensure that we time travel only if we pass through the barrier 
		if(Loops && CurrentTime >= LoopEnd && PreviousTime < LoopEnd && (!TimeEnds || MaximumTime >= LoopEnd))
		{
			long over = CurrentTime - LoopEnd;
			
			while(over >= 0) // If we went to or past the time loop's end, we need to time travel
			{
				CurrentTime -= LoopLength(); // We time travel immediately upon hitting the end of the loop, so we retain extra time after the time travel event to progress forward again
				over -= LoopLength();
				
				Notify(new TimeEvent(over >= 0 ? over : CurrentTime - LoopStart)); // Once we're back inside the time loop, we advance forward some amount past the time loop's start
			}
		}
		
		// If time ends, make sure we don't go sailing off into the void of a timeless realm
		if(TimeEnds && CurrentTime >= MaximumTime)
		{
			CurrentTime = MaximumTime;
			Pause();
			
			Notify(new TimeEvent());
		}
		
		// We can now handle segment change notifications
		int now = Segment();
		int old = Segment(PreviousTime);
		
		if(now != old)
			Notify(new TimeEvent(now,old));
		
		return;
	}
	
	public void Dispose()
	{
		for(IObserver<TimeEvent> eye : Observers)
			eye.OnCompleted();
		
		Disposed = true;
		return;
	}
	
	public boolean Disposed()
	{return Disposed;}
	
	/**
	 * Determines the current time segment.
	 * Note that although this is a log n operation, n is the number of time segments.
	 * As such, log n should always be tiny.
	 * @return Returns the current time segment.
	 */
	public int Segment()
	{return Segment(CurrentTime);}
	
	/**
	 * Determines the time segment containing {@code time}.
	 * @param time The time to look for its time segment.
	 * @return Returns the time segment containing {@code time}.
	 */
	public int Segment(long time)
	{return BinarySearch(time);}
	
	/**
	 * Performs a binary search for {@code time} in the time segments.
	 * @param time The time to search for.
	 * @return Returns -1 if {@code time} is negative and the time segment it belongs to otherwise.
	 */
	protected int BinarySearch(long time)
	{
		// Binary Search will always return a segment unless time is negative since we cover the entire nonnegative ray
		if(time < 0)
			return -1;
		
		// We'll do two special cases here so that writing the binary search doesn't require a weird special case
		if(Segmentations.size() == 0 || time < Segmentations.get(0))
			return 0;
		
		if(time >= Segmentations.get(Segmentations.size() - 1))
			return Segmentations.size();
		
		// Set up the l and r pointers
		int l = 0;
		int r = Segmentations.size() - 2;
		
		while(l <= r)
		{
			int m = (l + r) >> 1;
			
			if(time >= Segmentations.get(m + 1))
				l = m + 1;
			else if(time < Segmentations.get(m))
				r = m - 1;
			else
				return m + 1;
		}
		
		// This is impossible, but clearly Java hasn't solved the halting problem, so unreachable code shouldn't be an error
		// What a dumb language
		return -1;
	}
	
	/**
	 * Determines if the time segement {@code segment} contains the time {@code time}.
	 * @param segment The segment to look into.
	 * @param time The time to check {@code segment} for containment.
	 * @return Returns true if {@code segment} contains {@code time}.
	 */
	public boolean SegmentContainsTime(int segment, long time)
	{
		if(time < 0)
			return false;
		
		int size = Segmentations.size();
		
		if(segment == 0)
			return size == 0 || time < Segmentations.get(0);
		
		if(segment < 0 || segment > size)
			return false;
		
		if(segment == size)
			return time >= Segmentations.get(segment - 1);
		
		return time >= Segmentations.get(segment - 1) && time < Segmentations.get(segment);
	}
	
	/**
	 * Partitions an existing time segment into two.
	 * One begins at the previous beginning time.
	 * The other begins at {@code time}.
	 * @param time The time for the new time segment to begin. This value must be unique and nonnegative to have an effect.
	 * @return Returns true if the time segment was added and false otherwise.
	 */
	public boolean AddSegment(long time)
	{
		if(time <= 0L)
			return false;
		
		int containing_segment = Segment(time);
		
		if(containing_segment == 0 && time != 0)
		{
			Segmentations.add(0,time);
			return true;
		}
		
		if(Segmentations.get(containing_segment - 1) == time)
			return false;
		
		Segmentations.add(containing_segment,time);
		return true;
	}
	
	/**
	 * Gets the beginning time of the {@code index}th segment.
	 * @param index The segment to fetch the beginning time of.
	 * @return Returns the beginning time of the {@code index}th segment.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least {@code SegmentCount()}.
	 */
	public long GetSegment(int index)
	{
		if(index == 0)
			return 0;
		
		return Segmentations.get(index - 1);
	}
	
	/**
	 * Removes the segment beginning at time {@code time}.
	 * Note that the first segment beginning a time 0 can never be removed.
	 * @param time The time the segment to remove begins at.
	 * @return Returns true if the segment was found and removed and false otherwise.
	 */
	public boolean RemoveSegment(long time)
	{
		int seg = Segment(time);
		
		if(seg == 0)
			return false;
		
		if(Segmentations.get(seg - 1) != time)
			return false;
		
		Segmentations.remove(seg - 1);
		return true;
	}
	
	/**
	 * Removes a time segment.
	 * @param index The time segment to remove.
	 * @return Returns the time when the time segment began.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is less than 1 (the first segment always begins at 0) or if {@code index} is at least {@code SegmentCount()}.
	 */
	public long RemoveSegment(int index)
	{return Segmentations.remove(index - 1);}
	
	/**
	 * Determines the number of time segments in the time partition.
	 * @return Returns the number of time segements present.
	 */
	public int SegmentCount()
	{return 1 + Segmentations.size();}
	
	/**
	 * Causes time to be experienced starting at whatever the current time is.
	 */
	public void Play()
	{
		boolean ptemp = Play;
		Play = true;
		
		if(!ptemp)
			Notify(new TimeEvent(true,false,false));
		
		return;
	}
	
	/**
	 * Causes time to be experienced and sets the current time to the beginning of time segement {@code segment}.
	 * @param segment The time segment to start playing from.
	 * @throws IndexOutOfBoundsException Thrown if {@code index} is negative or at least {@code SegmentCount()}.
	 */
	public void Play(int segment)
	{
		Play(GetSegment(segment));
		return;
	}
	
	/**
	 * Causes time to be experienced and sets the current time to {@code start}.
	 * @param start
	 * The time to start experiencing time at.
	 * Setting the time this way does not cause time loops or time clamps to immediately occur.
	 * The normal course of time advancement will see to these events if they occur.
	 * @throws IllegalArgumentException Thrown if {@code start} is negative.
	 */
	public void Play(long start)
	{
		if(start < 0)
			throw new IllegalArgumentException();
		
		boolean ptemp = Play;
		long ttemp = CurrentTime;
		
		CurrentTime = start;
		Play = true;
		
		if(!ptemp)
			Notify(new TimeEvent(true,false,false));
		
		if(ttemp != start)
			Notify(new TimeEvent(start,ttemp));
		
		int now = Segment();
		int old = Segment(ttemp);
		
		if(now != old)
			Notify(new TimeEvent(now,old));
		
		return;
	}
	
	/**
	 * Causes time to no longer be experienced but keeps the current time fixed.
	 */
	public void Pause()
	{
		boolean ptemp = Play;
		Play = false;
		
		if(ptemp)
			Notify(new TimeEvent(false,true,false));
		
		return;
	}
	
	/**
	 * Causes time to no longer be experienced and the current time is set to 0.
	 */
	public void Stop()
	{
		boolean ptemp = Play;
		long ttemp = CurrentTime;
		
		Play = false;
		CurrentTime = 0L;
		
		if(ptemp)
			Notify(new TimeEvent(false,false,true));
		
		if(ttemp != 0L)
			Notify(new TimeEvent(0L,ttemp));
		
		int now = Segment();
		int old = Segment(ttemp);
		
		if(now != old)
			Notify(new TimeEvent(now,old));
		
		return;
	}
	
	/**
	 * Sets if time loops.
	 * @param loop If true, then time loops. If false, then time does not loop.
	 * @param start The (inclusive) start of the time loop. If {@code loop} is false, then this value is ignored.
	 * @param end
	 * The (exclusive) end of the time loop.
	 * If {@code loop} is false, then this value is ignored.
	 * Picking a loop end before the current time does not cause time to immediately loop.
	 * Time only loops if time advances through the end of the time loop in its ordinary course of motion.
	 * @throws IllegalArgumentException Thrown if {@code start} or {@code end} is negative or if {@code start} >= {@code end}.
	 */
	public void Loop(boolean loop, long start, long end)
	{
		if(loop && (start < 0L || end < 0L || start >= end))
			throw new IllegalArgumentException();
		
		Loops = loop;
		
		if(Loops)
		{
			LoopStart = start;
			LoopEnd = end;
		}
		
		return;
	}
	
	/**
	 * Sets if there is a maximum attainable time.
	 * If the maximum time is equal to the loop end, the time loop takes prioirty.
	 * @param time_ends If true, then there is a maximum attainable time value. If false, time goes on forever.
	 * @param EoT The (inclusive) maximum attainable time value. This value is ignored if {@code time_ends} is false.
	 * @throws IllegalArgumentException Thrown if {@code EoT} is negative.
	 */
	public void MaximumTime(boolean time_ends, long EoT)
	{
		if(time_ends && EoT < 0L)
			throw new IllegalArgumentException();
		
		TimeEnds = time_ends;
		
		if(TimeEnds)
		{
			MaximumTime = EoT;
		
			if(CurrentTime >= MaximumTime)
			{
				CurrentTime = MaximumTime;
				Pause();
				
				Notify(new TimeEvent());
			}
		}
		
		return;
	}
	
	public void Subscribe(IObserver<TimeEvent> eye)
	{
		if(eye == null)
			throw new NullPointerException();
		
		Observers.add(eye);
		return;
	}
	
	public void Unsubscribe(IObserver<TimeEvent> eye)
	{
		if(eye == null)
			throw new NullPointerException();
		
		Observers.remove(eye);
		return;
	}
	
	/**
	 * Notifies all observers about the event {@code e}.
	 * @param e The time event.
	 */
	protected void Notify(TimeEvent e)
	{
		for(IObserver<TimeEvent> eye : Observers)
			eye.OnNext(e);
		
		return;
	}
	
	@Override public String toString()
	{
		String ret = "{0";
		
		for(Long l : Segmentations)
			ret += ", " + l;
		
		return ret + "}";
	}
	
	/**
	 * Determines if time is being experienced.
	 * @return Returns true if time is being experienced and false otherwise.
	 */
	public boolean Playing()
	{return Play;}
	
	/**
	 * Determines if time is not being experienced.
	 * @return Returns true if time is not being experienced and false otherwise.
	 */
	public boolean Paused()
	{return !Playing();}
	
	/**
	 * The current time according to the time partition.
	 */
	public long CurrentTime()
	{return CurrentTime;}
	
	/**
	 * The total elapsed time according to the time partition.
	 */
	public long ElapsedTime()
	{return ElapsedTime;}
	
	/**
	 * Determines if time is looping.
	 * @return Returns true if time is looping and false otherwise. 
	 */
	public boolean Looping()
	{return Loops;}
	
	/**
	 * Determines when the time loop starts (if there is a time loop).
	 * This value is inclusive.
	 * @return Returns the start time of the time loop.
	 */
	public long LoopStart()
	{return LoopStart;}
	
	/**
	 * Determines when the time loop ends (if there is a time loop).
	 * This value is exclusive.
	 * @return Returns the end time of the time loop.
	 */
	public long LoopEnd()
	{return LoopEnd;}
	
	/**
	 * Determines the length of the time loop (if there is a time loop).
	 * @return Returns the length of the time loop.
	 */
	public long LoopLength()
	{return LoopEnd - LoopStart;}
	
	/**
	 * Determines if time ends.
	 * @return Returns true if there is a maximum attainable current time and false otherwise.
	 */
	public boolean DoesTimeEnd()
	{return TimeEnds;}
	
	/**
	 * Determines the maximum attainable current time (if time ends).
	 * @return Returns the maximum attainable current time.
	 */
	public long EndOfTime()
	{return MaximumTime;}
	
	/**
	 * The current time.
	 */
	protected long CurrentTime;
	
	/**
	 * The total elapsed time experienced.
	 */
	protected long ElapsedTime;
	
	/**
	 * If true, then time loops.
	 */
	protected boolean Loops;
	
	/**
	 * If time loops, this is when the time loop starts.
	 */
	protected long LoopStart;
	
	/**
	 * If time loops, this is when the time loop ends.
	 * This value is exclusive.
	 */
	protected long LoopEnd;
	
	/**
	 * If true, then there is a maximum time attainable.
	 */
	protected boolean TimeEnds;
	
	/**
	 * If time ends, then this is the maximum time attainable.
	 * This value is inclusive.
	 */
	protected long MaximumTime;
	
	/**
	 * If true, then time is experienced.
	 * If false, time is not experienced.
	 */
	protected boolean Play;
	
	/**
	 * If true, this component is initialized.
	 */
	protected boolean Initialized;
	
	/**
	 * If true, this component has been disposed of.
	 */
	protected boolean Disposed;
	
	/**
	 * The times when a new time segment beings.
	 * The last time segement carries on to infinity.
	 */
	protected ArrayList<Long> Segmentations;
	
	/**
	 * The observers of this timeline.
	 */
	protected LinkedList<IObserver<TimeEvent>> Observers;
	
	/**
	 * Represents something happening in the timeline.
	 * @author Dawn Nye
	 */
	public static class TimeEvent
	{
		/**
		 * Creates an end of time event.
		 */
		protected TimeEvent()
		{
			Type = EventType.EOT;
			
			NewTimeSegement = 0;
			OldTimeSegement = 0;
			JumpTo = 0L;
			JumpFrom = 0L;
			TimeRemaining = 0L;
			
			return;
		}
		
		/**
		 * Creates a time event for passing into a new time segment.
		 * @param segment The time segment advanced into.
		 * @param old The time segment advanced out of.
		 */
		protected TimeEvent(int segment, int old)
		{
			NewTimeSegement = segment;
			OldTimeSegement = old;
			Type = EventType.SEGMENT_CHANGE;
			
			JumpTo = 0L;
			JumpFrom = 0L;
			TimeRemaining = 0L;
			
			return;
		}
		
		/**
		 * Creates a time event for when a non-time loop jump is made.
		 * @param to The time jumped to.
		 * @param from The time jumped from.
		 */
		protected TimeEvent(long to, long from)
		{
			JumpTo = to;
			JumpFrom = from;
			Type = EventType.JUMP;
			
			NewTimeSegement = 0;
			OldTimeSegement = 0;
			TimeRemaining = 0L;
			
			return;
		}
		
		/**
		 * Creates a time event for a time loop jump or the end of time.
		 * @param remaining The remaining time left to advance after the time jump has been made.
		 */
		protected TimeEvent(long remaining)
		{
			TimeRemaining = remaining;
			Type = EventType.LOOP;
			
			NewTimeSegement = 0;
			OldTimeSegement = 0;
			JumpTo = 0L;
			JumpFrom = 0L;
			
			return;
		}
		
		/**
		 * Creates a time event for time starting, pausing, or stopping.
		 * @param play If true, time started.
		 * @param pause If true, time paused.
		 * @param stop If true, then time stopped.
		 */
		protected TimeEvent(boolean play, boolean pause, boolean stop)
		{
			if(play && !pause && !stop)
				Type = EventType.PLAY;
			else if(!play && pause && !stop)
				Type = EventType.PAUSE;
			else if(!play && !pause && stop)
				Type = EventType.STOP;
			else
				Type = EventType.INVALID;
			
			NewTimeSegement = 0;
			OldTimeSegement = 0;
			JumpTo = 0L;
			JumpFrom = 0L;
			TimeRemaining = 0L;
			
			return;
		}
		
		@Override public String toString()
		{
			String ret = null;
			
			switch(Type)
			{
			case PLAY:
				ret = "This is a play event.";
				break;
			case PAUSE:
				ret = "This is a pause event.";
				break;
			case STOP:
				ret = "This is a stop event.";
				break;
			case LOOP:
				ret = "This is a time loop event with " + TimeRemaining + " time remaining to process.";
				break;
			case JUMP:
				ret = "This is a time jump event from time " + JumpFrom + " to time " + JumpTo + ".";
				break;
			case SEGMENT_CHANGE:
				ret = "This is a time segment change event from segment " + OldTimeSegement + " to segment " + NewTimeSegement + ".";
				break;
			case EOT:
				ret = "This is an end of time event.";
				break;
			default:
				ret = "This is an invalid time event.";
				break;
			}
			
			return ret;
		}
		
		/**
		 * True if this is a play event and false otherwise.
		 */
		public boolean IsPlayEvent()
		{return Type == EventType.PLAY;}
		
		/**
		 * True if this is a pause event and false otherwise.
		 */
		public boolean IsPauseEvent()
		{return Type == EventType.PAUSE;}
		
		/**
		 * True if this is a play event and false otherwise.
		 */
		public boolean IsStopEvent()
		{return Type == EventType.STOP;}
		
		/**
		 * True if this is a time loop event and false otherwise.
		 */
		public boolean IsTimeLoop()
		{return Type == EventType.LOOP;}
		
		/**
		 * True if this is a time jump (not loop) event and false otherwise.
		 */
		public boolean IsTimeJump()
		{return Type == EventType.JUMP;}
		
		/**
		 * True if this is a time segment change event and false otherwise.
		 */
		public boolean IsSegmentChange()
		{return Type == EventType.SEGMENT_CHANGE;}
		
		/**
		 * True if this is an end of time event and false otherwise.
		 */
		public boolean IsEndOfTime()
		{return Type == EventType.EOT;}
		
		/**
		 * Determines the event type of this time event.
		 * @return Returns the type of even this time event is.
		 */
		public EventType EventType()
		{return Type;}
		
		/**
		 * The time segment time is now in during a segement change event.
		 * This valud is undefined otherwise.
		 */
		public final int NewTimeSegement;
		
		/**
		 * The time segment time was in during a segement change event.
		 * This valud is undefined otherwise.
		 */
		public final int OldTimeSegement;
		
		/**
		 * The time jumped to in a time jump (not loop) event.
		 * This value is undefined otherwise.
		 */
		public final long JumpTo;
		
		/**
		 * The time left in a time jump (not loop) event.
		 * This value is undefined otherwise.
		 */
		public final long JumpFrom;
		
		/**
		 * The time remaining to process in a time loop event.
		 * This value is undefined otherwise.
		 */
		public final long TimeRemaining;
		
		protected EventType Type;
		
		public static enum EventType
		{
			INVALID,
			PLAY,
			PAUSE,
			STOP,
			LOOP,
			JUMP,
			SEGMENT_CHANGE,
			EOT
		}
	}
	
	/**
	 * A time exception for when time is a little too timey-wimey.
	 * @author Dawn Nye
	 */
	public class TimeException extends Exception
	{
		/**
		 * Creates a blank time exception with no message.
		 */
		protected TimeException()
		{
			super();
			return;
		}
		
		/**
		 * Creates a time exception with the provided message.
		 * @param msg The message with this exception.
		 */
		protected TimeException(String msg)
		{
			super(msg);
			return;
		}
	}
}
