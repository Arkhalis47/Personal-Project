package game.actions;

import edu.monash.fit2099.engine.*;
import game.enums.Status;
import game.items.weapons.StormRuler;

/**
 * Special action to charge the storm ruler
 */
public class ChargeAction extends WeaponAction{

    /**
     * The storm ruler
     */
    private StormRuler stormRuler;

    /**
     * Constructor
     * @param stormRuler The storm ruler
     */
    public ChargeAction(StormRuler stormRuler) {
        super(stormRuler);
        this.stormRuler = stormRuler;
    }

    /**
     * Execute the charge action by charge one time to the storm ruler
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string to display the charge action has been executed
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if(actor.hasCapability(Status.HOSTILE_TO_ENEMY)){
            actor.removeCapability(Status.HOSTILE_TO_ENEMY);
        }
        stormRuler.addCharge(1);
        String result = actor + " charge Storm Ruler (" + stormRuler.getCharge() + '/' + stormRuler.getMAX_CHARGE() + ")";
        if (stormRuler.getCharge() == stormRuler.getMAX_CHARGE()) {
            actor.addCapability(Status.HOSTILE_TO_ENEMY);
            result += "\nThe Storm Ruler is fully charged";
        }
        return result;
    }

    /**
     * To return a string for display at the menu before charge action has been executed
     * @param actor The actor performing the action.
     * @return A string to display at the menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " charge Storm Ruler (" + stormRuler.getCharge() + '/' + stormRuler.getMAX_CHARGE() + ")";
    }

}
