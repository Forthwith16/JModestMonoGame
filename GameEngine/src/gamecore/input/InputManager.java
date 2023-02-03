package gamecore.input;

import java.util.NoSuchElementException;

import gamecore.GlobalConstants;
import gamecore.IUpdatable;
import gamecore.datastructures.Dictionary;
import gamecore.datastructures.LinkedList;
import gamecore.time.TimePartition;

/**
 * Manages and maps input and keeps track of additional state information for registered inputs.
 * Note that this should update each frame before anything that requires input information from it.
 * Also, this approach has the distinct advantage that all inputs are checked in a small, contiguous amount of time, thus making input desynchronization issues (hopefully) nonexistent.
 * @author Dawn Nye
 */
public class InputManager implements IUpdatable
{
	/**
	 * Creates a new input manager.
	 */
	public InputManager()
	{
		Inputs = new Dictionary<String,DictionaryEntry>();
		Clock = new TimePartition();
		
		return;
	}
	
	public void Initialize()
	{
		Clock.Initialize();
		LatestTime = Clock.ElapsedTime();
		
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
		LatestTime = Clock.ElapsedTime();
		
		for(DictionaryEntry t : Inputs.Values())
		{
			boolean b = t.Evaluate();
			
			// !b will be true more often, so put its check first
			if(!b && t.RawSatisfied())
				t.WhenUnsatisfied = LatestTime;
			else if(b && t.RawUnsatisfied())
				t.WhenSatisfied = LatestTime;
			else if(!t.RawSatisfied() && !t.RawUnsatisfied())
				t.WhenUnsatisfied = LatestTime; // A failsafe to avoid deadlock
		}
		
		return;
	}

	public void Dispose()
	{
		Disposed = true;
		return;
	}

	public boolean Disposed()
	{return Disposed;}
	
	/**
	 * Registers an input formula under the given name with the given expression.
	 * @param input The input name.
	 * @param func The expression to evaluate to check if it's satisfied (ORed with any others attached to the name {@code input}). Null will not be added but will create an input if the given one doesn't exist yet.
	 * @return Returns true if the input could be added, that is if {@code func} is not a duplicate {@code InputFunction} under the same name.
	 */
	public boolean AddInput(String input, InputFunction func)
	{
		DictionaryEntry t = null;
		
		// If this is a new input, just add it
		if(!Inputs.ContainsKey(input))
		{
			t = new DictionaryEntry(input,LatestTime);
			t.Formula = new LinkedList<InputFunction>();

			if(func != null) // Special case: we allow inputs to be created without anything to satisfy them, so it's fine to use null for func
				t.Formula.AddFront(func);
			
			Inputs.Add(input,t);
			return true;
		}
		else if(func == null)
			return false;
		else
			t = Inputs.Get(input);
		
		// We now should make sure we're not trying to add a duplicate
		if(t.Formula.contains(func))
			return false;
		
		t.Formula.AddLast(func);
		return true;
	}
	
	/**
	 * Registers an input formula under the given name with the given expression.
	 * This version wraps additional logic around it so that the input is satisfied once when {@code func} is first satisfied and then is unsatisfied until {@code func} is no long satisfied and then satisfied again.
	 * If {@code satisfied} is false, then this logic is inverted so that {@code func} must be unsatisfied to make this input satisfied once and then {@code func} must be satisfied and then unsatisfied before this input is satisfied again. 
	 * @param input The input name.
	 * @param func The expression to evaluate to check if it's satisfied (ORed with any others attached to the name {@code input}). Null will not be added but will create an input if the given one doesn't exist yet.
	 * @param satisfied If true, then this input is satisfied only when {@code func} is first satisfied. If false, then this input is satisfied only when {@code func} is first unsatisfied.
	 * @return Returns true if the input could be added, that is if {@code func} is not a duplicate {@code InputFunction} under the same name.
	 */
	public boolean AddInput(String input, InputFunction func, boolean satisfied)
	{
		DictionaryEntry t = null;
		
		// We first create the modified input function
		TypeWrapper wrapper = new TypeWrapper(satisfied ? func.Evaluate() : !func.Evaluate());
		InputFunction type = () ->
						 {
							boolean val = func.Evaluate();
							
							// If our input is ready, then we should check if we're either satisfied or unsatisfied as we desire
							if(wrapper.Ready)
								if(satisfied && val)
								{
									wrapper.Ready = false;
									return true;
								}
								else if(!satisfied && !val)
								{
									wrapper.Ready = false;
									return true;
								}
								else
									return false;
							
							// Our input is not ready, so see if it's ready to be ready
							if(satisfied && !val || !satisfied && val)
								wrapper.Ready = true;
							
							return false;
						 };
		
		// If this is a new input, just add it
		if(!Inputs.ContainsKey(input))
		{
			t = new DictionaryEntry(input,LatestTime);
			t.Formula = new LinkedList<InputFunction>();

			if(func != null) // Special case: we allow inputs to be created without anything to satisfy them, so it's fine to use null for func
				t.Formula.AddFront(type);
			
			Inputs.Add(input,t);
			return true;
		}
		else if(func == null)
			return false;
		else
			t = Inputs.Get(input);
		
		// We now should make sure we're not trying to add a duplicate
		if(t.Formula.contains(type))
			return false;
		
		t.Formula.AddLast(type);
		return true;
	}
	
