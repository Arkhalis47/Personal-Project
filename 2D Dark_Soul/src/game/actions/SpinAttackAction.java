package game.actions;

import edu.monash.fit2099.engine.*;
import game.enums.Status;
import game.items.weapons.GiantAxe;
import game.Utilities;

import java.util.ArrayList;

/**
 * This class is used to represent the Active Skill which is the SpinAttackAction
 */

public class SpinAttackAction extends ActiveDamagingAction {

    /**
     * To store the detected target
     */
    protected ArrayList<Actor> targets = new ArrayList<>();

    /**
     * Constructor
     * @param giantAxe The giant axe weapon
     */
    public SpinAttackAction(GiantAxe giantAxe) {
        super(giantAxe);
    }

    /**
     * This method is used to check the capabilities of the actor
     * @param actor The actor in question (eg Player)
     * @return The status of the actor whether its the player (unkindled) or an enemy(enemies)
     */
    private Enum<Status> checkCapabilities(Actor actor){
        if (actor.hasCapability(Status.HOSTILE_TO_UNKINDLED)){
            return Status.UNKINDLED;
        }
        return Status.ENEMIES;
    }


    /**
     * This method is used to check the enemies in the adjacent area to the actor holding the giant axe
     * @param actor The actor adjacent to the giant axe holder
     * @param map The map the target is on
     */
    private void addAttackableTargets(Actor actor, GameMap map){
        Enum<Status> status = checkCapabilities(actor);

        for (Exit exit : map.locationOf(actor).getExits()) {
            //checking if actor is present on adjacent side
            Location destination = exit.getDestination();
            if(destination.containsAnActor() && destination.getActor().hasCapability(status)){
                targets.add(destination.getActor());
            }
        }
    }

    /**
     * Execute the spin attack action by hurting a group of targets with calculated damage
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string to display the executed spin attack action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String message = actor + " swing the giant axe ";

        // check all the exit if they can be attack by the holder then
        // add it to the targets
        addAttackableTargets(actor, map);

        Weapon weapon = actor.getWeapon();
        int damage = weapon.damage();

        damage=damage/2;

        // for loop to loop through the list of target
        for (int i = 0; i < targets.size(); i++) {
            if (!Utilities.randomSuccessGenerator(weapon.chanceToHit())) {
                message += "\n" + actor + " misses " + targets.get(i) + ".";
                targets.remove(i);
            }
        }

        for (int i = 0; i < targets.size(); i++) {
            message += "\n" + actor + " " + weapon.verb() + " " + targets.get(i) + " for " + damage + " damage.";
            targets.get(i).hurt(damage);
            if (!targets.get(i).isConscious()) {
                Actions dropActions = new Actions();
                // drop all items
                for (Item item : targets.get(i).getInventory())
                    dropActions.add(item.getDropAction(actor));
                for (Action drop : dropActions)
                    drop.execute(targets.get(i), map);
                // remove actor
                message += System.lineSeparator() + targets.get(i) + " is killed.";
                actorDeath(actor, targets.get(i), map.locationOf(targets.get(i)), false);
            }
        }
        targets.clear();
        return message;
    }

    /**
     * To return a string at the menu with the description of spin attack action
     * @param actor The actor performing the action.
     * @return A string to display at the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " swings giant axe";
    }
}

