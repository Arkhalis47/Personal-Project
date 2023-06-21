package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Location;
import game.enums.Status;
import game.interfaces.MapKeeping;

/**
 * This class will implement the mechanics of the Burning Ground
 * Only dirt can be burned, and changed into Burning Ground
 */
public class BurningGround extends DamagingGround implements MapKeeping {
    /**
     * The number of time this Burning Ground will tick/burn anyone that is not immune to Burn Damage
     */
    final int INITIAL_BURN_TIME = 3;

    /**
     * The time when this Burning Ground will change back into Dirt
     */
    final int BURN_END_TIME = 0;

    /**
     * The number of burn time remaining
     */
    int burnTime;

    /**
     * The constructor, burning time is default to 3
     */
    public BurningGround(){
        super('V');
        burnTime = INITIAL_BURN_TIME;
        registerThisInstance();
    }

    /**
     * Implement the logic of damaging anyone that is prone to burn damage
     * @param location The location of the Ground
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        if(getBurnTime()== BURN_END_TIME){
            location.setGround(new Dirt());
        }
        if(location.containsAnActor()){
            Actor actorOnGround = location.getActor();
            if(actorOnGround.hasCapability(Status.PRONE_TO_BURN)){
                actorOnGround.hurt(25);
                if(!actorOnGround.isConscious()){
                    actorDeath(actorOnGround,location,false);
                }
            }
        }
        minusBurnTime(1);

    }

    /**
     * Manipulate the burn time, can only reduce but not increase, for now
     * @param numberOfSubtraction - how many burn times to reduce.
     */
    public void minusBurnTime(int numberOfSubtraction){
        burnTime -= numberOfSubtraction;
        if(burnTime < 0){
            burnTime = BURN_END_TIME;
        }
    }

    /**
     * The getter for burn time
     * @return - The number of burn time remaining.
     */
    public int getBurnTime(){
        return burnTime;
    }


    /**
     * When reset is called, reduce the burn time to 0
     */
    @Override
    public boolean cleanUp(boolean playerDied) {
        minusBurnTime(INITIAL_BURN_TIME);
        return true;
    }
}
