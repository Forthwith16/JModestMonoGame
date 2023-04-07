package gamecore.input;

import java.util.NoSuchElementException;

import gamecore.datastructures.LinkedList;
import gamecore.datastructures.maps.Dictionary;
import gamecore.input.binding.ConstantBinding;
import gamecore.input.binding.CustomBinding;
import gamecore.input.binding.InputBinding;
import gamecore.input.binding.InputBinding.InputAnalogueValue;
import gamecore.input.binding.InputBinding.InputDigitalValue;
import gamecore.input.binding.KeyBinding;
import gamecore.input.binding.MouseButtonBinding;

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
		map.Put(name,new ConstantBinding(name,false));
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
		map.Put(name,new ConstantBinding(name,satisfied));
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
		map.Put(name,new ConstantBinding(name,b_value,a_value));
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
		map.Put(name,new KeyBinding(name,key));
		return;
	}
	
	/**
	 * Adds a mouse button binding.
	 * If a binding with the given name already exists, it is overwritten.
	 * @param name The name of the binding.
	 * @param button
	 * The button to bind.
	 * The input values here are the button values found in this class.
	 * Each is prefixed with VB_ to make finding them easy.
	 */
	public void AddMouseButtonBinding(String name, int button)
	{
		map.Put(name,new MouseButtonBinding(name,button));
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
		
		map.Put(name,new CustomBinding(name,() -> map.Get(binding_a).DigitalEvaluation.Evaluate() && map.Get(binding_b).DigitalEvaluation.Evaluate(),
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
		
		map.Put(name,new CustomBinding(name,() -> map.Get(binding_a).DigitalEvaluation.Evaluate() || map.Get(binding_b).DigitalEvaluation.Evaluate(),
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
		
		map.Put(name,new CustomBinding(name,() -> !map.Get(binding).DigitalEvaluation.Evaluate(),() -> -map.Get(binding).AnalogueEvaluation.Evaluate()));
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
		map.Put(name,new CustomBinding(name,() -> map.Get(binding).DigitalEvaluation.Evaluate(),() -> map.Get(binding).AnalogueEvaluation.Evaluate()));
		
		return;
	}
	
	/**
	 * Adds an arbitrary, free-form input binding.
	 * Use this with caution but without worry, as it is safe but comes without the conveniences other Add wrapper functions offer.
	 * @param name The name of the binding.
	 * @param d_evaluation The digital evaluation function.
	 * @param a_evaluation The analogue evaluation function.
	 */
	public void AddArbitraryBinding(String name, InputDigitalValue d_evaluation, InputAnalogueValue a_evaluation)
	{
		map.Put(name,new CustomBinding(name,d_evaluation,a_evaluation));
		return;
	}
	
	/**
	 * Removes the given binding.
	 * @param name The name of the binding to remove.
	 * @return Returns true if the binding was removed and false otherwise.
	 */
	public boolean RemoveBinding(String name)
	{return map.RemoveByKey(name);}
	
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
	
	public static final int VB_LEFT_MOUSE_BUTTON = 1;
	public static final int VB_MIDDLE_MOUSE_BUTTON = 2;
	public static final int VB_RIGHT_MOUSE_BUTTON = 3;
	
	public static final int VB_MOUSE_BUTTON_4 = 4;
	public static final int VB_MOUSE_BUTTON_5 = 5;
	public static final int VB_MOUSE_BUTTON_6 = 6;
	public static final int VB_MOUSE_BUTTON_7 = 7;
	public static final int VB_MOUSE_BUTTON_8 = 8;
	public static final int VB_MOUSE_BUTTON_9 = 9;
	public static final int VB_MOUSE_BUTTON_10 = 10;
	public static final int VB_MOUSE_BUTTON_11 = 11;
	public static final int VB_MOUSE_BUTTON_12 = 12;
	public static final int VB_MOUSE_BUTTON_13 = 13;
	public static final int VB_MOUSE_BUTTON_14 = 14;
	public static final int VB_MOUSE_BUTTON_15 = 15;
	public static final int VB_MOUSE_BUTTON_16 = 16;
	public static final int VB_MOUSE_BUTTON_17 = 17;
	public static final int VB_MOUSE_BUTTON_18 = 18;
}
