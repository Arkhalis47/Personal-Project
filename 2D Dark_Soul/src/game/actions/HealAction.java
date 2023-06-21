package game.actions;

import edu.monash.fit2099.engine.*;
import game.items.EstusFlask;
import game.actors.Player;

/**
 * This class is used to create the healAction which is used by the player to increase his HP
 */
public class HealAction extends Action{
    private EstusFlask existingFlask;

    public HealAction(EstusFlask instance){
        existingFlask = instance;
    }

    /**
     * This execute method basically heals the actor and updates the estus flask charges (reducing the charge by 1 everytime)
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return message
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        actor.heal(-4); //if it's negative, it's a multiplier.

        existingFlask.updateCharges(-1);

        return "Unkindled drank from Estus Flask";
    }
    @Override
    public String menuDescription(Actor actor) {
        return "Unkindled drinks from Estus Flask " + existingFlask.getCharges() + "/3";
    }

}
