package game.actions;

import edu.monash.fit2099.engine.*;
import game.actors.SwapWeaponAction;
import game.enums.CinderEnum;
import game.enums.Status;
import game.enums.WeaponCapabilities;
import game.items.CindersOfALord;

/**
 * This class is used to carry out the transaction for any available item in the vendor that the player purchases.
 */
public class BuyAction extends Action {

    /**
     * Here we have two variables:
     * purchasedWeapon: used to store the weapon the player purchases.
     * costs: The cost of the purchasedWeapon.
     */
    private final Item purchasedWeapon;
    private final int costs;
    private Enum<CinderEnum> weaponEnum;
    private boolean buyLordWeapon;

    /**
     *
     * @param newPurchasedWeapon: The new item/weapon the player purchases
     * @param newCosts: The cost of the newly purchased weapon/item
     */
    public BuyAction(Item newPurchasedWeapon, int newCosts){
        this.purchasedWeapon = newPurchasedWeapon;
        this.costs = newCosts;
        buyLordWeapon = false;
    }

    /**
     *
     * @param newPurchasedWeapon: The new weapon(lord of cinder weapon) that the player trades cinder for
     * @param cinder: The cinder of a lord that the player trades
     */
    public BuyAction(Item newPurchasedWeapon, Enum<CinderEnum> cinder){
        this.purchasedWeapon = newPurchasedWeapon;
        this.costs = 0;
        weaponEnum = cinder;
        buyLordWeapon = true;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        String retVal = "";
        if (buyLordWeapon){
            //check if the player has selected the option to trade for a lordWeapon
            if (actor.hasCapability(weaponEnum)){
                //check if the player has the correct Cinder to trade for the corresponding weapon
                for (int i =0; i < actor.getInventory().size(); i++){
                    //if the player does have correct Cinder then remove the Cinder and proceed with the transaction
                    if (actor.getInventory().get(i).hasCapability(weaponEnum)){
                        actor.removeItemFromInventory(actor.getInventory().get(i));
                    }
                }
                SwapWeaponAction swap = new SwapWeaponAction(purchasedWeapon);
                swap.execute(actor, map);
                retVal = "Transaction passed";
            }
            else{
                retVal = "Transaction failed";
            }
        }

        else{
            //retrieve number of souls player has
            int souls = actor.asSoul().getSouls();

            //checking if he has enough souls to purchase an item
            if (souls - getCosts() < 0){
                retVal = "Transaction has failed. Player has insufficient number of souls";
            }

            //carry out the transaction

            souls -= getCosts();

            //Here a new SwapWeaponAction is created with the purchsed weapon as the parameter. Then the .execute is called
            //which executes the swap and swaps the current weapon in the inventory with the purchased one
            SwapWeaponAction swap = new SwapWeaponAction(getPurchasedWeapon());
            swap.execute(actor, map);

            actor.asSoul().setSouls(souls);
            retVal = "Transaction was successful";
        }

        return retVal;
    }

    @Override
    public String menuDescription(Actor actor) {
        if (buyLordWeapon){
            return "Trade Cinders of a Lord for " + getPurchasedWeapon();
        }
        return "Buy " + getPurchasedWeapon() + " for " + getCosts();
    }

    /**
     * Retrieve the cost of the purchased item
     * @return cost (integer)
     */
    public int getCosts(){
        return costs;
    }

    /**
     * A getter used to retrieve the purchased weapon/item
     * @return weapon of type Item
     */
    public Item getPurchasedWeapon(){
        return purchasedWeapon;
    }

}
