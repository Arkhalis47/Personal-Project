package game.items.weapons;

import edu.monash.fit2099.engine.*;
import game.Utilities;
import game.actions.AttackAction;
import game.actions.BurnGroundAction;
import game.enums.Status;
import game.enums.WeaponCapabilities;
import game.interfaces.Behaviour;
import game.interfaces.PassiveSkill;

import java.util.ArrayList;

/**
 * This class represent the Darkmoon Longbow
 */
public class DarkmoonLongbow extends RangeWeapon implements PassiveSkill {

    /**
     * ember form of the holder, either true or false
     */
    protected boolean emberForm;

    /**
     * The Constructor
     */
    public DarkmoonLongbow() {
        super("Darkmoon Longbow", 'D', 70, "shoots", 80, 3, false);
        addCapability(WeaponCapabilities.ALDRICH_WEAPON);
        this.emberForm = false;
        portable = false;
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
        if (Utilities.randomSuccessGenerator(15)) {
            return damage * 2;
        } else {
            return damage;
        }
    }

    /**
     * The add Range_Weapon enum to holder
     *
     * @param holder of the darkmoon longbow
     */
    public void addWeaponCapability(Actor holder){
        holder.addCapability(WeaponCapabilities.Range_Weapon);
    }

    /**
     * To perform the weapon ability of darkmoon longbow have
     *
     * @param holder of the darkmoon longbow
     * @param hitPoints the hit point of the holder
     * @param maxHitPoints the maximum hit point of the holder
     */
    public void weaponAbility(Actor holder, int hitPoints, int maxHitPoints){
        if (hitPoints <= 0.5 * maxHitPoints && !emberForm) {
            activateEmberForm(holder, hitPoints);
        }
    }

    /**
     * To activate the ember form by set emberForm to true and heal 20% of the current hit point to the holder
     * @param holder of the darkmoon longbow
     * @param holderHitPoint the hit point of the holder
     */
    public void activateEmberForm(Actor holder, int holderHitPoint){
        this.emberForm = true;
        holder.heal(holderHitPoint*20/100);
    }

    /**
     * To remove the ember form by set the emberForm to false
     */
    public void removeEmberForm(){
        this.emberForm = false;
    }
}
