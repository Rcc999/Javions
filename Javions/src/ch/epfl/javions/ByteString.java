package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;


/**
 * Operation on a ByteString.
 *
 * @author Tuan Dang Nguyen (361089)
 * @author Rayane Charif Chefchouni (339839)
 */
public final class ByteString {


    private final byte[] bytes;

    /**
     * Constructor: private to prevent instantiation
     *
     * @param bytes : array of bytes
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Returns a new ByteString whose bytes are the same as those of the receiver
     *
     * @param hexString : hexadecimal chain
     * @throws NumberFormatException    if the string passed as argument is not a valid hexadecimal string
     * @throws IllegalArgumentException if the string passed as argument has an odd length
     * @throws NullPointerException     if the string passed as argument is null
     * @returns the byte string of which the string passed as argument is the hexadecimal representation
     */
    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0);
        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytesBis;
        try {
            bytesBis = hf.parseHex(hexString);
        } catch (IllegalArgumentException exc) {
            throw new NumberFormatException("Invalid hexadecimal string: " + hexString);
        }
        return new ByteString(bytesBis);
    }

    /**
     * Returns the length of the chain
     *
     * @return the length of the chain
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Returns the byte (interpreted as unsigned) at the given index
     *
     * @param index : index of the bit given
     * @return the byte (interpreted as unsigned) at the given index, or throws IndexOutOfBoundsException if this one is invalid
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public int byteAt(int index) {
        Objects.checkIndex(index, bytes.length);
        return bytes[index] & 0xff;
    }

    /**
     * Returns the bytes between the indexes fromIndex (inclusive) and toIndex (excluded) as a value of type long
     *
     * @param fromIndex : initial index
     * @param toIndex   : final index
     * @return which returns the bytes between the indexes fromIndex (inclusive) and toIndex (excluded) as a value of type long
     * @throws IndexOutOfBoundsException if the indexes are invalid
     * @throws IllegalArgumentException  if the indexes are not in the correct order or if the number of bytes is greater than 8
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        int numberBytes = Long.BYTES;
        Preconditions.checkArgument(!(toIndex - fromIndex > numberBytes));
        long finalOctet = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            finalOctet <<= Long.BYTES;
            finalOctet |= (bytes[i] & 0xFF);
        }
        return finalOctet;
    }

    /**
     * Returns true if and only if the value passed to it is also an instance of ByteString and its bytes are identical to those of the receiver
     *
     * @param that0 : object to compare with
     * @return true if and only if the value passed to it is also an instance of ByteString and its bytes are identical to those of the receiver
     */
    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) {
            return Arrays.equals(bytes, that.bytes);
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code of the bytes of the string
     *
     * @return the value returned by the hashCode method of Arrays applied to the array containing the bytes,
     */
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }


    /**
     * Returns a string representation of the bytes of the string in hexadecimal
     *
     * @return a representation of the bytes of the string in hexadecimal
     */
    public String toString() {
        HexFormat hef = HexFormat.of().withDelimiter("").withUpperCase();
        return hef.formatHex(bytes);
    }

}

