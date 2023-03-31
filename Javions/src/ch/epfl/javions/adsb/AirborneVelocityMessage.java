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
        if(Double.isNaN(speedCalculator(rawMessage, subTypeCalculator(rawMessage))) || Double.isNaN(trackOrHeadingCalculator(rawMessage, subTypeCalculator(rawMessage)))) { return null; }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedCalculator(rawMessage, subTypeCalculator(rawMessage)), trackOrHeadingCalculator(rawMessage, subTypeCalculator(rawMessage)));
    }

    private static double speedCalculator(RawMessage rawMessage, int subType){
        return switch (subType) {
            case 1, 2 -> groundSpeedCalculator(rawMessage);
            case 3, 4 -> airSpeedCalculator(rawMessage);
            default -> Double.NaN;
        };
    }

    private static double trackOrHeadingCalculator(RawMessage rawMessage, int subType){
        return switch (subType) {
            case 1, 2 -> trackOrHeadingGroundCalculator(rawMessage);
            case 3, 4 -> trackOrHeadingAirCalculator(rawMessage);
            default -> Double.NaN;
        };
    }

    private static int [] groundMovementCalculator(RawMessage rawMessage){
        int Dew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 21, 1);
        int Vew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 11, 10) - 1;
        int Dns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 10, 1);
        int Vns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 0, 10) - 1;
        return new int[]{Dew, Vew, Dns, Vns};
    }


    private static double groundSpeedCalculator(RawMessage rawMessage){
        if(groundMovementCalculator(rawMessage)[1] == -1 || groundMovementCalculator(rawMessage)[3] == -1){ return Double.NaN; }
        if(subTypeCalculator(rawMessage) == 1 ){ return Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT); }
        if(subTypeCalculator(rawMessage) == 2){return 4 * Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT);}
        return Double.NaN;
    }

    private static double groundSpeedNormalized(RawMessage rawMessage){
        return Math.hypot(groundMovementCalculator(rawMessage)[1], groundMovementCalculator(rawMessage)[3]);
    }

    private static double groundHeadingEastWestCalculator(RawMessage rawMessage){
        return groundMovementCalculator(rawMessage)[0] == 0 ? groundMovementCalculator(rawMessage)[1] : -groundMovementCalculator(rawMessage)[1];
    }


    private static double groundHeadingNorthSouthCalculator(RawMessage rawMessage){
        return groundMovementCalculator(rawMessage) [2] == 0 ? groundMovementCalculator(rawMessage)[3] : -groundMovementCalculator(rawMessage)[3];
    }

    private static double trackOrHeadingGroundCalculator(RawMessage rawMessage){
        double trackOrHeadingGround =  Math.atan2(groundHeadingEastWestCalculator(rawMessage), groundHeadingNorthSouthCalculator(rawMessage));
        return trackOrHeadingGround < 0 ? trackOrHeadingGround + Units.Angle.TURN : trackOrHeadingGround;
    }

    private static int [] airMovementCalculator(RawMessage rawMessage){
        int SH = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 21, 1);
        int HDG = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 11, 10);
        int AS = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), 0, 10) - 1;
        return new int [] {SH, HDG, AS};
    }

    private static double airSpeedCalculator(RawMessage rawMessage){
        if(airMovementCalculator(rawMessage)[2] == -1){ return Double.NaN;}
        if(subTypeCalculator(rawMessage) ==  3){return Units.convertFrom(airMovementCalculator(rawMessage)[2], Units.Speed.KNOT );}
        if(subTypeCalculator(rawMessage) == 4){ return 4 * Units.convertFrom(airMovementCalculator(rawMessage)[2], Units.Speed.KNOT );}
        return Double.NaN ;
    }


    private static double trackOrHeadingAirCalculator(RawMessage rawMessage){
        if(airMovementCalculator(rawMessage)[0] == 1){ return Units.convertFrom(Math.scalb(airMovementCalculator(rawMessage)[1], -10), Units.Angle.RADIAN);}
        return Double.NaN;
    }

    private static int subTypeCalculator(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 48, 3);
    }

    private static int bitsInterpretedBySubType(RawMessage rawMessage){
        return Bits.extractUInt(rawMessage.payload(), 21, 22);
    }

    @Override
    public long timeStampNs() {return timeStampNs;}

    @Override
    public IcaoAddress icaoAddress() { return icaoAddress;}


}


