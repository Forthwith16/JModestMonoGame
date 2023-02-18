package gamecore.input.binding;

import gamecore.input.KeyboardStateMonitor;

/**
 * Encapsulates a key binding.
 * @author Dawn Nye
 */
public class KeyBinding extends InputBinding
{		
	/**
	 * Creates a new input binding from a key.
	 * @param name The name of the binding.
	 * @param key The key bound. The key values are given by KeyEvent's VK values.
	 */
	public KeyBinding(String name, int key)
	{
		super(name);
		
		BoundKey = key;
		
		DigitalEvaluation = () -> KeyboardStateMonitor.GetState().IsKeyPressed(BoundKey);
		AnalogueEvaluation = () -> KeyboardStateMonitor.GetState().IsKeyPressed(BoundKey) ? 1.0f : 0.0f;
		
		return;
	}
	
	@Override public boolean IsKeyBinding()
	{return true;}
	
	/**
	 * The bound key.
	 * The values this can take are the VK values in Java's KeyEvent.
	 */
	public final int BoundKey;
}