	/**
	 * Removes the entire input {@code input} from the input manager.
	 * @param input The input name.
	 * @return Returns true if the expression was removed and false otherwise.
	 */
	public boolean RemoveInput(String input)
	{return RemoveInput(input,null);}
	
	/**
	 * Removes the given expression from the input formula with the given name.
	 * @param input The input name.
	 * @param func The expression to remove from the input. If this is null, the entire input is removed.
	 * @return Returns true if the expression was removed and false otherwise.
	 */
	public boolean RemoveInput(String input, InputFunction func)
	{
		if(func == null)
			return Inputs.Remove(input);
		
		// If we don't have this input, we fail
		if(!Inputs.ContainsKey(input))
			return false;
		
		return Inputs.Get(input).Formula.remove(func);
	}

	/**
	 * Gets all of the current input bindings.
	 * @return Returns a list of all input bindings.
	 * The returned value is "safe" in the sense that it modifying it will not modify the input manager.
	 * They are not garunteed to appear in any particular order.
	 */
	public Iterable<String> GetInputBindings()
	{
		LinkedList<String> ret = new LinkedList<String>();
		
		for(String key : Inputs.Keys())
			ret.AddLast(key);

		return ret;
	}
	
	/**
	 * Checks if the input is satisfied.
	 * @param input The input to check.
	 * @return Returns true if the input is satisfied and false if it is not
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean InputSatisfied(String input)
	{return InputSatisfied(Inputs.Get(input));}
	
	/**
	 * Checks if the input is satisfied.
	 * @param input The input to check.
	 * @return Returns true if the input is satisfied and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean GracelessInputSatisfied(String input)
	{return GracelessInputSatisfied(Inputs.Get(input));}
	
	/**
	 * Checks if the input is satisfied and was first satisfied after {@code time}.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The time to satisfy the input after.
	 * @return Returns true if the input is satisfied and was first satisfied after the given time and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean InputSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return InputSatisfied(t) && (GlobalConstants.ALLOW_INPUT_GRACE ? t.WhenSatisfied > time - GlobalConstants.INPUT_GRACE_TIME : t.WhenSatisfied > time);
	}
	
	/**
	 * Checks if the input is satisfied and was first satisfied after the given time.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The time to satisfy the input after expressed in milliseconds.
	 * @return Returns true if the input is satisfied and was first satisfied after the given time. Returns false otherwise.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean GracelessInputSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return GracelessInputSatisfied(t) && t.WhenSatisfied > time;
	}
	
	/**
	 * Checks if the given input is satisfied based on its time stamps.
	 * Input grace times are permitted if enabled. 
	 */
	protected boolean InputSatisfied(DictionaryEntry t)
	{return GlobalConstants.ALLOW_INPUT_GRACE ? t.RawSatisfied() || LengthUnsatisfied(t.Name) < GlobalConstants.INPUT_GRACE_TIME : t.RawSatisfied();}
	
	/**
	 * Checks if the given input is satisfied based exclusively on its time stamps.
	 */
	protected boolean GracelessInputSatisfied(DictionaryEntry t)
	{return t.RawSatisfied();}
	
