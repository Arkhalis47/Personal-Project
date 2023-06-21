package game.actors.enemies;

import edu.monash.fit2099.engine.*;
import game.actions.AttackAction;
import game.actors.LordOfCinder;
import game.behaviours.AggressiveBehaviour;
import game.behaviours.FollowBehaviour;
import game.enums.CinderEnum;
import game.enums.Status;
import game.items.CindersOfALord;
import game.items.weapons.DarkmoonLongbow;

/**
 *  A class that represent the lord of cinder called Aldrich the Devourer
 */
public class AldrichTheDevourer extends LordOfCinder {

    /**
     * The Darkmoon Longbow of Aldrich
     */
    protected DarkmoonLongbow darkmoonLongbow;

    /**
     * Constructor
     * @param initMap initial map of Aldrich located
     */
    public AldrichTheDevourer(Location initMap) {
        super("Aldrich the Devourer", 'A', 350, initMap, 4);
        this.addItemToInventory(new CindersOfALord(CinderEnum.CINDER_ALDRICH));
        this.darkmoonLongbow = new DarkmoonLongbow();
        inventory.add(darkmoonLongbow);
        darkmoonLongbow.addWeaponCapability(this);
        addCapability(Status.HOSTILE_TO_UNKINDLED);
        setSouls(5000);
    }

    /**
     * To get the allowable action that can execute to Aldrich only
     *
     * @param otherActor the Actor that might be performing those allowable action
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return the allowable actions for other actor to execute
     */
    @Override
    public Actions getAllowableActions(Actor otherActor, String direction, GameMap map) {
        Actions actions = new Actions();
        // it can be attacked only by the HOSTILE opponent, and this action will not attack the HOSTILE enemy back.
        if (otherActor.hasCapability(Status.HOSTILE_TO_ENEMY)) {
            actions.add(new AttackAction(this, direction));
            Weapon weapon = otherActor.getWeapon();
            WeaponAction weaponActiveSkill = weapon.getActiveSkill(this, direction);
            if (weaponActiveSkill != null) {
                actions.add(weaponActiveSkill);
            }
        }
        return actions;
    }

    /**
     * Select and return an action to perform on the current turn by Aldrich
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action for Aldrich to perform in one ture
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {

        // check for aggro
        Actor actor = checkAggro(map);
        if (actor != null) {
            behaviours.add(new AggressiveBehaviour(actor));
            behaviours.add(new FollowBehaviour(actor));
        }
        else {
            behaviours.clear();
            map.moveActor(this, initialLocation);
        }

        darkmoonLongbow.weaponAbility(this, this.hitPoints, this.maxHitPoints);

        for (game.interfaces.Behaviour Behaviour : behaviours) {
            Action action = Behaviour.getAction(this, map);
            if (action != null)
                return action;
        }
        return new DoNothingAction();
    }

    /**
     * Handles the reset when reset is called
     */
    @Override
    public void resetInstance() {
        super.resetInstance();
        behaviours.clear();
        darkmoonLongbow.removeEmberForm();
    }

    /**
     * Check whether this actor still exist in the game or not (or should exist or not)
     */
    @Override
    public boolean isExist() {
        return isConscious();
    }
}
