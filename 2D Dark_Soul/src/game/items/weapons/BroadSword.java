package game.items.weapons;

import edu.monash.fit2099.engine.Weapon;
import game.Utilities;
import game.interfaces.PassiveSkill;

/**
 * This class is used to represent the BroadSword which is a weapon/item.
 */
public class BroadSword extends MeleeWeapon implements Weapon, PassiveSkill{

    /**
     * constructor
     */
    public BroadSword() {
        super("BroadSword", 'b', 30, "slashes", 80, 500);
    }

    /**
     * This method is used to return the damage that the broadSword deals to the enemy
     * @return damage (integer type)
     */
    public int damage(){
        //check if the broadSword has a chance to do critical strike
        return criticalHit(damage);
    }

    /**
     *This method represents the passive skill of the BroadSword which is the critical strike
     * @param damage: The amount of damage the BroadSword deals usually
     * @return damage (doubled if it lands on the chance to deal critical strike)
     */
    @Override
    public int criticalHit(int damage){
        if (Utilities.randomSuccessGenerator(20)){
            return damage*2;
        }
        else{
            return damage;
        }
    }
}
