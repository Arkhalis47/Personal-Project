package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import game.enums.Status;

/**
 * This class is used to provide a service to the player through the vendor. The service increases the player's max Hp
 * and Hp by 25 hit points.
 */
public class VendorServiceAction extends Action {

    @Override
    public String execute(Actor actor, GameMap map) {
        //retrieve number of souls player has
        int souls = actor.asSoul().getSouls();

        //check that player has enough number of souls to purchase the service
        if (souls - getCost() < 0){
            return "Transaction failed. Player has insufficient number of souls";
        }
        souls -= getCost();

        //increase player's HP and max HP by 25 hit points
        actor.increaseMaxHp(25);
        actor.heal(25);

        //update the total number of souls the player has (by upcasting to type soul first)
        (actor).asSoul().setSouls(souls);

        return "Player health and max HP successfully increased by 25 hp";
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Increase player's max HP modifier (200 souls)";
    }

    /**
     * This method returns the cost for using the vendor service
     * @return cost which is of type integer
     */
    public int getCost(){
        return 200;
    }
}
