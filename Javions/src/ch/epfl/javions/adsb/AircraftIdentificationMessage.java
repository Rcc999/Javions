package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * Represents an identification and category message
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {

    private static final String STRING = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ?????\s???????????????0123456789";
    private static final int CONSTANT_TYPECODE = 14;
    private static final int START_BITS_CA = 48;
    private static final int SIZE_BITS_CA = 3;
    private static final int SIZE_SEQUENCE_CALLSIGN = 8;
    private static final int START_BITS_CALLSIGN = 42;
    private static final int SIZE_BITS_CALLSIGN = 6;
    private static final int SPACE_CHAR_INDEX = 32;
    private static final int MAX_INDEX_STRING = 58;


    /**
     * Construct an identification and category message
     *
     * @param timeStampNs : the time stamp of the message
     * @param icaoAddress : the ICAO address of the aircraft
     * @param category    : the category of the aircraft
     * @param callSign    : the call sign of the aircraft
     * @throws NullPointerException     if the ICAO address or the call sign is null
     * @throws IllegalArgumentException if the time stamp in nanoseconde is negative
     */
    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException("");
        }
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    /**
     * Construct an identification and category message from a raw message
     *
     * @param rawMessage : the raw message
     * @return the identification and category message or null if the call sign is invalid
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        CallSign callSign = callSignOfAircraft(rawMessage);
        int category = categoryOfAircraft(rawMessage);
        return callSign == null ? null : new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }

    /**
     * Get the category of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the category of the aircraft
     */
    private static int categoryOfAircraft(RawMessage rawMessage) {
        int typeCode = rawMessage.typeCode();
        int mostSignificant4bitsCategory = CONSTANT_TYPECODE - typeCode;
        int cA = Bits.extractUInt(rawMessage.payload(), START_BITS_CA, SIZE_BITS_CA);
        return (mostSignificant4bitsCategory << 4) | cA;
    }

    /**
     * Get the call sign of the aircraft
     *
     * @param rawMessage : the raw message
     * @return the call sign of the aircraft or null if the call sign is invalid
     */
    private static CallSign callSignOfAircraft(RawMessage rawMessage) {
        StringBuilder stringBuilder = new StringBuilder(SIZE_SEQUENCE_CALLSIGN);
        int cI;
        for (int i = 0; i < 43; i += 6) {
            cI = Bits.extractUInt(rawMessage.payload(), START_BITS_CALLSIGN - i, SIZE_BITS_CALLSIGN);
            if ((cI >= MAX_INDEX_STRING || cI < 0)) {
                return null;
            }
            if (STRING.charAt(cI) == STRING.charAt(SPACE_CHAR_INDEX) && stringBuilder.isEmpty()) {
                continue;
            }
            stringBuilder.append(STRING.charAt(cI));
        }
        if (stringBuilder.toString().contains("?")) {
            return null;
        }
        return new CallSign(stringBuilder.toString().trim());
    }

    /**
     * Get the time stamp of the message
     *
     * @return the time stamp of the message
     */
    @Override
    public long timeStampNs() {
        return this.timeStampNs;
    }

    /**
     * Get the ICAO address of the aircraft
     *
     * @return the ICAO address of the aircraft
     */
    @Override
    public IcaoAddress icaoAddress() {
        return this.icaoAddress;
    }

}
