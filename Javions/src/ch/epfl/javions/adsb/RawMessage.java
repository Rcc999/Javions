package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Preconditions;

public record RawMessage() {

    private static long timeStampsNs;
    private static ByteString bytes;
    public static final int LENGTH = 14;

    public RawMessage{
        Preconditions.checkArgument(timeStampsNs < 0 );
    }

    //static RawMessage of(long timeStampsNs, byte [] bytes){}


}
