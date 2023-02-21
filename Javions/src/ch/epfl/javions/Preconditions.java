package ch.epfl.javions;

public final class Preconditions {

    public Preconditions() {}

    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
