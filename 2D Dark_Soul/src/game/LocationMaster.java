package game;

import edu.monash.fit2099.engine.Location;

/**
 * The class that takes care of remembering the actor's previous location
 */
public class LocationMaster {
    /**
     * The initial location of the actor
     */
    private Location initialLocation;

    /**
     * The previous location of the actor
     */
    private Location previousLocation;

    /**
     * Constructor
     * @param newInitLocation - The initial location of this actor
     */
    public LocationMaster(Location newInitLocation){
        initialLocation = newInitLocation;
        previousLocation = newInitLocation;
    }

    /**
     * This method will be used to update the initial location of the actor. Useful for save points
     * @param newLocation - The new initial location of the actor
     */
    public void updateInitialLocation(Location newLocation){
        initialLocation = newLocation;
    }

    /**
     * This method will be used to update the previous location of the actor.
     * @param previousLocation - The previous location of the actor
     */
    public void updatePreviousLocation(Location previousLocation){
        this.previousLocation  = previousLocation;
    }

    /**
     * Getter for previous location of the actor
     * @return - The previous location of the actor
     */
    public Location getPreviousLocation(){
        return previousLocation;
    }

    /**
     * Getter for initial location of the actor
     * @return - The initial location of the actor.
     */
    public Location getInitialLocation() {
        return initialLocation;
    }
}
