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
        /*if(rawMessage.typeCode() ==  19){

        }*/

        if(speedCalculator(rawMessage, subTypeCalculator(rawMessage)) ==  0) {return null;}
        return null;

    }

    private static double speedCalculator(RawMessage rawMessage, int subType){
        return switch (subType) {
            case 1, 2 -> groundSpeedCalculator(rawMessage);
            case 3, 4 -> airSpeedCalculator(rawMessage);
            default -> 0;
        };
    }



    private static double trackOrHeadingCalculator(RawMessage rawMessage){
     return Math.atan2(groundHeadingNorthSouthCalculator(rawMessage), groundHeadingEastWestCalculator(rawMessage));
    }



    private static int subTypeCalculator(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 48, 3);
    }

    private static int bitsInterpretedBySubType(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 21, 22);
    }

    private static int [] groundMovementCalculator(RawMessage rawMessage){
        int Dew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 21, 1);
        int Vew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 11, 10) - 1;
        int Dns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 10, 1);
        int Vns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 0, 10) - 1;
        return new int[]{Dew, Vew, Dns, Vns};
    }

    private static double groundSpeedNormalized(RawMessage rawMessage){
        return Math.hypot(groundMovementCalculator(rawMessage)[2], groundMovementCalculator(rawMessage)[4]);
    }

    private static double groundSpeedCalculator(RawMessage rawMessage){
        if(groundMovementCalculator(rawMessage)[2] == 0 || groundMovementCalculator(rawMessage)[4] == -1){return 0;}
        if(subTypeCalculator(rawMessage) == 1 ){return Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT); }
        if(subTypeCalculator(rawMessage) == 2){return 4 * Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT);}
        return 0;
    }


    private static double groundHeadingEastWestCalculator(RawMessage rawMessage){
        return groundMovementCalculator(rawMessage)[1] == 0 ? groundSpeedNormalized(rawMessage) : -groundSpeedNormalized(rawMessage);
    }


    private static double groundHeadingNorthSouthCalculator(RawMessage rawMessage){
        return groundMovementCalculator(rawMessage) [3] == 0 ? groundSpeedNormalized(rawMessage) : -groundSpeedNormalized(rawMessage);
    }


    private static int [] airMovementCalculator(RawMessage rawMessage){
        int SH = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 21, 1);
        int HDG = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 11, 10);
        int AS = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 0, 10) - 1;
        return new int [] {SH, HDG, AS};
    }

    private static double airSpeedCalculator(RawMessage rawMessage){
        if(airMovementCalculator(rawMessage)[3] == -1){ return 0;}
        if(subTypeCalculator(rawMessage) ==  3){return Units.convertFrom(airMovementCalculator(rawMessage)[3], Units.Speed.KNOT );}
        if(subTypeCalculator(rawMessage) == 4){ return 4 * Units.convertFrom(airMovementCalculator(rawMessage)[3], Units.Speed.KNOT );}
        return 0;
    }


    private static double airHeadingCalculator(RawMessage rawMessage){
        if(airMovementCalculator(rawMessage)[1] == 1){ return Units.convertFrom(Math.scalb(airMovementCalculator(rawMessage)[2], -10), Units.Angle.RADIAN);}
        return 0;
    }

    @Override
    public long timeStampNs() {return timeStampNs;}

    @Override
    public IcaoAddress icaoAddress() { return icaoAddress;}
}