	/**
	 * Gets the length of time that the given input has been satisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been satisfied or a negative value if it's not currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LengthSatisfied(String input)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - t.WhenSatisfied : -1L;
	}
	
	/**
	 * Gets the length of time that the given input has been satisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been satisfied or a negative value if it's not currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLengthSatisfied(String input)
	{return LengthSatisfied(input);}
	
	/**
	 * Gets the length of time that the given input has been satisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input satisfaction. This value can be negative depending on the value of {@code time}
	 * @return Returns the length of time the input has been satisfied or a negative value if it's not currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LengthSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - Math.max(t.WhenSatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time) : -1L;
	}
	
	/**
	 * Gets the length of time that the given input has been satisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input satisfaction. This value can be negative depending on the value of {@code time}.
	 * @return Returns the length of time the input has been satisfied or a negative value if it's not currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLengthSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - Math.max(t.WhenSatisfied,time) : -1L;
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is satisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LatestLengthSatisfied(String input)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - t.WhenSatisfied : t.WhenUnsatisfied - t.WhenSatisfied;
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is satisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLatestLengthSatisfied(String input)
	{return LatestLengthSatisfied(input);}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is satisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input satisfaction.
	 * @return Returns the length of time the input has been satisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LatestLengthSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - Math.max(t.WhenSatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time) : t.WhenUnsatisfied - Math.max(t.WhenSatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time);
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is satisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input satisfaction.
	 * @return Returns the length of time the input has been satisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLatestLengthSatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawSatisfied() ? LatestTime - Math.max(t.WhenSatisfied,time) : t.WhenUnsatisfied - Math.max(t.WhenSatisfied,time);
	}
	
	/**
	 * Gets the timestamp of the latest time the given input was first satisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the time when the given input was first satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long WhenSatisfied(String input)
	{return Inputs.Get(input).WhenSatisfied;}
	
	/**
	 * Checks if the input is unsatisfied.
	 * @param input The input to check.
	 * @return Returns true if the input is unsatisfied and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean InputUnsatisfied(String input)
	{return InputUnsatisfied(Inputs.Get(input));} // We could hardcode a marginally faster solution, but it really doesn't matter and this is better coding practice
	
	/**
	 * Checks if the input is unsatisfied.
	 * @param input The input to check.
	 * @return Returns true if the input is unsatisfied and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean GracelessInputUnsatisfied(String input)
	{return GracelessInputUnsatisfied(Inputs.Get(input));} // We could hardcode a marginally faster solution, but it really doesn't matter and this is better coding practice
	
	/**
	 * Checks if the input is unsatisfied and was first unsatisfied after the given time.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The time to unsatisfy the input after.
	 * @return Returns true if the input is unsatisfied and was first unsatisfied after the given time and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean InputUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return InputUnsatisfied(t) && (GlobalConstants.ALLOW_INPUT_GRACE ? t.WhenUnsatisfied > time - GlobalConstants.INPUT_GRACE_TIME : t.WhenUnsatisfied > time);
	}
	
	/**
	 * Checks if the input is unsatisfied and was first unsatisfied after the given time.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The time to unsatisfy the input after.
	 * @return Returns true if the input is unsatisfied and was first unsatisfied after the given time and false if it is not.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public boolean GracelessInputUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return GracelessInputUnsatisfied(t) && t.WhenUnsatisfied > time;
	}
	
	/**
	 * Checks if the given input is unsatisfied based on its time stamps.
	 */
	protected boolean InputUnsatisfied(DictionaryEntry t)
	{return GlobalConstants.ALLOW_INPUT_GRACE ? t.RawUnsatisfied() || LengthSatisfied(t.Name) < GlobalConstants.INPUT_GRACE_TIME : t.RawUnsatisfied();}
	
	/**
	 * Checks if the given input is unsatisfied based on its time stamps.
	 */
	protected boolean GracelessInputUnsatisfied(DictionaryEntry t)
	{return t.RawUnsatisfied();}
	
