package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
import game.Utilities;
import game.actors.enemies.Mimic;
import game.ground.Dirt;
import game.items.SoulTokenItem;

/**
 * This class will implement the logic of the chest opening
 */
public class OpenChestAction extends Action {

    /**
     * The location of the chest
     */
    private Location chestLocation;

    /**
     * The max number of soul token to drop
     */
    private final int MAX_SOUL_TOKEN = 3;

    /**
     * The min number of soul token to drop
     */
    private final int MIN_SOUL_TOKEN = 1;

    /**
     * The constructor
     * @param newChestLocation The location of the chest
     */
    public OpenChestAction(Location newChestLocation){
        chestLocation = newChestLocation;
    }

    /**
     * Open the chest, a 50 percent chance to get soul token, another to spawn a mimic
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return The descriptive string of actor opening the chest
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        chestLocation.setGround(new Dirt());
        String result = "";
        if(Utilities.randomSuccessGenerator(50)){
            int numberOfToken = Utilities.randomNumberGenerator(MAX_SOUL_TOKEN+1,MIN_SOUL_TOKEN);
            for(int i = 0; i<= numberOfToken-1;i++){
                chestLocation.addItem(new SoulTokenItem(chestLocation,100));
            }
            result += "And it drops " + numberOfToken + " Soul Token";
        }
        else{
            chestLocation.addActor(new Mimic("Mimic", map, MAX_SOUL_TOKEN, MIN_SOUL_TOKEN, chestLocation));
            result += "And it turns into a Mimic!!!";
        }
        return actor + " Opened the Chest \n" + result;
    }

    @Override
    /**
     * This function will return the action details used in the menu
     * @param actor The actor performing the action.
     * @return String that describe what will happen if player choose to do this action
     */
    public String menuDescription(Actor actor) {
        return "Open The Chest";
    }
}
