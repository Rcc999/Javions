package ch.epfl.javions;

public final class Preconditions {

    /**
     * Constructor: private - non instantiable
     */
    private Preconditions() {}

    /**
     *
     * @param shouldBeTrue: to check argument
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
