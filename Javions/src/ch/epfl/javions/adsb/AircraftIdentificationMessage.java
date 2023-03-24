package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {

    private static final String STRING = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ?????\s???????????????0123456789";


    private static AircraftIdentificationMessage aircraftIdentificationMessage;

    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException("");
        }
        Preconditions.checkArgument(timeStampNs >= 0);
    }

    @Override
    public long timeStampNs() {
        return this.timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return this.icaoAddress;
    }


    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        /*System.out.println(rawMessage.icaoAddress());
        System.out.println(rawMessage.timeStampNs());
        System.out.println(categoryOfAircraft(rawMessage));
        System.out.println(callSignOfAircraft(rawMessage));*/
        CallSign callSign = callSignOfAircraft(rawMessage);
        int category = categoryOfAircraft(rawMessage);
        return callSign == null  ?  null : new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }
        private static int categoryOfAircraft (RawMessage rawMessage){
            int typeCode = rawMessage.typeCode();
            //if(typeCode >= 1 && typeCode <= 4) {
                int mostSignificant4bitsCategory = 14 - typeCode;
                int cA = Bits.extractUInt(rawMessage.payload(), 48, 3);
                return (mostSignificant4bitsCategory << 4) | cA;
            //}
            //return 0;
        }


        private static CallSign callSignOfAircraft (RawMessage rawMessage){
            StringBuilder stringBuilder = new StringBuilder(8);
            int cI;
            for (int i = 0; i < 43; i += 6) {
                cI = Bits.extractUInt(rawMessage.payload(), 42 - i, 6);
                if((cI >= 58 || cI < 0)) {return null;}
                if(STRING.charAt(cI) == STRING.charAt(32) && stringBuilder.isEmpty()){
                    continue;
                }
                stringBuilder.append(STRING.charAt(cI));
            }
            if (stringBuilder.toString().contains("?")) { return null; }
            return new CallSign(stringBuilder.toString().trim());
        }
    }
