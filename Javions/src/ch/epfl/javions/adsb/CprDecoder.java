package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;

public class CprDecoder {

    private CprDecoder(){};

    public static GeoPos decodePosition(double x0, double y0, double x1,double y1, int mostRecent){
        Preconditions.checkArgument(mostRecent == 0 || mostRecent ==  1);
        return null;
    }

}
