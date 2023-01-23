package gamecore.input;

import java.util.NoSuchElementException;

import gamecore.datastructures.Dictionary;
import gamecore.datastructures.LinkedList;

/**
 * Maps string names to raw input commands.
 * This allows easy reuse and remapping.
 * For example, we map have some input "Up" which we map to the w key.
 * We could alter the mapping to instead map "Up" to the up arrow key or even both the up arrow and the w key.
 * Then every object using the input map will be aware of the change without having to modify itsown behavior.
 * @author Dawn Nye
 */
public class InputMap
{
	/**
	 * Obtains the singleton instance of this class.
	 * @return Returns the singleton instance of this class.
	 */
	public static InputMap Map()
	{
		if(_m == null)
			_m = new InputMap();
		
		return _m;
	}
	
	/**
	 * Initializes the input map.
	 */
	protected InputMap()
	{
		map = new Dictionary<String,InputBinding>();
		return;
	}
	
	/**
	 * Adds an unbound binding with the given name.
	 * It always has a digital value of false and an analogue value of 0.0.
	 * If a binding with the given name already exists, it is overwritten.
	 * @param name The name of the binding.
	 */
	public void AddEmptyBinding(String name)
	{
		map.Put(name,new InputBinding(name,false));
		return;
	}
	
	/**
	 * Adds an unbound binding with the given name.
	 * It has a constant value specified by {@code satisfied}.
	 * If a binding with the given name already exists, it is overwritten.
	 * @param name The name of the binding.
	 * @param satisfied If true, the input is always satisfied. If false, it is never satisfied.
	 */
	public void AddConstantBinding(String name, boolean satisfied)
	{
		map.Put(name,new InputBinding(name,satisfied));
		return;
	}
	
	/**
	 * Adds an unbound binding with the given name.
	 * It has a constant value specified by {@code b_value} and {@code a_value}.
	 * If a binding with the given name already exists, it is overwritten.
	 * @param name The name of the binding.
	 * @param b_value If true, the input is always satisfied. If false, it is never satisfied.
	 * @param a_value This is the analogue value this binding always produces.
	 */
	public void AddConstantBinding(String name, boolean b_value, double a_value)
	{
		map.Put(name,new InputBinding(name,b_value,a_value));
		return;
	}
	
	/**
	 * Adds a key binding.
	 * If a binding with the given name already exists, it is overwritten.
	 * @param name The name of the binding.
	 * @param key The key to bind. The values {@code key} can take are given in KeyEvent by Java's VK values.
	 */
	public void AddKeyBinding(String name, int key)
	{
		map.Put(name,new InputBinding(name,key));
		return;
	}
	
	/**
	 * Combines two bindings (without eliminating their original bindings) as follows.
	 * The digital evaluation is the logical and of both bindings.
	 * The analogue evaluation is the min of both bindings.
	 * @param name The name of the new binding (must be distinct from the provided bindings).
	 * @param binding_a The name of the first binding to AND.
	 * @param binding_b The name of the second binding to AND.
	 */
	public void AddANDBinding(String name, String binding_a, String binding_b)
	{
		if(name == binding_a || name == binding_b)
			return;
		
		map.Put(name,new InputBinding(name,() -> map.Get(binding_a).DigitalEvaluation.Evaluate() && map.Get(binding_b).DigitalEvaluation.Evaluate(),
				() -> Math.min(map.Get(binding_a).AnalogueEvaluation.Evaluate(),map.Get(binding_b).AnalogueEvaluation.Evaluate())));
		return;
	}
	
