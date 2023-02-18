package gamecore.input.binding;

/**
 * Encapsulates a fully custom input binding.
 * @author Dawn Nye
 */
public class CustomBinding extends InputBinding
{
	/**
	 * Creates a new input binding with custom evaluation functions.
	 * @param name The name of the binding.
	 * @param digital The digital evaluation.
	 * @param analogue The analogue evaluation.
	 */
	public CustomBinding(String name, InputDigitalValue digital, InputAnalogueValue analogue)
	{
		super(name);
		
		DigitalEvaluation = digital;
		AnalogueEvaluation = analogue;
		
		return;
	}
}