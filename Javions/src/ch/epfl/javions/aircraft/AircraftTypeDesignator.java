package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String string) {

    /**
     *
     * @param string: type designator of the aircraft. The constructor checks if it is valid or not.
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
