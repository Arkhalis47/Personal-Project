package game.ground;

import edu.monash.fit2099.engine.*;
import game.TeleportationMaster;
import game.actions.ActivateBonfireAction;
import game.actions.RestAction;
import game.actions.TeleportAction;
import game.enums.Status;

/**
 * This class will implement the Bonfire, which is our save point in this game
 */
public class Bonfire extends Ground {

    /**
     * The name of the bonfire
     */
    private String bonfireName;


    /**
     * The location of this bonfire spawn location for the player
     */
    private Location bonfireSpawnLocation;

    /**
     * A boolean which tells whether this bonfire is activated or not
     */
    private boolean activated;

    /**
     * The constructor
     * @param newBonfireName The name of the bonfire
     * @param initialSpawnLocation The location of the spawn location of the bonfire
     */
    public Bonfire(String newBonfireName, Location initialSpawnLocation){
        super('B');
        bonfireName = newBonfireName;
        bonfireSpawnLocation = initialSpawnLocation;
        activated = false;
    }

    /**
     * Tells whether actors can enter this or not
     * For now, it will not allow any player to enter
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Tells whether to block any thrown object/range attack
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }

    /**
     * Return the Rest Action to the actor / or any UNKINDLED
     * Also allow player to teleport to all the activated bonfire
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return - action list containing the RestAction
     */
    @Override
    public Actions allowableActions(Actor actor, Location location, String direction) {
        Actions actionList = super.allowableActions(actor, location, direction);
        if(actor.hasCapability(Status.UNKINDLED)){
            if(activated){
                actionList.add(new RestAction(bonfireName, bonfireSpawnLocation));
                TeleportationMaster.getInstance().getTeleportationPlaces(actionList,this);
            }
            else{
                actionList.add(new ActivateBonfireAction(bonfireName,this, bonfireSpawnLocation));
            }
        }
        return actionList;
    }

    /**
     * Activate the bonfire
     */
    public void activateBonfire(){
        activated = true;
    }

    /**
     * Return a new teleport action to this bonfire
     * @return Teleport action to this bonfire
     */
    public TeleportAction getTeleportAction(){
        return new TeleportAction(bonfireName,bonfireSpawnLocation, true);
    }

}
