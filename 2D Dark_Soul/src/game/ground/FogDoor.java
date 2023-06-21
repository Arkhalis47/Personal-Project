package game.ground;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;
import game.actions.TeleportAction;
import game.enums.Status;

/**
 * This class is used to represent the Fog Door
 */
public class FogDoor extends Ground {
    /**
     * link: The second fog door that the first fog door will be linked to
     * name:Name of the Fog Door
     * fogDoorLocation:The spawn location of the fog door
     */
    private FogDoor link;
    private String name;
    private Location fogDoorLocation;

    public FogDoor(String newName, Location newFogDoorLocation) {
        super('=');
        name = newName;
        fogDoorLocation = newFogDoorLocation;
    }

    /**
     * This method is to add the TeleportAction to the fog door
     * It first checks if the player is standing on the fog door only then it displays the action
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return actionlist
     */
    @Override
    public Actions allowableActions(Actor actor, Location location, String direction) {
        Actions actionList = super.allowableActions(actor, location, direction);
        //check if the actor is a player and the actor is standing on the fog door before displaying the action
        if(actor.hasCapability(Status.UNKINDLED) && (location.containsAnActor()) && link != null){
            actionList.add(new TeleportAction(link.name,link.fogDoorLocation, false));
        }
        return actionList;
    }

    /**
     *
     * @param linkedFogDoor: the fog door that will be linked to with the first fog door.
     */
    public void setLink(FogDoor linkedFogDoor){
        link = linkedFogDoor;
    }

}
