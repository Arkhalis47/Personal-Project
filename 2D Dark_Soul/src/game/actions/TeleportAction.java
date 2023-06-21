package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.TeleportationMaster;

/**
 * This class will implement the logic of teleportation
 *
 */
public class TeleportAction extends Action {

    /**
     * The name of the destination
     */
    private String destinationName;

    /**
     * The location of the destination
     */
    private Location destination;

    /**
     * The boolean to check whether to update the player's location to spawn if reset is called
     */
    private boolean toUpdateLocation;


    /**
     * The constructor
     * @param targetName - The name of the location
     * @param teleportLocation - The location of the destination
     */
    public TeleportAction(String targetName, Location teleportLocation, boolean updateLocation){
        super();
        destinationName = targetName;
        destination = teleportLocation;
        toUpdateLocation = updateLocation;
    }

    /**
     * Teleport the actor to the destination
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A descriptive string of where the actor teleported to
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        map.moveActor(actor,destination);
        if(toUpdateLocation){
            TeleportationMaster.getInstance().updatePlayerLocation(destination);
        }
        return "Player teleported to " + destinationName;
    }

    /**
     * This function will return the action details used in the menu
     * @param actor The actor performing the action.
     * @return String that describe what will happen if player choose to do this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Teleport to " + destinationName;
    }
}
