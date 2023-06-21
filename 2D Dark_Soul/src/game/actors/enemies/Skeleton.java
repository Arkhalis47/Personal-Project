package game.actors.enemies;

import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.actions.AttackAction;
import game.behaviours.AggressiveBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Status;
import game.enums.WeaponCapabilities;
import game.interfaces.Behaviour;
import game.interfaces.Resettable;
import game.items.weapons.BroadSword;
import game.items.weapons.GiantAxe;

import java.util.List;


public class Skeleton extends Enemies implements Resettable {

    /**
     * The initial location of the skeleton, used for reset
     */
    private final Location initLocation;

    /**
     * Boolean to tell whether this skeleton already revived once
     */
    private boolean revival;

    /**
     *
     * @param name Name of the Skeleton
     * @param initLocation - initial location to spawn
     * @param newResettable - if this is a new Skeleton, insert true, else false
     */
    public Skeleton(String name, Location initLocation, boolean newResettable) {
        super(name, 's', 100, initLocation.map());
        behaviours.add(new WanderBehaviour());
        this.initLocation = initLocation;
        if(newResettable){
            registerInstance();
        }
        if (!Utilities.randomSuccessGenerator(50)){
            inventory.add(new GiantAxe());
        }
        else {
            inventory.add(new BroadSword());
        }
        setSouls(250);
        revival = false;
    }

    /**
     * To get the weapon from actor's inventory as item
     * @param inventory A list of item from an actor
     * @return The first weapon from inventory as item
     */
    public Item getWeaponAsItem(List<Item> inventory){
        for (Item item : inventory) {
            if (item.asWeapon() != null)
                return item;
        }
        return null;
    }

    /**
     * To get undirected active skill from a weapon
     * @param weaponAllowableAction A list of action from a weapon
     * @return the first undirected active skill from weapon
     */
    public Action getUndirectedActiveSkill(List<Action> weaponAllowableAction){
        if (!weaponAllowableAction.isEmpty()){
            for (Action undirectedActiveSkill : weaponAllowableAction) {
                return undirectedActiveSkill;
            }
        }
        return null;
    }

    /**
     * Select and return an action to perform on the current turn by the skeleton
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return The action for the skeleton to perform in one ture
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        Action actionToDo = new DoNothingAction();
        behavioursEdit();

        Item weaponAsItem = this.getWeaponAsItem(this.getInventory());
        if (weaponAsItem.hasCapability(WeaponCapabilities.UNDIRECTED_ACTIVE_SKILL) && !(following)) {
            if (Utilities.randomSuccessGenerator(50)){
                return getUndirectedActiveSkill(weaponAsItem.getAllowableActions());
            }
        }

        // loop through all behaviours
        for(Behaviour Behaviour : behaviours) {
            Action action = Behaviour.getAction(this, map);
            if (action != null)
                actionToDo = action;
        }
        return actionToDo;
    }

    /**
     * To get the allowable action that can execute to the skeleton
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
        if(otherActor.hasCapability(Status.HOSTILE_TO_ENEMY)) {
            actions.add(new AttackAction(this,direction));
        }
        return actions;
    }

    /**
     * Remove the skeleton from the map when reset is called
     */
    @Override
    public boolean cleanUp(boolean playerDied) {
        getMap().removeActor(this);
        return true;
    }

    /**
     * Handles the reset of skeleton when reset is called
     */
    @Override
    public void resetInstance() {
        initLocation.addActor(new Skeleton("Skeleton",initLocation,false));
    }

    /**
     * Check whether this actor still exist in the game or not (or should exist or not)
     */
    @Override
    public boolean isExist() {
        return true;
    }

    /**
     * To hurt the skeleton with points and check if skeleton is conscious in the first time then it will have a 50
     * percent chance to revive from dead
     * @param points number of hitpoints to deduct.
     */
    @Override
    public void hurt(int points) {
        super.hurt(points);
        if (!isConscious() && !revival && Utilities.randomSuccessGenerator(50)){
            heal(maxHitPoints);
            revival = true;
        }
    }
}
