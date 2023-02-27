package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {
    public AircraftTypeDesignator{
        Pattern num = Pattern.compile("[A-Z0-9]{2,4}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }
}
