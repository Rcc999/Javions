package ch.epfl.javions;

/**
 * Check whether a condition is true or raise exception
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class Preconditions {

    /**
     * Constructor: private - non instantiable
     */
    private Preconditions() {}

    /**
     * Checks if the given boolean is true, if not throws an IllegalArgumentException
     *
     * @param shouldBeTrue teh boolean needing to be checked
     * @throws IllegalArgumentException if the boolean is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue)  {
            throw new IllegalArgumentException();
        }
    }

}
