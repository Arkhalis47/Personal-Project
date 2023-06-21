package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import game.items.SoulTokenItem;

/**
 * This class will implement the picking up the Soul Token feature.
 */
public class PickUpSoulAction extends Action {

    /**
     * The instance of the SoulTokenItem
     */
    private SoulTokenItem existingToken;

    /**
     * Constructor
     * @param instance - The SoulTokenItem object
     */
    public PickUpSoulAction(SoulTokenItem instance){
        existingToken = instance;
    }

    /**
     * Add the number of souls of Soul Token to the actor, in this case the player
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return - a descriptive string of number of souls picked up
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        actor.asSoul().addSouls(existingToken.getSouls());
        existingToken.destroyInstance();
        actor.removeItemFromInventory(existingToken);
        return actor + " got " + existingToken.getSouls() + ".";
    }

    /**
     * This function will return the action details used in the menu
     * @param actor The actor performing the action.
     * @return String that describe what will happen if player choose to do this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Pick up and use " + existingToken.getSouls() + " soul";
    }
}
