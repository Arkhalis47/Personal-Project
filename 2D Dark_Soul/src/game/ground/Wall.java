package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;

/**
 * This class will implement the function of wall in the game, which will block any actor
 */
public class Wall extends Ground {

	/**
	 * The constructor
	 */
	public Wall() {
		super('#');
	}

	/**
	 * Return a boolean to check whether the actor can enter this ground or not
	 * In current implementation, actor cannot enter the wall
	 */
	@Override
	public boolean canActorEnter(Actor actor) {
		return false;
	}

	/**
	 * Return a boolean to check whether to block thrown object/ range attack
	 * Wall can block any range attack
	 */
	@Override
	public boolean blocksThrownObjects() {
		return true;
	}
}
