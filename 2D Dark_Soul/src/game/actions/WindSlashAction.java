package game.actions;

import edu.monash.fit2099.engine.*;
import game.items.weapons.StormRuler;
import game.enums.Status;

/**
 * Special action to burn the ground
 */
public class WindSlashAction extends ActiveDamagingAction {

    /**
     * The Actor perform the wind slash action
     */
    protected Actor target;

    /**
     * The direction of incoming wind slash attack
     */
    protected String direction;

    /**
     * The storm ruler
     */
    protected StormRuler stormRuler;

    /**
     * Constructor
     * @param stormRuler The storm ruler
     * @param target The target that will be wind slashed
     * @param direction The direction of incoming wind slash attack
     */
    public WindSlashAction(StormRuler stormRuler, Actor target, String direction){
        super(stormRuler);
        this.stormRuler = stormRuler;
        this.target = target;
        this.direction = direction;
    }

    /**
     * To execute the wind slash action by slashing the target
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string to show the target has been wind slashed
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        Weapon weapon = actor.getWeapon(); // better to get the damage from stormruler straightaway
        int damage = (weapon.damage() * 2);

        String result = actor + " wind slash " + target + " for " + damage + " damage.";
        target.hurt(damage);

        stormRuler.clearCharge();

        //skeleton revives here before removed from map
        if (!target.isConscious()) {
            // drop Cinders of a lord

            Actions dropActions = new Actions();
            // drop all items
            for (Item item : target.getInventory())
                dropActions.add(item.getDropAction(actor));
            for (Action drop : dropActions)
                drop.execute(target, map);

            // remove actor
            result += System.lineSeparator() + target + " is killed.";
            actorDeath(actor, target, map.locationOf(target), false);
        }
        target.addCapability(Status.STUNNED);
        result += System.lineSeparator() + target + " is stunned.";
        return result;
    }

    /**
     * To return a string for display before the wind slash action has been executed
     * @param actor The actor performing the action.
     * @return A string for display before the wind slash action has been executed
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " wind slash " + target + " at " + direction;
    }
}
