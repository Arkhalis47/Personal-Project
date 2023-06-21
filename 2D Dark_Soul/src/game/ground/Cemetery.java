package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;
import game.Utilities;
import game.actors.enemies.Undead;

import java.util.Random;

/**
 * This Cemetery class will be our spawner of Undead, talk about spooky.
 */
public class Cemetery extends Ground {
    /**
     * The range to spawn enemies, this is the minimum coordinate manipulator
     */
    private final int MINIMUM_COORDINATE = -3;

    /**
     * The range to spawn enemies, this is the maximum coordinate manipulator
     */
    private final int MAXIMUM_COORDINATE = 3;

    /**
     * The constructor
     */
    public Cemetery() {
        super('C');
    }

    /**
     * Return a boolean to check whether the actor can enter this ground or not
     * In current implementation, actor cannot enter the cemetery
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Return a boolean to check whether to block thrown object/ range attack
     * Cemetery can block any range attack
     */
    @Override
    public boolean blocksThrownObjects() {
        return true;
    }

    /**
     * Taking care of the logic of spawning undead within the range from (MINIMUM_COORDINATE) to (MAXIMUM_COORDINATE)
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        if(Utilities.randomSuccessGenerator(25)){
            Location newSpawnLocation = getUndeadSpawnLocation(location,0);
            if(newSpawnLocation!=null){
                GameMap initialMap = newSpawnLocation.map();
                newSpawnLocation.addActor(new Undead("Undead",initialMap));
            }
        }
    }

    /**
     * Get the location to spawn the new Undead
     * @param location - The location of this Cemetery
     * @param numberOfCalls - The number of calls to this method, if called for three times and still found no place to spawn, abort the spawning.
     * @return - The location to spawn the Undead
     */
    private Location getUndeadSpawnLocation(Location location, int numberOfCalls){
        Random rand = new Random();
        int initialX = location.x();
        int initialY = location.y();
        int randomX = MINIMUM_COORDINATE + rand.nextInt(MAXIMUM_COORDINATE-MINIMUM_COORDINATE+1);
        int randomY = MINIMUM_COORDINATE + rand.nextInt(MAXIMUM_COORDINATE-MINIMUM_COORDINATE+1);
        if(numberOfCalls == 3){
            return null; //abort the call here.
        }
        if((randomX == 0 && randomY == 0) || (location.map().at(initialX+randomX,initialY+randomY).containsAnActor())){
            return getUndeadSpawnLocation(location, numberOfCalls+1); //Recursion here when the random location is not available
        }
        return location.map().at(initialX+randomX,initialY+randomY);
    }
}
