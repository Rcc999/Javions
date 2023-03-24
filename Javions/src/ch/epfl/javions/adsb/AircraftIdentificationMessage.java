package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {

    private static final String STRING = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ?????\s???????????????0123456789";
    private static final int CONSTANT_TYPECODE = 14;
    private static final int START_BITS_CA = 48;
    private static final int SIZE_BITS_CA =  3;
    private static final int SIZE_SEQUENCE_CALLSIGN = 8;
    private static final int START_BITS_CALLSIGN = 42;
    private  static final int SIZE_BITS_CALLSIGN = 6;
    private static final int SPACE_CHAR_INDEX = 32;
    private static final int MAX_INDEX_STRING = 58;


    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {throw new NullPointerException("");}
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        CallSign callSign = callSignOfAircraft(rawMessage);
        int category = categoryOfAircraft(rawMessage);
        return callSign == null  ?  null : new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }
        private static int categoryOfAircraft (RawMessage rawMessage){
            int typeCode = rawMessage.typeCode();
            int mostSignificant4bitsCategory = CONSTANT_TYPECODE - typeCode;
            int cA = Bits.extractUInt(rawMessage.payload(), START_BITS_CA, SIZE_BITS_CA);
            return (mostSignificant4bitsCategory << 4) | cA;
        }

        private static CallSign callSignOfAircraft (RawMessage rawMessage){
            StringBuilder stringBuilder = new StringBuilder(SIZE_SEQUENCE_CALLSIGN);
            int cI;
            for (int i = 0; i < 43; i += 6) {
                cI = Bits.extractUInt(rawMessage.payload(), START_BITS_CALLSIGN - i, SIZE_BITS_CALLSIGN);
                if((cI >= MAX_INDEX_STRING || cI < 0)) {return null;}
                if(STRING.charAt(cI) == STRING.charAt(SPACE_CHAR_INDEX) && stringBuilder.isEmpty()){continue;}
                stringBuilder.append(STRING.charAt(cI));
            }
            if (stringBuilder.toString().contains("?")) { return null; }
            return new CallSign(stringBuilder.toString().trim());
        }

    @Override
    public long timeStampNs() {
        return this.timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return this.icaoAddress;
    }

    }
