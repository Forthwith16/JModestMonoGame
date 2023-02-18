package gamecore.input.binding;

import gamecore.input.MouseStateMonitor;

/**
 * Encapsulates a mouse button binding.
 * @author Dawn Nye
 */
public class MouseButtonBinding extends InputBinding
{		
	/**
	 * Creates a new input binding from a mouse button.
	 * @param name The name of the binding.
	 * @param button
	 * The button bound.
	 * The values this can take are the values in Java's MouseEvent.
	 * Java one indexes its buttons, so the left button is 1, the middle button is 2, and the right button is 3.
	 * Further buttons after that are 4, 5, 6, etc.
	 */
	public MouseButtonBinding(String name, int button)
	{
		super(name);
		
		BoundButton = button;
		
		DigitalEvaluation = () -> MouseStateMonitor.GetState().IsButtonPressed(BoundButton);
		AnalogueEvaluation = () -> MouseStateMonitor.GetState().IsButtonPressed(BoundButton) ? 1.0f : 0.0f;
		
		return;
	}
	
	@Override public boolean IsMouseBinding()
	{return true;}
	
	/**
	 * The bound button.
	 * The values this can take are the values in Java's MouseEvent.
	 * Java one indexes its buttons, so the left button is 1, the middle button is 2, and the right button is 3.
	 * Further buttons after that are 4, 5, 6, etc.
	 */
	public final int BoundButton;
}