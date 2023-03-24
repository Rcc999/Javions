package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading) implements Message {

    public AirborneVelocityMessage {
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
        if(icaoAddress ==  null){ throw new NullPointerException();}
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        if(rawMessage.typeCode() ==  19){

        }


        if(speedCalculator(rawMessage) ==  0) {return null;}

        return null;

    }

    private static double speedCalculator(RawMessage rawMessage){
        int  subType =  Bits.extractUInt(rawMessage.payload(), 48, 3);
        int Vew = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 11, 10);
        int Vns = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 0, 10);
        int AS = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 0, 10);
        double groundSpeed = Math.hypot(Vew - 1, Vns - 1);
        double groundSpeedInKnots = Units.convertFrom(groundSpeed, Units.Speed.KNOT );
        if(Vew == 0 || Vns == 0){return 0;}
        if(subType == 1 ){return groundSpeedInKnots; }
        if(subType == 2){return 4 * groundSpeedInKnots;}
        if(subType ==  3){return Units.convertFrom(AS, Units.Speed.KNOT );}
        if(subType == 4){ return 4 * Units.convertFrom(AS, Units.Speed.KNOT );}
        return 0;
    }

    private static double trackOrHeadingCalculator(RawMessage rawMessage){
        int subType =  Bits.extractUInt(rawMessage.payload(), 48, 3);
        int Dew = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 21, 1);
        int Dns = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 10, 1);
        int SH = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 21, 1);
        int HDG = Bits.extractUInt(Bits.extractUInt(rawMessage.payload(), 21, 22), 11, 10);

        return 0;
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
