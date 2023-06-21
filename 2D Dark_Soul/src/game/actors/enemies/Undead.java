package game.actors.enemies;


import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.actions.AttackAction;
import game.actions.SuicideAction;
import game.behaviours.AggressiveBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Status;

/**
 * An undead minion.
 */
public class Undead extends Enemies {
	/**
	 * Constructor.
	 * All Undeads are represented by an 'u' and have 30 hit points.
	 * @param name the name of this Undead
	 */
	public Undead(String name, GameMap initMap) {
		super(name, 'u', 50, initMap);
		behaviours.add(new WanderBehaviour());
		setSouls(50);
	}

	/**
	 * At the moment, we only make it can be attacked by enemy that has HOSTILE capability
	 * You can do something else with this method.
	 *
	 * @param otherActor the Actor that might be performing attack
	 * @param direction  String representing the direction of the other Actor
	 * @param map        current GameMap
	 * @return list of actions
	 * @see Status#HOSTILE_TO_ENEMY
	 */
	@Override
	public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
		Actions actions = new Actions();
		// it can be attacked only by the HOSTILE opponent, and this action will not attack the HOSTILE enemy back.
		if(otherActor.hasCapability(Status.HOSTILE_TO_ENEMY)) {
			actions.add(new AttackAction(this,direction));
		}
		return actions;
	}

	/**
	 * Select and return an action to perform on the current turn by undead
	 *
	 * @param actions    collection of possible Actions for this Actor
	 * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
	 * @param map        the map containing the Actor
	 * @param display    the I/O object to which messages may be written
	 * @return
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		Action actionToDo = new DoNothingAction();
		behavioursEdit();
		// 10% chance to die instantly every turn
		if (Utilities.randomSuccessGenerator(10) && !following) {
			return new SuicideAction(this);
		}

		// loop through all behaviours
		for(game.interfaces.Behaviour Behaviour : behaviours) {
			Action action = Behaviour.getAction(this, map);
			if (action != null)
				actionToDo = action;
		}
		return actionToDo;
	}

	/**
	 * Remove the undead from the map when reset is called
	 */
	@Override
	public boolean cleanUp(boolean playerDied) {
		getMap().removeActor(this);
		return true;
	}
}
