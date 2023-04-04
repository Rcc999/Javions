package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Description of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AircraftDescription(String string) {

    /**
     * Check the description of an aircraft
     *
     * @param string the description of an aircraft. The constructor checks if it is valid or not.
     * @throws IllegalArgumentException if the description is invalid
     */
    public AircraftDescription {
        if (string.equals("")) {
            Preconditions.checkArgument(true);
        } else {
            Pattern num = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
            Preconditions.checkArgument(num.matcher(string).matches());
        }
    }

}

