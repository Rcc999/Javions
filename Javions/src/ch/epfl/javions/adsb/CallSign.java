package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record CallSign(String string) {
    //Check - unsure
    //seems true to me
    public CallSign{
        Pattern num = Pattern.compile(string);
        Preconditions.checkArgument(num.matcher("[A-Z0-9 ]{0,8}").matches());
    }
}
