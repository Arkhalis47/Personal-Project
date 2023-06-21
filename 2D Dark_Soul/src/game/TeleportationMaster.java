package game;


import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Location;
import game.actors.Player;
import game.ground.Bonfire;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will handle all the points of bonfire for teleportation purposes
 */
public class TeleportationMaster {

    /**
     * The list of activated bonfire
     */
    private List<Bonfire> availableBonfire;

    /**
     * The instance of this class
     */
    private static TeleportationMaster instance;

    /**
     * The player in game
     */
    private Player playerInGame;

    /**
     * Constructor
     */
    private TeleportationMaster(){
        availableBonfire = new ArrayList<>();
    }

    /**
     * The static factory method to generate an instance of this class
     * @return instance
     */
    public static TeleportationMaster getInstance(){
        if(instance == null){
            instance = new TeleportationMaster();
        }
        return instance;
    }

    /**
     * Add a new activated bonfire to the availableBonfire list
     * @param newBonfire the new bonfire
     */
    public void appendNewBonfire(Bonfire newBonfire){
        availableBonfire.add(newBonfire);
    }

    /**
     * This will allow the bonfire to return all the action to teleport to all the activated bonfire, except itself
     * @param actionList The action list of the bonfire
     * @param excludedBonfire The bonfire that will not return a teleport action
     * @return The action list with all the teleport action to bonfires
     */
    public Actions getTeleportationPlaces(Actions actionList, Bonfire excludedBonfire){
        for(Bonfire bonfire : availableBonfire){
            if(bonfire != excludedBonfire){
                actionList.add(bonfire.getTeleportAction());
            }
        }
        return actionList;
    }

    /**
     * Set the player in game
     * @param newPlayer The player in game
     */
    public void setPlayerInGame(Player newPlayer){
        playerInGame = newPlayer;
    }

    /**
     * Update the location of the player when the player teleport to a new place
     * @param newLocation The destination of the player
     */
    public void updatePlayerLocation(Location newLocation){
        playerInGame.getLocationMaster().updateInitialLocation(newLocation);
    }
}
