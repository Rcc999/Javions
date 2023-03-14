package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

public record RawMessage(long timeStampNs,ByteString bytes) {

    public static final int LENGTH = 14;
    private static Crc24 crc24;
    private static IcaoAddress icaoAddress;
    private static final int DF_VALUE = 17;
    private static final int START_ME = 51;
    private static final int SIZE_SIGNIFICANT_BIT = 5;
    private static final int ME_START_OCTET = 4;
    private static final int ME_LAST_OCTET = 11;
    private static final int START_DF = 3;
    private static final int ICAO_START = 1;
    private static final int ICAO_LAST = 4;
    private static final int ICAO_DIGIT_EXTRACT = 6;



    public RawMessage{
        Preconditions.checkArgument(timeStampNs >= 0 );
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    public static RawMessage of(long timeStampsNs, byte [] bytes){
        ByteString bytesBis = new ByteString(bytes);
        return crc24.crc(bytes) == 0 ? new RawMessage(timeStampsNs, bytesBis) : null;
    }

    public static int size(byte byte0){return downLinkFormat(byte0) == DF_VALUE ? LENGTH : 0;}

    public static int typeCode(long payload){
        return Bits.extractUInt(payload,START_ME,SIZE_SIGNIFICANT_BIT);
    }

    private static int downLinkFormat(int b){
        return Bits.extractUInt(b, START_DF, SIZE_SIGNIFICANT_BIT) ;
    }

    public int downLinkFormat(){
        return downLinkFormat(bytes.byteAt(0));
    }


    public IcaoAddress icaoAddress(){
        return new IcaoAddress(HexFormat.of().withUpperCase().toHexDigits(bytes.bytesInRange(ICAO_START, ICAO_LAST) ,  ICAO_DIGIT_EXTRACT));
    }

    public long payload(){
        return bytes.bytesInRange(ME_START_OCTET, ME_LAST_OCTET);
    }

    public int typeCode(){
        return typeCode(payload());
    }

}
