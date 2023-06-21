package game.ground;

import edu.monash.fit2099.engine.Ground;
import game.enums.Abilities;

/**
 * A class that represents bare dirt.
 */
public class Dirt extends Ground {

	/**
	 * The constructor
	 * Dirt can be burned
	 */
	public Dirt() {
		super('.');
		addCapability(Abilities.BURN_GROUND);
	}
}
