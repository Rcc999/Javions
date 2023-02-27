package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {
    public AircraftDescription {
        Pattern num = Pattern.compile("[HLPRSTV-][0123468][EJPT-]");
        Preconditions.checkArgument(num.matcher(string).matches());
    }

}

