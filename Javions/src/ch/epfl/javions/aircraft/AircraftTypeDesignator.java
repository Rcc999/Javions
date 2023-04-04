package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Check the type designator of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AircraftTypeDesignator(String string) {

    /**
     * The constructor checks if the type designator is valid or not.
     *
     * @param string: type designator of the aircraft.
     * @throws IllegalArgumentException if the type designator is invalid
     */
    public AircraftTypeDesignator{
        if(string.equals("")){
            Preconditions.checkArgument(true);
        }
        else{
            Pattern num = Pattern.compile("[A-Z0-9]{2,4}");
            Preconditions.checkArgument(num.matcher(string).matches());
        }
    }
}
