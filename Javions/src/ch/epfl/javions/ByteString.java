package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {


    private final byte[] bytes;

    public ByteString(byte[] bytes) { this.bytes = bytes.clone();}

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

    public int size() { return bytes.length; }

    public int byteAt(int index) {
        Objects.checkIndex(index, bytes.length);
        return bytes[index] & 0xff;
    }


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

    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) {
            return Arrays.equals(bytes, that.bytes);
        } else {
            return false;
        }
    }

    public int hashCode(){ return Arrays.hashCode(bytes);}

    public String toString(){
        HexFormat hef= HexFormat.of().withDelimiter("").withUpperCase();
        return hef.formatHex(bytes);
    }

}

