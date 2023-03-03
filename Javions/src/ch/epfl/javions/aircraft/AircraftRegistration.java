package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftRegistration(String string) {

    /**
     *
     * @param string: registration of an aircraft. The constructor checks if it is valid or not.
     */
    public AircraftRegistration{
        Preconditions.checkArgument(!string.isEmpty());
        Pattern num = Pattern.compile("[A-Z0-9 .?/_+-]+");
        Preconditions.checkArgument(num.matcher(string).matches());
    }
}
