package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record CallSign(String string) {

    public CallSign{
        Pattern num = Pattern.compile("[A-Z0-9 ]{0,8}");
        Preconditions.checkArgument(num.matcher(string).matches());
    }
}
