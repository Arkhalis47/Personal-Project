package game.actors.enemies;
import edu.monash.fit2099.engine.*;

import game.behaviours.AggressiveBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.enums.Status;
import game.interfaces.Behaviour;
import game.interfaces.MapKeeping;
import game.interfaces.Soul;

import java.util.ArrayList;

/**
 *  A class that represent the enemies of this game
 */
public abstract class Enemies extends Actor implements MapKeeping, Soul{
    /**
     * Number of souls this enemies have
     */
    private int soul;

    /**
     * Boolean to check whether this enemy is following the player/ any UNKINDLED
     */
    protected boolean following;

    /**
     * A list of behaviour
     */
    protected ArrayList<Behaviour> behaviours = new ArrayList<>();

    /**
     * The map this enemy is in
     */
    private final GameMap map;

    /**
     * Constructor.
     *
     * @param name        the name of the Actor
     * @param displayChar the character that will represent the Actor in the display
     * @param hitPoints   the Actor's starting hit points
     */
    public Enemies(String name, char displayChar, int hitPoints, GameMap initMap) {
        super(name, displayChar, hitPoints);
        registerThisInstance();
        map = initMap;
        following = false;
        addCapability(Status.HOSTILE_TO_UNKINDLED);
        addCapability(Status.ENEMIES);
    }

    /**
     * Select and return an action to perform on the current turn of the enemy
     *
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return the action for enemies to perform in one ture
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        // loop through all behaviours
        Action actionToDo = new DoNothingAction();
        for(Behaviour Behaviour : behaviours) {
            Action action = Behaviour.getAction(this, map);
            if (action != null)
                actionToDo = action;
        }
        return actionToDo;
    }

    /**
     * Getter for getting the map of the enemy standing
     * @return the map that this enemy located
     */
    public GameMap getMap(){
        return map;
    }

    /**
     * To show the name and the current hit point and max hit point of the enemy
     * @return a string to show this enemy's name, hit point and max hit point
     */
    @Override
    public String toString() {
        return name + " (" + hitPoints + "/" + maxHitPoints + ")";
    }

    /**
     * To deduct the enemy by the point value if the deducted hit point is lower than 0 then hit point will be 0
     * @param points number of hit points to deduct.
     */
    @Override
    public void hurt(int points) {
        hitPoints -= points;
        if (hitPoints < 0){
            hitPoints = 0;
        }
    }

    /**
     * To edit the behaviours of enemies in different situation
     */
    public void behavioursEdit(){
        Actor actor = checkAggro(map);
        behaviours.clear();
        if (actor != null) {
            behaviours.add(new FollowBehaviour(actor));
            following = true;
            behaviours.add(new AggressiveBehaviour(actor));
        }
        else {
            following = false;
            behaviours.add(new WanderBehaviour());
        }
    }

    /**
     * To check the aggro in certain area that in the game map
     *
     * @param map The map the enemy is on.
     * @return The player that has been detected
     */
    public Actor checkAggro(GameMap map) {
        int firstX = Math.max(map.locationOf(this).x() - 3, map.getXRange().min());
        int firstY = Math.max(map.locationOf(this).y() - 3, map.getYRange().min());
        int lastX = Math.min(map.locationOf(this).x() + 3, map.getXRange().max());
        int lastY = Math.min(map.locationOf(this).y() + 3, map.getYRange().max());

        for (int x = firstX; x <= lastX; x++) {
            for (int y = firstY; y <= lastY; y++) {
                Location location = map.at(x,y);
                if (location.containsAnActor()) {
                    if (this.hasCapability(Status.HOSTILE_TO_UNKINDLED) &&
                            location.getActor().hasCapability(Status.UNKINDLED)) {
                        return location.getActor();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Getter for getting the soul of the enemy
     * @return the set soul of the enemy
     */
    @Override
    public int getSouls() {
        return soul;
    }

    /**
     * Setter to set to soul of the enemy
     * @param newSouls the soul count to set
     */
    @Override
    public void setSouls(int newSouls) {
        soul = newSouls;
    }
}

