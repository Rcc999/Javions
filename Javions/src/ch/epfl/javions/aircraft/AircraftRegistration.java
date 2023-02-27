package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {
    public AircraftRegistration{
        Preconditions.checkArgument(!string.isEmpty());
        Pattern num = Pattern.compile("[A-Z0-9 .?/_+-]+");
        Preconditions.checkArgument(num.matcher(string).matches());
    }
}
