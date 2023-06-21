package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.ResetManager;

/**
 * This class will implement the Rest feature of the game
 * Will call ResetManager to run resting reset
 */
public class RestAction extends Action {

    /**
     * The name of the bonfire where the player will rest at
     */
    private String bonfireName;

    /**
     * The location of the bonfire
     */
    private Location bonfireLocation;

    /**
     * The constructor
     * @param newBonfireName - The name of the bonfire
     * @param newBonfireLocation - The location of the bonfire
     */
    public RestAction(String newBonfireName, Location newBonfireLocation){
        super();
        bonfireName = newBonfireName;
        bonfireLocation = newBonfireLocation;
    }

    /**
     * Run the reset (rest at bonfire)
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return - Telling the user that the Player rested at bonfire
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        ResetManager.getInstance().runRest(bonfireLocation);
        return actor + " Rested At " + bonfireName;
    }

    /**
     * This function will return the action details used in the menu
     * @param actor The actor performing the action.
     * @return String that describe what will happen if player choose to do this action
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Rest At " + bonfireName;
    }
}
