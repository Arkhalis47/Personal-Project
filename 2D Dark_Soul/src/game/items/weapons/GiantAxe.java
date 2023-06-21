package game.items.weapons;

import edu.monash.fit2099.engine.Weapon;
import game.actions.SpinAttackAction;
import game.enums.WeaponCapabilities;

/**
 * This class is used to represent the Giant Axe which is a weapon/item.
 */
public class GiantAxe extends MeleeWeapon implements Weapon {

    private SpinAttackAction spinAttackAction;

    /**
     * This is constructor for the giant axe which adds the appropriate weapon capability to the giant axe and
     * adds the SpinAttackAction as well
     */
    public GiantAxe() {
        super("Giant Axe", 'G', 50, "swings", 80, 1000);
        addCapability(WeaponCapabilities.UNDIRECTED_ACTIVE_SKILL);
        this.spinAttackAction = new SpinAttackAction(this);
        allowableActions.add(spinAttackAction);
    }

}
