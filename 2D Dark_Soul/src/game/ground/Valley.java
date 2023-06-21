package game.ground;

import edu.monash.fit2099.engine.*;
import game.enums.Status;
import game.ground.DamagingGround;

/**
 * The gorge or endless gap that is dangerous for the Player.
 */
public class Valley extends DamagingGround {

	/**
	 * The constructor
	 */
	public Valley() {
		super('+');
	}

	/**
	 * Take care of the logic of allowing actor to enter. Only those who are prone to fall can enter the Valley.
	 * @param actor the Actor to check
	 * @return false or actor cannot enter.
	 */
	@Override
	public boolean canActorEnter(Actor actor){
		boolean flag = false;
		if(actor.hasCapability(Status.PRONE_TO_FALL)){
			flag = true;
		}
		else{
			flag = false;
		}
		return flag;
	}

	/**
	 * Taking care of the logic of killing the player.
	 * @param location The location of the Ground
	 */
	@Override
	public void tick(Location location) {
		super.tick(location);
		if(location.containsAnActor()){
			Actor playerAtHere = location.getActor();
			//Given that player is not immune to fall ie didn't have the ability to float
			if(playerAtHere.hasCapability(Status.PRONE_TO_FALL)){
				actorDeath(playerAtHere,location,true);
			}
		}
	}


}
