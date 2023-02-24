package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {


    private final byte[] bytes;

    /**
     * Constructor: private:
     * @param bytes
     */
    public ByteString(byte[] bytes) { this.bytes = bytes.clone();}

    /**
     *
     * @param hexString : hexadecimal chain
     * @returns the byte string of which the string passed as argument is the hexadecimal representation
     * or throws these exceptions.
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
     *
     * @return the length of the chain
     */
    public int size() { return bytes.length; }

    /**
     *
     * @param index : index of the bit given
     * @return the byte (interpreted as unsigned) at the given index, or throws IndexOutOfBoundsException if this one is invalid
     */
    public int byteAt(int index) {
        Objects.checkIndex(index, bytes.length);
        return bytes[index] & 0xff;
    }

    /**
     *
     * @param fromIndex : initial index
     * @param toIndex : final index
     * @return which returns the bytes between the indexes fromIndex (inclusive) and toIndex (excluded) as a value of type long
     * or throws these exceptions.
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
     *
     * @param that0 : object to compare with
     * @return  true if and only if the value passed to it is also an instance of ByteString and its bytes are identical to those of the receiver
     */
    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) {
            return Arrays.equals(bytes, that.bytes);
        } else {
            return false;
        }
    }

    /**
     *
     * @return the value returned by the hashCode method of Arrays applied to the array containing the bytes,
     */
    public int hashCode(){ return Arrays.hashCode(bytes);}


    /**
     *
     * @return a representation of the bytes of the string in hexadecimal
     */
    public String toString(){
        HexFormat hef= HexFormat.of().withDelimiter("").withUpperCase();
        return hef.formatHex(bytes);
    }

}

