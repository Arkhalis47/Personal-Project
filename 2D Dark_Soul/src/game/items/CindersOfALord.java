package game.items;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import game.enums.CinderEnum;
import game.enums.Status;

/**
 * A class that represent CindersOfALord item that will dropped by lord of cinder
 */
public class CindersOfALord extends Item {

    /**
     * The constructor
     *
     * @param cinder Enum from CinderEnum
     */
    public CindersOfALord(CinderEnum cinder){
        super("Cinders of a lord", 'L', true);
        addCapability(cinder);
    }

    /**
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        super.tick(currentLocation, actor);
        if(actor.hasCapability(Status.UNKINDLED)){
            portable = false;
        }
    }
}
