package game.interfaces;

import game.ResetManager;

/**
 * An interface to make sure that instance clean themselves out of the map when reset is called
 */
public interface MapKeeping {
    /**
     * Register this instance to the list of map keeping, ResetManager will loop through this list to clean up the map
     */
    default void registerThisInstance(){
        ResetManager.getInstance().appendMapKeepingInstance(this);
    }

    /**
     * A method for the class to give the logic of cleaning themselves out of map
     * @param playerDied - whether the player died
     */
    boolean cleanUp(boolean playerDied);
}
