package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;

public final class ByteString {


    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes;
    }

    public static ByteString ofHexadecimalString(String hexString) {

        Preconditions.checkArgument(hexString.length() % 2 != 0);

        //HexFormat hf = HexFormat.of().withUpperCase();
        //HexFormat hex = new HexFormat();
        //byte[] bytes = hf.parseHex(hexString);
        HexFormat hf = HexFormat.of().withUpperCase();

        byte[] bytes = new byte[]{(byte) 0x01, (byte) 0xAB};
        String string = hf.formatHex(bytes); // vaut "01AB"
        byte[] bytes2 = hf.parseHex(string); // identique à bytes
        System.out.println(Arrays.equals(bytes, bytes2)); // true

        return new ByteString(bytes);

    }

    public int size() {
        return bytes.length;
    }

    public int byteAt(int index) {

        if (index < 0 || index >= bytes.length) {
            throw new IndexOutOfBoundsException();
        }
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
        if (that0 instanceof ByteString that && Arrays.equals(((ByteString) that0).bytes, that.bytes)) {
            return true;
        }
        return false;
    }

    public int hashCode(){
        return Arrays.hashCode(bytes);
    }

    public String toString(){
        return HexFormat.of().wi
    }

}