	/**
	 * Gets the length of time that the given input has been unsatisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been unsatisfied or a negative value if it's currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LengthUnsatisfied(String input)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - t.WhenUnsatisfied : -1L;
	}
	
	/**
	 * Gets the length of time that the given input has been unsatisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been unsatisfied or a negative value if it's currently satisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLengthUnsatisfied(String input)
	{return LengthUnsatisfied(input);}
	
	/**
	 * Gets the length of time that the given input has been unsatisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input unsatisfaction.
	 * @return Returns the length of time the input has been unsatisfied or a negative value if it's currently satisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LengthUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - Math.max(t.WhenUnsatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time) : -1L;
	}
	
	/**
	 * Gets the length of time that the given input has been unsatisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input unsatisfaction.
	 * @return Returns the length of time the input has been unsatisfied or a negative value if it's currently satisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLengthUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - Math.max(t.WhenUnsatisfied,time) : -1L;
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is unsatisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been unsatisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LatestLengthUnsatisfied(String input)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - t.WhenUnsatisfied : t.WhenSatisfied - t.WhenUnsatisfied;
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is unsatisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the length of time the input has been unsatisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLatestLengthUnsatisfied(String input)
	{return LatestLengthUnsatisfied(input);}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is unsatisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input unsatisfaction.
	 * @return Returns the length of time the input has been unsatisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long LatestLengthUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - Math.max(t.WhenUnsatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time) : t.WhenSatisfied - Math.max(t.WhenUnsatisfied,GlobalConstants.ALLOW_INPUT_GRACE ? time - GlobalConstants.INPUT_GRACE_TIME : time);
	}
	
	/**
	 * Gets the latest length of time that the given input has been satisfied whether it currently is unsatisfied or not.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @param time The leftward cutoff time for input unsatisfaction.
	 * @return Returns the length of time the input has been unsatisfied. This value can be negative depending on the value of {@code time}.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long GracelessLatestLengthUnsatisfied(String input, long time)
	{
		DictionaryEntry t = Inputs.Get(input);
		return t.RawUnsatisfied() ? LatestTime - Math.max(t.WhenUnsatisfied,time) : t.WhenSatisfied - Math.max(t.WhenUnsatisfied,time);
	}
	
	/**
	 * Gets the timestamp of the latest time the given input was first unsatisfied.
	 * All times are in terms of milliseconds.
	 * @param input The input to check.
	 * @return Returns the time when the given input was first unsatisfied.
	 * @throws NoSuchElementException Thrown if the input is not registered with the input manager.
	 */
	public long WhenUnsatisfied(String input)
	{return Inputs.Get(input).WhenUnsatisfied;}
	
	/**
	 * The latest time at the beginning of the latest update cycle.
	 * This information is kept as total elapsed time rather than an absolute system time.
	 */
	protected long LatestTime;
	
	/**
	 * The source of our timing information.
	 */
	protected TimePartition Clock;
	
	/**
	 * Contains a mapping from input names to a triple containing a list of functions that satisfy the input along with a time stamp for the last time it was true and false respectively.
	 * The state of the input can be determined by comparing the two timestamps to see which one is latest. Preference should go to the unsatisfied state (false).
	 */
	protected Dictionary<String,DictionaryEntry> Inputs;
	
	/**
	 * If true, then this game component is initialized.
	 */
	protected boolean Initialized;
	
	/**
	 * If true, then this game component has been disposed.
	 */
	protected boolean Disposed;
	
	/**
	 * Used to store a dictionary entry. 
	 * @author Dawn Nye
	 */
	protected class DictionaryEntry
	{
		public DictionaryEntry(String name, long t)
		{
			Formula = new LinkedList<InputFunction>();
			Name = name;
			
			// We will always initialize inputs to an unsatisfied state with the minimal length of assumed unsatisfaction possible
			// We will also ensure that the length of unsatisfaction also exceeds the grace period if enabled
			WhenSatisfied = GlobalConstants.ALLOW_INPUT_GRACE ? t - GlobalConstants.INPUT_GRACE_TIME : t - 1;
			WhenUnsatisfied = t;
			
			return;
		}
		
		/**
		 * Evaluates the formula of this input.
		 * @return Returns true if any of the formula's components are true and false only if they are all false.
		 */
		public boolean Evaluate()
		{
			for(InputFunction func : Formula)
				if(func.Evaluate())
					return true;
			
			return false;
		}
		
		/**
		 * The individual components of what it means to satisfy the input (in an OR relationship).
		 */
		public LinkedList<InputFunction> Formula;
		
		/**
		 * The name of the input because we sometimes need this information and don't want to crowd things with useless parameters.
		 */
		public String Name;
		
		/**
		 * The timestamp for when this input was most recently first satisfied.
		 */
		public long WhenSatisfied;
		
		/**
		 * The timestamp for when this input was most recently first unsatisfied.
		 */
		public long WhenUnsatisfied;
		
		/**
		 * True if this input is satisfied without any additional processing.
		 */
		public boolean RawSatisfied()
		{return WhenSatisfied > WhenUnsatisfied;}
		
		/**
		 * True if this input is unsatisfied without any additional processing.
		 */
		public boolean RawUnsatisfied()
		{return WhenUnsatisfied > WhenSatisfied;}
	}
	
	/**
	 * A wrapper around a keypress to allow us remember if a key was pressed or not.
	 * @author Dawn Nye
	 */
	protected class TypeWrapper
	{
		public TypeWrapper(boolean ready)
		{
			Ready = ready;
			return;
		}
		
		public boolean Ready;
	}
	
	/**
	 * An input function that checks the state of the system and evaluates to satisfied or not satisfied.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface InputFunction
	{
		/**
		 * Evaluates the state of the system to determine if this input is satisfied.
		 * @return Returns true if the input condition is satisfied and false otherwise.
		 */
		public abstract boolean Evaluate();
	}
}
