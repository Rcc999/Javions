package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    public AircraftRegistration{
        Pattern num = Pattern.compile(string);
        Preconditions.checkArgument(num.matcher("[A-Z0-9 .?/_+-]+").matches());
    }
}
