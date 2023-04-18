package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Calculate the Airborne velocity message
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {

    private static final int CONSTANT_TYPECODE_START = 48;
    private static final int CONSTANT_TYPECODE_SIZE = 3;
    private static final int BIT_TO_INTERPRET_TYPECODE = 21;
    private static final int BIT_TO_INTERPRET_SUBTYPE_SIZE = 22;
    private static final int DEW_AND_SH_START = 21;
    private static final int SIZE_OF_1 = 1;
    private static final int VEW_AND_HDG_START = 11;
    private static final int VEW_AND_HDG_SIZE = 10;
    private static final int DNS_START = 10;
    private static final int SIZE_OF_10 = 10;
    private static final int VNS_AND_AS_START = 0;

    /**
     * Construct an airborne velocity message
     *
     * @param timeStampNs    : the time at which the message was received
     * @param icaoAddress    : the ICAO address of the aircraft
     * @param speed          : the speed of the aircraft
     * @param trackOrHeading : the track or heading of the aircraft
     * @throws NullPointerException     if icaoAddress is null
     * @throws IllegalArgumentException if timeStampNs or speed or trackOrHeading is negative
     */
    public AirborneVelocityMessage {
        Preconditions.checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
        if (icaoAddress == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Construct an airborne velocity message from a raw message
     *
     * @param rawMessage : the raw message
     * @return the airborne velocity message or null if the message is invalid or null if the message is invalid (e.g. if the speed or the track/heading is NaN)
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        if (Double.isNaN(speedCalculator(rawMessage, subTypeCalculator(rawMessage))) || Double.isNaN(trackOrHeadingCalculator(rawMessage, subTypeCalculator(rawMessage)))) {
            return null;
        }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), speedCalculator(rawMessage, subTypeCalculator(rawMessage)), trackOrHeadingCalculator(rawMessage, subTypeCalculator(rawMessage)));
    }

    /**
     * Calculate the subtype of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the subtype of the aircraft
     */
    private static int subTypeCalculator(RawMessage rawMessage) {
        return Bits.extractUInt(rawMessage.payload(), CONSTANT_TYPECODE_START, CONSTANT_TYPECODE_SIZE);
    }

    /**
     * Calculate the bits interpreted by the subtype
     *
     * @param rawMessage :  the raw message
     * @return : the bits interpreted by the subtype
     */
    private static int bitsInterpretedBySubType(RawMessage rawMessage) {
        return Bits.extractUInt(rawMessage.payload(), BIT_TO_INTERPRET_TYPECODE, BIT_TO_INTERPRET_SUBTYPE_SIZE);
    }

    /**
     * Calculate the speed of the aircraft in account  of the typecode
     *
     * @param rawMessage : the raw message
     * @param subType    : the subtype of the message
     * @return the speed of the aircraft
     */

    private static double speedCalculator(RawMessage rawMessage, int subType) {
        return switch (subType) {
            case 1, 2 -> groundSpeedCalculator(rawMessage);
            case 3, 4 -> airSpeedCalculator(rawMessage);
            default -> Double.NaN;
        };
    }

    /**
     * Calculate the track or heading of the aircraft in account of the typecode
     *
     * @param rawMessage : the raw message
     * @param subType    : the subtype of the message
     * @return the track or heading of the aircraft
     */
    private static double trackOrHeadingCalculator(RawMessage rawMessage, int subType) {
        return switch (subType) {
            case 1, 2 -> trackOrHeadingGroundCalculator(rawMessage);
            case 3, 4 -> trackOrHeadingAirCalculator(rawMessage);
            default -> Double.NaN;
        };
    }

    /**
     * Calculate ground speed of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the ground speed of the aircraft
     */
    private static int[] groundMovementCalculator(RawMessage rawMessage) {
        int Dew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), DEW_AND_SH_START, SIZE_OF_1);
        int Vew = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), VEW_AND_HDG_START, VEW_AND_HDG_SIZE) - 1;
        int Dns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), DNS_START, SIZE_OF_1);
        int Vns = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), VNS_AND_AS_START, SIZE_OF_10) - 1;
        return new int[]{Dew, Vew, Dns, Vns};
    }


    /**
     * Calculate the ground speed of the aircraft taken the subtype into account
     *
     * @param rawMessage : the raw message
     * @return the ground speed of the aircraft in the correct unit
     */
    private static double groundSpeedCalculator(RawMessage rawMessage) {
        if (groundMovementCalculator(rawMessage)[1] == -1 || groundMovementCalculator(rawMessage)[3] == -1) {
            return Double.NaN;
        }
        if (subTypeCalculator(rawMessage) == 1) {
            return Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT);
        }
        if (subTypeCalculator(rawMessage) == 2) {
            return 4 * Units.convertFrom(groundSpeedNormalized(rawMessage), Units.Speed.KNOT);
        }
        return Double.NaN;
    }

    /**
     * Normalize the ground speed of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the normalized ground speed of the aircraft
     */
    private static double groundSpeedNormalized(RawMessage rawMessage) {
        return Math.hypot(groundMovementCalculator(rawMessage)[1], groundMovementCalculator(rawMessage)[3]);
    }

    /**
     * Calculate the cap of the aircraft from East to West with the SH bit taken into account
     *
     * @param rawMessage : the raw message
     * @return the cap of the aircraft
     */
    private static double groundHeadingEastWestCalculator(RawMessage rawMessage) {
        return groundMovementCalculator(rawMessage)[0] == 0 ? groundMovementCalculator(rawMessage)[1] : -groundMovementCalculator(rawMessage)[1];
    }

    /**
     * Calculate the cap of the aircraft from North to South with the SH bit taken into account
     *
     * @param rawMessage : the raw message
     * @return the cap of the aircraft
     */
    private static double groundHeadingNorthSouthCalculator(RawMessage rawMessage) {
        return groundMovementCalculator(rawMessage)[2] == 0 ? groundMovementCalculator(rawMessage)[3] : -groundMovementCalculator(rawMessage)[3];
    }

    /**
     * Calculate the track or heading of the aircraft from the ground
     *
     * @param rawMessage : the raw message
     * @return the track or heading of the aircraft
     */
    private static double trackOrHeadingGroundCalculator(RawMessage rawMessage) {
        double trackOrHeadingGround = Math.atan2(groundHeadingEastWestCalculator(rawMessage), groundHeadingNorthSouthCalculator(rawMessage));
        return trackOrHeadingGround < 0 ? trackOrHeadingGround + Units.Angle.TURN : trackOrHeadingGround;
    }

    /**
     * Calculate the air speed of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the air speed of the aircraft
     */
    private static int[] airMovementCalculator(RawMessage rawMessage) {
        int SH = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), DEW_AND_SH_START, SIZE_OF_1);
        int HDG = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), VEW_AND_HDG_START, VEW_AND_HDG_SIZE);
        int AS = Bits.extractUInt(bitsInterpretedBySubType(rawMessage), VNS_AND_AS_START, SIZE_OF_10) - 1;
        return new int[]{SH, HDG, AS};
    }

    /**
     * Calculate the air speed of the aircraft taken the subtype into account
     *
     * @param rawMessage : the raw message
     * @return the air speed of the aircraft in the correct unit
     */
    private static double airSpeedCalculator(RawMessage rawMessage) {
        if (airMovementCalculator(rawMessage)[2] == -1) {
            return Double.NaN;
        }
        if (subTypeCalculator(rawMessage) == 3) {
            return Units.convertFrom(airMovementCalculator(rawMessage)[2], Units.Speed.KNOT);
        }
        if (subTypeCalculator(rawMessage) == 4) {
            return 4 * Units.convertFrom(airMovementCalculator(rawMessage)[2], Units.Speed.KNOT);
        }
        return Double.NaN;
    }

    /**
     * Calculate the track or heading of the aircraft from the air taken the SH bit into account
     *
     * @param rawMessage : the raw message
     * @return the track or heading of the aircraft
     */
    private static double trackOrHeadingAirCalculator(RawMessage rawMessage) {
        return airMovementCalculator(rawMessage)[0] == 1 ? Units.convertFrom(Math.scalb(airMovementCalculator(rawMessage)[1], -10), Units.Angle.TURN) : Double.NaN;
    }


    /**
     * Calculate the time stamp of the message
     *
     * @return the time stamp of the message
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * Calculate the ICAO address of the aircraft
     *
     * @return the ICAO address of the aircraft
     */
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }
}


