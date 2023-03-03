package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {

    /**
     *
     * @param string: the address of each flying object. The constructor checks if it is valid or not.
     */
    public IcaoAddress{
        Preconditions.checkArgument(!string.isEmpty());
        Pattern num = Pattern.compile("[0-9A-F]{6}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }

}

