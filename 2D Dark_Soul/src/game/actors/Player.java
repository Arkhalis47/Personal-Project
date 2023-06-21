package game.actors;

import edu.monash.fit2099.engine.*;
import edu.monash.fit2099.engine.addons.DesignOSoulsAddOn;
import game.*;
import game.enums.Abilities;
import game.enums.Status;
import game.interfaces.Soul;
import game.items.weapons.BroadSword;
import game.items.EstusFlask;
import game.items.SoulTokenItem;
import game.items.weapons.GiantAxe;

/**
 * Class representing the Player.
 */
public class Player extends Actor implements Soul, DesignOSoulsAddOn{

	/**
	 * The menu which the user will use to choose an action
	 */
	private final Menu menu = new Menu();

	/**
	 * The number of soul this player has
	 */
	private int soul;

	/**
	 * The estus flask of the player
	 */
	private EstusFlask estusFlask = new EstusFlask(); //new estus flask object with 3 charges

	/**
	 * The location master of the player
	 */
	private LocationMaster locationMaster;

	/**
	 * Constructor.
	 *
	 * @param name        Name to call the player in the UI
	 * @param displayChar Character to represent the player in the UI
	 * @param hitPoints   Player's starting number of hitpoints
	 */
	public Player(String name, char displayChar, int hitPoints, Location initBonfire) {
		super(name, displayChar, hitPoints);
		this.addCapability(Status.HOSTILE_TO_ENEMY);
		this.addCapability(Abilities.REST);
		this.addCapability(Status.UNKINDLED);
		this.addCapability(Status.PRONE_TO_BURN);
		this.addCapability(Status.PRONE_TO_FALL);
		this.addItemToInventory(estusFlask); //add the estus flask to the player inventory
		this.addItemToInventory(new BroadSword()); //add broadsword to the player inventory
		locationMaster = new LocationMaster(initBonfire);
		ResetManager.getInstance().setPlayer(this);
		TeleportationMaster.getInstance().setPlayerInGame(this);
	}

	/**
	 * Allow player to select an action to do during its turn
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		//Saves the previous location
		locationMaster.updatePreviousLocation(map.locationOf(this));
		//Print the status
		display.println(this + " holding " + getWeapon() + "," + getSouls() + " souls");
		// Handle multi-turn Actions
		if (lastAction.getNextAction() != null)
			return lastAction.getNextAction();

		return menu.showMenu(this, actions, display);
	}

	/**
	 * Return the number of soul the player has
	 */
	@Override
	public int getSouls() {
		return soul;
	}

	/**
	 * Set the number of soul this player will have
	 */
	@Override
	public void setSouls(int newSouls) {
		soul = newSouls;
	}

	/**
	 * This method will be used to reset the player when the player died
	 * @param deathLocation - The location where the player died
	 * @param soulAtPreLocation - The boolean which tells whether Soul Token should be placed at previous position of the player
	 */
	public void resetPlayerInstance(Location deathLocation, boolean soulAtPreLocation) {
		if (soulAtPreLocation){
			deathLocation = locationMaster.getPreviousLocation();
		}
		SoulTokenItem soulToken = new SoulTokenItem(deathLocation,0);
		soulToken.addSoulTokenItem(this);
		locationMaster.getInitialLocation().map().moveActor(this,locationMaster.getInitialLocation());
		this.heal(maxHitPoints);
		estusFlask.updateCharges(3);
	}

	/**
	 * This method will be used to reset the player when the player rested at Bonfire
	 */
	public void resetPlayerInstance(Location newBonfireLocation){
		locationMaster.updateInitialLocation(newBonfireLocation);
		this.heal(maxHitPoints);
		estusFlask.updateCharges(3);
	}

	/**
	 * This method is used to heal the Player
	 * @param points number of hitpoints to add. Or the multiplier to heal
	 */
	@Override
	public void heal(int points){
		//If points is negative, means its multiplier
		if (points < 0){
			double point = Math.abs(points);
			double multiplier = point / 10;
			super.heal((int) Math.round(maxHitPoints*multiplier));
		}
		else{
			super.heal(points);
		}
	}

	/**
	 * This method is to reduce the hitpoints of the player
	 */
	@Override
	public void hurt(int points){
		super.hurt(points);
		if(hitPoints < 0){
			hitPoints = 0;
		}
	}

	/**
	 * Add the number of soul into the possession of the player
	 */
	@Override
	public boolean addSouls(int souls) {
		soul += souls;
		return true;
	}

	/**
	 * This getter will return the location master of the player
	 * @return The location master
	 */
	public LocationMaster getLocationMaster() {
		return locationMaster;
	}

	/**
	 * Return a descriptive string of the name and the hitpoints of the player
	 */
	@Override
	public String toString() {
		return name + "(" + hitPoints + "/" + maxHitPoints + ")";
	}
}
