package game.items.weapons;


import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.actions.ChargeAction;
import game.actors.SwapWeaponAction;
import game.enums.Status;
import game.enums.WeaponCapabilities;
import game.actions.WindSlashAction;
import game.interfaces.PassiveSkill;
import game.items.weapons.MeleeWeapon;

/**
 * A class that represent storm ruler
 */
public class StormRuler extends MeleeWeapon implements PassiveSkill {

    /**
     * The charge value of storm ruler
     */
    protected int charge;

    /**
     * The action for charging storm ruler
     */
    private WeaponAction chargeAction;

    /**
     * The maximum charge of storm ruler
     */
    private final int MAX_CHARGE = 3;

    /**
     * To check the storm ruler in actor's inventory if no then add swap weapon action else add charge action
     *
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        super.tick(currentLocation, actor);
        allowableActions.clear();
        if(!(actor.hasCapability(WeaponCapabilities.STORM_RULER))){
            allowableActions.add(new SwapWeaponAction(this));
        }
        else{
            if(charge!= MAX_CHARGE){
                allowableActions.add(chargeAction);
            }
        }
    }

    /**
     * Constructor
     */
    public StormRuler() {
        super("Storm Ruler", '7', 70, "hit", 60, 2000);
        addCapability(WeaponCapabilities.DULLNESS);
        this.charge = 0;
        portable = false;
        chargeAction = new ChargeAction(this);
        allowableActions.add(new SwapWeaponAction(this));
        addCapability(WeaponCapabilities.STORM_RULER);
    }

    /**
     * The amount of damage with the calculated critical hit
     *
     * @return the damage after the critical hit has been calculated
     */
    public int damage() {
        return criticalHit(damage);
    }

    /**
     * This method represents the passive skill of the weapon which is the critical hit
     * @param damage: The default damage of the weapon
     * @return damage (doubled if it lands on the chance to deal critical hit)
     */
    @Override
    public int criticalHit(int damage) {
        if (Utilities.randomSuccessGenerator(20)) {
            return damage * 2;
        } else {
            return damage;
        }
    }

    /**
     * To increase the charge by the charge value given
     * @param chargeValue The charge value that will be added
     */
    public void addCharge(int chargeValue) {
        charge += chargeValue;
        if (charge == MAX_CHARGE) {
            allowableActions.remove(chargeAction);
        }
    }

    /**
     * To clear the charge after wind slash action has been executed
     */
    public void clearCharge() {
        charge = 0;
        WeaponAction chargeAction = new ChargeAction(this);
        allowableActions.add(chargeAction);
        this.chargeAction = chargeAction;
    }

    /**
     * To get the active skill provide by storm ruler
     * @param target the target actor
     * @param direction the direction of target, e.g. "north"
     * @return the active skill of the storm ruler have
     */
    public WeaponAction getActiveSkill(Actor target, String direction) {
        if ((charge == MAX_CHARGE) && target.hasCapability(Status.WEAK_TO_STORMRULER)) {
            return new WindSlashAction(this, target, direction);
        } else {
            return null;
        }
    }

    /**
     * Getter to get the charge
     * @return charge
     */
    public int getCharge(){
        return charge;
    }

    /**
     * Getter to get the maximum value of charge
     * @return MAX_CHARGE
     */
    public int getMAX_CHARGE(){
        return  MAX_CHARGE;
    }
}
