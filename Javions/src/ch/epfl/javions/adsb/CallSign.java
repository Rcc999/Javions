package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * Check call sign of an aircraft
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record CallSign(String string) {

    /**
     * The constructor checks if the call sign is valid or not
     *
     * @param string : call sign of an aircraft
     * @throws IllegalArgumentException if the call sign is invalid
     */
    public CallSign {
        Pattern num = Pattern.compile("[A-Z0-9 ]{0,8}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }
}
