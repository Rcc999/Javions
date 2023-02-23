package ch.epfl.javions;

import java.util.HexFormat;

public final class ByteString {

    private final byte[] bytes;

    public ByteString(byte[] bytes) {
        this.bytes = bytes;
    }

    public static ByteString ofHexadecimalString(String hexString){

        Preconditions.checkArgument(hexString.length() % 2 != 0);

        HexFormat hf = HexFormat.of().withUpperCase();
        //HexFormat hex = new HexFormat();
        byte[] bytes = hf.parseHex(hexString);
        return new ByteString(bytes);

        }

        public int size(){return bytes.length;}

    public int byteAt(int index) {

        if (index < 0 || index >= bytes.length) {
            throw new IndexOutOfBoundsException();
        }
        return bytes[index] & 0xff;
    }


    public long bytesInRange(int fromIndex, int toIndex){

        if (fromIndex < 0 || toIndex > bytes.length || toIndex <= fromIndex) {
            throw new IndexOutOfBoundsException();
        }
        if (toIndex - fromIndex > 8) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        for (int i = toIndex - 1; i >= fromIndex; i--) {
            result = (result << 8) | (bytes[i] & 0xff);
        }
        return result;
    }




}
