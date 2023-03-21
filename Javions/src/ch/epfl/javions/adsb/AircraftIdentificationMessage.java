package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {

    private static final String string = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ???? ???????????????0123456789";
    private static AircraftIdentificationMessage aircraftIdentificationMessage;

    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException("");
        }
        Preconditions.checkArgument(timeStampNs < 0);
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }


    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        return new AircraftIdentificationMessage(aircraftIdentificationMessage.timeStampNs(), aircraftIdentificationMessage.icaoAddress(), categoryOfAircraft(rawMessage), callSignOfAircraft(rawMessage));

    }
        private static int categoryOfAircraft (RawMessage rawMessage){
            int typeCode = rawMessage.typeCode();
            int mostSignificant4bitsCategory = 14 - typeCode;
            int cA = Bits.extractUInt(rawMessage.payload(), 48, 3);
            return (mostSignificant4bitsCategory << 4) | cA;
        }


        private static CallSign callSignOfAircraft (RawMessage rawMessage){
            StringBuilder stringBuilder = new StringBuilder(8);
            int cI;
            for (int i = 0; i < 49; i += 6) {
                cI = Bits.extractUInt(rawMessage.payload(), 42 - i, 6);
                if(string.charAt(cI) == string.charAt(32) && stringBuilder.isEmpty()){
                    continue;
                }
                stringBuilder.append(string.charAt(cI));
            }
            if (stringBuilder.toString().contains("?")) {return null;}
            return new CallSign(stringBuilder.toString());

        }
    }
