package game.actions;

import java.util.Random;
import java.util.List;

import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.enums.WeaponCapabilities;
import game.enums.Status;

/**
 * Special Action for attacking other Actors.
 */
public class AttackAction extends DamagingAction {

	/**
	 * The Actor that is to be attacked
	 */
	protected Actor target;

	/**
	 * The direction of incoming attack.
	 */
	protected String direction;

	protected boolean blocked;

	/**
	 * Constructor.
	 *
	 * @param target the Actor to attack
	 */
	public AttackAction(Actor target, String direction) {
		this.target = target;
		this.direction = direction;
		this.blocked = false;
	}

	public AttackAction(Actor target, boolean blocked){
		this.target = target;
		this.blocked = blocked;
	}

	/**
	 * The method help to get weapon as an item
	 * @param inventory the inventory that of an actor
	 * @return The first weapon detected in the inventory as item
	 */
	public Item getWeaponAsItem(List<Item> inventory){
		for (Item item : inventory) {
			if (item.asWeapon() != null)
				return item;
		}
		return null;
	}

	/**
	 * Execute the attack action by hurting the target with calculated damage
	 * @param actor The actor performing the action.
	 * @param map The map the actor is on.
	 * @return A string to display the executed attack action
	 */
	@Override
	public String execute(Actor actor, GameMap map) {

		if (blocked){
			return actor + " attack is blocked.";
		}

		Item weaponAsItem = getWeaponAsItem(actor.getInventory());

		Weapon weapon = actor.getWeapon();
		//This will be our random generator for chance to hit
		if(!Utilities.randomSuccessGenerator(weapon.chanceToHit())){
			return actor + " misses " + target + ".";
		}

		int damage = weapon.damage();

		//Determine to execute dullness or not
		if (weaponAsItem != null) {
			if (weaponAsItem.hasCapability(WeaponCapabilities.DULLNESS)){
				if (!target.hasCapability(Status.WEAK_TO_STORMRULER)){
					damage = damage / 2;
				}
			}
		}

		String result = actor + " " + weapon.verb() + " " + target + " for " + damage + " damage.";
		target.hurt(damage);
		//skeleton revives here before removed from map
		if (!target.isConscious()) {
			Actions dropActions = new Actions();
			// drop all items
			for (Item item : target.getInventory())
				dropActions.add(item.getDropAction(actor));
			for (Action drop : dropActions)
				drop.execute(target, map);
			result += System.lineSeparator() + target + " is killed.";
			actorDeath(actor,target, map.locationOf(target),false);
		}
		return result;
	}

	/**
	 * To return a string at the menu with the description of attack action
	 * @param actor The actor performing the action.
	 * @return A string to display at the menu
	 */
	@Override
	public String menuDescription(Actor actor) {
		if (actor.hasCapability(WeaponCapabilities.Range_Weapon)){
			return actor + " attacks " + target;
		}
		return actor + " attacks " + target + " at " + direction;
	}
}
