package game.items;

import edu.monash.fit2099.engine.Item;
import game.actions.HealAction;

/**
 * This is a class which represents the estus flask item.
 */
public class EstusFlask extends Item {
    /**
     * MAX_CHARGES: used to store the max number of charges for the estus flask
     * charges: The number of times the player could use the estus flask
     */
    private final int MAX_CHARGES = 3;
    private int charges;
    HealAction healAction;

    public EstusFlask() {
        super("Estus Flask", 'e', false);
        charges = MAX_CHARGES;
        //adding a new HealAction to the estus flask
        healAction = new HealAction(this);
        allowableActions.add(healAction);
    }

    /**
     * A emthod used to update the number of charges on the estus flask
     * @param numberOfCharge: The current number of charges in the estus flask
     */
    public void updateCharges(int numberOfCharge){
        charges += numberOfCharge;
        charges = Math.min(MAX_CHARGES,charges);
        //removing healAction if no more charges are available
        allowableActions.clear();
        if (charges > 0){
            allowableActions.add(new HealAction(this));
        }
    }

    public int getCharges(){
        return charges;
    }

}
