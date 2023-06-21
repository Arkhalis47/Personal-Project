package game;


import edu.monash.fit2099.engine.Location;
import game.actors.Player;
import game.interfaces.MapKeeping;
import game.interfaces.Resettable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A global Singleton manager that does soft-reset on the instances.
 *
 */
public class ResetManager {
    /**
     * A list of resettable instances (any classes that implements Resettable,
     * such as Player implements Resettable will be stored in here)
     */
    private List<Resettable> resettableList;

    /**
     * A list of MapKeeping instance, anything that must be removed from the map when reset occurs will be stored here
     */
    private List<MapKeeping> mapKeepingList;

    private Player playerInGame;

    /**
     * A singleton reset manager instance
     */
    private static ResetManager instance;

    private final String DEATH_MESSAGE = "YOU DIED";

    /**
     * Get the singleton instance of reset manager
     * @return edu.monash.fit2099.ResetManager singleton instance
     */
    public static ResetManager getInstance(){
        if(instance == null){
            instance = new ResetManager();
        }
        return instance;
    }

    /**
     * Constructor
     */
    private ResetManager(){
        resettableList = new ArrayList<>();
        mapKeepingList = new ArrayList<>();
    }

    /**
     * Reset the game by traversing through all the list, this will be used when Player rested at Bonfire
     *
     *
     */
    public void runRest(Location updatedBonfireLocation){
        cleanUp(false); //clean up the map
        for(Resettable resettable: resettableList){
            if (resettable.isExist()){
                resettable.resetInstance();
            }
        }
        playerInGame.resetPlayerInstance(updatedBonfireLocation);
    }

    /**
     * Reset the game by traversing through all the list, this will be used when Player died
     * @param deathLocation - The location where the Player died
     * @param soulAtPreLocation - The boolean to tell whether to place the Soul Token at the previous location of the actor.
     */
    public void runDeath(Location deathLocation, boolean soulAtPreLocation){
        cleanUp(true); //clean up the map
        for(Resettable resettable: resettableList){
            if (resettable.isExist()){
                resettable.resetInstance();
            }
        }
        playerInGame.resetPlayerInstance(deathLocation,soulAtPreLocation); //Reset player instance
    }

    /**
     * Add the Resettable instance to the list
     * @param resettable the interface instance
     */
    public void appendResetInstance(Resettable resettable){
        resettableList.add(resettable);
    }

    /**
     * Add the MapKeeping instance to the List
     * @param mapKeeping The MapKeeping instance
     */
    public void appendMapKeepingInstance(MapKeeping mapKeeping){
        mapKeepingList.add(mapKeeping);}

    /**
     * clean up instances (actor, item, or ground) that doesn't exist anymore in the map
     *
     */
    private void cleanUp(boolean playerDied){
        //Clean the MapKeeping instance
        ListIterator<MapKeeping> mapKeepingListIterator = mapKeepingList.listIterator();
        while(mapKeepingListIterator.hasNext()){
            MapKeeping nextMapKeeping = mapKeepingListIterator.next();
            boolean cleaned = nextMapKeeping.cleanUp(playerDied);
            if(cleaned){
                mapKeepingListIterator.remove();
            }
        }
    }

    /**
     * A setter for the player, this will tell the ResetManager who is the Player, used in the Player Constructor
     * @param player - The player in the game.
     */
    public void setPlayer(Player player){
        playerInGame = player;
    }

    /**
     * Return a small death message to be display in the message log
     * @return The death message to show when player died
     */
    public String getDeathMessage(){
        return DEATH_MESSAGE;
    }


}
