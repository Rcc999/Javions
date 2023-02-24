package ch.epfl.javions;

public final class Preconditions {

    /**
     * Constructor: private - non instantiable
     */
    private Preconditions() {}
    
    /**
     * check if assessment is true
     * @param shouldBeTrue teh boolean needing to be checked
     * @throws IllegalArgumentException when the assesment is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
