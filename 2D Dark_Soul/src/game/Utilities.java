package game;
import java.util.Random;

/**
 * This class will be used for any utility method
 */
public class Utilities {
    /**
     * A wrapper for getting pass and fails
     * @param chanceToSuccess : success rate
     * @return true if success, false if fail
     */
    public static boolean randomSuccessGenerator(int chanceToSuccess){
        Random rand = new Random();
        return rand.nextInt(100) <= chanceToSuccess;
    }

    /**
     * A wrapper to return a random number between the upper bound(excluded) and lower bound(included)
     * @param upperBound The upper bound of the range to return (excluded)
     * @param lowerBound The lower bound of the range to return (included)
     * @return A random number between the upper bound(excluded) and lower bound(included)
     */
    public static int randomNumberGenerator(int upperBound, int lowerBound){
        Random rand = new Random();
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    }
}
