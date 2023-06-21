package game.ground;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;
import game.ResetManager;
import game.enums.Status;

/**
 * The abstract class for any Ground that will cause damage to actor.
 */
public abstract class DamagingGround extends Ground {
    public DamagingGround(char displayChar){
        super(displayChar);
    }

    /**
     * This method will take care of the logic of dealing with any actor's death.
     * @param actor - The actor that died
     * @param newSoulTokenLocation - The location of actor that died, this is really there to take care of death of Player.
     * @param soulAtPreLocation - Whether to place the Soul Token at the previous location of the Player.
     */
    public void actorDeath(Actor actor, Location newSoulTokenLocation, boolean soulAtPreLocation){
        Display display = new Display();
        if(!actor.hasCapability(Status.UNKINDLED)){
            newSoulTokenLocation.map().removeActor(actor);
            if(actor.hasCapability(Status.LORD_OF_CINDER)){
                display.println("LORD OF CINDER FALLEN");
            }
        }
        else{
            display.println(ResetManager.getInstance().getDeathMessage());
            ResetManager.getInstance().runDeath(newSoulTokenLocation,soulAtPreLocation);
        }
    }
}
