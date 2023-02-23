package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public final class ByteString {


    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public static ByteString ofHexadecimalString(String hexString) {

        Preconditions.checkArgument(hexString.length() % 2 != 0);

        /*HexFormat hf = HexFormat.of().withUpperCase();
        //HexFormat hex = new HexFormat();
        byte[] bytes = hf.parseHex(hexString);*/

        HexFormat hf = HexFormat.of().withUpperCase();
        byte[] bytesBis;
        try {
            bytesBis = hf.parseHex(hexString);
        } catch (IllegalArgumentException e) {
            throw new NumberFormatException("Invalid hexadecimal string: " + hexString);
        }

        return new ByteString(bytesBis);

    }

    public int size() {
        return bytes.length;
    }

    public int byteAt(int index) {

        Objects.checkIndex(index, bytes.length);

        return bytes[index] & 0xff;
    }


    public long bytesInRange(int fromIndex, int toIndex) {

        if (fromIndex < 0 || toIndex > bytes.length || toIndex <= fromIndex) {
            throw new IndexOutOfBoundsException();
        }

        Preconditions.checkArgument(toIndex - fromIndex > 8);

        long result = 0;
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            result = (result << 8) | (bytes[i] & 0xff);
        }
        return result;
    }

    public boolean equals(Object that0) {
        if (that0 instanceof ByteString that) {
            return Arrays.equals(bytes, that.bytes);
        } else {
            return false;
        }
    }

    public int hashCode(){
        return Arrays.hashCode(bytes);
    }

    public String toString(){
        HexFormat hef= HexFormat.of().withDelimiter("").withUpperCase();
        return hef.formatHex(bytes);
    }

}

