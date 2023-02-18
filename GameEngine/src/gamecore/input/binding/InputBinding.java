package gamecore.input.binding;

/**
 * Encapsulates an input binding.
 * @author Dawn Nye
 */
public abstract class InputBinding
{
	/**
	 * Creates an empty input binding that always evaluates to false or 0.0.
	 * @param name The name of the binding.
	 */
	protected InputBinding(String name)
	{
		Name = name;
		
		// These values should always be immediately overwritten, but we'll assign them anyway
		DigitalEvaluation = () -> false;
		AnalogueEvaluation = () -> 0.0;
		
		return;
	}
	
	/**
	 * True iff this is a key binding.
	 */
	public boolean IsKeyBinding()
	{return false;}
	
	/**
	 * The name of this binding.
	 */
	public final String Name;
	
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