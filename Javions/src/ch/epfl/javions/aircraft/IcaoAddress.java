package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    public IcaoAddress{
        Pattern num = Pattern.compile(string);
        Preconditions.checkArgument(num.matcher("[0-9A-F]{6}").matches());
    }

}

/** To be DELETED later
 * Some notes: matcher is a method of Pattern. The whole "num.matcher("[0-9A-F]{6}").matches()" check
 * if address's elements belong in that list [0-9A-F] and the address has 6 characters
 */
