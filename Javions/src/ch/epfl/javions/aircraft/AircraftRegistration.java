package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Registration of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AircraftRegistration(String string) {

    /**
     * The constructor checks if the registration is valid or not
     *
     * @param string : registration of an aircraft
     * @throws IllegalArgumentException if the registration is empty or invalid
     */
    public AircraftRegistration {
        Pattern num = Pattern.compile("[A-Z0-9 .?/_+-]+");
        Preconditions.checkArgument(!string.isEmpty() && num.matcher(string).matches());
    }
}
