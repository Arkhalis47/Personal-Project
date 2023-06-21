package game.ground;

import edu.monash.fit2099.engine.*;
import game.actions.OpenChestAction;
import game.interfaces.Resettable;

/**
 * This class will implement the mystery chest feature in the game
 */
public class MysteryChest extends Ground implements Resettable {

    /**
     * The location of the chest
     */
    Location chestLocation;

    /**
     * The Constructor
     */
    public MysteryChest(Location initialLocation) {
        super('?');
        chestLocation = initialLocation;
        registerInstance();
    }

    /**
     * Returns an action list to actors to interact with it
     * This will allow the actor to open the chest
     */
    @Override
    public Actions allowableActions(Actor actor, Location location, String direction) {
        Actions actionList = super.allowableActions(actor, location, direction);
        actionList.add(new OpenChestAction(chestLocation));
        return actionList;
    }

    @Override
    /**
     * Reinsert the chest into the map
     */
    public void resetInstance() {
        chestLocation.setGround(this);
    }

    @Override
    /**
     * Tells whether this chest need to reset or not
     * It needs to be reset everytime
     */
    public boolean isExist() {
        return true;
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
