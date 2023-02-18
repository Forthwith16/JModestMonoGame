package gamecore.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.NoSuchElementException;

import gamecore.datastructures.vectors.Vector2i;

/**
 * Keeps track of the mouse state via a singleton attached as a MouseListener everywhere necessary.
 * @author Dawn Nye
 */
public class MouseStateMonitor implements MouseListener, MouseMotionListener, MouseWheelListener
{
	/**
	 * Constructs a new MouseStateMonitor to monitor the mouse state.
	 */
	protected MouseStateMonitor()
	{
		CurrentState = new MouseState();
		return;
	}
	
	public void mouseClicked(MouseEvent e)
	{return;}
	
	public void mousePressed(MouseEvent e)
	{
		CurrentState.UpdateButtonState(e,true);
		return;
	}
	
	public void mouseReleased(MouseEvent e)
	{
		CurrentState.UpdateButtonState(e,false);
		return;
	}
	
	public void mouseEntered(MouseEvent e)
	{return;}
	
	public void mouseExited(MouseEvent e)
	{return;}
	
	public void mouseDragged(MouseEvent e)
	{
		CurrentState.UpdatePositionState(e,true);
		return;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		CurrentState.UpdatePositionState(e,false);
		return;
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		CurrentState.UpdateWheelState(e);
		return;
	}
	
	/**
	 * The mouse state.
	 */
	protected MouseState CurrentState;
	
	/**
	 * Obtains the one true mouse monitor.
	 * @return Returns the singleton instance of this class.
	 */
	public static MouseStateMonitor GetMonitor()
	{
		if(Monitor == null)
			return Monitor = new MouseStateMonitor();
			
		return Monitor;
	}
	
	/**
	 * Gets the current state of the mouse.
	 * @return Returns the current state of the mouse.
	 */
	public static MouseState GetState()
	{
		// We make a copy because we want to preserve a snapshot of the mouse at the state when it was requested
		if(State == null)
			return State = new MouseState(GetMonitor().CurrentState);
		
		return State;
	}
	
	/**
	 * The one true monitor.
	 */
	protected static MouseStateMonitor Monitor;
	
	/**
	 * A copy of the current state of the mouse that exists independently of the actively updated state.
	 */
	protected static MouseState State;
	
	/**
	 * A state of the mouse.
	 * @author Dawn Nye
	 */
	public static class MouseState
	{
		/**
		 * Creates a blank mouse state.
		 * All keys are assumed to be released until otherwise notified.
		 */
		protected MouseState()
		{
			MouseButtonStates = new boolean[NUM_MOUSE_BUTTONS];
			
			MouseX = Integer.MIN_VALUE;
			MouseY = Integer.MIN_VALUE;
			
			MouseWheelPosition = 0;
			return;
		}
		
		/**
		 * Copies a mouse state.
		 * @param state The state to copy.
		 */
		protected MouseState(MouseState state)
		{
			MouseButtonStates = new boolean[NUM_MOUSE_BUTTONS];
			
			for(int i = 0;i < NUM_MOUSE_BUTTONS;i++)
				MouseButtonStates[i] = state.MouseButtonStates[i];
			
			MouseX = state.MouseX;
			MouseY = state.MouseY;
			
			MouseWheelPosition = state.MouseWheelPosition;
			return;
		}
		
		/**
		 * Updates the known state of the mouse.
		 * Use this version for mouse button clicks.
		 * @param e The mouse state delta.
		 * @param pressed If true, then this was a button press event. If false, it was a button release.
		 */
		protected void UpdateButtonState(MouseEvent e, boolean pressed)
		{
			// First things first, we want to consume the mouse event
			// This doesn't stop text boxes and such from processing it, but it does stop external programs from eating the input to do weird things
			e.consume();
			
			// We keep track of only sane buttons
			int code = e.getButton();
			
			if(code >= 0 && code < NUM_MOUSE_BUTTONS)
			{
				boolean changed = MouseButtonStates[code] != pressed;
				
				// If the mouse actually changed state, we need to update and null out State
				if(changed)
				{
					MouseButtonStates[code] = pressed;
					State = null;
				}
			}
			
			return;
		}
		
		/**
		 * Updates the known state of the mouse.
		 * Use this version for mouse movements.
		 * @param e The mouse state delta.
		 * @param drag If true, this was a mouse drag (meaning a button is pressed). If false, this was an ordinary mouse move. This value is mostly useless but available.
		 */
		protected void UpdatePositionState(MouseEvent e, boolean drag)
		{
			// First things first, we want to consume the mouse event
			// This doesn't stop text boxes and such from processing it, but it does stop external programs from eating the input to do weird things
			e.consume();
			
			// Now we need to keep track of mouse positions
			MouseX = e.getX();
			MouseY = e.getY();
			
			State = null;
			return;
		}
		
