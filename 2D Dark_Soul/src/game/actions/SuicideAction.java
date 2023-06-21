package game.actions;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * Special Action to allow an actor to die instantly.
 */
public class SuicideAction extends Action {

    /**
     * The Actor that will die instantly
     */
    protected Actor actor;

    /**
     * Constructor
     * @param actor The actor performing the action.
     */
    public SuicideAction(Actor actor) {
        this.actor = actor;
    }

    /**
     * Execute the suicide action to allow the actor die instantly
     * @param actor The actor performing the action.
     * @param map The map the actor is on.
     * @return A string to show the actor is dead
     */
    @Override
    public String execute(Actor actor, GameMap map){
        map.removeActor(actor);
        return menuDescription(actor);
    }

    /**
     * To return a string to display the actor is dead
     * @param actor The actor performing the action.
     * @return A string to display the actor is dead
     */
    @Override
    public String menuDescription(Actor actor){
        return actor + " is dead";
    }
}