package game.actors;

import edu.monash.fit2099.engine.*;
import game.enums.Status;
import game.interfaces.Behaviour;
import game.interfaces.Resettable;
import game.interfaces.Soul;

import java.util.ArrayList;

/**
 * The boss of Design o' Souls
 *
 */
public abstract class LordOfCinder extends Actor implements Resettable, Soul {

    /**
     * The initial location of this Lord Of Cinder
     */
    protected Location initialLocation;

    /**
     * The empty list of behaviour of this Lord Of Cinder
     */
    protected ArrayList<Behaviour> behaviours = new ArrayList<>();

    /**
     * The number of soul of this Lord of Cinder has
     */
    private int soul;

    private final int aggroRange;

    /**
     * Constructor.
     */
    public LordOfCinder(String name, char displayChar, int hitPoints, Location newInitialLocation, int aggroRange) {
        super(name, displayChar, hitPoints);
        initialLocation = newInitialLocation;
        registerInstance();
        addCapability(Status.LORD_OF_CINDER);
        addCapability(Status.ENEMIES);
        this.aggroRange = aggroRange;
    }

    /**
     * @param actions    collection of possible Actions for this Actor
     * @param lastAction The Action this Actor took last turn. Can do interesting things in conjunction with Action.getNextAction()
     * @param map        the map containing the Actor
     * @param display    the I/O object to which messages may be written
     * @return DoNothingAction
     */
    @Override
    public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
        return new DoNothingAction();
    }


    /**
     * To return a string to display the name, hit point and max hit point of lord of cinder
     * @return A descriptive string of the name and hitpoints
     */
    @Override
    public String toString(){ return name + "(" + hitPoints + "/" + maxHitPoints + ")"; }

    /**
     * To reset lord of cinder to initial status
     */
    @Override
    public void resetInstance() {
        this.heal(maxHitPoints);
        initialLocation.map().moveActor(this,initialLocation);
    }

    /**
     * Getter to get the soul of lord of cinder
     * @return The number of soul this lord of cinder has
     */
    @Override
    public int getSouls() {
        return soul;
    }

    /**
     * Setter to set the soul of lord of cinder
     * @param newSouls the soul count to set
     */
    @Override
    public void setSouls(int newSouls) {
        soul = newSouls;
    }

    /**
     * To deduct the hit point of lord of cinder
     * @param points number of hitpoints to deduct.
     */
    @Override
    public void hurt(int points) {
        super.hurt(points);
        if(hitPoints < 0){
            hitPoints = 0;
        }
    }

    /**
     * To check the aggro in certain area that in the game map
     *
     * @param map The map the enemy is on.
     * @return The player that has been detected
     */
    public Actor checkAggro(GameMap map) {
        int firstX = Math.max(map.locationOf(this).x() - aggroRange, map.getXRange().min());
        int firstY = Math.max(map.locationOf(this).y() - aggroRange, map.getYRange().min());
        int lastX = Math.min(map.locationOf(this).x() + aggroRange, map.getXRange().max());
        int lastY = Math.min(map.locationOf(this).y() + aggroRange, map.getYRange().max());

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
}
