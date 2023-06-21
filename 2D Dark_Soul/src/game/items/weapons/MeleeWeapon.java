package game.items.weapons;

import edu.monash.fit2099.engine.WeaponItem;

/**
 * This class represent the melee weapon existed in the game
 */
public class MeleeWeapon extends WeaponItem {

    private final int price;
    /**
     * Constructor.
     *
     * @param name        name of the item
     * @param displayChar character to use for display when item is on the ground
     * @param damage      amount of damage this weapon does
     * @param verb        verb to use for this weapon, e.g. "hits", "zaps"
     * @param hitRate     the probability/chance to hit the target.
     */
    public MeleeWeapon(String name, char displayChar, int damage, String verb, int hitRate, int newPrice) {
        super(name, displayChar, damage, verb, hitRate);
        this.price = newPrice;
        portable = false;
    }

    /**
     * The getter for price of the weapon
     * @return - Price of the weapon
     */
    public int getPrice(){
        return price;
    }
}
