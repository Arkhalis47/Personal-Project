package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Location;
import game.ResetManager;
import game.enums.Status;

/**
 * This abstract class will be extended by any action that damages any actors
 */
public abstract class DamagingAction extends Action {

    /**
     * This method will take care of the death of any actor, when killed the damaging action
     * @param attacker - Actor that killed the target
     * @param target - Actor that was killed
     * @param newSoulTokenLocation - The death location of the actor, this is really just here to take care of placement of soul token
     * @param soulAtPreLocation - Boolean to tell whether to place the soul token at the previous location of the player
     */
    public void actorDeath(Actor attacker, Actor target, Location newSoulTokenLocation, boolean soulAtPreLocation){
        Display display = new Display();
        if(!target.hasCapability(Status.UNKINDLED)){
            newSoulTokenLocation.map().removeActor(target);
            attacker.asSoul().addSouls(target.asSoul().getSouls());
            if(target.hasCapability(Status.LORD_OF_CINDER)){
                display.println("LORD OF CINDER FALLEN");
            }
        }
        else{
            display.println(ResetManager.getInstance().getDeathMessage());
            ResetManager.getInstance().runDeath(newSoulTokenLocation,soulAtPreLocation);
        }
    }
}
