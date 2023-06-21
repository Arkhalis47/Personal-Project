package game.actors.enemies;

import edu.monash.fit2099.engine.*;
import game.behaviours.FollowBehaviour;
import game.enums.CinderEnum;
import game.enums.Status;
import game.interfaces.Behaviour;
import game.actors.LordOfCinder;
import game.behaviours.AggressiveBehaviour;
import game.items.CindersOfALord;
import game.actions.AttackAction;
import game.items.weapons.YhormGreatMachete;

/**
 *  A class that represent the lord of cinder called Yhorm the giant
 */
public class YhormTheGiant extends LordOfCinder {

    /**
     * The Yhorm great machete
     */
    protected YhormGreatMachete yhormGreatMachete;

    /**
     * Constructor
     * @param initMap The initial map of Yhorm located
     */
    public YhormTheGiant(Location initMap) {
        super("Yhorm The Giant", 'Y', 500, initMap, 3);
        this.addItemToInventory(new CindersOfALord(CinderEnum.CINDER_YHORM));
//        this.addItemToInventory(new CindersOfALord());
        this.yhormGreatMachete = new YhormGreatMachete();
        inventory.add(yhormGreatMachete);
        addCapability(Status.HOSTILE_TO_UNKINDLED);
        addCapability(Status.WEAK_TO_STORMRULER);
        setSouls(5000);
    }

    /**
     * To get the allowable action that can execute to Yhorm only e.g. the wind slash action
     *
     * @param otherActor the Actor that might be performing those allowable action
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return The allowable actions for other actor to execute
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
     * Select and return an action to perform on the current turn by Yhorm
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return The action for Yhorm to perform in one ture
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        // check Yhrom has been stunned or not
        if(hasCapability(Status.STUNNED)){
            removeCapability(Status.STUNNED);
            return new DoNothingAction();
        }
        // check for aggro
        Actor actor = checkAggro(map);
        if (actor != null) {
            behaviours.add(new FollowBehaviour(actor));
            behaviours.add(new AggressiveBehaviour(actor));
        }
        else {
            behaviours.clear();
            map.moveActor(this, initialLocation);
        }


        if (hitPoints <= 0.5 * maxHitPoints && !yhormGreatMachete.getEmberForm()) {
            yhormGreatMachete.activateEmberForm();
            return yhormGreatMachete.getBurnGroundAction(this, map);
        }

        for (Behaviour Behaviour : behaviours) {
            Action action = Behaviour.getAction(this, map);
            if (action != null)
                return action;
        }
        return new DoNothingAction();
    }

    /**
     * Handles the reset of Yhorm when reset is called
     */
    @Override
    public void resetInstance() {
        super.resetInstance();
        behaviours.clear();
        yhormGreatMachete.removeEmberForm();
    }

    /**
     * Check whether this actor still exist in the game or not (or should exist or not)
     */
    @Override
    public boolean isExist() {
        return isConscious();
    }
}