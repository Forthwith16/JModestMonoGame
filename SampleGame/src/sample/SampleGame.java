package sample;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import gamecore.GameEngine;
import gamecore.datastructures.vectors.Vector2i;
import gamecore.gui.gamecomponents.AnimatedComponent;
import gamecore.input.InputManager;
import gamecore.input.InputMap;
import gamecore.input.MouseStateMonitor;
import gamecore.sprites.Animation;

/**
 * Plays a sample game.
 * @author Dawn Nye
 */
public class SampleGame extends GameEngine
{
	/**
	 * Creates a new sample game.
	 */
	public SampleGame()
	{
		super("CSC 207 Sample Game",null,500 + 16,500 + 39);
		
		Input = new InputManager();
		Bindings = InputMap.Map();
		
		return;
	}
	
	@Override protected void Initialize()
	{
		// Add the input manager to the game and make it as a service
		AddComponent(Input);
		AddService(Input);
		
		// Initialize some key bindings
		Bindings.AddKeyBinding("A",KeyEvent.VK_Z);
		Bindings.AddKeyBinding("B",KeyEvent.VK_X);
		
		Bindings.AddKeyBinding("Exit",KeyEvent.VK_ESCAPE);
		
		Bindings.AddKeyBinding("m_Left",KeyEvent.VK_LEFT);
		Bindings.AddKeyBinding("a_Left",KeyEvent.VK_A);
		Bindings.AddORBinding("Left","m_Left","a_Left");
		
		Bindings.AddKeyBinding("m_Right",KeyEvent.VK_RIGHT);
		Bindings.AddKeyBinding("a_Right",KeyEvent.VK_D);
		Bindings.AddORBinding("Right","m_Right","a_Right");
		
		Bindings.AddKeyBinding("m_Up",KeyEvent.VK_UP);
		Bindings.AddKeyBinding("a_Up",KeyEvent.VK_W);
		Bindings.AddORBinding("Up","m_Up","a_Up");
		
		Bindings.AddKeyBinding("m_Down",KeyEvent.VK_DOWN);
		Bindings.AddKeyBinding("a_Down",KeyEvent.VK_S);
		Bindings.AddORBinding("Down","m_Down","a_Down");
		
		Bindings.AddMouseButtonBinding("Click",Bindings.VB_LEFT_MOUSE_BUTTON);
		
		// Initialize some input tracking
		Input.AddInput("A",() -> Bindings.GetBinding("A").DigitalEvaluation.Evaluate());
		Input.AddInput("B",() -> Bindings.GetBinding("B").DigitalEvaluation.Evaluate());
		Input.AddInput("Exit",() -> Bindings.GetBinding("Exit").DigitalEvaluation.Evaluate());
		Input.AddInput("Left",() -> Bindings.GetBinding("Left").DigitalEvaluation.Evaluate());
		Input.AddInput("Right",() -> Bindings.GetBinding("Right").DigitalEvaluation.Evaluate());
		Input.AddInput("Up",() -> Bindings.GetBinding("Up").DigitalEvaluation.Evaluate());
		Input.AddInput("Down",() -> Bindings.GetBinding("Down").DigitalEvaluation.Evaluate());
		Input.AddInput("Click",() -> Bindings.GetBinding("Click").DigitalEvaluation.Evaluate(),true);
		
		// Create a koopa troopa
		try
		{
			Koopa = new AnimatedComponent(new Animation(new File("assets/animations/FRKT.animation")));
			AddComponent(Koopa);
			Koopa.ReflectHorizonal(20.0);
		}
		catch(IOException e)
		{System.err.println("Unable to load FRKT.animation");}
		
		return;
	}
	
	@Override protected void LateInitialize()
	{return;}
	
	@Override protected void Update(long delta)
	{
		int delta_x = 0;
		int delta_y = 0;
		double speed = 4.0;
		
		if(Input.GracelessInputSatisfied("Left"))
			delta_x--;
		
		if(Input.GracelessInputSatisfied("Right"))
			delta_x++;
		
		if(Input.GracelessInputSatisfied("Up"))
			delta_y--;
		
		if(Input.GracelessInputSatisfied("Down"))
			delta_y++;
		
		Vector2i delta_p = new Vector2i(delta_x,delta_y);
		
		if(!delta_p.IsZero())
			Koopa.Translate(delta_p.Normalized().Multiply(speed));
		
		if(Input.GracelessInputSatisfied("Click"))
			Koopa.Translate(MouseStateMonitor.GetState().GetPosition().Subtract(Koopa.GetPosition()));
		
		return;
	}
	
	@Override protected void LateUpdate(long delta)
	{
		if(Input.GracelessInputSatisfied("Exit"))
			Quit();
		
		return;
	}
	
	@Override protected void Dispose()
	{return;}
	
	@Override protected void LateDispose()
	{return;}
	
	/**
	 * A koopa troopa.
	 */
	protected AnimatedComponent Koopa;
	
	/**
	 * The input manager for the game.
	 * This is registered as a service.
	 */
	protected final InputManager Input;
	
	/**
	 * The key bindings for this game.
	 * This value is a singleton and can also be obtained from InputMap.Map().
	 */
	protected final InputMap Bindings;
}
