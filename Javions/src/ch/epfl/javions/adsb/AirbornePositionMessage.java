package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity,
                                      double x, double y) implements Message{

    public AirbornePositionMessage {
        if (icaoAddress == null) { throw new NullPointerException("");}
        Preconditions.checkArgument(timeStampNs < 0 || (parity == 0 || parity == 1) || (x >= 0 && x < 1) || (y >= 0 && y < 1));
    }
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {return icaoAddress;}

    public static AirbornePositionMessage of(RawMessage rawMessage){
        return null;
    }


    private static double altitudeCalculator(RawMessage rawMessage){
        int alt =  Bits.extractUInt(rawMessage.payload(), 36, 12);
        if(determineParity(rawMessage) == 1){
            return (double) -1000 + removeBitForQ1(alt) * 25;
       } else {

          return 0;
        }
    }

    private static int determineParity(RawMessage rawMessage){
        return Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 36, 12) , 4, 1);
    }

    private static int removeBitForQ1(int num) {
        int mask = (1 << 4) - 1;
        return (char) ((num & ((~mask) << 1)) >>> 1) | (num & mask);
    }

}


