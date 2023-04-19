package gamecore;

import gamecore.datastructures.matrices.Matrix2D;

/**
 * Represents a drawable component of a game.
 * @author Dawn Nye
 */
public interface IDrawable extends IUpdatable
{
	/**
	 * Initiates a draw call of this component in Java.
	 * @param cam
	 * The camera matrix for the next draw call.
	 * This allows universal transformations according to some universal parent object we call a camera.
	 * This changes how we view objects without changing their position in world space.
	 * @throws NullPointerException A null pointer will eventually be thrown if {@code cam} is null and this function is not called with a non-null parameter before Swing's next paint cycle.
	 */
	public void Draw(Matrix2D cam);
}
