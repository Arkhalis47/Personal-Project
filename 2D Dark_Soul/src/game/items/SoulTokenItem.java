package game.items;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.addons.DesignOSoulsAddOn;
import game.actions.PickUpSoulAction;
import game.interfaces.MapKeeping;
import game.interfaces.Soul;

/**
 * This class will take care of the logic of Soul Token, an item that will be placed in the map when player died.
 */
public class SoulTokenItem extends Item implements Soul, DesignOSoulsAddOn, MapKeeping {

    /**
     * The number of soul this instance has
     */
    private int soul;

    /**
     * The Location of this instance
     */
    private Location soulLocation;

    /**
     * Constructor
     * @param newSoulLocation - The initial location of the Soul Token
     */
    public SoulTokenItem(Location newSoulLocation, int initialSoul){
        super("Soul Token", '$',true);
        soulLocation = newSoulLocation;
        allowableActions.add(new PickUpSoulAction(this));
        registerThisInstance();
        setSouls(initialSoul);
    }

    /**
     * The method that will take care of the logic of removing soul token from the map
     * And setting the instance to null
     */
    public void destroyInstance(){
        soulLocation.removeItem(this);
    }

    /**
     * Return the number of soul this soul token has
     */
    @Override
    public int getSouls() {
        return soul;
    }

    /**
     * Set the number of soul this soul token will have
     */
    @Override
    public void setSouls(int newSouls) {
        soul = newSouls;
    }



    /**
     * Allow soul token to update its current location at every turn
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        super.tick(currentLocation, actor);
        soulLocation = currentLocation; //update location
    }

    /**
     * Allow soul token to update its current location at every turn
     */
    @Override
    public void tick(Location currentLocation) {
        super.tick(currentLocation);
        soulLocation = currentLocation; //update location
    }

    /**
     * Remove the soul token from the map if the player died
     */
    @Override
    public boolean cleanUp(boolean playerDied) {
        if(playerDied){
            destroyInstance();
            return true;
        }
        return false;
    }

    /**
     * This method will take care of transferring the player soul to this Soul Token
     * And place it in the map that Player died in
     * @param actor - The Player that died
     */
    public void addSoulTokenItem(Actor actor){
        actor.asSoul().transferSouls(this);
        soulLocation.addItem(this);
    }
}
