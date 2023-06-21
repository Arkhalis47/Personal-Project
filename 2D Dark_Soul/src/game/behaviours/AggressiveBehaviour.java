package game.behaviours;

import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;
import game.interfaces.Behaviour;
import game.items.weapons.RangeWeapon;

import java.util.List;

/**
 * A class that make a non player actor to attack the detected target
 */
public class AggressiveBehaviour implements Behaviour {
    /**
     * The target that the attacker is targeting
     */
    private Actor target;

    /**
     * Constructor
     * @param target - The target the attacker is targeting
     */
    public AggressiveBehaviour(Actor target) {
        this.target = target;
    }

    /**
     * If the target is within range, return AttackAction, else return null
     * @param actor the Actor acting
     * @param map the GameMap containing the Actor
     */
    @Override
    public Action getAction(Actor actor, GameMap map) {
        if (asRangeWeapon(actor.getWeapon()) != null){
            RangeWeapon rangeWeapon = asRangeWeapon(actor.getWeapon());
            return rangeWeapon.rangeAttack(actor, target, map, rangeWeapon.getShootingRange(), rangeWeapon.isPenetrable());
        }

        for (Exit exit : map.locationOf(actor).getExits()) {
            Location destination = exit.getDestination();
            if(destination.containsAnActor()){
                if(destination.getActor() == target){
                    return new AttackAction(target, exit.getName());
                }
            }
        }
        return null;
    }

    /**
     * Casts this Weapon to a RangeWeapon if possible.
     *
     * @return a reference to the current Weapon as type RangeWeapon, or null if this Weapon isn't a RangeWeapon
     */
    public RangeWeapon asRangeWeapon(Weapon weapon) {
        return weapon instanceof RangeWeapon ? (RangeWeapon) weapon : null;
    }
}
