package game.actions;

import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.enums.Abilities;
import game.enums.Status;
import game.enums.WeaponCapabilities;
import game.ground.BurningGround;

/**
 * Special action to burn the ground
 */
public class BurnGroundAction extends Action {

    /**
     * To execute the burn ground action by burning the ground
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string to display the burn ground action has been executed
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        for (Exit exit : map.locationOf(actor).getExits()) {
            if(exit.getDestination().getGround().hasCapability(Abilities.BURN_GROUND)){
                exit.getDestination().setGround(new BurningGround());
            }
        }
        return menuDescription(actor);
    }

    /**
     * To return a string to display executed burn ground action
     * @param actor The actor performing the action.
     * @return a string for display after executed the burn ground action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " execute ember form.";
    }
}
