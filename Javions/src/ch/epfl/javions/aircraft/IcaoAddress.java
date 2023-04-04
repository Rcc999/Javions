package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Check the ICAO address of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record IcaoAddress(String string) {

    /**
     * The constructor checks if the ICAO address is valid or not.
     *
     * @param string : the address of each flying object.
     * @throws IllegalArgumentException if the address is empty or invalid
     */
    public IcaoAddress{
        Preconditions.checkArgument(!string.isEmpty());
        Pattern num = Pattern.compile("[0-9A-F]{6}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }

}

