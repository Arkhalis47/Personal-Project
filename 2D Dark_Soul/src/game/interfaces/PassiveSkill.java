package game.interfaces;

import game.Utilities;

public interface PassiveSkill {

    /**
     * This method represents the passive skill of the weapon which is the critical hit
     * @param damage: The default damage of the weapon
     * @return damage (doubled if it lands on the chance to deal critical hit)
     */
    int criticalHit(int damage);
}
