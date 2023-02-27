package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {

    //Check. question about point 3.1.1 and verify if regex is correct
    public IcaoAddress{
        Preconditions.checkArgument(!string.isEmpty());
        Pattern num = Pattern.compile("[0-9A-F]{6}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }

}

/** To be DELETED later
 * Some notes: matcher is a method of Pattern. The whole "num.matcher("[0-9A-F]{6}").matches()" check
 * if address's elements belong in that list [0-9A-F] and the address has 6 characters
 */
