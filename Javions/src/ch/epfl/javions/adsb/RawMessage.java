package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;


/**
 * Represents a raw ADS-B message.
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    private static final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
    private static final int DF_VALUE = 17;
    private static final int START_ME = 51;
    private static final int SIZE_SIGNIFICANT_BIT = 5;
    private static final int ME_START_OCTET = 4;
    private static final int ME_LAST_OCTET = 11;
    private static final int START_DF = 3;
    private static final int ICAO_START = 1;
    private static final int ICAO_LAST = 4;
    private static final int ICAO_DIGIT_EXTRACT = 6;

    /**
     * Creates a new RawMessage from a timestamp in nanoseconds and a byte array of length 14.
     *
     * @param timeStampNs : the timestamp in nanoseconds
     * @param bytes       : the byte array of length 14
     * @throws IllegalArgumentException if the timestamp is negative or the byte array is not of length 14
     */
    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    /**
     * Creates a new RawMessage from a timestamp in nanoseconds and a byte array of length 14.
     *
     * @param timeStampsNs : the timestamp in nanoseconds
     * @param bytes        : the byte array of length 14
     * @return : a new RawMessage if the CRC is correct, null otherwise
     */
    public static RawMessage of(long timeStampsNs, byte[] bytes) {
        ByteString bytesBis = new ByteString(bytes);
        return crc24.crc(bytes) == 0 ? new RawMessage(timeStampsNs, bytesBis) : null;
    }

    /**
     * Returns the size of the message if the message is a downlink format 17 message, 0 otherwise.
     *
     * @param byte0 : the first byte of the message
     * @return : the size of the message if the message is a downlink format 17 message, 0 otherwise
     */
    public static int size(byte byte0) {
        return downLinkFormat(byte0) == DF_VALUE ? LENGTH : 0;
    }


    /**
     * Returns the type code of the message.
     *
     * @param payload : the payload of the message
     * @return : the type code of the message
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, START_ME, SIZE_SIGNIFICANT_BIT);
    }

    /**
     * Returns the downlink format of the message.
     *
     * @param b : the first byte of the message
     * @return : the downlink format of the message
     */
    private static int downLinkFormat(int b) {
        return Bits.extractUInt(b, START_DF, SIZE_SIGNIFICANT_BIT);
    }

    /**
     * Returns the downlink format of the message.
     *
     * @return : the downlink format of the message
     */
    public int downLinkFormat() {
        return downLinkFormat(bytes.byteAt(0));
    }

    /**
     * Returns the ICAO address of the aircraft.
     *
     * @return : the ICAO address of the aircraft
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress(HexFormat.of().withUpperCase().toHexDigits(bytes.bytesInRange(ICAO_START, ICAO_LAST), ICAO_DIGIT_EXTRACT));
    }

    /**
     * Returns the payload of the message.
     *
     * @return : the payload of the message
     */
    public long payload() {
        return bytes.bytesInRange(ME_START_OCTET, ME_LAST_OCTET);
    }

    /**
     * Returns the type code of the message.
     *
     * @return : the type code of the message
     */
    public int typeCode() {
        return typeCode(payload());
    }

}
