package game.actors.enemies;

import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.actions.AttackAction;
import game.behaviours.WanderBehaviour;
import game.enums.Status;
import game.ground.MysteryChest;
import game.interfaces.MapKeeping;
import game.items.SoulTokenItem;

/**
 *  A class that represent a mimic
 */
public class Mimic extends Enemies implements MapKeeping {

    /**
     * The initial location of the mimic
     */
    protected final Location initLocation;

    /**
     * The Constructor
     *
     * @param name of the mimic
     * @param initMap initial map of the mimic located
     * @param maxSoulToken the maximum number of soul token that the mimic will drop after being killed
     * @param minSoulToken the minimum number of soul token that the mimic will drop after being killed
     * @param initLocation the initial location of the mimic located
     */
    public Mimic(String name, GameMap initMap, int maxSoulToken, int minSoulToken, Location initLocation) {
        super(name, 'M', 100, initMap);
        behaviours.add(new WanderBehaviour());
        this.initLocation = initLocation;
        int numberOfToken = Utilities.randomNumberGenerator(maxSoulToken + 1, minSoulToken);
        for(int i = 0; i<= numberOfToken -1; i++){
            SoulTokenItem soulTokenItem = new SoulTokenItem(initMap.locationOf(this), 100);
            addItemToInventory(soulTokenItem);
        }
        registerThisInstance();
        setSouls(200);
    }

    /**
     * To get the allowable action that can execute to the mimic
     *
     * @param otherActor the Actor that might be performing attack
     * @param direction  String representing the direction of the other Actor
     * @param map        current GameMap
     * @return the allowable actions for other actor to execute
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
     * Select and return an action to perform on the current turn by the mimic
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action for the mimic to perform in one ture
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        Action actionToDo = new DoNothingAction();
        behavioursEdit();

        // loop through all behaviours
        for(game.interfaces.Behaviour Behaviour : behaviours) {
            Action action = Behaviour.getAction(this, map);
            if (action != null)
                actionToDo = action;
        }
        return actionToDo;
    }


    /**
     * Creates and returns an intrinsic weapon for the mimic.
     * The mimic 'kick' for 55 damage.
     *
     * @return a freshly-instantiated IntrinsicWeapon for mimic
     */
    @Override
    protected IntrinsicWeapon getIntrinsicWeapon() {
        return new IntrinsicWeapon(55, "kick");
    }


    /**
     * Remove the mimic from the map and set a mystery chest at that location when reset is called
     */
    @Override
    public boolean cleanUp(boolean playerDied) {
        getMap().removeActor(this);
        return true;
    }
}
