package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import game.enums.Status;

/**
 * A class that represents the floor inside a building.
 */
public class Floor extends Ground {

	/**
	 * The constructor
	 */
	public Floor() {
		super('_');
	}

	/**
	 * Take care of the logic of allowing actor to enter. Only the UNKINDLED can enter floor
	 * @param actor the Actor to check
	 * @return - Whether this Actor can enter the floor.
	 */
	@Override
	public boolean canActorEnter(Actor actor) {
		boolean canEnter = false;
		if(actor.hasCapability(Status.UNKINDLED)){
			canEnter = true;
		}
		return canEnter;
	}
}
