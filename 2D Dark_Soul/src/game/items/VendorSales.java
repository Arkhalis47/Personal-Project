package game.items;

import edu.monash.fit2099.engine.Item;
import game.actors.enemies.AldrichTheDevourer;
import game.enums.CinderEnum;
import game.items.weapons.BroadSword;
import game.items.weapons.DarkmoonLongbow;
import game.items.weapons.GiantAxe;
import game.items.weapons.YhormGreatMachete;

import java.util.ArrayList;

/**
 * This class is used to store the items/weapons that the vendor will sell to the player
 */
public class VendorSales {
    /**
     * We have 4 ArrayLists:
     * weapons: To store all weapons other than the Lord of Cinder weapons
     * lordWeapons: Used to store Lord of Cinder weapons
     * costs: Used to store the cost of weapons
     * cinderEnum: used to store the Enum of CinderOfALord item which is used to trade for the lordWeapons
     */

    private ArrayList<Item> weapons = new ArrayList<Item>();

    private ArrayList<Item> lordWeapons = new ArrayList<Item>();
    private ArrayList<Integer> costs = new ArrayList<Integer>();
    private ArrayList<Enum<CinderEnum>> cinderEnum = new ArrayList();

    /**
     * This constructor is used to initialize the items vendor has for trade/sale
     */
    public VendorSales(){
        weapons.add(new BroadSword());
        costs.add(500);

        weapons.add(new GiantAxe());
        costs.add(1000);

        lordWeapons.add(new YhormGreatMachete());
        cinderEnum.add(CinderEnum.CINDER_YHORM);

        lordWeapons.add(new DarkmoonLongbow());
        cinderEnum.add(CinderEnum.CINDER_ALDRICH);

    }

    /**
     *
     * @return returns the arraylist of weapons
     */
    public ArrayList<Item> getWeapons(){
        return new ArrayList<>(weapons);
    }

    /**
     * Used to retrieve the costs for the weapons
     * @return costs for the corresponding weapon
     */
    public ArrayList<Integer> getCosts(){
        return new ArrayList<>(costs);
    }

    /**
     * Used to retrieve the weapons of Lord of Cinder
     * @return weapons
     */
    public ArrayList<Item> getLordWeapons(){
        return new ArrayList<>(lordWeapons);
    }

    /**
     * Used to retrieve the enum for the CinderOfALord item
     * @return enum for the corresponding cinder
     */
    public ArrayList<Enum<CinderEnum>> getCinderEnum(){
        return cinderEnum;
    }
}
