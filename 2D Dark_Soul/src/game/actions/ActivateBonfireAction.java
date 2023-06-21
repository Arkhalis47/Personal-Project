package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.TeleportationMaster;
import game.ground.Bonfire;

/**
 * This class will implement the logic of activating the bonfire
 */
public class ActivateBonfireAction extends Action {

    /**
     * The bonfire to be activated
     */
    private Bonfire thisBonfire;

    /**
     * The name of the bonfire
     */
    private String bonfireName;

    /**
     * The location of the bonfire
     */
    private Location bonfireLocation;

    /**
     * The constructor
     * @param newBonfireName The name of the bonfire
     * @param targetBonfire The bonfire to be activated
     */
    public ActivateBonfireAction(String newBonfireName, Bonfire targetBonfire, Location newBonfireLocation){
        super();
        thisBonfire = targetBonfire;
        bonfireName = newBonfireName;
        bonfireLocation = newBonfireLocation;
    }

    @Override
    /**
     * Activate the bonfire
     */
    public String execute(Actor actor, GameMap map) {
        thisBonfire.activateBonfire();
        TeleportationMaster.getInstance().appendNewBonfire(thisBonfire);
        TeleportationMaster.getInstance().updatePlayerLocation(bonfireLocation);
        return "Player activated " + bonfireName;
    }

    @Override
    /**
     * This function will return the action details used in the menu
     * @param actor The actor performing the action.
     * @return String that describe what will happen if player choose to do this action
     */
    public String menuDescription(Actor actor) {
        return "Activate " + bonfireName;
    }
}
