package game.items.weapons;

import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;

/**
 * This class represent the range weapon existed in the game
 */
public class RangeWeapon extends WeaponItem {

    /**
     * The shooting range of the range weapon
     */
    protected int shootingRange;

    /**
     * whether the range weapon is penetrable or not
     */
    protected boolean penetrable;

    /**
     * The constructor
     *
     * @param name name of the item
     * @param displayChar character to use for display when item is on the ground
     * @param damage amount of damage this weapon does
     * @param verb verb to use for this weapon, e.g. "hits", "zaps"
     * @param hitRate the probability/chance to hit the target.
     * @param shootingRange default shooting range of the range weapon
     * @param penetrable whether the range weapon is penetrable or not
     */
    public RangeWeapon(String name, char displayChar, int damage, String verb, int hitRate, int shootingRange, boolean penetrable) {
        super(name, displayChar, damage, verb, hitRate);
        this.shootingRange = shootingRange;
        this.penetrable = penetrable;
    }

    /**
     * Getter of the shooting range
     *
     * @return shooting range
     */
    public int getShootingRange() {
        return shootingRange;
    }

    /**
     * Getter of penetrable
     *
     * @return penetrable
     */
    public boolean isPenetrable() {
        return penetrable;
    }

    /**
     *
     * @param actor the actor who hold the range weapon
     * @param target the target to shoot
     * @param map of the actor located
     * @param shootingRange the shooting range of the range weapon
     * @param penetrable whether the range weapon is penetrable or not
     * @return attack action to perform shooting
     */
    public Action rangeAttack(Actor actor, Actor target, GameMap map, int shootingRange, boolean penetrable) {
        int firstX = Math.max(map.locationOf(actor).x() - shootingRange, map.getXRange().min());
        int firstY = Math.max(map.locationOf(actor).y() - shootingRange, map.getYRange().min());
        int lastX = Math.min(map.locationOf(actor).x() + shootingRange, map.getXRange().max());
        int lastY = Math.min(map.locationOf(actor).y() + shootingRange, map.getYRange().max());

        for (int x = firstX; x <= lastX; x++) {
            for (int y = firstY; y <= lastY; y++) {
                Location location = map.at(x, y);
                if (location.containsAnActor()) {
                    if (location.getActor() == target) {
                        if (!penetrable){
                            return new AttackAction(target, rangeCheckBlock(actor, target, map));
                        }
                        else {
                            return new AttackAction(target, false);
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * To check whether the shot has been blocked if the range weapon is not penetrable
     * @param actor the actor who hold the range weapon
     * @param target the target to shoot
     * @param map the map that the actor located
     * @return attack action with ture for the parameter when the shot has been blocked else
     *         set the blocked parameter to ture
     */
    public boolean rangeCheckBlock(Actor actor, Actor target, GameMap map){
        Location here = map.locationOf(actor);
        Location there = map.locationOf(target);

        NumberRange xs, ys;
        if (here.x() != there.x() || here.y() != there.y()) {
            xs = new NumberRange(Math.min(here.x(), there.x()), Math.abs(here.x() - there.x()) + 1);
            ys = new NumberRange(Math.min(here.y(), there.y()), Math.abs(here.y() - there.y()) + 1);

            for (int x : xs) {
                for (int y : ys) {
                    if(map.at(x, y).getGround().blocksThrownObjects())
                        return true;
                }
            }
        }
        return false;
    }
}
