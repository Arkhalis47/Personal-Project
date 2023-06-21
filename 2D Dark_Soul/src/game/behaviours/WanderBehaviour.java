package game.behaviours;

import java.util.ArrayList;
import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.interfaces.Behaviour;

/**
 * A class that make a non player actor to wonder around
 */
public class WanderBehaviour extends Action implements Behaviour {
	
	private final Random random = new Random();

	/**
	 * Returns a MoveAction to wander to a random location, if possible.  
	 * If no movement is possible, returns null.
	 * 
	 * @param actor the Actor enacting the behaviour
	 * @param map the map that actor is currently on
	 * @return an Action, or null if no MoveAction is possible
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		ArrayList<Action> actions = new ArrayList<Action>();
		
		for (Exit exit : map.locationOf(actor).getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
            	actions.add(exit.getDestination().getMoveAction(actor, "around", exit.getHotKey()));
            }
        }

		if (!actions.isEmpty()) {
			return actions.get(random.nextInt(actions.size()));
		}
		else {
			return null;
		}

	}

	/**
	 * To return a string for display wander behaviour action while executed
	 *
	 * @param actor The actor performing the action.
	 * @param map The map the actor is on.
	 * @return A string for display wander behaviour action while executed
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		return menuDescription(actor);
	}

	/**
	 * To return a string for display
	 *
	 * @param actor The actor performing the action.
	 * @return The string for display
	 */
	@Override
	public String menuDescription(Actor actor) {
		return "Raagrh...";
	}
}