	/**
	 * Combines two bindings (without eliminating their original bindings) as follows.
	 * The digital evaluation is the logical or of both bindings.
	 * The analogue evaluation is the max of both bindings. 
	 * @param name The name of the new binding (must be distinct from the provided bindings).
	 * @param binding_a The name of the first binding to OR.
	 * @param binding_b The name of the second binding to OR.
	 */
	public void AddORBinding(String name, String binding_a, String binding_b)
	{
		if(name == binding_a || name == binding_b)
			return;
		
		map.Put(name,new InputBinding(name,() -> map.Get(binding_a).DigitalEvaluation.Evaluate() || map.Get(binding_b).DigitalEvaluation.Evaluate(),
									() -> Math.max(map.Get(binding_a).AnalogueEvaluation.Evaluate(),map.Get(binding_b).AnalogueEvaluation.Evaluate())));
		return;
	}
	
	/**
	 * Inverts a binding (without eliminating its original binding) as follows.
	 * The digital evaluation is the logical not of the binding.
	 * The analogue evaluation is the additive inverse of the binding. 
	 * @param name The name of the new binding (must be distinct from the provided binding).
	 * @param binding The name of the first binding to OR.
	 */
	public void AddInverseBinding(String name, String binding)
	{
		if(name == binding)
			return;
		
		map.Put(name,new InputBinding(name,() -> !map.Get(binding).DigitalEvaluation.Evaluate(),() -> -map.Get(binding).AnalogueEvaluation.Evaluate()));
		return;
	}
	
	/**
	 * Creates an alias for another binding.
	 * @param name The name of the alias (must be distinct from the original binding).
	 * @param binding The name of the binding to create an alias for.
	 */
	public void AddAlias(String name, String binding)
	{
		if(name == binding)
			return;
		
		// We don't just use the evaluations themselves because we want to keep track of changes
		// This does slow things down a bit and requires that they prerequisite bindings don't disappear, but this is reasonable 
		map.Put(name,new InputBinding(name,() -> map.Get(binding).DigitalEvaluation.Evaluate(),() -> map.Get(binding).AnalogueEvaluation.Evaluate()));
		
		return;
	}
	
	/**
	 * Removes the given binding.
	 * @param name The name of the binding to remove.
	 * @return Returns true if the binding was removed and false otherwise.
	 */
	public boolean RemoveBinding(String name)
	{return map.Remove(name);}
	
	/**
	 * Checks to see if the given binding exists.
	 * @param name The binding name to check for.
	 * @return Returns true if the input map contains a binding named {@code name} and false otherwise.
	 */
	public boolean ContainsBinding(String name)
	{return map.ContainsKey(name);}
	
	/**
	 * Removes all bindings from the map.
	 */
	public void Clear()
	{
		map.Clear();
		return;
	}
	
	/**
	 * Gets all of the current bindings.
	 * @return Returns a collection of input bindings.
	 */
	public Iterable<String> GetBindings()
	{
		LinkedList<String> ret = new LinkedList<String>();
		
		for(String key : map.Keys())
			ret.AddLast(key);

		return ret;
	}
	
	/**
	 * Evaluates a binding digitally.
	 * @param name The name of the binding to evaluate.
	 * @return True if the binding is satisfied and false otherwise.
	 * @throws NoSuchElementException Thrown if key does not exist in the dictionary.
	 */
	public boolean EvaluateDigital(String name)
	{return GetBinding(name).DigitalEvaluation.Evaluate();}
	
	/**
	 * Evaluates a binding in an analogue manner.
	 * @param name The name of the binding to evaluate.
	 * @return The current value of the binding.
	 * @throws NoSuchElementException Thrown if key does not exist in the dictionary.
	 */
	public double EvaluateAnalogue(String name)
	{return GetBinding(name).AnalogueEvaluation.Evaluate();}
	
	/**
	 * Retrieves the input binding with the specified name.
	 * @param name The binding to fetch.
	 * @return The input binding with the name {@code name}.
	 * @throws NoSuchElementException Thrown if key does not exist in the dictionary.
	 */
	public InputBinding GetBinding(String name)
	{return map.Get(name);}
	
	/**
	 * The mapping from strings to bindings.
	 */
	private final Dictionary<String,InputBinding> map;
	
