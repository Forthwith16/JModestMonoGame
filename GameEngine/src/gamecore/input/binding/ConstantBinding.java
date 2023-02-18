package gamecore.input.binding;

/**
 * Encapsulates a constant input binding.
 * @author Dawn Nye
 */
public class ConstantBinding extends InputBinding
{
	/**
	 * Creates an empty input binding that always has the provided constant value.
	 * @param name The name of the binding.
	 * @param value DigitalEvaluation resolves to this while AnalogueEvaluation resolves to 0.0f for false and 1.0f for true.
	 */
	public ConstantBinding(String name, boolean value)
	{
		super(name);
		
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
	public ConstantBinding(String name, boolean b_value, double a_value)
	{
		super(name);
		
		DigitalEvaluation = () -> b_value;
		AnalogueEvaluation = () -> a_value;
		
		return;
	}
}