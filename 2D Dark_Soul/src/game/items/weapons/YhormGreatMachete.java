package game.items.weapons;

import edu.monash.fit2099.engine.*;
import game.actions.BurnGroundAction;
import game.enums.Status;
import game.enums.WeaponCapabilities;

/**
 * This class represent the Yhorm great machete
 */
public class YhormGreatMachete extends WeaponItem {

    /**
     * whether the ember form has been activated or not
     */
    private boolean emberForm;

    /**
     * The Constructor
     */
    public YhormGreatMachete() {
        super("Yhorm’s Great Machete", 'M', 95, "hit", 60);
        addCapability(WeaponCapabilities.YHORM_WEAPON);
        allowableActions.add(new BurnGroundAction());
        this.emberForm = false;
        portable = false;
    }

    /**
     * @return the hitRate with addition 30 if ember form activated else return default hitRate
     */
    public int chanceToHit(){
        if (emberForm) {
            return hitRate + 30;
        }
        else {
            return hitRate;
        }
    }

    /**
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        super.tick(currentLocation, actor);
        if(actor.hasCapability(Status.PRONE_TO_BURN)){
            actor.removeCapability(Status.PRONE_TO_BURN);
        }
    }

    /**
     * To get the burn ground action
     * @param actor the actor who hold Yhorm great machete
     * @param map the map of the actor located
     * @return the burn ground action
     */
    public Action getBurnGroundAction(Actor actor, GameMap map) {
        return new BurnGroundAction();
    }

    /**
     * To activate the ember form by set emberForm to true
     */
    public void activateEmberForm(){
        this.emberForm = true;
    }

    /**
     * To remove the ember form by set emberForm to false
     */
    public void removeEmberForm(){
        this.emberForm = false;
    }

    /**
     * Getter to get the emberForm
     *
     * @return emberForm
     */
    public boolean getEmberForm(){
        return this.emberForm;
    }
}