		/**
		 * Updates the known state of the mouse.
		 * Use this version for wheel movements.
		 * @param e The mouse state delta.
		 */
		protected void UpdateWheelState(MouseWheelEvent e)
		{
			// First things first, we want to consume the mouse event
			// This doesn't stop text boxes and such from processing it, but it does stop external programs from eating the input to do weird things
			e.consume();
			
			// Updating the mouse wheel is easy and blind
			MouseWheelPosition += e.getWheelRotation();
			
			State = null;
			return;
		}
		
		/**
		 * Determines if the mouse button {@code button} is pressed.
		 * @param button
		 * The button to check.
		 * The input values here are the button values found in MouseEvent.
		 * Java one indexes these so that the left button is 1, the middle button is 2, and the right button is 3.
		 * Other buttons continue from there.
		 * @return Returns true if the button is pressed and false otherwise.
		 * @throws NoSuchElementException Thrown if {@code button} corresponds to a button that does not exist.
		 */
		public boolean IsButtonPressed(int button)
		{
			if(button < 0 || button >= NUM_MOUSE_BUTTONS)
				throw new NoSuchElementException();
			
			return MouseButtonStates[button];
		}
		
		/**
		 * Determines if the mouse button {@code button} is release.
		 * @param button
		 * The button to check.
		 * The input values here are the button values found in MouseEvent.
		 * Java one indexes these so that the left button is 1, the middle button is 2, and the right button is 3.
		 * Other buttons continue from there.
		 * @return Returns true if the button is release and false otherwise.
		 * @throws NoSuchElementException Thrown if {@code button} corresponds to a button that does not exist.
		 */
		public boolean IsButtonReleased(int button)
		{return !IsButtonPressed(button);}
		
		/**
		 * Obtains the current x position of the mouse.
		 */
		public int GetXPosition()
		{return MouseX;}
		
		/**
		 * Obtains the current y position of the mouse.
		 */
		public int GetYPosition()
		{return MouseY;}
		
		/**
		 * Obtains the current position of the mouse.
		 */
		public Vector2i GetPosition()
		{return new Vector2i(MouseX,MouseY);}
		
		/**
		 * Obtains the current mouse x position delta.
		 * @param previous The previous mouse state to calculate a delta from. Because we do not control when Java's Swing thread fires events, this is the only safe way to implement deltas.
		 */
		public int GetDeltaX(MouseState previous)
		{return MouseX - previous.MouseX;}
		
		/**
		 * Obtains the current mouse y position delta.
		 * @param previous The previous mouse state to calculate a delta from. Because we do not control when Java's Swing thread fires events, this is the only safe way to implement deltas.
		 */
		public int GetDeltaY(MouseState previous)
		{return MouseY - previous.MouseY;}
		
		/**
		 * Obtains the current mouse position delta.
		 * @param previous The previous mouse state to calculate a delta from. Because we do not control when Java's Swing thread fires events, this is the only safe way to implement deltas.
		 */
		public Vector2i GetDelta(MouseState previous)
		{return new Vector2i(GetDeltaX(previous),GetDeltaY(previous));}
		
		/**
		 * Obtains the mouse wheel position.
		 */
		public int GetMouseWheelPosition()
		{return MouseWheelPosition;}
		
		/**
		 * Obtains the mouse wheel delta.
		 * @param previous The previous mouse state to calculate a delta from. Because we do not control when Java's Swing thread fires events, this is the only safe way to implement deltas.
		 */
		public int GetMouseWheelDelta(MouseState previous)
		{return MouseWheelPosition - previous.MouseWheelPosition;}
		
		@Override public String toString()
		{
			String ret = "Position: (" + MouseX + "," + MouseY + ") Wheel: " + MouseWheelPosition + "\n";
			ret += "Buttons: {";
			
			for(int i = 0;i < NUM_MOUSE_BUTTONS;i++)
				if(MouseButtonStates[i])
					ret += i + ", ";
			
			return ret.substring(ret.length() - 2).equals(", ") ? ret.substring(0,ret.length() - 2) + "}" : ret + "}";
		}
		
		/**
		 * The mouse x position.
		 */
		protected int MouseX;
		
		/**
		 * The mouse y position.
		 */
		protected int MouseY;
		
		/**
		 * The mouse wheel position.
		 */
		protected int MouseWheelPosition;
		
		/**
		 * The mouse button states.
		 * A value of true indicates that the mouse button is pressed.
		 * A value of false indicates that the mouse button is released.
		 */
		protected boolean[] MouseButtonStates;
		
		/**
		 * The number of mouse buttons we keep track of.
		 */
		protected static final int NUM_MOUSE_BUTTONS = 0x13; // Apparently you have 3 bits allocated for normal mouse buttons and 4 bits available for up to 15 additional buttons, and Java 1-indexes buttons
	}
}
