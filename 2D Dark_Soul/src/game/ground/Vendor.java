package game.ground;

import edu.monash.fit2099.engine.*;
import game.items.VendorSales;
import game.actions.BuyAction;
import game.actions.VendorServiceAction;
import game.enums.Status;
/**
 * This class represents the Vendor/Fire Keeper
 */
public class Vendor extends Ground{

    /**
     * The Constructor
     */
    public Vendor() {
        super('F');
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * This method is used to add the BuyAction and VendorServiceAction to the vendor
     * For the BuyAction we first create a new VendorSales object to get the items the vendor will be selling
     * Then using a for loop we iterate through all of them and display the options to the player along with the
     * corresponding prices
     * @param actor the Actor acting
     * @param location the current Location
     * @param direction the direction of the Ground from the Actor
     * @return actionlist
     */
    public Actions allowableActions(Actor actor, Location location, String direction) {
        Actions actionList = super.allowableActions(actor, location, direction);
        if(actor.hasCapability(Status.UNKINDLED)) {
            //here we create a new VendorSales object if the actor is the Player
            VendorSales vendorSales = new VendorSales();
            //iterate through the weapons arraylist in the vendorSales and display the options to the player
            for (int i = 0; i < vendorSales.getWeapons().size(); i++) {
                BuyAction buyAction = new BuyAction(vendorSales.getWeapons().get(i), vendorSales.getCosts().get(i));
                actionList.add(buyAction);
            }
            //iterate through the lord weapon arraylist and display the trading options to the player
            for (int i = 0; i <= vendorSales.getLordWeapons().size()-1; i++){
                actionList.add(new BuyAction(vendorSales.getLordWeapons().get(i), vendorSales.getCinderEnum().get(i)));
            }
            //this represents the vendorServiceAction which the player can use to purchase the max hp increase service
            VendorServiceAction vendorServiceAction = new VendorServiceAction();
            actionList.add(vendorServiceAction);
        }
        return actionList;
    }
}