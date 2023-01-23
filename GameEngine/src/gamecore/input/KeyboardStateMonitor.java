package gamecore.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.NoSuchElementException;

/**
 * Keeps track of the keyboard state via a singleton attached as a KeyListener everywhere necessary.
 * @author Dawn Nye
 */
public class KeyboardStateMonitor implements KeyListener
{
	/**
	 * Constructs a KeyboardStateMonitor to monitor the keyboard.
	 */
	protected KeyboardStateMonitor()
	{
		CurrentState = new KeyboardState();
		return;
	}
	
	public void keyTyped(KeyEvent e)
	{return;} // keyTyped events canonically do not produce key codes, so they're worthless

	public void keyPressed(KeyEvent e)
	{
		CurrentState.UpdateState(e,true); // keyPressed events keep generating so long as the key is down
		return;
	}

	public void keyReleased(KeyEvent e)
	{
		CurrentState.UpdateState(e,false);
		return;
	}
	
	/**
	 * The keyboard state.
	 */
	protected KeyboardState CurrentState;
	
	/**
	 * Obtains the one true keyboard monitor.
	 * @return Returns the singleton instance of this class.
	 */
	public static KeyboardStateMonitor GetMonitor()
	{
		if(Monitor == null)
			return Monitor = new KeyboardStateMonitor();
			
		return Monitor;
	}
	
	/**
	 * Gets the current state of the keyboard.
	 * @return Returns the current state of the keyboard.
	 */
	public static KeyboardState GetState()
	{
		// We make a copy because we want to preserve a snapshot of the keyboard at the state when it was requested
		if(State == null)
			return State = GetMonitor().new KeyboardState(GetMonitor().CurrentState);
		
		return State;
	}
	
	/**
	 * The one true monitor.
	 */
	protected static KeyboardStateMonitor Monitor;
	
	/**
	 * A copy of the current state of the keyboard that exists independently of the actively updated state.
	 */
	protected static KeyboardState State;
	
	/**
	 * A state of the keyboard.
	 * @author Dawn Nye
	 */
	public class KeyboardState
	{
		/**
		 * Creates a blank keyboard state.
		 * All keys are assumed to be released until otherwise notified.
		 */
		protected KeyboardState()
		{
			KeyStates = new boolean[NUM_KEYS];
			return;
		}
		
		/**
		 * Copies a keyboard state.
		 * @param state The state to copy.
		 */
		protected KeyboardState(KeyboardState state)
		{
			KeyStates = new boolean[NUM_KEYS];
			
			for(int i = 0;i < NUM_KEYS;i++)
				KeyStates[i] = state.KeyStates[i];
			
			return;
		}
		
		/**
		 * Updates the known state of the keyboard.
		 * @param e The keyboard state delta.
		 * @param pressed If true, then this was a key press event.
		 */
		protected void UpdateState(KeyEvent e, boolean pressed)
		{
			// First things first, we want to consume the key event
			// This doesn't stop text boxes and such from processing it, but it does stop external programs from eating the input to do weird things
			// For example, pressing F10 won't do some weird thing that prevents subsequent inputs from being read properly
			e.consume();
			
			// We only keep track of the sane keys (plus some extras)
			int code = e.getKeyCode();
			
			if(code < 0 || code >= NUM_KEYS)
				return;
			
			boolean changed = KeyStates[code] != pressed;
			
			// If the keyboard actually changed state, we need to update and null out State
			if(changed)
			{
				KeyStates[code] = pressed;
				State = null;
			}
			
			return;
		}
		
		/**
		 * Determines if the key {@code key} is pressed.
		 * @param key The key to check. The input values here are the VK values found in KeyEvent.
		 * @return Returns true if the key is pressed and false otherwise.
		 * @throws NoSuchElementException Thrown if {@code key} corresponds to a key that does not exist.
		 */
		public boolean IsKeyPressed(int key)
		{
			if(key < 0 || key >= NUM_KEYS)
				throw new NoSuchElementException();
			
			return KeyStates[key];
		}
		
		/**
		 * Determines if the key {@code key} is released.
		 * @param key The key to check. The input values here are the VK values found in KeyEvent.
		 * @return Returns true if the key is released and false otherwise.
		 * @throws NoSuchElementException Thrown if {@code key} corresponds to a key that does not exist.
		 */
		public boolean IsKeyReleased(int key)
		{return !IsKeyPressed(key);}
		
		@Override public String toString()
		{
			String ret = "{";
			
			for(int i = 0;i < NUM_KEYS;i++)
				if(KeyStates[i])
					ret += i + ", ";
			
			return ret.length() > 1 ? ret.substring(0,ret.length() - 2) + "}" : "{}";
		}
		
		/**
		 * The keyboard states.
		 * A value of true indicates that the key is pressed.
		 * A value of false indicates that they key is released.
		 */
		protected boolean[] KeyStates;
		
		/**
		 * The number of keys we keep track of.
		 */
		protected static final int NUM_KEYS = 0x20F;
	}
}
