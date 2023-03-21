package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity,
                                      double x, double y) implements Message{


    public AirbornePositionMessage {
        if (icaoAddress == null) { throw new NullPointerException("");}
        Preconditions.checkArgument(timeStampNs < 0 || (parity == 0 || parity == 1) || (x >= 0 && x < 1) || (y >= 0 && y < 1));
    }


    public static AirbornePositionMessage of(RawMessage rawMessage){
        return null;
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }



}