	/**
	 * The singleton instance of this class.
	 */
	private static InputMap _m;
	
	/**
	 * Encapsulates an input binding.
	 * @author Dawn Nye
	 */
	public class InputBinding
	{
		/**
		 * Creates an empty input binding that always evaluates to false or 0.0.
		 * @param name The name of the binding.
		 */
		public InputBinding(String name)
		{
			Name = name;
			
			BoundKey = VK_NONE;
			
			DigitalEvaluation = () -> false;
			AnalogueEvaluation = () -> 0.0;
			
			return;
		}
		
		/**
		 * Creates an empty input binding that always has the provided constant value.
		 * @param name The name of the binding.
		 * @param value DigitalEvaluation resolves to this while AnalogueEvaluation resolves to 0.0f for false and 1.0f for true.
		 */
		public InputBinding(String name, boolean value)
		{
			Name = name;
			
			BoundKey = VK_NONE;
			
			DigitalEvaluation = () -> value;
			AnalogueEvaluation = () -> (value ? 1.0 : 0.0);
			
			return;
		}
		
		/**
		 * Creates an empty input binding that always has the provided constant values.
		 * @param name The name of the binding.
		 * @param b_value DigitalEvaluation resolves to this.
		 * @param a_value AnalogueEvaluation resolves to this.
		 */
		public InputBinding(String name, boolean b_value, double a_value)
		{
			Name = name;
			
			BoundKey = VK_NONE;
			
			DigitalEvaluation = () -> b_value;
			AnalogueEvaluation = () -> a_value;
			
			return;
		}
		
		/**
		 * Creates a new input binding from a key.
		 * @param name The name of the binding.
		 * @param key The key bound. The key values are given by KeyEvent's VK values.
		 */
		public InputBinding(String name, int key)
		{
			Name = name;
			
			BoundKey = key;
			
			DigitalEvaluation = () -> KeyboardStateMonitor.GetState().IsKeyPressed(key);
			AnalogueEvaluation = () -> KeyboardStateMonitor.GetState().IsKeyPressed(key) ? 1.0f : 0.0f;
			
			return;
		}
		
		/**
		 * Creates a new input binding with custom evaluation functions.
		 * @param name The name of the binding.
		 * @param digital The digital evaluation.
		 * @param analogue The analogue evaluation.
		 */
		public InputBinding(String name, InputDigitalValue digital, InputAnalogueValue analogue)
		{
			Name = name;
			
			BoundKey = VK_NONE;
			
			DigitalEvaluation = digital;
			AnalogueEvaluation = analogue;
			
			return;
		}
		
		/**
		 * True iff this is a key binding.
		 */
		public boolean IsKeyBinding()
		{return BoundKey != VK_NONE;}
		
		/**
		 * The name of this binding.
		 */
		public final String Name;
		
		/**
		 * The bound key.
		 * The values this can take are the VK values in Java's KeyEvent.
		 */
		public final int BoundKey;
		
		/**
		 * Evaluates this binding digitally.
		 */
		public InputDigitalValue DigitalEvaluation;
		
		/**
		 * Evaluates this binding as an analogue range.
		 * If this binding does not stem from an analogue source, the resulting value is either 0.0 or 1.0
		 */
		public InputAnalogueValue AnalogueEvaluation;
		
		/**
		 * The key code for there not being a key.
		 */
		public static final int VK_NONE = -1;
	}
	
	/**
	 * Determines the digital value of an input.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface InputDigitalValue
	{
		/**
		 * Determines the digital value of an input.
		 * @return Returns true if the input condition is digitally satisfied and false otherwise.
		 */
		public abstract boolean Evaluate();
	}
	
	/**
	 * Determines the analogue value of an input.
	 * @author Dawn Nye
	 */
	@FunctionalInterface public interface InputAnalogueValue
	{
		/**
		 * Determines the analogue value of an input.
		 * @return Returns the analogue value of the input.
		 */
		public abstract double Evaluate();
	}
}
